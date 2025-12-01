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
import com.github.renancvitor.inventory.application.user.dto.UserPasswordUpdateData;
import com.github.renancvitor.inventory.application.user.service.UserService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerUpdatePasswordTests {

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
        void shouldUpdatePasswordSuccessfully() {
            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "senhaAtual123",
                    "novaSenha456",
                    "novaSenha456");

            UserDetailData detailData = new UserDetailData(loggedInUser);

            when(userService.updatePassword(eq(loggedInUser.getId()), eq(data), eq(loggedInUser)))
                    .thenReturn(detailData);

            ResponseEntity<UserDetailData> response = userController.updatePassword(loggedInUser.getId(), data,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(detailData);

            verify(userService).updatePassword(loggedInUser.getId(), data, loggedInUser);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldFailValidationWhenCurrentPasswordIsBlank() {
            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "",
                    "novaSenha123",
                    "novaSenha123");

            Set<ConstraintViolation<UserPasswordUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("currentPassword"));
        }

        @Test
        void shouldFailValidationWhenNewPasswordIsBlank() {
            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "senhaAtual",
                    "",
                    "confirm");

            Set<ConstraintViolation<UserPasswordUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("newPassword"));
        }

        @Test
        void shouldFailValidationWhenConfirmNewPasswordIsBlank() {
            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "senhaAtual",
                    "novaSenha",
                    "");

            Set<ConstraintViolation<UserPasswordUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("confirmNewPassword"));
        }

        @Test
        void shouldPropagateExceptionWhenServiceFails() {
            UserPasswordUpdateData data = new UserPasswordUpdateData(
                    "senhaAtual",
                    "novaSenha",
                    "novaSenha");

            RuntimeException exception = new RuntimeException("Erro ao atualizar senha");

            when(userService.updatePassword(any(), any(), any()))
                    .thenThrow(exception);

            assertThatThrownBy(() -> userController.updatePassword(loggedInUser.getId(), data, loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro ao atualizar senha");

            verify(userService).updatePassword(any(), eq(data), eq(loggedInUser));
        }
    }

}
