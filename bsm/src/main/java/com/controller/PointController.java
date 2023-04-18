package com.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.PointTracker;
import com.models.Event.Level;
import com.o2cmparser.RoundParser;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class PointController {


    @GetMapping("/points/{firstName}/{lastName}")
    public Map<Level, ?> getUserPoints(@PathVariable String firstName, @PathVariable String lastName) throws IOException {
        PointTracker pt = RoundParser.getPoints(firstName, lastName);

        return pt.getPointMap();
    }
}
