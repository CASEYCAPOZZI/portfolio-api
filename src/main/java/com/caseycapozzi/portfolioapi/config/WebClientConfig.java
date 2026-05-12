package com.caseycapozzi.portfolioapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // We call .builder() directly here to avoid the "UnsatisfiedDependencyException"
        return WebClient.builder().build();
    }
}