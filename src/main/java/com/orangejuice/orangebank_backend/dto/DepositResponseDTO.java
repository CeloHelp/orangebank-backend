package com.orangejuice.orangebank_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DepositResponseDTO {
    private String message;
    private BigDecimal newBalance;
    private LocalDateTime dateTime;
    private BigDecimal value;

    public DepositResponseDTO() {}

    public DepositResponseDTO(String message, BigDecimal newBalance, LocalDateTime dateTime, BigDecimal value) {
        this.message = message;
        this.newBalance = newBalance;
        this.dateTime = dateTime;
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
} 