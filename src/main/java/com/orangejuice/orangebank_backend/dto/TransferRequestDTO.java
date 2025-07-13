package com.orangejuice.orangebank_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class TransferRequestDTO {
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;
    
    @NotNull(message = "Tipo de transferência é obrigatório")
    @Pattern(regexp = "^(INTERNAL|EXTERNAL)$", message = "Tipo deve ser INTERNAL ou EXTERNAL")
    private String transferType;
    
    @Pattern(regexp = "^[0-9]{6}$", message = "Número da conta deve ter 6 dígitos")
    private String destinationAccountNumber;
    
    // Constructors
    public TransferRequestDTO() {}
    
    public TransferRequestDTO(BigDecimal amount, String transferType, String destinationAccountNumber) {
        this.amount = amount;
        this.transferType = transferType;
        this.destinationAccountNumber = destinationAccountNumber;
    }
    
    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getTransferType() {
        return transferType;
    }
    
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
    
    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }
    
    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }
} 