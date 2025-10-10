package com.github.renancvitor.inventory.application.authentication.dto;

import com.github.renancvitor.inventory.application.user.dto.UserSummaryData;

public record JWTTokenData(String tokem, UserSummaryData user, Boolean firstAccess) {
}
