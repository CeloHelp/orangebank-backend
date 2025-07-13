package com.orangejuice.orangebank_backend.controller;

import com.orangejuice.orangebank_backend.dto.DepositRequestDTO;
import com.orangejuice.orangebank_backend.dto.TransactionDTO;
import com.orangejuice.orangebank_backend.dto.TransferRequestDTO;
import com.orangejuice.orangebank_backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping("/{userId}/deposit")
    public ResponseEntity<TransactionDTO> deposit(
            @PathVariable Long userId,
            @Valid @RequestBody DepositRequestDTO request) {
        try {
            TransactionDTO transaction = transactionService.deposit(userId, java.math.BigDecimal.valueOf(request.getValue()));
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(
            @PathVariable Long userId,
            @Valid @RequestBody DepositRequestDTO request) {
        try {
            TransactionDTO transaction = transactionService.withdraw(userId, java.math.BigDecimal.valueOf(request.getValue()));
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{userId}/transfer")
    public ResponseEntity<TransactionDTO> transfer(
            @PathVariable Long userId,
            @Valid @RequestBody TransferRequestDTO request) {
        try {
            TransactionDTO transaction;
            
            if ("INTERNAL".equals(request.getTransferType())) {
                // Internal transfer between user's own accounts
                String direction = "TO_INVESTMENT"; // Default direction
                transaction = transactionService.internalTransfer(userId, request.getAmount(), direction);
            } else {
                // External transfer to another user
                transaction = transactionService.externalTransfer(userId, request.getAmount(), request.getDestinationAccountNumber());
            }
            
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{userId}/transfer/internal")
    public ResponseEntity<?> internalTransfer(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String direction) {
        try {
            TransactionDTO transaction = transactionService.internalTransfer(userId, amount, direction);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(@PathVariable Long userId) {
        List<TransactionDTO> transactions = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{userId}/paginated")
    public ResponseEntity<Page<TransactionDTO>> getUserTransactionsPaginated(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<TransactionDTO> transactions = transactionService.getUserTransactionsPaginated(userId, pageable);
        return ResponseEntity.ok(transactions);
    }
} 