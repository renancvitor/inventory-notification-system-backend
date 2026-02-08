package com.github.renancvitor.inventory.service.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.dto.JWTTokenData;
import com.github.renancvitor.inventory.application.authentication.dto.LoginData;
import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.authentication.service.TokenService;
import com.github.renancvitor.inventory.application.user.repository.UserRepository;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthenticationServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User loggedInUser;
    private UserTypeEntity userTypeEntity;

    @BeforeEach
    void setup() {
        userTypeEntity = TestEntityFactory.createUserTypeAdmin();

        loggedInUser = TestEntityFactory.createUser();
        loggedInUser.setPassword("strongPsw");
        loggedInUser.setUserType(userTypeEntity);
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldSuccessfullyAuthenticate() {
            LoginData data = new LoginData(loggedInUser.getPerson().getEmail(), loggedInUser.getPassword());

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(loggedInUser);

            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
            when(tokenService.generateToken(loggedInUser)).thenReturn("randomJwt");

            JWTTokenData result = authenticationService.authentication(data, authenticationManager);

            assertNotNull(result);
            assertEquals("randomJwt", result.token());
            assertEquals(loggedInUser.getId(), result.user().id());
        }

        @Test
        void shouldPassCorrectUserToTokenService() {
            LoginData data = new LoginData(loggedInUser.getPerson().getEmail(), loggedInUser.getPassword());

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(loggedInUser);

            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
            when(tokenService.generateToken(loggedInUser)).thenReturn("randomJwt");

            authenticationService.authentication(data, authenticationManager);

            verify(tokenService).generateToken(loggedInUser);
        }

        @Test
        void shouldReturnCorrectFirstAccessFlag() {
            loggedInUser.setFirstAccess(true);
            LoginData data = new LoginData(loggedInUser.getPerson().getEmail(), loggedInUser.getPassword());

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(loggedInUser);

            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
            when(tokenService.generateToken(loggedInUser)).thenReturn("jwt");

            JWTTokenData result = authenticationService.authentication(data, authenticationManager);

            assertTrue(result.firstAccess());
            assertEquals("jwt", result.token());
        }

        @Test
        void shouldBuildUserSummaryDataCorrectly() {
            LoginData data = new LoginData(loggedInUser.getPerson().getEmail(), loggedInUser.getPassword());

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(loggedInUser);

            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
            when(tokenService.generateToken(loggedInUser)).thenReturn("jwt");

            JWTTokenData result = authenticationService.authentication(data, authenticationManager);

            assertEquals(loggedInUser.getPerson().getPersonName(), result.user().personName());
            assertEquals(loggedInUser.getPerson().getEmail(), result.user().personEmail());
            assertEquals(UserTypeEnum.fromId(loggedInUser.getUserType().getId()).getDisplayName(),
                    result.user().nameUserType());
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldThrowBadCredentialsExceptionWhenAuthenticationFails() {
            LoginData data = new LoginData(loggedInUser.getPerson().getEmail(), loggedInUser.getPassword());

            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            assertThrows(BadCredentialsException.class,
                    () -> authenticationService.authentication(data, authenticationManager));
        }

        @Test
        void shouldThrowIfAuthenticationReturnsNullPrincipal() {
            LoginData data = new LoginData(loggedInUser.getPerson().getEmail(), loggedInUser.getPassword());

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(null);

            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

            assertThrows(NullPointerException.class,
                    () -> authenticationService.authentication(data, authenticationManager));
        }
    }

}
