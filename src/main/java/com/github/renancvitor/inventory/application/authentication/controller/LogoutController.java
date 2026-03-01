package com.github.renancvitor.inventory.application.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("access_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

}
