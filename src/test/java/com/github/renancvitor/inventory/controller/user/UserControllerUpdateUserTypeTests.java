package com.github.renancvitor.inventory.controller.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.user.controller.UserController;
import com.github.renancvitor.inventory.application.user.dto.UserDetailData;
import com.github.renancvitor.inventory.application.user.dto.UserTypeUpdateData;
import com.github.renancvitor.inventory.application.user.service.UserService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerUpdateUserTypeTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User loggedInUser;
    private UserTypeEntity userTypeEntity;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setup() {
        loggedInUser = TestEntityFactory.createUser();
        userTypeEntity = TestEntityFactory.createUserTypeCommon();
        loggedInUser.setUserType(userTypeEntity);
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldUpdateUserTypeSuccessfully() {
            UserTypeUpdateData data = new UserTypeUpdateData(2);

            UserDetailData detailData = new UserDetailData(loggedInUser);

            when(userService.updateUserType(eq(loggedInUser.getId()), eq(data), eq(loggedInUser)))
                    .thenReturn(detailData);

            ResponseEntity<UserDetailData> response = userController.updateUserType(loggedInUser.getId(), data,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(detailData);

            verify(userService).updateUserType(loggedInUser.getId(), data, loggedInUser);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldPropagateExceptionWhenServiceFails() {
            UserTypeUpdateData data = new UserTypeUpdateData(2);

            RuntimeException exception = new RuntimeException("Erro ao atualizar tipo de usuário");

            when(userService.updateUserType(any(), any(), any()))
                    .thenThrow(exception);

            assertThatThrownBy(() -> userController.updateUserType(loggedInUser.getId(), data, loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro ao atualizar tipo de usuário");

            verify(userService).updateUserType(any(), eq(data), eq(loggedInUser));
        }

        @Test
        void shouldFailValidationWhenIdUserTypeIsNull() {
            UserTypeUpdateData data = new UserTypeUpdateData(null);

            Set<ConstraintViolation<UserTypeUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("idUserType"));
        }
    }

}
