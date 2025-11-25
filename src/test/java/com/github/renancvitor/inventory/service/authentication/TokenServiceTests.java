package com.github.renancvitor.inventory.service.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.renancvitor.inventory.application.authentication.service.TokenService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TokenServiceTests {

    @InjectMocks
    private TokenService tokenService;

    private User loggedInUser;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(tokenService, "secret", "secretTest");

        loggedInUser = TestEntityFactory.createUser();
        loggedInUser.getPerson().getEmail();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldGenerateToken() {
            String token = tokenService.generateToken(loggedInUser);

            assertNotNull(token);
            assertTrue(token.length() > 0);
        }

        @Test
        void shouldSuccessfullyGetSubject() {
            String token = tokenService.generateToken(loggedInUser);
            String subject = tokenService.getSubject(token);

            assertEquals(loggedInUser.getPerson().getEmail(), subject);
        }

        @Test
        void shouldGenerateTokenWithCorrectSubject() {
            String token = tokenService.generateToken(loggedInUser);
            String subject = tokenService.getSubject(token);

            assertEquals(loggedInUser.getPerson().getEmail(), subject);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldErrorWithInvalidToken() {
            String invalidToken = "aaa.bbb.ccc";

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> tokenService.getSubject(invalidToken));

            assertTrue(exception.getMessage().contains("Token JWT inválido ou expirado."));
        }

        @Test
        void shouldThrowWhenUserIsNull() {
            assertThrows(NullPointerException.class,
                    () -> tokenService.generateToken(null));
        }

        @Test
        void shouldThrowWhenUserPersonIsNull() {
            loggedInUser.setPerson(null);

            assertThrows(NullPointerException.class,
                    () -> tokenService.generateToken(loggedInUser));
        }

        @Test
        void shouldGenerateTokenEvenIfEmailIsNull() {
            loggedInUser.getPerson().setEmail(null);

            String token = tokenService.generateToken(loggedInUser);

            assertNotNull(token);
            assertTrue(token.length() > 0);

            String subject = tokenService.getSubject(token);
            assertNull(subject);
        }

        @Test
        void shouldThrowWhenTokenIsExpired() {
            Algorithm algorithm = Algorithm.HMAC256("secretTest");

            String expiredToken = JWT.create()
                    .withIssuer("Inventory Notification System API.")
                    .withSubject(loggedInUser.getPerson().getEmail())
                    .withExpiresAt(Instant.now().minusSeconds(10)) // já expirado
                    .sign(algorithm);

            RuntimeException e = assertThrows(RuntimeException.class,
                    () -> tokenService.getSubject(expiredToken));

            assertTrue(e.getMessage().contains("Token JWT inválido ou expirado."));
        }
    }

}
