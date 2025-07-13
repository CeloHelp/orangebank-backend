package com.orangejuice.orangebank_backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String symbol;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType type;
    
    @Column(nullable = false)
    private String sector;
    
    @Column(name = "current_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(name = "daily_variation", precision = 5, scale = 2)
    private BigDecimal dailyVariation = BigDecimal.ZERO;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;
    
    @Column(name = "average_price", precision = 19, scale = 2)
    private BigDecimal averagePrice = BigDecimal.ZERO;
    
    @Column(name = "total_invested", precision = 19, scale = 2)
    private BigDecimal totalInvested = BigDecimal.ZERO;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
    
    // Constructors
    public Asset() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Asset(String symbol, String name, AssetType type, String sector, BigDecimal currentPrice, User user) {
        this();
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.sector = sector;
        this.currentPrice = currentPrice;
        this.user = user;
    }
    
    // Business methods
    public void buy(Integer quantity, BigDecimal price) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
        
        if (this.quantity == 0) {
            this.averagePrice = price;
        } else {
            BigDecimal totalValue = this.averagePrice.multiply(BigDecimal.valueOf(this.quantity))
                    .add(totalCost);
            this.averagePrice = totalValue.divide(BigDecimal.valueOf(this.quantity + quantity), 2, BigDecimal.ROUND_HALF_UP);
        }
        
        this.quantity += quantity;
        this.totalInvested = this.totalInvested.add(totalCost);
    }
    
    public BigDecimal sell(Integer quantity, BigDecimal price) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (quantity > this.quantity) {
            throw new IllegalArgumentException("Quantidade insuficiente para venda");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        
        BigDecimal totalValue = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal costBasis = this.averagePrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal profit = totalValue.subtract(costBasis);
        
        this.quantity -= quantity;
        this.totalInvested = this.averagePrice.multiply(BigDecimal.valueOf(this.quantity));
        
        if (this.quantity == 0) {
            this.averagePrice = BigDecimal.ZERO;
        }
        
        return profit;
    }
    
    public BigDecimal getCurrentValue() {
        return this.currentPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
    
    public BigDecimal getProfitLoss() {
        return getCurrentValue().subtract(this.totalInvested);
    }
    
    public BigDecimal getProfitLossPercentage() {
        if (this.totalInvested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfitLoss().divide(this.totalInvested, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public AssetType getType() {
        return type;
    }
    
    public void setType(AssetType type) {
        this.type = type;
    }
    
    public String getSector() {
        return sector;
    }
    
    public void setSector(String sector) {
        this.sector = sector;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public BigDecimal getDailyVariation() {
        return dailyVariation;
    }
    
    public void setDailyVariation(BigDecimal dailyVariation) {
        this.dailyVariation = dailyVariation;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getAveragePrice() {
        return averagePrice;
    }
    
    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }
    
    public BigDecimal getTotalInvested() {
        return totalInvested;
    }
    
    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
} 