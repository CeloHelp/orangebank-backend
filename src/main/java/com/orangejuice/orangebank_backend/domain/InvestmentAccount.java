package com.orangejuice.orangebank_backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_accounts")
public class InvestmentAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructors
    public InvestmentAccount() {
        this.createdAt = LocalDateTime.now();
    }
    
    public InvestmentAccount(String accountNumber, User user) {
        this();
        this.accountNumber = accountNumber;
        this.user = user;
    }
    
    // Business methods
    public void receiveTransfer(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }
        this.balance = this.balance.add(amount);
    }
    
    public void transfer(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência");
        }
        this.balance = this.balance.subtract(amount);
    }
    
    public void buyAsset(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da compra deve ser maior que zero");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a compra");
        }
        this.balance = this.balance.subtract(amount);
    }
    
    public void sellAsset(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da venda deve ser maior que zero");
        }
        this.balance = this.balance.add(amount);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
} 