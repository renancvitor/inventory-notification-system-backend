package com.github.renancvitor.inventory.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.user.dto.UserDetailData;
import com.github.renancvitor.inventory.application.user.dto.UserPasswordUpdateData;
import com.github.renancvitor.inventory.application.user.repository.UserRepository;
import com.github.renancvitor.inventory.application.user.service.UserService;
import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;
import com.github.renancvitor.inventory.exception.types.common.ValidationException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceUpdatePasswordTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SystemLogPublisherService logPublisherService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    @InjectMocks
    private UserService userService;

    private User loggedInUser;
    private UserTypeEntity userTypeEntity;
    private Person person;

    @BeforeEach
    void setup() {
        loggedInUser = TestEntityFactory.createUser();
        userTypeEntity = TestEntityFactory.createUserTypeCommon();
        person = TestEntityFactory.createPerson();

        person.setId(97L);

        loggedInUser.setId(1L);
        loggedInUser.setUserType(userTypeEntity);
        loggedInUser.setPassword("$2a$10$encryptedPasswordHash");
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldUpdatePasswordSuccessfully() {
            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "currentPswl123",
                    "NewPsw#2024",
                    "NewPsw#2024");

            when(userRepository.findByIdAndActiveTrue(loggedInUser.getId()))
                    .thenReturn(Optional.of(loggedInUser));

            when(passwordEncoder.matches("currentPswl123", loggedInUser.getPassword()))
                    .thenReturn(true);

            when(userService.strongPassword("NewPsw#2024"))
                    .thenReturn(true);

            when(passwordEncoder.encode("NewPsw#2024"))
                    .thenReturn("encrypted_new_password");

            when(userRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UserDetailData result = userService.updatePassword(loggedInUser.getId(), data, loggedInUser);

            assertNotNull(result);
            assertEquals(loggedInUser.getId(), result.id());

            assertEquals("encrypted_new_password", loggedInUser.getPassword());

            assertFalse(loggedInUser.getFirstAccess());

            verify(userRepository).save(loggedInUser);
            verify(logPublisherService).publish(
                    eq("USER_UPDATED_PASSWORD"),
                    contains("usuário: " + loggedInUser.getUsername()),
                    any(),
                    any());
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldThrowNotFoundWhenUserDoesNotExist() {
            when(userRepository.findByIdAndActiveTrue(loggedInUser.getId()))
                    .thenReturn(Optional.empty());

            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "currentPswl123",
                    "NewPsw#2024",
                    "NewPsw#2024");

            assertThrows(EntityNotFoundException.class,
                    () -> userService.updatePassword(loggedInUser.getId(), data, loggedInUser));
        }

        @Test
        void shouldThrowValidationWhenCurrentPasswordDoesNotMatch() {
            when(userRepository.findByIdAndActiveTrue(loggedInUser.getId()))
                    .thenReturn(Optional.of(loggedInUser));

            when(passwordEncoder.matches("wrondPsw", loggedInUser.getPassword()))
                    .thenReturn(false);

            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "wrondPsw",
                    "NewPsw#2024",
                    "NewPsw#2024");

            assertThrows(ValidationException.class,
                    () -> userService.updatePassword(loggedInUser.getId(), data, loggedInUser));
        }

        @Test
        void shouldThrowValidationWhenNewPasswordAndConfirmationDoNotMatch() {
            when(userRepository.findByIdAndActiveTrue(loggedInUser.getId()))
                    .thenReturn(Optional.of(loggedInUser));

            when(passwordEncoder.matches("currentPswl123", loggedInUser.getPassword()))
                    .thenReturn(true);

            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "currentPswl123",
                    "NewPsw#2024",
                    "different");

            assertThrows(ValidationException.class,
                    () -> userService.updatePassword(loggedInUser.getId(), data, loggedInUser));
        }

        @Test
        void shouldThrowValidationWhenNewPasswordIsWeak() {
            when(userRepository.findByIdAndActiveTrue(loggedInUser.getId()))
                    .thenReturn(Optional.of(loggedInUser));

            when(passwordEncoder.matches("currentPswl123", loggedInUser.getPassword()))
                    .thenReturn(true);

            doReturn(false).when(userService).strongPassword("weak");

            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "currentPswl123",
                    "weak",
                    "weak");

            assertThrows(ValidationException.class,
                    () -> userService.updatePassword(loggedInUser.getId(), data, loggedInUser));
        }
    }

}
