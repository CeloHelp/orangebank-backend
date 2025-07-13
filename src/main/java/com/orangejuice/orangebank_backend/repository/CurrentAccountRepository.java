package com.orangejuice.orangebank_backend.repository;

import com.orangejuice.orangebank_backend.domain.CurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {
    
    Optional<CurrentAccount> findByAccountNumber(String accountNumber);
    
    Optional<CurrentAccount> findByUserId(Long userId);
    
    boolean existsByAccountNumber(String accountNumber);
} 