package com.o2cmparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EventParser {
    private static Element body = null;

    public static void fetchHTML(String href) throws IOException {
        body = parseHTML(href).body();
    }

    public static Document parseHTML(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		return doc;
	}

    public static boolean containsSemi() {
        Elements roundSelect = body.getElementsByTag("select");
        String rounds = roundSelect.text();

        return rounds.contains("Semi-Final");
    }

    public static boolean containsQuarter() {
        Elements roundSelect = body.getElementsByTag("select");
        String rounds = roundSelect.text();

        return rounds.contains("Quarter-Final");
    }

    public static List<String> getEventDances() {
        Elements danceNames = body.getElementsByAttributeValue("class", "h3");
        List<String> dances = new ArrayList<>();
        danceNames.forEach(dance -> dances.add(dance.text()));
        dances.removeIf(d -> d.equals("Summary"));
        return dances;
    }

    // private static int getCompetitorNumber(String firstName, String lastName) {
    //     Elements personsTable = body.getElementsByAttributeValue("width", "400");
    //     String[] personsData = personsTable.text().split(" ");

    //     int competitorNumber = -1;
    //     for (int i = 0; i < personsData.length - 2; i++) {
    //         if (personsData[i].matches("-?(0|[1-9]\\d*)")) {
    //             competitorNumber = Integer.parseInt(personsData[i]);
    //         }
    //         if (personsData[i].equals(firstName) && personsData[i + 1].equals(lastName + ",")) {
    //             break;
    //         }
    //     }

    //     return competitorNumber;
    // }

    // // Wow I did not need to do this actually
    // private static int getPlacement(int competitorNumber) {
    //     int numDances = getEventDances().size();

    //     // Retrieves overall placement from Summary table
    //     if (numDances > 1) {
    //         Elements eventPlacementsTable = body.getElementsByAttributeValue("width", "250");
    //         String[] placementDetails = eventPlacementsTable.text().split(" ");

    //         for (int i = 0; i < placementDetails.length; i++) {
    //             if (placementDetails[i].matches("-?(0|[1-9]\\d*)") && Integer.parseInt(placementDetails[i]) == competitorNumber) {
    //                 return Integer.parseInt(placementDetails[i + numDances + 2]);
    //             }
    //         }
    //     }

    //     // The placement 
    //     return -1;
    // }

    public static void main(String[] args) throws IOException {
        fetchHTML("https://results.o2cm.com/scoresheet3.asp?event=tub19&heatid=40323021"); 
        getEventDances();
    }
}
