package com.github.renancvitor.inventory.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.user.dto.UserListingData;
import com.github.renancvitor.inventory.application.user.repository.UserRepository;
import com.github.renancvitor.inventory.application.user.service.UserService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceListTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SystemLogPublisherService logPublisherService;

    @InjectMocks
    private UserService userService;

    private User loggedInUser;
    private UserTypeEntity userTypeEntity;

    @BeforeEach
    void setup() {
        loggedInUser = TestEntityFactory.createUser();
        userTypeEntity = TestEntityFactory.createUserTypeCommon();

        loggedInUser.setUserType(userTypeEntity);
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldListAllUsersWhenActiveIsNull() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> page = new PageImpl<>(List.of(loggedInUser));

            when(userRepository.findAll(pageable))
                    .thenReturn(page);

            Page<UserListingData> result = userService.list(pageable, loggedInUser, null);

            assertEquals(1, result.getTotalElements());
            verify(userRepository).findAll(pageable);
            verify(authenticationService).authorize(List.of(UserTypeEnum.ADMIN));
        }

        @Test
        void shouldFilterUsersByActiveTrue() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> page = new PageImpl<>(List.of(loggedInUser));

            when(userRepository.findAllByActive(true, pageable))
                    .thenReturn(page);

            Page<UserListingData> result = userService.list(pageable, loggedInUser, true);

            assertEquals(1, result.getTotalElements());
            verify(userRepository).findAllByActive(true, pageable);
            verify(authenticationService).authorize(List.of(UserTypeEnum.ADMIN));
        }

        @Test
        void shouldFilterUsersByActiveFalse() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> page = new PageImpl<>(List.of(loggedInUser));

            when(userRepository.findAllByActive(false, pageable))
                    .thenReturn(page);

            Page<UserListingData> result = userService.list(pageable, loggedInUser, false);

            assertEquals(1, result.getTotalElements());
            verify(userRepository).findAllByActive(false, pageable);
            verify(authenticationService).authorize(List.of(UserTypeEnum.ADMIN));
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldIgnoreActiveFilterWhenNull() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> page = new PageImpl<>(List.of(loggedInUser));

            when(userRepository.findAll(pageable))
                    .thenReturn(page);

            Page<UserListingData> result = userService.list(pageable, loggedInUser, null);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(userRepository).findAll(pageable);
        }

        @Test
        void shouldFailWhenUserIsNotAuthorized() {
            Pageable pageable = PageRequest.of(0, 10);

            doThrow(new AuthorizationException(List.of(UserTypeEnum.ADMIN.getId())))
                    .when(authenticationService)
                    .authorize(anyList());

            assertThrows(
                    AuthorizationException.class,
                    () -> userService.list(pageable, loggedInUser, null));
        }
    }

}
