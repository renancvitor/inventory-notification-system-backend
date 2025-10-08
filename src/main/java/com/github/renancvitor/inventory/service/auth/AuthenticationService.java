package com.github.renancvitor.inventory.service.auth;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.enums.user.UserTypeEnum;
import com.github.renancvitor.inventory.dto.authentication.JWTTokenData;
import com.github.renancvitor.inventory.dto.authentication.LoginData;
import com.github.renancvitor.inventory.dto.user.UserSummaryData;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByPersonEmailAndActiveTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    public JWTTokenData authentication(LoginData loginData, AuthenticationManager authenticationManager) {
        var token = new UsernamePasswordAuthenticationToken(loginData.email(), loginData.password());
        Authentication authentication = authenticationManager.authenticate(token);

        User user = (User) authentication.getPrincipal();
        String jwt = tokenService.generateToken(user);

        UserSummaryData userSummaryData = new UserSummaryData(user);

        boolean firstAccess = user.getFirstAccess();

        return new JWTTokenData(jwt, userSummaryData, firstAccess);
    }

    public void authorize(List<UserTypeEnum> allowedUserTypes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Integer loggedInUserTypeId = user.getUserType().getId();
        boolean isAuthorized = allowedUserTypes.stream()
                .map(UserTypeEnum::getId)
                .anyMatch(id -> id.equals(loggedInUserTypeId));

        if (!isAuthorized) {
            throw new AuthorizationException(allowedUserTypes.stream()
                    .map(UserTypeEnum::getId).toList());
        }
    }

}
