package com.caseycapozzi.portfolioapi.controller;

import com.caseycapozzi.portfolioapi.dto.StatusResponse;

import java.time.LocalDateTime;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

	private final Environment environment;

	public StatusController(Environment environment) {
		this.environment = environment;
	}

	@GetMapping("/api/v1/status")
	public StatusResponse status() {
		String[] activeProfiles = environment.getActiveProfiles();
		String env = activeProfiles.length > 0 ? String.join(",", activeProfiles) : "production";
		return new StatusResponse("UP and running!", env, LocalDateTime.now());
	}
}
