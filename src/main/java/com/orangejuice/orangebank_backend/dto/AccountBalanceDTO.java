package com.orangejuice.orangebank_backend.dto;

import java.math.BigDecimal;

public class AccountBalanceDTO {
    
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    
    // Constructors
    public AccountBalanceDTO() {}
    
    public AccountBalanceDTO(String accountNumber, BigDecimal balance, String accountType) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
    }
    
    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
} 