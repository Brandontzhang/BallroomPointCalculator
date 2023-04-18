package com.models;

import java.util.HashMap;

import org.json.JSONObject;

import com.models.Event.Level;
import com.models.Event.Style;

public class PointTracker {

    private HashMap<Event.Level, HashMap<Event.Style, HashMap<String, Integer>>> pointMap = new HashMap<>();

    public PointTracker() {
        this.setup();
    }

    private void setup() {
        for (Level level : Level.values()) {
            pointMap.put(level, new HashMap<>());
        }

        for (HashMap<Style, HashMap<String, Integer>> styleMap : pointMap.values()) {
            for (Style style : Style.values()) {
                styleMap.put(style, new HashMap<>());
            }
        }
    }

    public void addPoints(Level level, Style style, String dance, int points) {
        HashMap<Style, HashMap<String, Integer>> stylePoints = pointMap.get(level);
        HashMap<String, Integer> dancePoints = stylePoints.get(style);

        dancePoints.put(dance, dancePoints.getOrDefault(dance, 0) + points);
    }

    public int getPoints(Level level, Style style, String dance) {
        HashMap<Style, HashMap<String, Integer>> stylePoints = pointMap.get(level);
        HashMap<String, Integer> dancePoints = stylePoints.get(style);

        return dancePoints.get(dance);
    }

    public JSONObject getPointsJSON() {
        return new JSONObject(this.pointMap);
    }

    public HashMap<Event.Level, HashMap<Event.Style, HashMap<String, Integer>>> getPointMap() {
        return this.pointMap;
    }

}
