package com.caseycapozzi.portfolioapi.logic;

import com.caseycapozzi.portfolioapi.model.WaterCondition;
import com.caseycapozzi.portfolioapi.model.WeatherCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BitePredictorTest {

	private BitePredictor predictor;

	@BeforeEach
	void setUp() {
		predictor = new BitePredictor();
	}

	@Test
	void calculateScore_brulePrimeConditions_capsAt100() {
		WaterCondition water = WaterCondition.builder().cfs(170.0).build();
		WeatherCondition weather = WeatherCondition.builder().trend("Falling").build();

		assertEquals(100, predictor.calculateScore(water, weather, "Brule"));
	}

	@Test
	void calculateScore_bruleRisingPressureAndHighFlow_clampsToZero() {
		WaterCondition water = WaterCondition.builder().cfs(400.0).build();
		WeatherCondition weather = WeatherCondition.builder().trend("Rising").build();

		assertEquals(0, predictor.calculateScore(water, weather, "Brule"));
	}

	@Test
	void calculateScore_redCedarSweetSpotWithSteadyPressure() {
		WaterCondition water = WaterCondition.builder().cfs(800.0).build();
		WeatherCondition weather = WeatherCondition.builder().trend("Steady").build();

		assertEquals(70, predictor.calculateScore(water, weather, "RedCedar"));
	}

	@Test
	void calculateWadingStatus_bruleIdealHeight() {
		assertEquals(
				"Ideal: Perfect wading levels for the Hwy 2 run.",
				predictor.calculateWadingStatus(1.5, "Brule"));
	}

	@Test
	void calculateWadingStatus_nonBruleUsesGenericMessage() {
		assertEquals(
				"Standard wading - use caution.",
				predictor.calculateWadingStatus(2.0, "RedCedar"));
	}
}
