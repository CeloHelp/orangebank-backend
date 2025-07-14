package com.orangejuice.orangebank_backend.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Definir secret e expiration manualmente para o teste usando reflexão
        Field secretField = JwtService.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtService, "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
        Field expField = JwtService.class.getDeclaredField("jwtExpiration");
        expField.setAccessible(true);
        expField.set(jwtService, 3600000L); // 1 hora
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        UserDetails userDetails = new User("usuarioTeste", "senha", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        String username = jwtService.extractUsername(token);
        assertEquals("usuarioTeste", username);
    }

    @Test
    void testExtractClaim() {
        UserDetails userDetails = new User("usuarioClaim", "senha", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);
        Claims claims = jwtService.extractClaim(token, c -> c);
        assertEquals("usuarioClaim", claims.getSubject());
    }

    @Test
    void testExtractUsername_InvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(Exception.class, () -> {
            jwtService.extractUsername(invalidToken);
        });
    }

    @Test
    void testExtractClaim_InvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(Exception.class, () -> {
            jwtService.extractClaim(invalidToken, c -> c.getSubject());
        });
    }
} 