package com.orangejuice.orangebank_backend.repository;

import com.orangejuice.orangebank_backend.domain.Asset;
import com.orangejuice.orangebank_backend.domain.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    
    List<Asset> findByUserId(Long userId);
    
    List<Asset> findByUserIdAndType(Long userId, AssetType type);
    
    Optional<Asset> findByUserIdAndSymbol(Long userId, String symbol);
    
    List<Asset> findBySymbol(String symbol);
    
    List<Asset> findByType(AssetType type);
    
    List<Asset> findBySector(String sector);
} 