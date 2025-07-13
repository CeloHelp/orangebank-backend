package com.orangejuice.orangebank_backend.repository;

import com.orangejuice.orangebank_backend.domain.Transaction;
import com.orangejuice.orangebank_backend.domain.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Transaction> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, TransactionType type);
    
    List<Transaction> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findBySourceAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(Long sourceAccountId, Long destinationAccountId);
} 