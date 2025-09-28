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
import com.github.renancvitor.inventory.dto.user.UserDetailData;
import com.github.renancvitor.inventory.dto.user.UserListiningData;
import com.github.renancvitor.inventory.dto.user.UserLogData;
import com.github.renancvitor.inventory.dto.user.UserLogPasswordData;
import com.github.renancvitor.inventory.dto.user.UserPasswordUpdateData;
import com.github.renancvitor.inventory.dto.user.UserTypeUpdateData;
import com.github.renancvitor.inventory.exception.factory.NotFoundExceptionFactory;
import com.github.renancvitor.inventory.exception.types.common.ValidationException;
import com.github.renancvitor.inventory.exception.types.user.AccessDeniedException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.repository.UserRepository;
import com.github.renancvitor.inventory.repository.UserTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final AuthenticationService authenticationService;
    private final SystemLogPublisherService logPublisherService;

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

    @Transactional
    public UserDetailData updatePassword(Long id, UserPasswordUpdateData data, User loggedInUser) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> NotFoundExceptionFactory.user(id));

        UserLogPasswordData oldData = UserLogPasswordData.fromEntity(user);

        if (!user.getId().equals(id)) {
            throw new AccessDeniedException();
        }

        if (!passwordEncoder.matches(data.currentPassword(), user.getPassword())) {
            throw new ValidationException("Usuário", "senha atual", "a senha atual está incorreta");
        }

        if (!data.newPassword().equals(data.confirmNewPassword())) {
            throw new ValidationException("Usuário", "nova senha e confirmação nova senha", "não coincidem");
        }

        if (!strongPassword(data.newPassword())) {
            throw new ValidationException("Usuário", "nova senha",
                    "mínimo de 8 caracteres, pelo menos 1 letra maiúscula, 1 minúscula, 1 número e 1 caractere especial");
        }

        String newEncryptedPassword = passwordEncoder.encode(data.newPassword());
        user.updatePassword(newEncryptedPassword);

        user.setFirstAccess(false);
        User updatedUser = userRepository.save(user);

        UserLogPasswordData newData = UserLogPasswordData.fromEntity(updatedUser);

        logPublisherService.publish(
                "USER_UPDATED_PASSWORD",
                "Usuário atualizou sua senha - (usuário: " + loggedInUser.getUsername() + ")",
                oldData,
                newData);

        return new UserDetailData(updatedUser);
    }

    @Transactional
    public UserDetailData updateUserType(Long id, UserTypeUpdateData data, User loggedInUser) {
        authenticationService.authorize(List.of(UserTypeEnum.ADMIN));

        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> NotFoundExceptionFactory.user(id));

        UserLogData oldData = UserLogData.fromEntity(user);

        if (user.getUserType().getId() == null) {
            throw new ValidationException("Usuário", "tipo do usuário",
                    "deve ser preenchido corretamente");
        }

        if (data.idUserType() != null) {
            UserTypeEntity userTypeEntity = userTypeRepository.findById(data.idUserType())
                    .orElseThrow(() -> NotFoundExceptionFactory.userType(data.idUserType()));
            user.setUserType(userTypeEntity);
        }

        User updatedUserTypeEntity = userRepository.save(user);
        UserLogData newData = UserLogData.fromEntity(updatedUserTypeEntity);

        logPublisherService.publish(
                "UPDATED_USER_TYPE",
                "Tipo de usuário atualizado pelo usuário " + loggedInUser.getUsername(),
                oldData,
                newData);

        return new UserDetailData(user);
    }

    @Transactional
    public void delete(Long id, User loggedInUser) {
        authenticationService.authorize(List.of(UserTypeEnum.ADMIN));

        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> NotFoundExceptionFactory.user(id));

        UserLogData oldData = UserLogData.fromEntity(user);

        user.setActive(false);

        User updatedUser = userRepository.save(user);
        UserLogData newData = UserLogData.fromEntity(updatedUser);

        logPublisherService.publish(
                "USER_DELETED",
                "Usuário inativado (soft delete) pelo usuário " + loggedInUser.getUsername(),
                oldData,
                newData);
    }

    @Transactional
    public void activate(Long id, User loggedInUser) {
        authenticationService.authorize(List.of(UserTypeEnum.ADMIN));

        User user = userRepository.findByIdAndActiveFalse(id)
                .orElseThrow(() -> NotFoundExceptionFactory.user(id));

        UserLogData oldData = UserLogData.fromEntity(user);

        user.setActive(true);

        User updatedUser = userRepository.save(user);
        UserLogData newData = UserLogData.fromEntity(updatedUser);

        logPublisherService.publish(
                "USER_ACTIVATED",
                "Usuário reativado (soft restore) pelo usuário " + loggedInUser.getUsername(),
                oldData,
                newData);
    }

    public boolean strongPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@$!%#?&])[A-Za-z\\\\d@$!%#?&]{8,}$";
        return password != null && password.matches(regex);
    }

}
