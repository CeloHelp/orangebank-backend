package com.orangejuice.orangebank_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AssetPurchaseRequestDTO {
    
    @NotNull(message = "Símbolo do ativo é obrigatório")
    @Pattern(regexp = "^[A-Z0-9]{4,6}$", message = "Símbolo deve ter entre 4 e 6 caracteres maiúsculos")
    private String symbol;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantity;
    
    // Constructors
    public AssetPurchaseRequestDTO() {}
    
    public AssetPurchaseRequestDTO(String symbol, Integer quantity) {
        this.symbol = symbol;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
} 