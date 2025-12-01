package com.github.renancvitor.inventory.controller.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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
import com.github.renancvitor.inventory.application.user.service.UserService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerDeleteTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

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
        void shouldDeleteUserSuccessfully() {
            doNothing().when(userService).delete(loggedInUser.getId(), loggedInUser);

            ResponseEntity<Void> response = userController.delete(loggedInUser.getId(), loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();

            verify(userService).delete(loggedInUser.getId(), loggedInUser);
        }
    }

    @Nested
    class NegativeCases {

        @Test
        void shouldPropagateExceptionWhenServiceFails() {
            RuntimeException exception = new RuntimeException("Erro ao deletar usuário");

            doThrow(exception)
                    .when(userService)
                    .delete(any(), any());

            assertThatThrownBy(() -> userController.delete(loggedInUser.getId(), loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro ao deletar usuário");

            verify(userService).delete(any(), eq(loggedInUser));
        }
    }

}
