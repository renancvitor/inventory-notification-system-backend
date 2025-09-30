package com.github.renancvitor.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.renancvitor.inventory.dto.authentication.JWTTokenData;
import com.github.renancvitor.inventory.dto.authentication.LoginData;
import com.github.renancvitor.inventory.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<JWTTokenData> authentication(@RequestBody @Valid LoginData data) {
        JWTTokenData jwtTokenData = authenticationService.authentication(data, authenticationManager);
        return ResponseEntity.ok(jwtTokenData);
    }

}
