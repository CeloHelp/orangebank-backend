package com.orangejuice.orangebank_backend.domain;

public enum AssetType {
    STOCK("Ação"),
    CDB("CDB"),
    TREASURY("Tesouro Direto"),
    FUND("Fundo de Investimento");
    
    private final String description;
    
    AssetType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 