package com.orangejuice.orangebank_backend.service;

import com.orangejuice.orangebank_backend.domain.*;
import com.orangejuice.orangebank_backend.dto.TransactionDTO;
import com.orangejuice.orangebank_backend.repository.AssetRepository;
import com.orangejuice.orangebank_backend.repository.TransactionRepository;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AssetService assetService;

    private User user;
    private InvestmentAccount investmentAccount;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        investmentAccount = new InvestmentAccount();
        investmentAccount.setBalance(new BigDecimal("1000.00"));
        user.setInvestmentAccount(investmentAccount);
    }

    @Test
    void testBuyAsset_Success() {
        String symbol = "BOIB3";
        int quantity = 10;
        BigDecimal price = new BigDecimal("25.50");
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal fee = totalCost.multiply(new BigDecimal("0.01"));
        BigDecimal totalWithFees = totalCost.add(fee);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assetRepository.findByUserIdAndSymbol(1L, symbol)).thenReturn(Optional.empty());
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionDTO dto = assetService.buyAsset(1L, symbol, quantity);

        assertNotNull(dto);
        assertEquals(TransactionType.ASSET_PURCHASE, dto.getType());
        assertEquals(totalCost, dto.getAmount());
        assertEquals(fee, dto.getFeeAmount());
        assertEquals(totalWithFees, dto.getNetAmount());
        verify(assetRepository, times(1)).save(any(Asset.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testBuyAsset_InsufficientBalance() {
        String symbol = "BOIB3";
        int quantity = 1000; // Vai ultrapassar o saldo
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            assetService.buyAsset(1L, symbol, quantity);
        });
        assertEquals("Saldo insuficiente na conta de investimento", ex.getMessage());
    }

    @Test
    void testBuyAsset_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            assetService.buyAsset(99L, "BOIB3", 1);
        });
        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void testSellAsset_Success() {
        String symbol = "BOIB3";
        int quantity = 5;
        BigDecimal price = new BigDecimal("25.50");
        Asset asset = new Asset();
        asset.setSymbol(symbol);
        asset.setQuantity(10);
        asset.setCurrentPrice(price);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assetRepository.findByUserIdAndSymbol(1L, symbol)).thenReturn(Optional.of(asset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionDTO dto = assetService.sellAsset(1L, symbol, quantity);
        assertNotNull(dto);
        assertEquals(TransactionType.ASSET_SALE, dto.getType());
        verify(assetRepository, times(1)).save(any(Asset.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testSellAsset_InsufficientQuantity() {
        String symbol = "BOIB3";
        int quantity = 20; // maior que o disponível
        Asset asset = new Asset();
        asset.setSymbol(symbol);
        asset.setQuantity(5);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assetRepository.findByUserIdAndSymbol(1L, symbol)).thenReturn(Optional.of(asset));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            assetService.sellAsset(1L, symbol, quantity);
        });
        assertEquals("Quantidade insuficiente para venda", ex.getMessage());
    }

    @Test
    void testSellAsset_AssetNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assetRepository.findByUserIdAndSymbol(1L, "BOIB3")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            assetService.sellAsset(1L, "BOIB3", 1);
        });
        assertEquals("Ativo não encontrado na carteira", ex.getMessage());
    }

    @Test
    void testGetUserAssets() {
        Asset asset1 = new Asset();
        asset1.setSymbol("BOIB3");
        Asset asset2 = new Asset();
        asset2.setSymbol("NUV3");
        when(assetRepository.findByUserId(1L)).thenReturn(java.util.List.of(asset1, asset2));
        var result = assetService.getUserAssets(1L);
        assertEquals(2, result.size());
        assertEquals("BOIB3", result.get(0).getSymbol());
        assertEquals("NUV3", result.get(1).getSymbol());
    }

    @Test
    void testGetUserAsset() {
        Asset asset = new Asset();
        asset.setSymbol("BOIB3");
        when(assetRepository.findByUserIdAndSymbol(1L, "BOIB3")).thenReturn(Optional.of(asset));
        var result = assetService.getUserAsset(1L, "BOIB3");
        assertTrue(result.isPresent());
        assertEquals("BOIB3", result.get().getSymbol());
    }

    @Test
    void testGetAvailableAssets() {
        Asset asset1 = new Asset();
        asset1.setSymbol("BOIB3");
        asset1.setUser(null);
        Asset asset2 = new Asset();
        asset2.setSymbol("NUV3");
        asset2.setUser(null);
        Asset asset3 = new Asset();
        asset3.setSymbol("USER1");
        asset3.setUser(new User()); // não deve aparecer
        when(assetRepository.findAll()).thenReturn(java.util.List.of(asset1, asset2, asset3));
        var result = assetService.getAvailableAssets();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getSymbol().equals("BOIB3")));
        assertTrue(result.stream().anyMatch(a -> a.getSymbol().equals("NUV3")));
    }
} 