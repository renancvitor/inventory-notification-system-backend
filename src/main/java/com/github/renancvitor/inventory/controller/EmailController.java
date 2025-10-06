package com.github.renancvitor.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.dto.email.EmailRequest;
import com.github.renancvitor.inventory.dto.email.EmailResponse;
import com.github.renancvitor.inventory.service.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<EmailResponse> send(@RequestBody @Valid EmailRequest request,
            @AuthenticationPrincipal User loggedInUser) {
        EmailResponse response = emailService.sendEmail(request, loggedInUser);

        return ResponseEntity.status(response.success() ? 200 : 500).body(response);
    }

}
