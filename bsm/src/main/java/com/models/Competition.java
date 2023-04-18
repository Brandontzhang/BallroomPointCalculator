package com.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.models.Event.Level;
import com.models.Event.Style;
/**
 * Holding a competitions data
 */
public class Competition {

  	private String name;

    private Calendar date;

	private List<Event> events = new ArrayList<>();

	/**
	 * Class constructor 
	 * @param name
	 * @param date
	 */
		public Competition (String name, Calendar date) {
			this.name = name;
			this.date = date;
		}

	/**
	 * Returns the name
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return date
	 * @return Date
	 */
	public Calendar getDate() {
		return this.date;
	}

	/**
	 * Set date
	 * @param date
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}

	public void addEvent(Event e) {
		this.events.add(e);
	}

	public List<Event> getEvents() {
		return this.events;
	}

	public void printEvents() {
		this.events.forEach(event -> System.out.println(event.getPlacement()));
	}

	public List<Event> getEventByStyleAndLevel(Style style,Level level) {
		List<Event> events = new ArrayList<>(this.events);

		events.removeIf(e -> e.getLevel() != level || e.getStyle() != style);

		return events;
	}

	public List<Event> getEventByStyleAndLevel(Style style, List<Level> levels) {
		List<Event> events = new ArrayList<>(this.events);

		events.removeIf(e -> !levels.contains(e.getLevel()) || e.getStyle() != style);

		return events;
	}
    
}
