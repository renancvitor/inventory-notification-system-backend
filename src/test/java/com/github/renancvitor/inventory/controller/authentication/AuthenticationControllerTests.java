package com.github.renancvitor.inventory.controller.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import com.github.renancvitor.inventory.application.user.dto.UserSummaryData;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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
            HttpServletResponse response = mock(HttpServletResponse.class);

            when(authenticationService.authentication(
                    any(LoginData.class),
                    any(AuthenticationManager.class)))
                    .thenReturn(jwtTokenData);

            ResponseEntity<UserSummaryData> result = authenticationController.authentication(loginData);

            assertNotNull(result);
            assertEquals(200, result.getStatusCode().value());
            assertEquals(jwtTokenData.user(), result.getBody());

            verify(authenticationService).authentication(
                    any(LoginData.class),
                    any(AuthenticationManager.class));

            verify(response).addCookie(any(Cookie.class));
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldPropagateExceptionWhenServiceThrows() {
            HttpServletResponse response = mock(HttpServletResponse.class);

            when(authenticationService.authentication(
                    any(LoginData.class),
                    any(AuthenticationManager.class)))
                    .thenThrow(new RuntimeException("Auth failed"));

            assertThrows(RuntimeException.class, () -> authenticationController.authentication(loginData));

            verify(authenticationService).authentication(
                    any(LoginData.class),
                    any(AuthenticationManager.class));

            verifyNoInteractions(response);
        }
    }
}