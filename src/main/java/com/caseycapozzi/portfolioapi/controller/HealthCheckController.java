package com.caseycapozzi.portfolioapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	record HealthCheckResponse(String status, String message) {}

	@GetMapping("/health")
	public HealthCheckResponse health() {
		return new HealthCheckResponse("UP", "Casey's Portfolio API is live");
	}
}

