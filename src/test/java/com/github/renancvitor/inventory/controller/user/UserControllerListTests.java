package com.github.renancvitor.inventory.controller.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.user.controller.UserController;
import com.github.renancvitor.inventory.application.user.dto.UserListingData;
import com.github.renancvitor.inventory.application.user.service.UserService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.util.CustomPage;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerListTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User loggedInUser;
    private User userEntity;
    private UserTypeEntity userTypeEntity;

    @BeforeEach
    void setup() {
        loggedInUser = TestEntityFactory.createUser();
        userEntity = TestEntityFactory.createUser();
        userTypeEntity = TestEntityFactory.createUserTypeCommon();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldListUsersWithActiveFilter() {
            Pageable pageable = PageRequest.of(0, 10);

            userEntity.setId(7L);
            userEntity.setUserType(userTypeEntity);

            UserListingData listingData = new UserListingData(userEntity);
            Page<UserListingData> page = new PageImpl<>(List.of(listingData), pageable, 1);

            when(userService.list(eq(pageable), eq(loggedInUser), eq(true)))
                    .thenReturn(page);

            ResponseEntity<CustomPage<UserListingData>> response = userController.list(
                    true,
                    pageable,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().content()).hasSize(1);

            verify(userService).list(pageable, loggedInUser, true);
        }

        @Test
        void shouldListUsersWithoutActiveFilter() {
            Pageable pageable = PageRequest.of(0, 10);

            userEntity.setId(7L);
            userEntity.setUserType(userTypeEntity);

            UserListingData listingData = new UserListingData(userEntity);
            Page<UserListingData> page = new PageImpl<>(List.of(listingData), pageable, 1);

            when(userService.list(eq(pageable), eq(loggedInUser), isNull()))
                    .thenReturn(page);

            ResponseEntity<CustomPage<UserListingData>> response = userController.list(
                    null,
                    pageable,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().content()).hasSize(1);

            verify(userService).list(pageable, loggedInUser, null);
        }

        @Test
        void shouldApplyCustomPaginationAndSorting() {
            Pageable customPageable = PageRequest.of(
                    2,
                    5,
                    Sort.by("person.personName").descending());

            Page<UserListingData> page = new PageImpl<>(List.of(), customPageable, 0);

            when(userService.list(eq(customPageable), eq(loggedInUser), isNull()))
                    .thenReturn(page);

            ResponseEntity<CustomPage<UserListingData>> response = userController.list(
                    null,
                    customPageable,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().page()).isEqualTo(2);
            assertThat(response.getBody().size()).isEqualTo(5);

            verify(userService).list(customPageable, loggedInUser, null);
        }

        @Test
        void shouldReturnEmptyPageWhenNoUsersFound() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<UserListingData> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(userService.list(any(), any(), any()))
                    .thenReturn(emptyPage);

            ResponseEntity<CustomPage<UserListingData>> response = userController.list(
                    null,
                    pageable,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().content()).isEmpty();

            verify(userService).list(pageable, loggedInUser, null);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldPropagateExceptionWhenServiceFails() {
            Pageable pageable = PageRequest.of(0, 10);

            RuntimeException exception = new RuntimeException("Erro ao listar usuários");

            when(userService.list(any(), any(), any()))
                    .thenThrow(exception);

            assertThatThrownBy(() -> userController.list(true, pageable, loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro ao listar usuários");

            verify(userService).list(any(), eq(loggedInUser), eq(true));
        }
    }

}
