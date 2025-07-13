package com.orangejuice.orangebank_backend.controller;

import com.orangejuice.orangebank_backend.domain.Asset;
import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.dto.AccountBalanceDTO;
import com.orangejuice.orangebank_backend.dto.TransactionDTO;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import com.orangejuice.orangebank_backend.service.AssetService;
import com.orangejuice.orangebank_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AssetService assetService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getDashboard(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        Map<String, Object> dashboard = new HashMap<>();
        
        // User info
        dashboard.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail()
        ));
        
        // Account balances
        List<AccountBalanceDTO> balances = List.of(
                new AccountBalanceDTO(
                        user.getCurrentAccount().getAccountNumber(),
                        user.getCurrentAccount().getBalance(),
                        "Conta Corrente"
                ),
                new AccountBalanceDTO(
                        user.getInvestmentAccount().getAccountNumber(),
                        user.getInvestmentAccount().getBalance(),
                        "Conta Investimento"
                )
        );
        dashboard.put("balances", balances);
        
        // Recent transactions (last 5)
        List<TransactionDTO> recentTransactions = transactionService.getUserTransactions(userId)
                .stream()
                .limit(5)
                .toList();
        dashboard.put("recentTransactions", recentTransactions);
        
        // User assets
        List<Asset> userAssets = assetService.getUserAssets(userId);
        dashboard.put("assets", userAssets);
        
        // Portfolio summary
        BigDecimal totalInvested = userAssets.stream()
                .map(Asset::getTotalInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal currentValue = userAssets.stream()
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalProfitLoss = currentValue.subtract(totalInvested);
        BigDecimal totalProfitLossPercentage = totalInvested.compareTo(BigDecimal.ZERO) > 0 
                ? totalProfitLoss.divide(totalInvested, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        Map<String, Object> portfolio = Map.of(
                "totalInvested", totalInvested,
                "currentValue", currentValue,
                "totalProfitLoss", totalProfitLoss,
                "totalProfitLossPercentage", totalProfitLossPercentage,
                "assetCount", userAssets.size()
        );
        dashboard.put("portfolio", portfolio);
        
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        Map<String, Object> summary = new HashMap<>();
        
        // Total balance (current + investment)
        BigDecimal totalBalance = user.getCurrentAccount().getBalance()
                .add(user.getInvestmentAccount().getBalance());
        
        // Asset portfolio value
        List<Asset> userAssets = assetService.getUserAssets(userId);
        BigDecimal portfolioValue = userAssets.stream()
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        summary.put("totalBalance", totalBalance);
        summary.put("currentAccountBalance", user.getCurrentAccount().getBalance());
        summary.put("investmentAccountBalance", user.getInvestmentAccount().getBalance());
        summary.put("portfolioValue", portfolioValue);
        summary.put("totalAssets", userAssets.size());
        
        return ResponseEntity.ok(summary);
    }
} 