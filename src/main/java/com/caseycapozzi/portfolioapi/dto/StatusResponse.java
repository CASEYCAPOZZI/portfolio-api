package com.caseycapozzi.portfolioapi.dto;

import java.time.LocalDateTime;

public record StatusResponse(String status, String environment, LocalDateTime timestamp) {}
