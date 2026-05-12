package com.caseycapozzi.portfolioapi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaterCondition {
    private String siteName;
    private Double cfs;          // Flow (Cubic Feet per Second)
    private Double gaugeHeight;  // Depth (Feet)
    private String lastUpdated;
}