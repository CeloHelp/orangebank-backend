package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.*;
import com.orangejuice.orangebank_backend.dto.TransactionDTO;
import com.orangejuice.orangebank_backend.repository.CurrentAccountRepository;
import com.orangejuice.orangebank_backend.repository.TransactionRepository;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CurrentAccountRepository currentAccountRepository;
    
    private static final BigDecimal TRANSFER_FEE_RATE = new BigDecimal("0.005"); // 0.5%
    private static final BigDecimal STOCK_FEE_RATE = new BigDecimal("0.01"); // 1%
    private static final BigDecimal STOCK_TAX_RATE = new BigDecimal("0.15"); // 15%
    private static final BigDecimal FIXED_INCOME_TAX_RATE = new BigDecimal("0.22"); // 22%
    
    public TransactionDTO deposit(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        CurrentAccount currentAccount = user.getCurrentAccount();
        currentAccount.deposit(amount);
        
        Transaction transaction = new Transaction(
                TransactionType.DEPOSIT,
                amount,
                user,
                "Depósito na conta corrente"
        );
        
        transaction.setSourceAccount(currentAccount);
        transaction.setNetAmount(amount);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }
    
    public TransactionDTO withdraw(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        CurrentAccount currentAccount = user.getCurrentAccount();
        currentAccount.withdraw(amount);
        
        Transaction transaction = new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                user,
                "Saque da conta corrente"
        );
        
        transaction.setSourceAccount(currentAccount);
        transaction.setNetAmount(amount);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }
    
    public TransactionDTO internalTransfer(Long userId, BigDecimal amount, String direction) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        CurrentAccount currentAccount = user.getCurrentAccount();
        InvestmentAccount investmentAccount = user.getInvestmentAccount();
        
        Transaction transaction;
        
        if ("TO_INVESTMENT".equals(direction)) {
            // Transfer from current to investment
            currentAccount.transfer(amount);
            investmentAccount.receiveTransfer(amount);
            
            transaction = new Transaction(
                    TransactionType.INTERNAL_TRANSFER,
                    amount,
                    user,
                    "Transferência da conta corrente para conta de investimento"
            );
            transaction.setSourceAccount(currentAccount);
            transaction.setDestinationAccount(currentAccount); // Same account for internal transfer
        } else {
            // Transfer from investment to current
            investmentAccount.transfer(amount);
            currentAccount.receiveTransfer(amount);
            
            transaction = new Transaction(
                    TransactionType.INTERNAL_TRANSFER,
                    amount,
                    user,
                    "Transferência da conta de investimento para conta corrente"
            );
            transaction.setSourceAccount(currentAccount);
            transaction.setDestinationAccount(currentAccount); // Same account for internal transfer
        }
        
        transaction.setNetAmount(amount);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }
    
    public TransactionDTO externalTransfer(Long userId, BigDecimal amount, String destinationAccountNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        CurrentAccount sourceAccount = user.getCurrentAccount();
        
        CurrentAccount destinationAccount = currentAccountRepository.findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));
        
        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta");
        }
        
        // Calculate transfer fee
        BigDecimal feeAmount = amount.multiply(TRANSFER_FEE_RATE);
        BigDecimal netAmount = amount.subtract(feeAmount);
        
        // Perform transfer
        sourceAccount.transfer(amount);
        destinationAccount.receiveTransfer(netAmount);
        
        // Create transaction for source account
        Transaction transaction = new Transaction(
                TransactionType.TRANSFER,
                amount,
                user,
                "Transferência para conta " + destinationAccountNumber
        );
        
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setFeeAmount(feeAmount);
        transaction.setNetAmount(netAmount);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }
    
    public List<TransactionDTO> getUserTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<TransactionDTO> getUserTransactionsPaginated(Long userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToDTO);
    }
    
    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getFeeAmount(),
                transaction.getTaxAmount(),
                transaction.getNetAmount(),
                transaction.getCreatedAt(),
                transaction.getDescription()
        );
        
        if (transaction.getSourceAccount() != null) {
            dto.setSourceAccountNumber(transaction.getSourceAccount().getAccountNumber());
        }
        
        if (transaction.getDestinationAccount() != null) {
            dto.setDestinationAccountNumber(transaction.getDestinationAccount().getAccountNumber());
        }
        
        if (transaction.getAsset() != null) {
            dto.setAssetSymbol(transaction.getAsset().getSymbol());
        }
        
        return dto;
    }
} 