package com.caseycapozzi.portfolioapi.service;

import com.caseycapozzi.portfolioapi.model.UsgsResponse;
import com.caseycapozzi.portfolioapi.model.WaterCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class RiverService {

    private final WebClient webClient;
    private final String USGS_BASE_URL = "https://waterservices.usgs.gov/nwis/iv/";

    public RiverService(WebClient webClient) {
        this.webClient = webClient;
    }

    // This is the method the Controller is looking for
    public WaterCondition getRiverConditions(String stationId) {
        log.info("Fetching USGS data for station: {}", stationId);

        UsgsResponse response = this.webClient.get()
                .uri(USGS_BASE_URL + "?format=json&sites=" + stationId + "&parameterCd=00060,00065&siteStatus=all")
                .retrieve()
                .bodyToMono(UsgsResponse.class)
                .block(); 

        return mapToWaterCondition(response);
    }

    private WaterCondition mapToWaterCondition(UsgsResponse response) {
        if (response == null || response.getValue() == null || response.getValue().getTimeSeries().isEmpty()) {
            return WaterCondition.builder().siteName("Station Offline or No Data").build();
        }

        var timeSeriesList = response.getValue().getTimeSeries();
        String siteName = timeSeriesList.get(0).getSourceInfo().getSiteName();
        
        Double cfs = 0.0;
        Double height = 0.0;
        String lastUpdated = "Unknown";

        for (var ts : timeSeriesList) {
            if (!ts.getValues().get(0).getValuePoints().isEmpty()) {
                var point = ts.getValues().get(0).getValuePoints().get(0);
                String rawValue = point.getValue();
                lastUpdated = point.getDateTime();

                String varCode = ts.getVariable().getVariableCode().get(0).getValue();
                
                if ("00060".equals(varCode)) {
                    cfs = Double.parseDouble(rawValue);
                } else if ("00065".equals(varCode)) {
                    height = Double.parseDouble(rawValue);
                }
            }
        }

        return WaterCondition.builder()
                .siteName(siteName)
                .cfs(cfs)
                .gaugeHeight(height)
                .lastUpdated(lastUpdated)
                .build();
    }
}