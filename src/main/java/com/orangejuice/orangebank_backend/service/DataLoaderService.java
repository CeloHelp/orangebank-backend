package com.orangejuice.orangebank_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orangejuice.orangebank_backend.domain.Asset;
import com.orangejuice.orangebank_backend.domain.AssetType;
import com.orangejuice.orangebank_backend.domain.CurrentAccount;
import com.orangejuice.orangebank_backend.domain.InvestmentAccount;
import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.repository.AssetRepository;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class DataLoaderService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private String generateAccountNumber() {
        // Gerar número de conta aleatório (formato: 12345678-9)
        int accountNumber = (int) (Math.random() * 90000000) + 10000000;
        int digit = (int) (Math.random() * 10);
        return accountNumber + "-" + digit;
    }
    
    @PostConstruct
    public void loadInitialData() {
        try {
            if (userRepository.count() == 0) {
                loadUsers();
            }
            
            if (assetRepository.count() == 0) {
                loadAssets();
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados iniciais: " + e.getMessage());
        }
    }
    
    private void loadUsers() {
        try {
            ClassPathResource resource = new ClassPathResource("users-mock.json");
            JsonNode root = objectMapper.readTree(resource.getInputStream());
            JsonNode usersNode = root.get("users");
            
            for (JsonNode userNode : usersNode) {
                try {
                    User user = new User();
                    user.setName(userNode.get("name").asText());
                    user.setEmail(userNode.get("email").asText());
                    user.setCpf(userNode.get("cpf").asText());
                    
                    String birthDateStr = userNode.get("birthDate").asText();
                    LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                    user.setBirthDate(birthDate);
                    
                    // Definir senha padrão para usuários mock
                    user.setPassword(passwordEncoder.encode("123456"));
                    
                    // Check if user already exists before creating
                    if (!userService.userExists(user.getEmail(), user.getCpf())) {
                        // Criar contas bancárias
                        CurrentAccount currentAccount = new CurrentAccount();
                        currentAccount.setAccountNumber(generateAccountNumber());
                        currentAccount.setBalance(BigDecimal.ZERO);
                        currentAccount.setUser(user);
                        user.setCurrentAccount(currentAccount);
                        
                        InvestmentAccount investmentAccount = new InvestmentAccount();
                        investmentAccount.setAccountNumber(generateAccountNumber());
                        investmentAccount.setBalance(BigDecimal.ZERO);
                        investmentAccount.setUser(user);
                        user.setInvestmentAccount(investmentAccount);
                        
                        userRepository.save(user);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao criar usuário: " + e.getMessage());
                }
            }
            
            System.out.println("Usuários carregados com sucesso!");
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
        }
    }
    
    private void loadAssets() {
        try {
            ClassPathResource resource = new ClassPathResource("assets-mock.json");
            JsonNode root = objectMapper.readTree(resource.getInputStream());
            // Carregar ações (stocks)
            JsonNode stocksNode = root.get("stocks");
            for (JsonNode stockNode : stocksNode) {
                try {
                    String symbol = stockNode.get("symbol").asText();
                    String name = stockNode.get("name").asText();
                    String sector = stockNode.get("sector").asText();
                    BigDecimal currentPrice = new BigDecimal(stockNode.get("currentPrice").asText());
                    BigDecimal dailyVariation = new BigDecimal(stockNode.get("dailyVariation").asText());
                    if (assetRepository.findBySymbol(symbol).size() == 0) {
                        Asset asset = new Asset();
                        asset.setSymbol(symbol);
                        asset.setName(name);
                        asset.setType(AssetType.STOCK);
                        asset.setSector(sector);
                        asset.setCurrentPrice(currentPrice);
                        asset.setDailyVariation(dailyVariation);
                        asset.setUser(null);
                        assetRepository.save(asset);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao criar ativo: " + e.getMessage());
                }
            }
            // Carregar fundos (funds)
            JsonNode fundsNode = root.get("funds");
            if (fundsNode != null) {
                for (JsonNode fundNode : fundsNode) {
                    try {
                        String symbol = fundNode.get("symbol").asText();
                        String name = fundNode.get("name").asText();
                        String sector = fundNode.get("sector").asText();
                        BigDecimal currentPrice = new BigDecimal(fundNode.get("currentPrice").asText());
                        BigDecimal dailyVariation = new BigDecimal(fundNode.get("dailyVariation").asText());
                        if (assetRepository.findBySymbol(symbol).size() == 0) {
                            Asset asset = new Asset();
                            asset.setSymbol(symbol);
                            asset.setName(name);
                            asset.setType(AssetType.FUND);
                            asset.setSector(sector);
                            asset.setCurrentPrice(currentPrice);
                            asset.setDailyVariation(dailyVariation);
                            asset.setUser(null);
                            assetRepository.save(asset);
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao criar fundo: " + e.getMessage());
                    }
                }
            }
            // Carregar CDBs (cdbs)
            JsonNode cdbsNode = root.get("cdbs");
            if (cdbsNode != null) {
                for (JsonNode cdbNode : cdbsNode) {
                    try {
                        String symbol = cdbNode.get("symbol").asText();
                        String name = cdbNode.get("name").asText();
                        String sector = cdbNode.get("sector").asText();
                        BigDecimal currentPrice = new BigDecimal(cdbNode.get("currentPrice").asText());
                        BigDecimal dailyVariation = new BigDecimal(cdbNode.get("dailyVariation").asText());
                        if (assetRepository.findBySymbol(symbol).size() == 0) {
                            Asset asset = new Asset();
                            asset.setSymbol(symbol);
                            asset.setName(name);
                            asset.setType(AssetType.CDB);
                            asset.setSector(sector);
                            asset.setCurrentPrice(currentPrice);
                            asset.setDailyVariation(dailyVariation);
                            asset.setUser(null);
                            assetRepository.save(asset);
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao criar CDB: " + e.getMessage());
                    }
                }
            }
            // Carregar Tesouro Direto (treasuries)
            JsonNode treasuriesNode = root.get("treasuries");
            if (treasuriesNode != null) {
                for (JsonNode treasuryNode : treasuriesNode) {
                    try {
                        String symbol = treasuryNode.get("symbol").asText();
                        String name = treasuryNode.get("name").asText();
                        String sector = treasuryNode.get("sector").asText();
                        BigDecimal currentPrice = new BigDecimal(treasuryNode.get("currentPrice").asText());
                        BigDecimal dailyVariation = new BigDecimal(treasuryNode.get("dailyVariation").asText());
                        if (assetRepository.findBySymbol(symbol).size() == 0) {
                            Asset asset = new Asset();
                            asset.setSymbol(symbol);
                            asset.setName(name);
                            asset.setType(AssetType.TREASURY);
                            asset.setSector(sector);
                            asset.setCurrentPrice(currentPrice);
                            asset.setDailyVariation(dailyVariation);
                            asset.setUser(null);
                            assetRepository.save(asset);
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao criar Tesouro Direto: " + e.getMessage());
                    }
                }
            }
            System.out.println("Ativos carregados com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao carregar ativos: " + e.getMessage());
        }
    }
} 