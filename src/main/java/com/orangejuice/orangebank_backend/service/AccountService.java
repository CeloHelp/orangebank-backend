package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.CurrentAccount;
import com.orangejuice.orangebank_backend.domain.Transaction;
import com.orangejuice.orangebank_backend.domain.TransactionType;
import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.dto.DepositRequestDTO;
import com.orangejuice.orangebank_backend.dto.DepositResponseDTO;
import com.orangejuice.orangebank_backend.dto.TransferRequestDTO;
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
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(value);
        transaction.setNetAmount(value);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDescription("Depósito na Conta Corrente");
        transaction.setDestinationAccount(account);
        transactionRepository.save(transaction);

        return new DepositResponseDTO(
                "Depósito realizado com sucesso!",
                account.getBalance(),
                transaction.getCreatedAt(),
                value
        );
    }

    @Transactional
    public DepositResponseDTO withdraw(DepositRequestDTO request) {
        if (request.getValue() == null || request.getValue() <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
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
        if (account.getBalance().compareTo(value) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar o saque.");
        }
        account.setBalance(account.getBalance().subtract(value));
        currentAccountRepository.save(account);

        // Registrar movimentação
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(value);
        transaction.setNetAmount(value);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDescription("Saque na Conta Corrente");
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);

        return new DepositResponseDTO(
                "Saque realizado com sucesso!",
                account.getBalance(),
                transaction.getCreatedAt(),
                value
        );
    }

    @Transactional
    public DepositResponseDTO transfer(Long userId, TransferRequestDTO request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser maior que zero.");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        User user = userOpt.get();
        CurrentAccount sourceAccount = user.getCurrentAccount();
        if (sourceAccount == null) {
            throw new IllegalArgumentException("Conta Corrente não encontrada para o usuário.");
        }
        BigDecimal value = request.getAmount();
        if (sourceAccount.getBalance().compareTo(value) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência.");
        }
        if ("INTERNAL".equals(request.getTransferType())) {
            // Transferência interna: entre conta corrente e investimento
            if (request.getDirection() == null ||
                !(request.getDirection().equals("TO_INVESTMENT") || request.getDirection().equals("TO_CURRENT"))) {
                throw new IllegalArgumentException("Direção da transferência interna deve ser TO_INVESTMENT ou TO_CURRENT.");
            }
            // Buscar conta investimento do usuário
            if (user.getInvestmentAccount() == null) {
                throw new IllegalArgumentException("Conta Investimento não encontrada para o usuário.");
            }
            if (request.getDirection().equals("TO_INVESTMENT")) {
                // Corrente -> Investimento
                if (sourceAccount.getBalance().compareTo(value) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente na Conta Corrente.");
                }
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(value));
                user.getInvestmentAccount().setBalance(user.getInvestmentAccount().getBalance().add(value));
                currentAccountRepository.save(sourceAccount);
                // Supondo que há um investmentAccountRepository, salvar também
                // investmentAccountRepository.save(user.getInvestmentAccount());
            } else {
                // Investimento -> Corrente
                if (user.getInvestmentAccount().getBalance().compareTo(value) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente na Conta Investimento.");
                }
                user.getInvestmentAccount().setBalance(user.getInvestmentAccount().getBalance().subtract(value));
                sourceAccount.setBalance(sourceAccount.getBalance().add(value));
                currentAccountRepository.save(sourceAccount);
                // Supondo que há um investmentAccountRepository, salvar também
                // investmentAccountRepository.save(user.getInvestmentAccount());
            }
            // Registrar movimentação
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setType(TransactionType.INTERNAL_TRANSFER);
            transaction.setAmount(value);
            transaction.setNetAmount(value);
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setDescription("Transferência interna: " + request.getDirection());
            transaction.setSourceAccount(sourceAccount);
            // Não setar destinationAccount se destino for InvestmentAccount
            transactionRepository.save(transaction);
            return new DepositResponseDTO(
                    "Transferência interna realizada com sucesso!",
                    sourceAccount.getBalance(),
                    transaction.getCreatedAt(),
                    value
            );
        } else if ("EXTERNAL".equals(request.getTransferType())) {
            // Transferência externa: para outra conta corrente
            if (request.getDestinationAccountNumber() == null) {
                throw new IllegalArgumentException("Número da conta de destino é obrigatório para transferência externa.");
            }
            Optional<CurrentAccount> destOpt = currentAccountRepository.findByAccountNumber(request.getDestinationAccountNumber());
            if (destOpt.isEmpty()) {
                throw new IllegalArgumentException("Conta de destino não encontrada.");
            }
            CurrentAccount destAccount = destOpt.get();
            if (destAccount.getId().equals(sourceAccount.getId())) {
                throw new IllegalArgumentException("Não é possível transferir para a própria conta.");
            }
            // Desconta da conta de origem
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(value));
            currentAccountRepository.save(sourceAccount);
            // Credita na conta de destino
            destAccount.setBalance(destAccount.getBalance().add(value));
            currentAccountRepository.save(destAccount);
            // Registrar movimentação
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setType(TransactionType.TRANSFER);
            transaction.setAmount(value);
            transaction.setNetAmount(value);
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setDescription("Transferência para conta " + request.getDestinationAccountNumber());
            transaction.setSourceAccount(sourceAccount);
            transaction.setDestinationAccount(destAccount);
            transactionRepository.save(transaction);
            return new DepositResponseDTO(
                    "Transferência realizada com sucesso!",
                    sourceAccount.getBalance(),
                    transaction.getCreatedAt(),
                    value
            );
        } else {
            throw new IllegalArgumentException("Tipo de transferência inválido.");
        }
    }
} 