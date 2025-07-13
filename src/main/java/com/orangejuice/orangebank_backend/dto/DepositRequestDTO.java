package com.orangejuice.orangebank_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DepositRequestDTO {
    @NotNull(message = "O valor do depósito é obrigatório")
    @Min(value = 1, message = "O valor do depósito deve ser maior que zero")
    private Double value;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long userId;

    public DepositRequestDTO() {}

    public DepositRequestDTO(Double value, Long userId) {
        this.value = value;
        this.userId = userId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
} 