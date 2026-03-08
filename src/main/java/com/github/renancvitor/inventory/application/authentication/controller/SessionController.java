package com.github.renancvitor.inventory.application.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.renancvitor.inventory.application.user.dto.UserSummaryData;
import com.github.renancvitor.inventory.domain.entity.user.User;

@RestController
@RequestMapping("/auth")
public class SessionController {

    @GetMapping("/me")
    public ResponseEntity<UserSummaryData> getCurrentUser(@AuthenticationPrincipal User user) {

        return ResponseEntity.ok(new UserSummaryData(user));
    }
    
}
