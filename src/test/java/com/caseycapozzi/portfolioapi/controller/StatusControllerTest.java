package com.caseycapozzi.portfolioapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatusControllerTest {

	@Test
	void status_usesProductionWhenNoActiveProfiles() throws Exception {
		Environment environment = mock(Environment.class);
		when(environment.getActiveProfiles()).thenReturn(new String[] {});

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StatusController(environment)).build();

		mockMvc.perform(get("/api/v1/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP and running!"))
				.andExpect(jsonPath("$.environment").value("production"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	void status_reportsActiveProfiles() throws Exception {
		Environment environment = mock(Environment.class);
		when(environment.getActiveProfiles()).thenReturn(new String[] { "dev", "local" });

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StatusController(environment)).build();

		mockMvc.perform(get("/api/v1/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.environment").value("dev,local"));
	}
}
