package com.github.renancvitor.inventory.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.enums.user.UserTypeEnum;
import com.github.renancvitor.inventory.dto.user.UserCreationData;
import com.github.renancvitor.inventory.dto.user.UserListiningData;
import com.github.renancvitor.inventory.dto.user.UserLogData;
import com.github.renancvitor.inventory.exception.factory.NotFoundExceptionFactory;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.repository.UserRepository;
import com.github.renancvitor.inventory.repository.UserTypeRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final AuthenticationService authenticationService;
    private final SystemLogPublisherService logPublisherService;

    public UserService(PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            UserTypeRepository userTypeRepository,
            AuthenticationService authenticationService,
            SystemLogPublisherService logPublisherService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.authenticationService = authenticationService;
        this.logPublisherService = logPublisherService;
    }

    public Page<UserListiningData> list(Pageable pageable, User loggedInUser, Boolean active) {
        authenticationService.authorize(List.of(UserTypeEnum.ADMIN));

        if (active != null) {
            return userRepository.findAllByActive(active, pageable).map(UserListiningData::new);
        }

        return userRepository.findAll(pageable).map(UserListiningData::new);
    }

    @Transactional
    public void create(Person person, UserCreationData data, User loggedInUser) {
        String encryptedPassword = passwordEncoder.encode(data.password());

        UserTypeEntity userType = userTypeRepository.findById(UserTypeEnum.COMMON.getId())
                .orElseThrow(() -> NotFoundExceptionFactory.userType(UserTypeEnum.COMMON.getId()));

        User user = new User(encryptedPassword, person, userType);
        userRepository.save(user);

        UserLogData newData = UserLogData.fromEntity(user);

        logPublisherService.publish(
                "USER_CREATED",
                "Novo usuário cadastrado pelo usuário " + loggedInUser.getUsername(),
                null,
                newData);
    }

}
