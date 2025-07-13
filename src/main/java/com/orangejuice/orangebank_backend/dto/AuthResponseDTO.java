package com.orangejuice.orangebank_backend.dto;

public class AuthResponseDTO {
    
    private String token;
    private String type = "Bearer";
    private UserDTO user;
    
    public AuthResponseDTO() {}
    
    public AuthResponseDTO(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public UserDTO getUser() {
        return user;
    }
    
    public void setUser(UserDTO user) {
        this.user = user;
    }
} 