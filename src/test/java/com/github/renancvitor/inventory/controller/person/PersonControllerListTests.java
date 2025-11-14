package com.github.renancvitor.inventory.controller.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.person.controller.PersonController;
import com.github.renancvitor.inventory.application.person.dto.PersonListingData;
import com.github.renancvitor.inventory.application.person.repository.PersonRepository;
import com.github.renancvitor.inventory.application.person.service.PersonService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.util.CustomPage;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PersonControllerListTests {

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
                void shouldListWithoutFilters() {
                        PersonListingData data = new PersonListingData(person);

                        Page<PersonListingData> page = new PageImpl<>(List.of(data), PageRequest.of(
                                        0, 10), 1);

                        lenient().when(personService.list(
                                        any(Pageable.class),
                                        any(User.class),
                                        isNull()))
                                        .thenReturn(page);

                        ResponseEntity<CustomPage<PersonListingData>> response = personController.list(
                                        null,
                                        PageRequest.of(0, 10),
                                        loggedInUser);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody().getContent()).hasSize(1);
                        assertThat(response.getBody().getContent().get(0).personName())
                                        .isEqualTo(person.getPersonName());
                        verify(personService).list(
                                        any(Pageable.class),
                                        eq(loggedInUser),
                                        isNull());
                        verifyNoMoreInteractions(personService);
                }

                @Test
                void shouldListWithActiveTrue() {
                        userTypeEntity = TestEntityFactory.createUserTypeAdmin();
                        PersonListingData data = new PersonListingData(person);

                        Page<PersonListingData> page = new PageImpl<>(List.of(data), PageRequest.of(
                                        0, 10), 1);

                        lenient().when(personService.list(
                                        any(Pageable.class),
                                        any(User.class),
                                        eq(true)))
                                        .thenReturn(page);

                        ResponseEntity<CustomPage<PersonListingData>> response = personController.list(
                                        true,
                                        PageRequest.of(0, 10),
                                        loggedInUser);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody().getContent()).hasSize(1);
                        assertThat(response.getBody().getContent().get(0).personName())
                                        .isEqualTo(person.getPersonName());
                        verify(personService).list(
                                        any(Pageable.class),
                                        eq(loggedInUser),
                                        eq(true));
                        verifyNoMoreInteractions(personService);
                }

                @Test
                void shouldListWithActiveFalse() {
                        userTypeEntity = TestEntityFactory.createUserTypeAdmin();
                        PersonListingData data = new PersonListingData(person);

                        Page<PersonListingData> page = new PageImpl<>(List.of(data), PageRequest.of(
                                        0, 10), 1);

                        lenient().when(personService.list(
                                        any(Pageable.class),
                                        any(User.class),
                                        eq(false)))
                                        .thenReturn(page);

                        ResponseEntity<CustomPage<PersonListingData>> response = personController.list(
                                        false,
                                        PageRequest.of(0, 10),
                                        loggedInUser);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody().getContent()).hasSize(1);
                        assertThat(response.getBody().getContent().get(0).personName())
                                        .isEqualTo(person.getPersonName());
                        verify(personService).list(
                                        any(Pageable.class),
                                        eq(loggedInUser),
                                        eq(false));
                        verifyNoMoreInteractions(personService);
                }

                @Test
                void shouldApplyCustomPaginationAndSorting() {
                        Pageable customPageable = PageRequest.of(
                                        2,
                                        5,
                                        Sort.by("active")
                                                        .descending());

                        Page<PersonListingData> page = new PageImpl<>(List.of(),
                                        customPageable,
                                        0);

                        when(personService.list(
                                        eq(customPageable),
                                        any(),
                                        any()))
                                        .thenReturn(page);

                        ResponseEntity<CustomPage<PersonListingData>> response = personController.list(
                                        null,
                                        customPageable,
                                        loggedInUser);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody().page()).isEqualTo(2);
                        assertThat(response.getBody().size()).isEqualTo(5);
                        assertThat(response.getBody().totalElements()).isEqualTo(0L);

                        verify(personService).list(
                                        eq(customPageable),
                                        any(),
                                        any());
                }
        }

        @Nested
        class NegativeCases {
                @Test
                void shouldThrowExceptionWhenLoggedInUserIsNull() {
                        assertThatThrownBy(() -> personController.list(
                                        null,
                                        PageRequest.of(
                                                        0,
                                                        10),
                                        null))
                                        .isInstanceOf(NullPointerException.class);
                }

                @Test
                void shouldReturnInternalServerErrorWhenServiceThrows() {
                        when(personService.list(
                                        any(Pageable.class),
                                        any(User.class),
                                        any()))
                                        .thenThrow(new RuntimeException("Service error"));

                        assertThatThrownBy(() -> personController.list(
                                        null, PageRequest.of(
                                                        0,
                                                        10),
                                        loggedInUser))
                                        .isInstanceOf(RuntimeException.class)
                                        .hasMessageContaining("Service error");

                        verify(personService).list(
                                        any(Pageable.class),
                                        eq(loggedInUser),
                                        isNull());
                }

                @Test
                void shouldReturnEmptyPageWhenNoPersonsFound() {
                        when(personService.list(
                                        any(Pageable.class),
                                        any(User.class),
                                        any()))
                                        .thenReturn(Page.empty());

                        ResponseEntity<CustomPage<PersonListingData>> response = personController.list(
                                        null,
                                        PageRequest.of(0, 10),
                                        loggedInUser);

                        assertThat(response.getBody().getContent()).isEmpty();
                }
        }

}
