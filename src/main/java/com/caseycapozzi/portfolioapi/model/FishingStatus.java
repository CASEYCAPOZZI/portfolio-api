package com.caseycapozzi.portfolioapi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FishingStatus {
    private WaterCondition water;
    private WeatherCondition weather;
    private int biteScore;
    private String summary;
    private String wadingStatus; // <--- Add this
}