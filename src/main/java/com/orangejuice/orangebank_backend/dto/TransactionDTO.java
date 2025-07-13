package com.orangejuice.orangebank_backend.dto;

import com.orangejuice.orangebank_backend.domain.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal feeAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private LocalDateTime createdAt;
    private String description;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private String assetSymbol;
    
    // Constructors
    public TransactionDTO() {}
    
    public TransactionDTO(Long id, TransactionType type, BigDecimal amount, BigDecimal feeAmount, 
                         BigDecimal taxAmount, BigDecimal netAmount, LocalDateTime createdAt, 
                         String description) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.feeAmount = feeAmount;
        this.taxAmount = taxAmount;
        this.netAmount = netAmount;
        this.createdAt = createdAt;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getFeeAmount() {
        return feeAmount;
    }
    
    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getNetAmount() {
        return netAmount;
    }
    
    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }
    
    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }
    
    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }
    
    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }
    
    public String getAssetSymbol() {
        return assetSymbol;
    }
    
    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }
} 