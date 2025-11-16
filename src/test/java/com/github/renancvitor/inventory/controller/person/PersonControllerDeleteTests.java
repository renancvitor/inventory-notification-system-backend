package com.github.renancvitor.inventory.controller.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
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

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.person.controller.PersonController;
import com.github.renancvitor.inventory.application.person.repository.PersonRepository;
import com.github.renancvitor.inventory.application.person.service.PersonService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PersonControllerDeleteTests {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private Person person;
    private User loggedInUser;
    private UserTypeEntity userTypeEntity;

    @BeforeEach
    void setup() {
        person = TestEntityFactory.createPerson();
        loggedInUser = TestEntityFactory.createUser();
        userTypeEntity = TestEntityFactory.createUserTypeAdmin();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldDeletePersonWithUserPermission() {
            userTypeEntity = TestEntityFactory.createUserTypeAdmin();
            loggedInUser.setUserType(userTypeEntity);

            ResponseEntity<Void> response = personController.delete(
                    person.getId(),
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(personService).delete(person.getId(), loggedInUser);
        }

        @Test
        void shouldReturnNoContentWhenDeleteCallTwice() {
            ResponseEntity<Void> respondeOne = personController.delete(
                    person.getId(), loggedInUser);

            assertThat(respondeOne.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            ResponseEntity<Void> responseTwo = personController.delete(
                    person.getId(), loggedInUser);

            assertThat(responseTwo.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(personService, times(2)).delete(
                    person.getId(),
                    loggedInUser);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldReturn404WhenPersonNotFound() {
            Long nonExistentId = 999L;

            doThrow(new EntityNotFoundException("Pessoa não encontrada."))
                    .when(personService).delete(eq(nonExistentId), any());

            assertThatThrownBy(() -> personController.delete(nonExistentId, loggedInUser))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Pessoa não encontrada.");
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceThrows() {
            doThrow(new RuntimeException("Erro inesperado"))
                    .when(personService).delete(eq(person.getId()), eq(loggedInUser));

            assertThatThrownBy(() -> personController.delete(person.getId(), loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro inesperado");
        }
    }

}
