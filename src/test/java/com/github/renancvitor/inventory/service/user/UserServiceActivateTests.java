package com.github.renancvitor.inventory.service.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.user.repository.UserRepository;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.application.user.service.UserService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceActivateTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SystemLogPublisherService logPublisherService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User loggedInUser;
    private User user;
    private UserTypeEntity userTypeEntity;

    @BeforeEach
    void setup() {
        loggedInUser = TestEntityFactory.createUser();
        userTypeEntity = TestEntityFactory.createUserTypeAdmin();
        loggedInUser.setUserType(userTypeEntity);

        user = TestEntityFactory.createUser();
        user.setUserType(userTypeEntity);
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldActivateUserSuccessfully() {
            doNothing().when(authenticationService).authorize(anyList());

            when(userRepository.findByIdAndActiveFalse(user.getId()))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            userService.activate(user.getId(), loggedInUser);

            assertTrue(user.getActive());

            verify(authenticationService).authorize(anyList());
            verify(userRepository).save(user);
            verify(logPublisherService).publish(
                    eq("USER_ACTIVATED"),
                    contains(loggedInUser.getUsername()),
                    any(),
                    any());
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldThrowNotFoundWhenInactiveUserDoesNotExist() {
            when(userRepository.findByIdAndActiveFalse(999L))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> userService.activate(999L, loggedInUser));
        }

        @Test
        void shouldThrowAuthorizationExceptionWhenUserIsNotAdmin() {
            loggedInUser.setUserType(TestEntityFactory.createUserTypeCommon());

            doThrow(new AuthorizationException(List.of(UserTypeEnum.ADMIN.getId())))
                    .when(authenticationService).authorize(anyList());

            assertThrows(AuthorizationException.class,
                    () -> userService.activate(user.getId(), loggedInUser));
        }
    }

}
