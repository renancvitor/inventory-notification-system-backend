package com.github.renancvitor.inventory.application.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.renancvitor.inventory.application.authentication.dto.JWTTokenData;
import com.github.renancvitor.inventory.application.authentication.dto.LoginData;
import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.user.dto.UserSummaryData;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<UserSummaryData> authentication(@RequestBody @Valid LoginData data,
            HttpServletResponse response) {
        JWTTokenData jwtTokenData = authenticationService.authentication(data, authenticationManager);

        Cookie cookie = new Cookie("access_token", jwtTokenData.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 2);

        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);

        return ResponseEntity.ok(jwtTokenData.user());
    }

}
