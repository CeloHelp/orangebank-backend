package com.orangejuice.orangebank_backend.dto;

import java.time.LocalDate;

public class UserDTO {
    
    private Long id;
    private String name;
    private String email;
    private String cpf;
    private LocalDate birthDate;
    
    // Constructors
    public UserDTO() {}
    
    public UserDTO(Long id, String name, String email, String cpf, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.birthDate = birthDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
} 