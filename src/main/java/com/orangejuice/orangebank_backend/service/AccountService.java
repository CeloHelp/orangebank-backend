package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.CurrentAccount;
import com.orangejuice.orangebank_backend.domain.Transaction;
import com.orangejuice.orangebank_backend.domain.TransactionType;
import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.dto.DepositRequestDTO;
import com.orangejuice.orangebank_backend.dto.DepositResponseDTO;
import com.orangejuice.orangebank_backend.repository.CurrentAccountRepository;
import com.orangejuice.orangebank_backend.repository.TransactionRepository;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CurrentAccountRepository currentAccountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public DepositResponseDTO deposit(DepositRequestDTO request) {
        if (request.getValue() == null || request.getValue() <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        User user = userOpt.get();
        CurrentAccount account = user.getCurrentAccount();
        if (account == null) {
            throw new IllegalArgumentException("Conta Corrente não encontrada para o usuário.");
        }
        BigDecimal value = BigDecimal.valueOf(request.getValue());
        account.setBalance(account.getBalance().add(value));
        currentAccountRepository.save(account);

        // Registrar movimentação
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCurrentAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setValue(value);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setDescription("Depósito na Conta Corrente");
        transactionRepository.save(transaction);

        return new DepositResponseDTO(
                "Depósito realizado com sucesso!",
                account.getBalance(),
                transaction.getDateTime(),
                value
        );
    }
} 