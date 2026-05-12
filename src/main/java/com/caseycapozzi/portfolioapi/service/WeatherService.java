package com.caseycapozzi.portfolioapi.service;

import com.caseycapozzi.portfolioapi.model.NwsResponse;
import com.caseycapozzi.portfolioapi.model.WeatherCondition;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeatherService {

    private final WebClient webClient;

    public WeatherService(WebClient webClient) {
        this.webClient = webClient;
    }

    // This is the method the Controller is looking for
    public WeatherCondition getWeatherForStation(String icaoCode) {
        NwsResponse response = this.webClient.get()
                .uri("https://api.weather.gov/stations/" + icaoCode + "/observations?limit=12")
                .header("User-Agent", "(caseycapozzi.com, casey@example.com)")
                .retrieve()
                .bodyToMono(NwsResponse.class)
                .block();

        return mapToWeatherCondition(response);
    }

    private WeatherCondition mapToWeatherCondition(NwsResponse response) {
        if (response == null || response.getFeatures() == null || response.getFeatures().isEmpty()) {
            return WeatherCondition.builder().description("N/A").trend("Steady").build();
        }

        var latestProps = response.getFeatures().get(0).getProperties();
        var historicalProps = response.getFeatures().get(response.getFeatures().size() - 1).getProperties();

        double currentRaw = latestProps.getBarometricPressure().getValue() != null ? latestProps.getBarometricPressure().getValue() : 101325.0;
        double historicalRaw = historicalProps.getBarometricPressure().getValue() != null ? historicalProps.getBarometricPressure().getValue() : currentRaw;

        double currentInHg = Math.round((currentRaw * 0.0002953) * 100.0) / 100.0;

        return WeatherCondition.builder()
                .temperature(latestProps.getTemperature().getValue())
                .barometricPressure(currentInHg)
                .description(latestProps.getTextDescription())
                .trend(calculateTrend(currentRaw, historicalRaw))
                .build();
    }

    private String calculateTrend(double current, double historical) {
        double deltaInHg = (current - historical) * 0.0002953;
        if (deltaInHg <= -0.02) return "Falling";
        if (deltaInHg >= 0.02) return "Rising";
        return "Steady";
    }
}