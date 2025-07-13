package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.CurrentAccount;
import com.orangejuice.orangebank_backend.domain.InvestmentAccount;
import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.dto.UserDTO;
import com.orangejuice.orangebank_backend.repository.CurrentAccountRepository;
import com.orangejuice.orangebank_backend.repository.InvestmentAccountRepository;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CurrentAccountRepository currentAccountRepository;
    
    @Autowired
    private InvestmentAccountRepository investmentAccountRepository;
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO);
    }
    
    public Optional<UserDTO> getUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf)
                .map(this::convertToDTO);
    }
    
    public Optional<UserDTO> getUserByEmailOrCpf(String emailOrCpf) {
        return userRepository.findByEmailOrCpf(emailOrCpf, emailOrCpf)
                .map(this::convertToDTO);
    }
    
    public User createUser(User user) {
        // Generate account numbers
        String currentAccountNumber = generateAccountNumber();
        String investmentAccountNumber = generateAccountNumber();
        
        // Create accounts
        CurrentAccount currentAccount = new CurrentAccount(currentAccountNumber, user);
        InvestmentAccount investmentAccount = new InvestmentAccount(investmentAccountNumber, user);
        
        // Set accounts to user
        user.setCurrentAccount(currentAccount);
        user.setInvestmentAccount(investmentAccount);
        
        // Save user (cascade will save accounts)
        return userRepository.save(user);
    }
    
    public boolean userExists(String email, String cpf) {
        return userRepository.existsByEmail(email) || userRepository.existsByCpf(cpf);
    }
    
    private String generateAccountNumber() {
        // Simple account number generation (6 digits)
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
    
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getBirthDate()
        );
    }
} 