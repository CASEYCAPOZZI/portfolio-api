package com.caseycapozzi.portfolioapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
    	registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000", 
                "https://caseycapozzi.com",
                "https://www.caseycapozzi.com"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
}
}

