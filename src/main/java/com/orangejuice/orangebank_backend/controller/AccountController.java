package com.orangejuice.orangebank_backend.controller;

import com.orangejuice.orangebank_backend.dto.DepositRequestDTO;
import com.orangejuice.orangebank_backend.dto.DepositResponseDTO;
import com.orangejuice.orangebank_backend.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Contas", description = "Operações de Conta Corrente e Investimento")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/deposit")
    @Operation(summary = "Realizar depósito na Conta Corrente", description = "Permite ao usuário depositar um valor em sua Conta Corrente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou erro de validação")
    })
    public ResponseEntity<?> deposit(@Valid @RequestBody DepositRequestDTO request) {
        try {
            DepositResponseDTO response = accountService.deposit(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Realizar saque na Conta Corrente", description = "Permite ao usuário sacar um valor de sua Conta Corrente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saque realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou erro de validação")
    })
    public ResponseEntity<?> withdraw(@Valid @RequestBody DepositRequestDTO request) {
        try {
            DepositResponseDTO response = accountService.withdraw(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 