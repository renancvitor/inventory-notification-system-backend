package com.github.renancvitor.inventory.controller.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.LocalDateTime;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.person.controller.PersonController;
import com.github.renancvitor.inventory.application.person.dto.PersonCreationData;
import com.github.renancvitor.inventory.application.person.dto.PersonDetailData;
import com.github.renancvitor.inventory.application.person.dto.PersonUserCreationData;
import com.github.renancvitor.inventory.application.person.repository.PersonRepository;
import com.github.renancvitor.inventory.application.person.service.PersonService;
import com.github.renancvitor.inventory.application.user.dto.UserCreationData;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PersonControllerCreateTests {

        private Validator validator;

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

        private User loggedInUser;
        private UserTypeEntity userTypeEntity;

        @BeforeEach
        void setup() {
                loggedInUser = TestEntityFactory.createUser();
                userTypeEntity = TestEntityFactory.createUserTypeAdmin();

                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                validator = factory.getValidator();
        }

        @Nested
        class PositiveCases {
                @Test
                void shouldCreatePersonWithValidData() {
                        PersonCreationData personData = new PersonCreationData(
                                        "Fulano de tal",
                                        "111.111.111-11",
                                        "email@email.com");

                        UserCreationData userData = new UserCreationData(
                                        personData.cpf(),
                                        "123456",
                                        UserTypeEnum.ADMIN.getId());

                        PersonUserCreationData data = new PersonUserCreationData(personData, userData);

                        PersonDetailData detail = new PersonDetailData(
                                        1L,
                                        personData.personName(),
                                        personData.cpf(),
                                        personData.email(),
                                        LocalDateTime.now());

                        userTypeEntity = TestEntityFactory.createUserTypeAdmin();
                        loggedInUser.setUserType(userTypeEntity);

                        when(personService.create(eq(personData), eq(userData), eq(loggedInUser)))
                                        .thenReturn(detail);

                        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                                        .fromUriString("http://localhost");

                        ResponseEntity<PersonDetailData> response = personController.create(
                                        data,
                                        uriComponentsBuilder,
                                        loggedInUser);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                        assertThat(response.getHeaders().getLocation())
                                        .isEqualTo(URI.create("http://localhost/person/" + 1L));
                        assertThat(response.getBody()).isNotNull();
                        assertThat(response.getBody().id()).isEqualTo(1L);
                        assertThat(response.getBody().personName()).isEqualTo("Fulano de tal");

                        verify(personService).create(eq(personData), eq(userData), eq(loggedInUser));
                }

                @Test
                void shouldCreatePersonWithBodyMapping() {
                        PersonCreationData personData = new PersonCreationData(
                                        "Fulano de tal",
                                        "111.111.111-11",
                                        "email@email.com");

                        UserCreationData userData = new UserCreationData(
                                        personData.cpf(),
                                        "123456",
                                        UserTypeEnum.ADMIN.getId());

                        PersonUserCreationData data = new PersonUserCreationData(personData, userData);

                        PersonDetailData detail = new PersonDetailData(
                                        1L,
                                        personData.personName(),
                                        personData.cpf(),
                                        personData.email(),
                                        LocalDateTime.now());

                        userTypeEntity = TestEntityFactory.createUserTypeAdmin();
                        loggedInUser.setUserType(userTypeEntity);

                        when(personService.create(eq(personData), eq(userData), eq(loggedInUser)))
                                        .thenReturn(detail);

                        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                                        .fromUriString("http://localhost");

                        ResponseEntity<PersonDetailData> response = personController.create(
                                        data,
                                        uriComponentsBuilder,
                                        loggedInUser);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(detail);
                }
        }

        @Nested
        class NegativeCases {
                @Test
                void shouldReturnBadRequestWhenPersonNameIsNUll() {
                        PersonCreationData personData = new PersonCreationData(
                                        null,
                                        "111.111.111-11",
                                        "email@email.com");

                        Set<ConstraintViolation<PersonCreationData>> violations = validator.validate(personData);

                        assertThat(violations).isNotEmpty();
                        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("personName"));
                }

                @Test
                void shouldReturnBadRequestWhenCpfIsNull() {
                        PersonCreationData personData = new PersonCreationData(
                                        "Fulano de tal",
                                        null,
                                        "email@email.com");

                        Set<ConstraintViolation<PersonCreationData>> violations = validator.validate(personData);

                        assertThat(violations).isNotEmpty();
                        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("cpf"));
                }

                @Test
                void shouldReturnBadRequestWhenEmailIsNull() {
                        PersonCreationData personData = new PersonCreationData(
                                        "Fulano de tal",
                                        "111.111.111-11",
                                        null);

                        Set<ConstraintViolation<PersonCreationData>> violations = validator.validate(personData);

                        assertThat(violations).isNotEmpty();
                        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
                }

                @Test
                void shouldReturnInternalServerErrorWHenServiceThrows() {
                        PersonCreationData personData = new PersonCreationData(
                                        "Fulano de tal",
                                        "111.111.111-11",
                                        "email@email.com");

                        UserCreationData userData = new UserCreationData(
                                        personData.cpf(),
                                        "123456",
                                        UserTypeEnum.ADMIN.getId());

                        PersonUserCreationData data = new PersonUserCreationData(personData, userData);

                        when(personService.create(
                                        eq(personData),
                                        eq(userData),
                                        eq(loggedInUser)))
                                        .thenThrow(new RuntimeException("Erro inesperado"));

                        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                                        .fromUriString("http://localhost");

                        assertThatThrownBy(() -> personController.create(data, uriComponentsBuilder, loggedInUser))
                                        .isInstanceOf(RuntimeException.class)
                                        .hasMessageContaining("Erro inesperado");
                }
        }

}
