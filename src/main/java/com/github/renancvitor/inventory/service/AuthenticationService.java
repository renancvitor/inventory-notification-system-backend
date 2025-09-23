package com.github.renancvitor.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.dto.authentication.JWTTokenData;
import com.github.renancvitor.inventory.dto.authentication.LoginData;
import com.github.renancvitor.inventory.dto.user.UserSummaryData;
import com.github.renancvitor.inventory.repository.UserRepository;

public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

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

}
