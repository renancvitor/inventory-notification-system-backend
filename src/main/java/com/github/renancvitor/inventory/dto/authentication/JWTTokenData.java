package com.github.renancvitor.inventory.dto.authentication;

import com.github.renancvitor.inventory.dto.user.UserSummaryData;

public record JWTTokenData(String tokem, UserSummaryData user, Boolean firstAccess) {
}
