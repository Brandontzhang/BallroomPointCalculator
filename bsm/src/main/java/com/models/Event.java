package com.models;

import java.io.IOException;
import java.util.List;

import com.o2cmparser.EventParser;

public class Event {

  public enum Level {
    NEWCOMER, 
    BRONZE, 
    SILVER, 
    GOLD, 
    OPEN, SYLLABUS
  }

  public enum Style {
    SMOOTH, 
    STANDARD,
    LATIN,
    RHYTHM
  }
  
  private Style style;

  private int placement;

  private Level level;

  private String href;

  private int points = -1;

  private List<String> dance = null;

  public Event(Style style, int placement, Level level) {
    this.style = style;
    this.placement = placement;
    this.level = level;
  }

  public Style getStyle() {
    return this.style;
  }

  public void setStyle(Style style) {
    this.style = style;
  }

  public int getPlacement() {
    return this.placement;
  }

  public void setPlacement(int placement) {
    this.placement = placement;
  }

  public Level getLevel() {
    return this.level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public String getHref() {
    return this.href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public List<String> getDance() throws IOException {
    if (this.dance != null) {
      return this.dance;
    }

    EventParser.fetchHTML(href);
    return EventParser.getEventDances();
  }

  public int getPoints() throws IOException {
    // Cache it in this class if 
    // if points have already been calculated, no need to do it again
    if (this.points != -1) {
      return this.points;
    }
    // Fetch the html href
    EventParser.fetchHTML(this.href);

    // Continue if there was a semi/quarter
    boolean hasSemi = EventParser.containsSemi();
    boolean hasQuarter = EventParser.containsQuarter();

    // calculate the points

    if (!hasSemi) {
      return 0;
    }

    if (placement >= 4) {
      return hasQuarter ? 1 : 0;
    }

    if (placement == 1) {
      return 3;
    }

    if (placement == 2) {
      return 2;
    }

    if (placement == 3) {
      return 1;
    } 

    return 0;
  }

}
