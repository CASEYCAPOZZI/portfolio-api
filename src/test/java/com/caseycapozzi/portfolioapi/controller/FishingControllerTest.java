package com.caseycapozzi.portfolioapi.controller;

import com.caseycapozzi.portfolioapi.logic.BitePredictor;
import com.caseycapozzi.portfolioapi.model.WaterCondition;
import com.caseycapozzi.portfolioapi.model.WeatherCondition;
import com.caseycapozzi.portfolioapi.service.RiverService;
import com.caseycapozzi.portfolioapi.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FishingController.class)
@Import(BitePredictor.class)
class FishingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RiverService riverService;

	@MockitoBean
	private WeatherService weatherService;

	@Test
	void bruleStatus_returnsFishingPayload() throws Exception {
		WaterCondition water = WaterCondition.builder()
				.siteName("BOIS BRULE RIVER AT BRULE, WI")
				.cfs(156.0)
				.gaugeHeight(1.83)
				.build();
		WeatherCondition weather = WeatherCondition.builder()
				.trend("Falling")
				.temperature(4.3)
				.build();

		when(riverService.getRiverConditions("04025500")).thenReturn(water);
		when(weatherService.getWeatherForStation("KSUW")).thenReturn(weather);

		mockMvc.perform(get("/api/fishing/brule/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.biteScore").value(100))
				.andExpect(jsonPath("$.summary").value("Bois Brule Update: Prime conditions. The bite is on."))
				.andExpect(jsonPath("$.water.cfs").value(156.0));
	}

	@Test
	void colfaxStatus_returnsFishingPayload() throws Exception {
		WaterCondition water = WaterCondition.builder()
				.siteName("RED CEDAR RIVER NEAR COLFAX, WI")
				.cfs(776.0)
				.gaugeHeight(2.73)
				.build();
		WeatherCondition weather = WeatherCondition.builder()
				.trend("Steady")
				.temperature(14.8)
				.build();

		when(riverService.getRiverConditions("05367500")).thenReturn(water);
		when(weatherService.getWeatherForStation("KLUM")).thenReturn(weather);

		mockMvc.perform(get("/api/fishing/colfax/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.biteScore").value(70))
				.andExpect(jsonPath("$.summary").value("Red Cedar Update: Fair conditions. Might find a few in the eddies."));
	}
}
