package com.o2cmparser;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.models.Competition;
import com.models.Event;
import com.models.PointTracker;
import com.models.Event.Level;
import com.models.Event.Style;
import com.o2cmparser.filters.FilterMethods.UserCompetitionFilter;
import com.o2cmparser.filters.FilterMethods.UserEventsFilter;

public class RoundParser {

    private static Element body = null;

    private static void fetchHTML(String firstName, String lastName) throws IOException {
        String url = String.format("https://results.o2cm.com/individual.asp?szFirst=%s&szLast=%s&DoSearch=Search", firstName, lastName);
        body = parseHtml(url).body();
    }

    public static Document parseHtml(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		return doc;
	}

	private static List<Competition> getUserComps() throws IOException {
        if (body == null) {
            throw new NullPointerException("HTML has not been retrieved");
        }

        Elements tableRows = body.getElementsByTag("tr");
        Elements tempTableRows = tableRows.clone();

        tempTableRows.filter(new UserCompetitionFilter());
        tempTableRows.removeIf(e -> e.childrenSize() == 0 || e.child(0).childrenSize() == 0);

        List<String> compData = new ArrayList<>();
        tempTableRows.forEach(e -> compData.add(e.text()));

        List<Competition> comps = new ArrayList<>();
    
        for (int i = 0; i < compData.size(); i++) {
            String[] compInfo = compData.get(i).split("\\s*-\\s*");
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt("20" + compInfo[2]), Integer.parseInt(compInfo[0]) - 1, Integer.parseInt(compInfo[1]), 0, 0);
            Competition newComp = new Competition(compInfo[3], date);
            comps.add(newComp);
        }

		return comps;
	}

	public static List<Competition> populateEventsOnComps(List<Competition> comps) throws IOException {
        if (body == null) {
            throw new NullPointerException("HTML has not been retrieved");
        }

		Elements tableRows = body.getElementsByTag("tr");
        Elements tempTableRows = tableRows.clone();

		tempTableRows.filter(new UserEventsFilter());

		ArrayDeque<Competition> compDeque = new ArrayDeque<>(comps);
        List<Competition> processedComps = new ArrayList<>();

        int emptyLines = 2;
		tempTableRows.stream().skip(emptyLines).forEach(e -> {
			if (e.text().isEmpty()) {
                if (compDeque.peek() != null) {
                    processedComps.add(compDeque.poll());
                }
            } else {
                Competition comp = compDeque.peek();
                if (comp != null) {
                    String eventDetails = e.text();
                    // Determine: level, placement, and style
                    Event newEvent = parseEventDetails(eventDetails);
                    
                    if (newEvent != null) {
                        newEvent.setHref(getHref(e));
                        comp.addEvent(newEvent);
                    }
                }
            }
		});

		return processedComps;
	}

    private static Event parseEventDetails(String eventDetails) {
        int placement = Integer.parseInt(eventDetails.replaceAll("\\).*", ""));

        Level level = Level.OPEN;

        if (stringContains(eventDetails, new String[]{"BRONZE", "BEGINNER"}, new String[]{"PRE-BRONZE", "JUNIOR"})) {
            level = Level.BRONZE;
        } else if (stringContains(eventDetails, new String[]{"SILVER", "INTERMEDIATE"})) {
            level = Level.SILVER;
        } else if (stringContains(eventDetails, new String[]{"GOLD", "ADVANCED"})) {
            level = Level.GOLD;
        } else if (stringContains(eventDetails, new String[]{"NEWCOMER"})) {
            level = Level.NEWCOMER;
        } else if (stringContains(eventDetails, new String[]{"SYLLABUS"})) {
            level = Level.SYLLABUS;
        } else {
            // Also currently contains invalid types (PRE-BRONZE), but we don't need to calculate points for open for now
            level = Level.OPEN;
        }

        Style style = Style.LATIN;
        if (stringContains(eventDetails, new String[]{"LATIN", "INTL. PASO DOBLE", "INTL. RUMBA", "INTL. SAMBA", "INTL. CHA CHA", "INTL. JIVE"})) {
            style = Style.LATIN;
        } else if (stringContains(eventDetails, new String[]{"RHYTHM", "AM. SWING", "AM. RUMBA", "AM. CHA CHA", "AM. BOLERO", "AM. MAMBO"})) {
            style = Style.RHYTHM;
        } else if (stringContains(eventDetails, new String[]{"STANDARD", "INTL. WALTZ", "INTL. TANGO", "INTL. FOXTROT", "INTL. V. WALTZ", "INTL. QUICKSTEP"})) {
            style = Style.STANDARD;
        } else if (stringContains(eventDetails, new String[]{"SMOOTH", "AM. WALTZ", "AM. TANGO", "AM. FOXTROT", "AM. V. WALTZ"})) {
            style = Style.SMOOTH;
        } else {
            return null;
        }

        return new Event(style, placement, level);
    }

    private static String getHref(Element e) {
		if (e.hasAttr("href")) {
			return e.attr("href");
		} 
		while (e.childrenSize() > 0) {
				e = e.child(0);
				if (e.hasAttr("href")) {
					return e.attr("href");
				} 
		}

		return "";
	}

    private static boolean stringContains(String eventDetails, String[] checks) {
        String eventDetailsUpper = eventDetails.toUpperCase();
        BiFunction<Boolean, String, Boolean> checkStringContains = (b1, check) -> b1 || eventDetailsUpper.contains(check);
        BinaryOperator<Boolean> or = (b1, b2) -> b1 || b2;
        Boolean identity = false;

        boolean containsOneOfChecks = Stream.of(checks).reduce(identity, checkStringContains, or);

        return containsOneOfChecks;
    } 

    private static boolean stringContains(String eventDetails, String[] checks, String[] exceptions) {
        String eventDetailsUpper = eventDetails.toUpperCase();
        BiFunction<Boolean, String, Boolean> checkStringContains = (b1, check) -> b1 || eventDetailsUpper.contains(check);
        BinaryOperator<Boolean> or = (b1, b2) -> b1 || b2;
        Boolean identity = false;

        boolean containsOneOfChecks = Stream.of(checks).reduce(identity, checkStringContains, or);

        boolean containsOneOfExceptions = Stream.of(exceptions).reduce(identity, checkStringContains, or);

        return containsOneOfChecks && !containsOneOfExceptions;
    } 

    public static PointTracker getPoints(String firstName, String lastName) throws IOException {
        fetchHTML(firstName, lastName);
		List<Competition> comps = getUserComps();
		comps = populateEventsOnComps(comps);
        List<Event> events = getEventsWithPoints(comps);
        PointTracker userPoints = populatePointTracker(events);

        return userPoints;
    }

    // Only events where the user placed higher than 8th can count towards points
    private static List<Event> getEventsWithPoints(List<Competition> comps) {
        // Get all the events from the competitions
        Function<Competition, Stream<Event>> compToEvents =  c -> c.getEvents().stream();
        List<Event> allEvents = comps.stream().flatMap(compToEvents).collect(Collectors.toList());
        allEvents.removeIf(event -> event.getPlacement() > 8);

        return allEvents;
    }

    private static PointTracker populatePointTracker(List<Event> allEvents) {
        PointTracker pointTracker = new PointTracker();
        // Start populating the points
        allEvents.stream().forEach(event -> {
                try {
                    event.getDance().forEach(dance -> {
                        try {
                            pointTracker.addPoints(event.getLevel(), event.getStyle(), dance, event.getPoints());
                        } catch (IOException e) {
                            System.out.println("Could not fetch data from O2CM");
                        }
                    });
                } catch (IOException e) {
                    System.out.println("Could not fetch data from O2CM");
                }
        });

        return pointTracker;
    }

	public static void main(String[] args) throws IOException {

        // PointTracker pt = getPoints("Brandon", "Zhang");
        // System.out.println("Cha Cha: " + pointTracker.getPoints(Level.GOLD, Style.LATIN, "Cha Cha"));
        // System.out.println("Rumba: " + pointTracker.getPoints(Level.GOLD, Style.LATIN, "Rumba"));
        // System.out.println("Samba: " + pointTracker.getPoints(Level.GOLD, Style.LATIN, "Samba"));
        // System.out.println("Jive: " + pointTracker.getPoints(Level.GOLD, Style.LATIN, "Jive"));

        // System.out.println("Cha Cha: " + pointTracker.getPoints(Level.GOLD, Style.RHYTHM, "Cha Cha"));
        // System.out.println("Rumba: " + pointTracker.getPoints(Level.GOLD, Style.RHYTHM, "Rumba"));
        // System.out.println("Samba: " + pointTracker.getPoints(Level.GOLD, Style.RHYTHM, "Swing"));
        // System.out.println("Jive: " + pointTracker.getPoints(Level.GOLD, Style.RHYTHM, "Mambo"));

        // Potential for improvement:
        // Get measurements: is the slowdown in the html retrieval (not much to do there), or calculations?
        // 1. Thread for fetching html
        // 2. Thread for calculating points

	}
}