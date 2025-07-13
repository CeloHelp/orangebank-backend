package com.orangejuice.orangebank_backend.controller;

import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.dto.AccountBalanceDTO;
import com.orangejuice.orangebank_backend.dto.UserDTO;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import com.orangejuice.orangebank_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<UserDTO> getUserByEmailOrCpf(@RequestParam String emailOrCpf) {
        Optional<UserDTO> user = userService.getUserByEmailOrCpf(emailOrCpf);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            UserDTO userDTO = new UserDTO(
                    createdUser.getId(),
                    createdUser.getName(),
                    createdUser.getEmail(),
                    createdUser.getCpf(),
                    createdUser.getBirthDate()
            );
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/balances")
    public ResponseEntity<List<AccountBalanceDTO>> getUserBalances(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        
        List<AccountBalanceDTO> balances = List.of(
                new AccountBalanceDTO(
                        user.getCurrentAccount().getAccountNumber(),
                        user.getCurrentAccount().getBalance(),
                        "Conta Corrente"
                ),
                new AccountBalanceDTO(
                        user.getInvestmentAccount().getAccountNumber(),
                        user.getInvestmentAccount().getBalance(),
                        "Conta Investimento"
                )
        );
        
        return ResponseEntity.ok(balances);
    }
    
    private User convertToUser(UserDTO userDTO) {
        // This is a simplified conversion - in a real app, you'd have a proper mapper
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setCpf(userDTO.getCpf());
        user.setBirthDate(userDTO.getBirthDate());
        return user;
    }
} 