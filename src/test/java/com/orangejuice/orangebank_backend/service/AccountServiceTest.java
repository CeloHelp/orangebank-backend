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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentAccountRepository currentAccountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private CurrentAccount currentAccount;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        currentAccount = new CurrentAccount();
        currentAccount.setAccountNumber("123456-7");
        currentAccount.setBalance(new BigDecimal("500.00"));
        user.setCurrentAccount(currentAccount);
    }

    @Test
    void testDeposit_Success() {
        DepositRequestDTO request = new DepositRequestDTO();
        request.setUserId(1L);
        request.setValue(200.00);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentAccountRepository.save(any(CurrentAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DepositResponseDTO response = accountService.deposit(request);
        assertNotNull(response);
        assertEquals("Depósito realizado com sucesso!", response.getMessage());
        assertEquals(new BigDecimal("700.00"), response.getNewBalance());
    }

    @Test
    void testWithdraw_Success() {
        DepositRequestDTO request = new DepositRequestDTO();
        request.setUserId(1L);
        request.setValue(100.00);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentAccountRepository.save(any(CurrentAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DepositResponseDTO response = accountService.withdraw(request);
        assertNotNull(response);
        assertEquals("Saque realizado com sucesso!", response.getMessage());
        assertEquals(new BigDecimal("400.00"), response.getNewBalance());
    }

    @Test
    void testWithdraw_InsufficientBalance() {
        DepositRequestDTO request = new DepositRequestDTO();
        request.setUserId(1L);
        request.setValue(1000.00);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdraw(request);
        });
        assertEquals("Saldo insuficiente para realizar o saque.", ex.getMessage());
    }

    @Test
    void testInternalTransfer_ToInvestment_Success() {
        // Corrente -> Investimento
        user.setInvestmentAccount(new com.orangejuice.orangebank_backend.domain.InvestmentAccount());
        user.getInvestmentAccount().setBalance(new BigDecimal("100.00"));
        com.orangejuice.orangebank_backend.dto.TransferRequestDTO request = new com.orangejuice.orangebank_backend.dto.TransferRequestDTO();
        request.setAmount(new BigDecimal("200.00"));
        request.setTransferType("INTERNAL");
        request.setDirection("TO_INVESTMENT");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentAccountRepository.save(any(CurrentAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        com.orangejuice.orangebank_backend.dto.DepositResponseDTO response = accountService.transfer(1L, request);
        assertNotNull(response);
        assertEquals("Transferência interna realizada com sucesso!", response.getMessage());
        assertEquals(new BigDecimal("300.00"), response.getNewBalance());
    }

    @Test
    void testInternalTransfer_ToCurrent_Success() {
        // Investimento -> Corrente
        user.setInvestmentAccount(new com.orangejuice.orangebank_backend.domain.InvestmentAccount());
        user.getInvestmentAccount().setBalance(new BigDecimal("500.00"));
        com.orangejuice.orangebank_backend.dto.TransferRequestDTO request = new com.orangejuice.orangebank_backend.dto.TransferRequestDTO();
        request.setAmount(new BigDecimal("200.00"));
        request.setTransferType("INTERNAL");
        request.setDirection("TO_CURRENT");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentAccountRepository.save(any(CurrentAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        com.orangejuice.orangebank_backend.dto.DepositResponseDTO response = accountService.transfer(1L, request);
        assertNotNull(response);
        assertEquals("Transferência interna realizada com sucesso!", response.getMessage());
        assertEquals(new BigDecimal("700.00"), response.getNewBalance());
    }

    @Test
    void testTransfer_InvalidAmount() {
        com.orangejuice.orangebank_backend.dto.TransferRequestDTO request = new com.orangejuice.orangebank_backend.dto.TransferRequestDTO();
        request.setAmount(BigDecimal.ZERO);
        request.setTransferType("INTERNAL");
        request.setDirection("TO_INVESTMENT");
        // Não mockar userRepository.findById(1L) aqui
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            accountService.transfer(1L, request);
        });
        assertEquals("O valor da transferência deve ser maior que zero.", ex.getMessage());
    }
} 