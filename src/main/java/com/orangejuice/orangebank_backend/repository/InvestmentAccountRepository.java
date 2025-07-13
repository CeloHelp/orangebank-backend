package com.orangejuice.orangebank_backend.repository;

import com.orangejuice.orangebank_backend.domain.InvestmentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestmentAccountRepository extends JpaRepository<InvestmentAccount, Long> {
    
    Optional<InvestmentAccount> findByAccountNumber(String accountNumber);
    
    Optional<InvestmentAccount> findByUserId(Long userId);
    
    boolean existsByAccountNumber(String accountNumber);
} 