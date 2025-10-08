package com.github.renancvitor.inventory.service.person;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.enums.user.UserTypeEnum;
import com.github.renancvitor.inventory.dto.person.PersonCreationData;
import com.github.renancvitor.inventory.dto.person.PersonDetailData;
import com.github.renancvitor.inventory.dto.person.PersonListingData;
import com.github.renancvitor.inventory.dto.person.PersonLogData;
import com.github.renancvitor.inventory.dto.user.UserCreationData;
import com.github.renancvitor.inventory.exception.factory.NotFoundExceptionFactory;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.repository.PersonRepository;
import com.github.renancvitor.inventory.service.auth.AuthenticationService;
import com.github.renancvitor.inventory.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonService {

        private final PersonRepository personRepository;
        private final UserService userService;
        private final AuthenticationService authenticationService;
        private final SystemLogPublisherService logPublisherService;

        public Page<PersonListingData> list(Pageable pageable, User loggedInUser, Boolean active) {
                authenticationService.authorize(List.of(UserTypeEnum.ADMIN));

                if (active != null) {
                        return personRepository.findAllByActive(active, pageable).map(PersonListingData::new);
                }

                return personRepository.findAll(pageable).map(PersonListingData::new);
        }

        @Transactional
        public PersonDetailData create(PersonCreationData personCreationData, UserCreationData userCreationData,
                        User loggedInUser) {
                authenticationService.authorize(List.of(UserTypeEnum.ADMIN));

                Person person = new Person(
                                personCreationData.personName(),
                                personCreationData.cpf(),
                                personCreationData.email());
                personRepository.save(person);

                UserCreationData userData = new UserCreationData(
                                person.getCpf(),
                                userCreationData.password(),
                                UserTypeEnum.COMMON.getId());
                userService.create(person, userData, loggedInUser);

                PersonLogData newData = PersonLogData.fromEntity(person);

                logPublisherService.publish(
                                "PERSON_CREATED",
                                "Pessoa cadastrada pelo usuário " + loggedInUser.getUsername(),
                                null,
                                newData);

                return new PersonDetailData(person);
        }

        @Transactional
        public void delete(Long id, User loggedInUser) {
                authenticationService.authorize(List.of(UserTypeEnum.ADMIN));

                Person person = personRepository.findByIdAndActiveTrue(id)
                                .orElseThrow(() -> NotFoundExceptionFactory.person(id));

                PersonLogData oldData = PersonLogData.fromEntity(person);

                person.setActive(false);

                Person updatedPerson = personRepository.save(person);
                PersonLogData newData = PersonLogData.fromEntity(updatedPerson);

                logPublisherService.publish(
                                "PERSON_DELETED",
                                "Pessoa inativado (soft delete) pelo usuário " + loggedInUser.getUsername(),
                                oldData,
                                newData);
        }

        @Transactional
        public void activate(Long id, User loggedInUser) {
                authenticationService.authorize(List.of(UserTypeEnum.ADMIN));

                Person person = personRepository.findByIdAndActiveFalse(id)
                                .orElseThrow(() -> NotFoundExceptionFactory.person(id));

                PersonLogData oldData = PersonLogData.fromEntity(person);

                person.setActive(true);

                Person updatedPerson = personRepository.save(person);
                PersonLogData newData = PersonLogData.fromEntity(updatedPerson);

                logPublisherService.publish(
                                "PERSON_ACTIVATED",
                                "Person reativado (soft restore) pelo usuário " + loggedInUser.getUsername(),
                                oldData,
                                newData);
        }

}
