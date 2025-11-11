package com.github.renancvitor.inventory.service.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.person.repository.PersonRepository;
import com.github.renancvitor.inventory.application.person.service.PersonService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PersonServiceDeleteTests {

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
        loggedInUser = TestEntityFactory.createUser();
        userTypeEntity = TestEntityFactory.createUserTypeAdmin();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldDeletePersonWithUserPermission() {
            loggedInUser.setUserType(userTypeEntity);

            when(personRepository.findByIdAndActiveTrue(person.getId()))
                    .thenReturn(Optional.of(person));

            when(personRepository.save(any(Person.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            personService.delete(person.getId(), loggedInUser);

            assertFalse(person.getActive());
            verify(personRepository).save(person);
            verify(authenticationService)
                    .authorize(List.of(UserTypeEnum.ADMIN));
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldNotDeletePersonWithNotAuthorizedUser() {
            userTypeEntity = TestEntityFactory.createUserTypeProductManager();
            loggedInUser.setUserType(userTypeEntity);

            doThrow(new AuthorizationException(List.of(UserTypeEnum.PRODUCT_MANAGER.getId())))
                    .when(authenticationService)
                    .authorize(any());

            assertThrows(AuthorizationException.class,
                    () -> personService.delete(person.getId(), loggedInUser));

            verify(personRepository, never()).save(any(Person.class));
        }

        @Test
        void shouldNotDeletePersonNotFound() {
            when(personRepository.findByIdAndActiveTrue(person.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> personService.delete(person.getId(), loggedInUser));

            verify(personRepository, never()).save(any(Person.class));
        }
    }

}
