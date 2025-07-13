package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.dto.AuthResponseDTO;
import com.orangejuice.orangebank_backend.dto.LoginRequestDTO;
import com.orangejuice.orangebank_backend.dto.RegisterRequestDTO;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setName("João Teste");
        registerRequest.setEmail("joao@email.com");
        registerRequest.setCpf("123.456.789-00");
        registerRequest.setBirthDate("1990-01-01");
        registerRequest.setPassword("senha123");

        user = new User();
        user.setId(1L);
        user.setName("João Teste");
        user.setEmail("joao@email.com");
        user.setCpf("123.456.789-00");
        user.setPassword("senhaCriptografada");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("123.456.789-00")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        AuthResponseDTO response = authService.register(registerRequest);
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
    }

    @Test
    void testLogin_Success() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("joao@email.com");
        loginRequest.setPassword("senha123");
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        AuthResponseDTO response = authService.login(loginRequest);
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
    }
} 