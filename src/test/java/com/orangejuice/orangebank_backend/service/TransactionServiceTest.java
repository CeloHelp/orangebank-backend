package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.CurrentAccount;
import com.orangejuice.orangebank_backend.domain.Transaction;
import com.orangejuice.orangebank_backend.domain.TransactionType;
import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.dto.TransactionDTO;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentAccountRepository currentAccountRepository;

    @InjectMocks
    private TransactionService transactionService;

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
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TransactionDTO dto = transactionService.deposit(1L, new BigDecimal("200.00"));
        assertNotNull(dto);
        assertEquals(TransactionType.DEPOSIT, dto.getType());
        assertEquals(new BigDecimal("200.00"), dto.getAmount());
    }

    @Test
    void testGetUserTransactions() {
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setType(TransactionType.DEPOSIT);
        t1.setAmount(new BigDecimal("100.00"));
        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setType(TransactionType.WITHDRAWAL);
        t2.setAmount(new BigDecimal("50.00"));
        when(transactionRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(t1, t2));
        List<TransactionDTO> result = transactionService.getUserTransactions(1L);
        assertEquals(2, result.size());
        assertEquals(TransactionType.DEPOSIT, result.get(0).getType());
        assertEquals(TransactionType.WITHDRAWAL, result.get(1).getType());
    }
} 