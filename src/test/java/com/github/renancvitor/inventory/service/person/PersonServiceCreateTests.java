package com.github.renancvitor.inventory.service.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.person.dto.PersonCreationData;
import com.github.renancvitor.inventory.application.person.dto.PersonDetailData;
import com.github.renancvitor.inventory.application.person.repository.PersonRepository;
import com.github.renancvitor.inventory.application.person.service.PersonService;
import com.github.renancvitor.inventory.application.user.dto.UserCreationData;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.application.user.service.UserService;
import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PersonServiceCreateTests {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @Mock
    private SystemLogPublisherService logPublisherService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

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
        void shouldCreatePersonAndUserWhenAdmin() {
            userTypeEntity = TestEntityFactory.createUserTypeAdmin();
            loggedInUser = TestEntityFactory.createUser();
            loggedInUser.setUserType(userTypeEntity);
            loggedInUser.setPerson(person);

            PersonCreationData personData = new PersonCreationData(
                    "Pessoa Teste",
                    "111.111.111-11",
                    "email@email.com");

            UserCreationData userData = new UserCreationData(
                    personData.cpf(),
                    "123456",
                    userTypeEntity.getId());

            Person savedPerson = new Person(
                    personData.personName(),
                    personData.cpf(),
                    personData.email());

            doNothing().when(authenticationService).authorize(List.of(UserTypeEnum.ADMIN));
            when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

            PersonDetailData result = personService.create(
                    personData,
                    userData,
                    loggedInUser);

            assertNotNull(result);
            assertEquals("Pessoa Teste", result.personName());
            assertEquals("email@email.com", result.email());
            assertEquals("111.111.111-11", result.cpf());

            verify(authenticationService).authorize(List.of(UserTypeEnum.ADMIN));

            verify(personRepository, times(1)).save(any(Person.class));

            ArgumentCaptor<UserCreationData> userDataCaptor = ArgumentCaptor.forClass(UserCreationData.class);
            verify(userService).create(eq(savedPerson), userDataCaptor.capture(), eq(loggedInUser));

            UserCreationData capturedUserData = userDataCaptor.getValue();

            assertEquals(UserTypeEnum.COMMON.getId(), capturedUserData.idUserType());

            assertEquals("111.111.111-11", capturedUserData.personCpf());
            assertEquals("123456", capturedUserData.password());
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldNotCreatePersonWithNotAuthorizedUser() {
            userTypeEntity = TestEntityFactory.createUserTypeCommon();
            loggedInUser = TestEntityFactory.createUser();
            loggedInUser.setUserType(userTypeEntity);
            loggedInUser.setPerson(person);

            doThrow(new AuthorizationException(List.of(UserTypeEnum.COMMON.getId())))
                    .when(authenticationService)
                    .authorize(any());

            PersonCreationData personData = new PersonCreationData(
                    "Pessoa Teste",
                    "111.111.111-11",
                    "email@email.com");

            UserCreationData userData = new UserCreationData(
                    personData.cpf(),
                    "123456",
                    userTypeEntity.getId());

            assertThrows(AuthorizationException.class,
                    () -> personService.create(personData, userData, loggedInUser));

            verifyNoInteractions(userService, logPublisherService);
            verify(personRepository, never()).save(any(Person.class));
        }

        @Test
        void shouldRollbackWhenPersonPersistenceFails() {
            userTypeEntity = TestEntityFactory.createUserTypeAdmin();
            loggedInUser = TestEntityFactory.createUser();
            loggedInUser.setUserType(userTypeEntity);
            loggedInUser.setPerson(person);

            PersonCreationData personData = new PersonCreationData(
                    "Pessoa Teste",
                    "111.111.111-11",
                    "email@email.com");

            UserCreationData userData = new UserCreationData(
                    personData.cpf(),
                    "123456",
                    userTypeEntity.getId());

            doNothing().when(authenticationService).authorize(List.of(UserTypeEnum.ADMIN));

            when(personRepository.save(any(Person.class)))
                    .thenThrow(new DataIntegrityViolationException("Erro de integridade."));

            assertThrows(DataIntegrityViolationException.class,
                    () -> personService.create(personData, userData, loggedInUser));

            verify(authenticationService).authorize(List.of(UserTypeEnum.ADMIN));
            verifyNoInteractions(userService, logPublisherService);
            verify(personRepository).save(any(Person.class));
        }

        @Test
        void shouldRollbackWhenUserCreationFails() {
            userTypeEntity = TestEntityFactory.createUserTypeAdmin();
            loggedInUser = TestEntityFactory.createUser();
            loggedInUser.setUserType(userTypeEntity);
            loggedInUser.setPerson(person);

            PersonCreationData personData = new PersonCreationData(
                    "Pessoa Teste",
                    "111.111.111-11",
                    "email@email.com");

            UserCreationData userData = new UserCreationData(
                    personData.cpf(),
                    "123456",
                    userTypeEntity.getId());

            Person savedPerson = new Person(
                    personData.personName(),
                    personData.cpf(),
                    personData.email());

            doNothing().when(authenticationService).authorize(List.of(UserTypeEnum.ADMIN));

            when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

            doThrow(new RuntimeException("Erro ao criar usuário"))
                    .when(userService).create(eq(savedPerson), any(UserCreationData.class), eq(loggedInUser));

            assertThrows(RuntimeException.class,
                    () -> personService.create(personData, userData, loggedInUser));

            verify(authenticationService).authorize(List.of(UserTypeEnum.ADMIN));
            verify(personRepository).save(any(Person.class));
            verify(userService).create(eq(savedPerson), any(UserCreationData.class), eq(loggedInUser));
            verifyNoInteractions(logPublisherService);
        }
    }

}
