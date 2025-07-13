package com.orangejuice.orangebank_backend.domain;

public enum TransactionType {
    DEPOSIT("Depósito"),
    WITHDRAWAL("Saque"),
    TRANSFER("Transferência"),
    INTERNAL_TRANSFER("Transferência Interna"),
    ASSET_PURCHASE("Compra de Ativo"),
    ASSET_SALE("Venda de Ativo"),
    FEE("Taxa"),
    TAX("Imposto");
    
    private final String description;
    
    TransactionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 