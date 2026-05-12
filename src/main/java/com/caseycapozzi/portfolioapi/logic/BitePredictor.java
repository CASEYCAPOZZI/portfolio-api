package com.caseycapozzi.portfolioapi.logic;

import com.caseycapozzi.portfolioapi.model.WaterCondition;
import com.caseycapozzi.portfolioapi.model.WeatherCondition;
import org.springframework.stereotype.Component;

@Component
public class BitePredictor {

    public int calculateScore(WaterCondition water, WeatherCondition weather, String riverName) {
        int score = 50;

        // Barometer Logic (Universal for both rivers)
        if ("Falling".equalsIgnoreCase(weather.getTrend())) score += 30;
        else if ("Rising".equalsIgnoreCase(weather.getTrend())) score -= 20;

        // River-Specific Flow Logic
        if ("Brule".equalsIgnoreCase(riverName)) {
            score += calculateBruleFlow(water.getCfs());
        } else {
            score += calculateRedCedarFlow(water.getCfs());
        }

        return Math.max(0, Math.min(100, score));
    }

    private int calculateBruleFlow(Double cfs) {
        // The Brule is smaller. 140-200 CFS is the "Sweet Spot" for wading.
        if (cfs == null) return 0;
        if (cfs >= 140 && cfs <= 200) return 20;
        if (cfs > 350) return -30; // Getting dangerous/blown out
        return 0;
    }

    private int calculateRedCedarFlow(Double cfs) {
        // Your existing Red Cedar logic
        if (cfs == null) return 0;
        if (cfs >= 600 && cfs <= 950) return 20;
        if (cfs > 1500) return -30;
        return 0;
    }

    public String calculateWadingStatus(Double height, String riverName) {
        
        if (!"Brule".equalsIgnoreCase(riverName)) {
            return "Standard wading - use caution.";
        }
    
        // Brule-specific wading thresholds
        if (height < 1.3) return "Low & Bony: Rocks are exposed, stealth is key.";
        if (height >= 1.3 && height <= 1.6) return "Ideal: Perfect wading levels for the Hwy 2 run.";
        if (height > 1.6 && height <= 1.9) return "Caution: Strong push. Use a wading staff near the campground.";
        return "Dangerous: High water. Wading not recommended; fish from the bank.";
    }
}