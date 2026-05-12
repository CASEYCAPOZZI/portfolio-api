package com.caseycapozzi.portfolioapi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherCondition {
    private Double temperature;      // In Celsius
    private Double barometricPressure; // In inHg (inches of Mercury)
    private String description;      // e.g., "Partly Cloudy"
    private String trend;            // "Falling", "Rising", or "Steady"
}