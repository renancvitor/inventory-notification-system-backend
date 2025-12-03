package com.github.renancvitor.inventory.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.github.renancvitor.inventory.application.user.dto.UserDetailData;
import com.github.renancvitor.inventory.application.user.dto.UserTypeUpdateData;
import com.github.renancvitor.inventory.application.user.repository.UserRepository;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.application.user.service.UserService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;
import com.github.renancvitor.inventory.exception.types.common.ValidationException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceUpdateUserTypeTests {

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
        private UserTypeEntity oldUserType;
        private UserTypeEntity newUserType;

        @BeforeEach
        void setup() {
                loggedInUser = TestEntityFactory.createUser();
                userTypeEntity = TestEntityFactory.createUserTypeAdmin();

                oldUserType = TestEntityFactory.createUserTypeCommon();
                newUserType = TestEntityFactory.createUserTypeProductManager();

                user = TestEntityFactory.createUser();
                user.setUserType(oldUserType);

                loggedInUser.setUserType(userTypeEntity);
        }

        @Nested
        class PositiveCases {
                @Test
                void shouldUpdateUserTypeSuccessfully() {
                        UserTypeUpdateData data = new UserTypeUpdateData(newUserType.getId());

                        when(userRepository.findByIdAndActiveTrue(user.getId()))
                                        .thenReturn(Optional.of(user));

                        when(userTypeRepository.findById(newUserType.getId()))
                                        .thenReturn(Optional.of(newUserType));

                        when(userRepository.save(user))
                                        .thenReturn(user);

                        doNothing().when(authenticationService)
                                        .authorize(anyList());

                        UserDetailData result = userService.updateUserType(user.getId(), data, loggedInUser);

                        assertNotNull(result);
                        assertEquals(user.getId(), result.id());
                        assertEquals(newUserType.getUserTypeName(),
                                        user.getUserType().getUserTypeName());

                        verify(authenticationService).authorize(anyList());
                        verify(userRepository).save(user);
                        verify(logPublisherService).publish(
                                        eq("UPDATED_USER_TYPE"),
                                        contains(loggedInUser.getUsername()),
                                        any(),
                                        any());
                }
        }

        @Nested
        class NegativeCases {
                @Test
                void shouldThrowNotFoundWhenUserDoesNotExist() {
                        when(userRepository.findByIdAndActiveTrue(999L))
                                        .thenReturn(Optional.empty());

                        UserTypeUpdateData data = new UserTypeUpdateData(UserTypeEnum.ADMIN.getId());

                        assertThrows(EntityNotFoundException.class,
                                        () -> userService.updateUserType(999L, data, loggedInUser));
                }

                @Test
                void shouldThrowValidationWhenUserTypeIdIsNullInsideUserEntity() {
                        UserTypeEntity invalidType = new UserTypeEntity();
                        invalidType.setId(null);
                        invalidType.setUserTypeName("COMMON");
                        user.setUserType(invalidType);

                        when(userRepository.findByIdAndActiveTrue(user.getId()))
                                        .thenReturn(Optional.of(user));

                        UserTypeUpdateData data = new UserTypeUpdateData(UserTypeEnum.ADMIN.getId());

                        assertThrows(ValidationException.class,
                                        () -> userService.updateUserType(user.getId(), data, loggedInUser));
                }

                @Test
                void shouldThrowNotFoundWhenTargetUserTypeDoesNotExist() {
                        UserTypeUpdateData data = new UserTypeUpdateData(999);

                        when(userRepository.findByIdAndActiveTrue(user.getId()))
                                        .thenReturn(Optional.of(user));

                        when(userTypeRepository.findById(999))
                                        .thenReturn(Optional.empty());

                        assertThrows(EntityNotFoundException.class,
                                        () -> userService.updateUserType(user.getId(), data, loggedInUser));
                }

                @Test
                void shouldThrowAuthorizationExceptionWhenUserIsNotAdmin() {
                        loggedInUser.setUserType(TestEntityFactory.createUserTypeCommon());

                        doThrow(new AuthorizationException(List.of(UserTypeEnum.ADMIN.getId())))
                                        .when(authenticationService).authorize(anyList());

                        UserTypeUpdateData data = new UserTypeUpdateData(UserTypeEnum.ADMIN.getId());

                        assertThrows(AuthorizationException.class,
                                        () -> userService.updateUserType(user.getId(), data, loggedInUser));
                }
        }

}
