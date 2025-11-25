package com.github.renancvitor.inventory.controller.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.controller.AuthenticationController;
import com.github.renancvitor.inventory.application.authentication.dto.JWTTokenData;
import com.github.renancvitor.inventory.application.authentication.dto.LoginData;
import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthenticationControllerTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private LoginData loginData;
    private JWTTokenData jwtTokenData;

    @BeforeEach
    void setup() {
        loginData = new LoginData("email@test.com", "123456");
        jwtTokenData = new JWTTokenData("jwt-token", null, false);
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldReturn200AndJWTTokenDataWhenAuthenticationSucceeds() {
            when(authenticationService.authentication(any(LoginData.class), any(AuthenticationManager.class)))
                    .thenReturn(jwtTokenData);

            ResponseEntity<JWTTokenData> response = authenticationController.authentication(loginData);

            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertEquals(jwtTokenData, response.getBody());
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldPropagateExceptionWhenServiceThrows() {
            when(authenticationService.authentication(any(LoginData.class), any(AuthenticationManager.class)))
                    .thenThrow(new RuntimeException("Auth failed"));

            assertThrows(RuntimeException.class, () -> authenticationController.authentication(loginData));
        }
    }
}