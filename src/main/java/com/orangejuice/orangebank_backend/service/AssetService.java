package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.*;
import com.orangejuice.orangebank_backend.dto.TransactionDTO;
import com.orangejuice.orangebank_backend.repository.AssetRepository;
import com.orangejuice.orangebank_backend.repository.TransactionRepository;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetService {
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    private static final BigDecimal STOCK_FEE_RATE = new BigDecimal("0.01"); // 1%
    private static final BigDecimal STOCK_TAX_RATE = new BigDecimal("0.15"); // 15%
    private static final BigDecimal FIXED_INCOME_TAX_RATE = new BigDecimal("0.22"); // 22%
    
    public TransactionDTO buyAsset(Long userId, String symbol, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Get current asset price (in a real system, this would come from a market data service)
        BigDecimal currentPrice = getCurrentAssetPrice(symbol);
        
        // Calculate total cost including fees
        BigDecimal totalCost = currentPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal feeAmount = totalCost.multiply(STOCK_FEE_RATE);
        BigDecimal totalWithFees = totalCost.add(feeAmount);
        
        // Check if user has enough balance in investment account
        InvestmentAccount investmentAccount = user.getInvestmentAccount();
        if (investmentAccount.getBalance().compareTo(totalWithFees) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente na conta de investimento");
        }
        
        // Deduct amount from investment account
        investmentAccount.buyAsset(totalWithFees);
        
        // Find or create asset for user
        Optional<Asset> existingAsset = assetRepository.findByUserIdAndSymbol(userId, symbol);
        Asset asset;
        
        if (existingAsset.isPresent()) {
            asset = existingAsset.get();
            asset.buy(quantity, currentPrice);
        } else {
            // Create new asset for user
            asset = new Asset(symbol, getAssetName(symbol), AssetType.STOCK, getAssetSector(symbol), currentPrice, user);
            asset.buy(quantity, currentPrice);
        }
        
        assetRepository.save(asset);
        
        // Create transaction record
        Transaction transaction = new Transaction(
                TransactionType.ASSET_PURCHASE,
                totalCost,
                user,
                "Compra de " + quantity + " " + symbol + " a R$ " + currentPrice
        );
        
        transaction.setAsset(asset);
        transaction.setFeeAmount(feeAmount);
        transaction.setNetAmount(totalWithFees);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }
    
    public TransactionDTO sellAsset(Long userId, String symbol, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        Asset asset = assetRepository.findByUserIdAndSymbol(userId, symbol)
                .orElseThrow(() -> new IllegalArgumentException("Ativo não encontrado na carteira"));
        
        if (asset.getQuantity() < quantity) {
            throw new IllegalArgumentException("Quantidade insuficiente para venda");
        }
        
        // Get current asset price
        BigDecimal currentPrice = getCurrentAssetPrice(symbol);
        
        // Calculate sale proceeds
        BigDecimal saleProceeds = currentPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal profit = asset.sell(quantity, currentPrice);
        
        // Calculate taxes
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (profit.compareTo(BigDecimal.ZERO) > 0) {
            taxAmount = profit.multiply(STOCK_TAX_RATE);
        }
        
        BigDecimal netProceeds = saleProceeds.subtract(taxAmount);
        
        // Add proceeds to investment account
        InvestmentAccount investmentAccount = user.getInvestmentAccount();
        investmentAccount.sellAsset(netProceeds);
        
        // Save asset (update quantity)
        assetRepository.save(asset);
        
        // Create transaction record
        Transaction transaction = new Transaction(
                TransactionType.ASSET_SALE,
                saleProceeds,
                user,
                "Venda de " + quantity + " " + symbol + " a R$ " + currentPrice
        );
        
        transaction.setAsset(asset);
        transaction.setTaxAmount(taxAmount);
        transaction.setNetAmount(netProceeds);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }
    
    public List<Asset> getUserAssets(Long userId) {
        return assetRepository.findByUserId(userId);
    }
    
    public Optional<Asset> getUserAsset(Long userId, String symbol) {
        return assetRepository.findByUserIdAndSymbol(userId, symbol);
    }
    
    public List<Asset> getAvailableAssets() {
        // Return only global assets (without user association)
        return assetRepository.findAll().stream()
                .filter(asset -> asset.getUser() == null)
                .collect(Collectors.toList());
    }
    
    private BigDecimal getCurrentAssetPrice(String symbol) {
        // In a real system, this would fetch from a market data service
        // For now, return a mock price based on symbol
        switch (symbol) {
            case "BOIB3": return new BigDecimal("25.50");
            case "BOIN3": return new BigDecimal("18.75");
            case "AGUA3": return new BigDecimal("42.30");
            case "ENER3": return new BigDecimal("35.80");
            case "NUV3": return new BigDecimal("120.45");
            case "CHIP3": return new BigDecimal("95.60");
            case "SOJA3": return new BigDecimal("32.40");
            case "CAFE3": return new BigDecimal("28.90");
            case "LIXO3": return new BigDecimal("15.75");
            case "GAS3": return new BigDecimal("38.20");
            default: return new BigDecimal("50.00");
        }
    }
    
    private String getAssetName(String symbol) {
        // Mock asset names
        switch (symbol) {
            case "BOIB3": return "Boi Bom";
            case "BOIN3": return "Boi Nobre";
            case "AGUA3": return "Água pra Todos";
            case "ENER3": return "Energia BR";
            case "NUV3": return "NuvemCinza";
            case "CHIP3": return "ChipZilla";
            case "SOJA3": return "Soja Brasil";
            case "CAFE3": return "Café Premium";
            case "LIXO3": return "Limpa Cidade";
            case "GAS3": return "Gás Natural";
            default: return "Ativo " + symbol;
        }
    }
    
    private String getAssetSector(String symbol) {
        // Mock sectors
        if (symbol.contains("BOI") || symbol.contains("SOJA") || symbol.contains("CAFE")) {
            return "Agro";
        } else if (symbol.contains("NUV") || symbol.contains("CHIP")) {
            return "Tecnologia";
        } else {
            return "Serviços";
        }
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
        
        if (transaction.getAsset() != null) {
            dto.setAssetSymbol(transaction.getAsset().getSymbol());
        }
        
        return dto;
    }
} 