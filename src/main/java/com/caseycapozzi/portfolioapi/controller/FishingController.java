package com.caseycapozzi.portfolioapi.controller;

import com.caseycapozzi.portfolioapi.logic.BitePredictor;
import com.caseycapozzi.portfolioapi.model.FishingStatus;
import com.caseycapozzi.portfolioapi.model.WaterCondition;
import com.caseycapozzi.portfolioapi.model.WeatherCondition;
import com.caseycapozzi.portfolioapi.service.RiverService;
import com.caseycapozzi.portfolioapi.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fishing")
public class FishingController {

    private final RiverService riverService;
    private final WeatherService weatherService;
    private final BitePredictor bitePredictor;

    public FishingController(RiverService riverService, WeatherService weatherService, BitePredictor bitePredictor) {
        this.riverService = riverService;
        this.weatherService = weatherService;
        this.bitePredictor = bitePredictor;
    }

    @GetMapping("/colfax/status")
    public FishingStatus getColfaxStatus() {
        WaterCondition water = riverService.getRiverConditions("05367500"); 
        WeatherCondition weather = weatherService.getWeatherForStation("KLUM"); // Menomonie
        
        // Fix: Added "RedCedar" to match the new 3-argument signature
        int score = bitePredictor.calculateScore(water, weather, "RedCedar");
        String wading = bitePredictor.calculateWadingStatus(water.getGaugeHeight(), "RedCedar");

        return FishingStatus.builder()
                .water(water)
                .weather(weather)
                .biteScore(score)
                .wadingStatus(wading)
                .summary("Red Cedar Update: " + generateSummary(score))
                .build();
    }

    @GetMapping("/brule/status")
    public FishingStatus getBruleStatus() {
        WaterCondition water = riverService.getRiverConditions("04025500");
        WeatherCondition weather = weatherService.getWeatherForStation("KSUW"); // Superior
        
        int score = bitePredictor.calculateScore(water, weather, "Brule");
        String wading = bitePredictor.calculateWadingStatus(water.getGaugeHeight(), "Brule");

        return FishingStatus.builder()
                .water(water)
                .weather(weather)
                .biteScore(score)
                .wadingStatus(wading)
                .summary("Bois Brule Update: " + generateSummary(score))
                .build();
    }

    // Fix: Defined the missing helper method
    private String generateSummary(int score) {
        if (score >= 80) return "Prime conditions. The bite is on.";
        if (score >= 50) return "Fair conditions. Might find a few in the eddies.";
        return "Tough conditions. Stay in the shop and tie some flies.";
    }
}