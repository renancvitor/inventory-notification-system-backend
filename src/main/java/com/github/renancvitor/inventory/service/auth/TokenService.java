package com.github.renancvitor.inventory.service.auth;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.renancvitor.inventory.domain.entity.user.User;

@Service
public class TokenService {

    @Value("${security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Inventory Notification System API.")
                    .withSubject(user.getPerson().getEmail())
                    .withClaim("id", user.getId())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException jwtCreationException) {
            throw new RuntimeException("Erro ao gerar token JWT.", jwtCreationException);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("Inventory Notification System API.")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException jwtVerificationException) {
            throw new RuntimeException("Token JWT inválido ou expeirado.", jwtVerificationException);
        }
    }

    private Instant expirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
