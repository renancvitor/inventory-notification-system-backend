package com.github.renancvitor.inventory.service.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.person.dto.PersonListingData;
import com.github.renancvitor.inventory.application.person.repository.PersonRepository;
import com.github.renancvitor.inventory.application.person.service.PersonService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PersonServiceListTests {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @Mock
    private SystemLogPublisherService logPublisherService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private PersonService personService;

    private Person person;
    private User loggedInUser;
    private UserTypeEntity userTypeEntity;

    @BeforeEach
    void setup() {
        person = TestEntityFactory.createPerson();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldListAllPeople() {
            Page<Person> page = new PageImpl<>(List.of(person));
            when(personRepository.findAll(PageRequest.of(0, 10)))
                    .thenReturn(page);

            Page<PersonListingData> result = personService.list(
                    PageRequest.of(0, 10),
                    loggedInUser, null);

            assertEquals(1, result.getTotalElements());

            verify(personRepository).findAll(PageRequest.of(
                    0, 10));
        }

        @Test
        void shouldFilterByActiveTrue() {
            Page<Person> page = new PageImpl<>(List.of(person));
            when(personRepository.findAllByActive(
                    true, PageRequest.of(0, 10)))
                    .thenReturn(page);

            Page<PersonListingData> result = personService.list(
                    PageRequest.of(0, 10),
                    loggedInUser, true);

            assertEquals(1, result.getTotalElements());

            verify(personRepository).findAllByActive(
                    true, PageRequest.of(0, 10));
        }

        @Test
        void shouldFilterByActiveFalse() {
            Page<Person> page = new PageImpl<>(List.of(person));
            when(personRepository.findAllByActive(
                    false, PageRequest.of(0, 10)))
                    .thenReturn(page);

            Page<PersonListingData> result = personService.list(
                    PageRequest.of(0, 10),
                    loggedInUser, false);

            assertEquals(1, result.getTotalElements());

            verify(personRepository).findAllByActive(
                    false, PageRequest.of(0, 10));
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldIgnoreFiltersWhenAllAreNull() {
            Page<Person> page = new PageImpl<>(List.of(person));
            when(personRepository.findAll(PageRequest.of(0, 10)))
                    .thenReturn(page);

            Page<PersonListingData> result = personService.list(
                    PageRequest.of(0, 10),
                    null, null);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            verify(personRepository).findAll(
                    eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldFailWhenUserIsNotAuthorized() {
            loggedInUser = TestEntityFactory.createUser();
            userTypeEntity = TestEntityFactory.createUserTypeCommon();
            loggedInUser.setUserType(userTypeEntity);

            doThrow(new AuthorizationException(List.of(UserTypeEnum.ADMIN.getId())))
                    .when(authenticationService)
                    .authorize(anyList());

            assertThrows(AuthorizationException.class,
                    () -> personService.list(PageRequest.of(0, 10), loggedInUser, null));
        }
    }

}
