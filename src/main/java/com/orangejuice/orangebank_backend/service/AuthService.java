package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.CurrentAccount;
import com.orangejuice.orangebank_backend.domain.InvestmentAccount;
import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.dto.AuthResponseDTO;
import com.orangejuice.orangebank_backend.dto.LoginRequestDTO;
import com.orangejuice.orangebank_backend.dto.RegisterRequestDTO;
import com.orangejuice.orangebank_backend.dto.UserDTO;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Verificar se usuário já existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        
        if (userRepository.findByCpf(request.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        
        // Criar novo usuário
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setCpf(request.getCpf());
        user.setBirthDate(LocalDate.parse(request.getBirthDate()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Criar contas bancárias
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setAccountNumber(generateAccountNumber());
        currentAccount.setBalance(BigDecimal.ZERO);
        currentAccount.setUser(user);
        user.setCurrentAccount(currentAccount);
        
        InvestmentAccount investmentAccount = new InvestmentAccount();
        investmentAccount.setAccountNumber(generateAccountNumber());
        investmentAccount.setBalance(BigDecimal.ZERO);
        investmentAccount.setUser(user);
        user.setInvestmentAccount(investmentAccount);
        
        // Salvar usuário
        User savedUser = userRepository.save(user);
        
        // Gerar token JWT
        String token = jwtService.generateToken(createUserDetails(savedUser));
        
        // Retornar resposta
        UserDTO userDTO = new UserDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCpf(),
                savedUser.getBirthDate()
        );
        
        return new AuthResponseDTO(token, userDTO);
    }
    
    public AuthResponseDTO login(LoginRequestDTO request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        
        User user = userOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        
        // Gerar token JWT
        String token = jwtService.generateToken(createUserDetails(user));
        
        // Retornar resposta
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getBirthDate()
        );
        
        return new AuthResponseDTO(token, userDTO);
    }
    
    private String generateAccountNumber() {
        // Gerar número de conta aleatório (formato: 12345678-9)
        int accountNumber = (int) (Math.random() * 90000000) + 10000000;
        int digit = (int) (Math.random() * 10);
        return accountNumber + "-" + digit;
    }
    
    private org.springframework.security.core.userdetails.UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
} 