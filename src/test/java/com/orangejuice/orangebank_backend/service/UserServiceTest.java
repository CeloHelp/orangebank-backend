package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.dto.UserDTO;
import com.orangejuice.orangebank_backend.repository.CurrentAccountRepository;
import com.orangejuice.orangebank_backend.repository.InvestmentAccountRepository;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentAccountRepository currentAccountRepository;
    @Mock
    private InvestmentAccountRepository investmentAccountRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João Teste");
        user.setEmail("joao@email.com");
        user.setCpf("123.456.789-00");
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        User created = userService.createUser(user);
        assertNotNull(created);
        assertEquals("João Teste", created.getName());
        assertEquals("joao@email.com", created.getEmail());
        assertEquals("123.456.789-00", created.getCpf());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<UserDTO> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals("João Teste", result.get().getName());
    }

    @Test
    void testGetUserByEmail() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        Optional<UserDTO> result = userService.getUserByEmail("joao@email.com");
        assertTrue(result.isPresent());
        assertEquals("João Teste", result.get().getName());
    }

    @Test
    void testGetUserByCpf() {
        when(userRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(user));
        Optional<UserDTO> result = userService.getUserByCpf("123.456.789-00");
        assertTrue(result.isPresent());
        assertEquals("João Teste", result.get().getName());
    }
} 