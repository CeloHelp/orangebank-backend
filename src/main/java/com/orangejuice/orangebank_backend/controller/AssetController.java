package com.orangejuice.orangebank_backend.controller;

import com.orangejuice.orangebank_backend.domain.Asset;
import com.orangejuice.orangebank_backend.dto.AssetPurchaseRequestDTO;
import com.orangejuice.orangebank_backend.dto.TransactionDTO;
import com.orangejuice.orangebank_backend.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {
    
    @Autowired
    private AssetService assetService;
    
    @GetMapping("/available")
    public ResponseEntity<List<Asset>> getAvailableAssets() {
        List<Asset> assets = assetService.getAvailableAssets();
        return ResponseEntity.ok(assets);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<Asset>> getUserAssets(@PathVariable Long userId) {
        List<Asset> assets = assetService.getUserAssets(userId);
        return ResponseEntity.ok(assets);
    }
    
    @GetMapping("/{userId}/{symbol}")
    public ResponseEntity<Asset> getUserAsset(@PathVariable Long userId, @PathVariable String symbol) {
        Optional<Asset> asset = assetService.getUserAsset(userId, symbol);
        return asset.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{userId}/buy")
    public ResponseEntity<TransactionDTO> buyAsset(
            @PathVariable Long userId,
            @Valid @RequestBody AssetPurchaseRequestDTO request) {
        try {
            TransactionDTO transaction = assetService.buyAsset(userId, request.getSymbol(), request.getQuantity());
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{userId}/sell")
    public ResponseEntity<TransactionDTO> sellAsset(
            @PathVariable Long userId,
            @Valid @RequestBody AssetPurchaseRequestDTO request) {
        try {
            TransactionDTO transaction = assetService.sellAsset(userId, request.getSymbol(), request.getQuantity());
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 