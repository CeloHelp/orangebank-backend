package com.orangejuice.orangebank_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orangejuice.orangebank_backend.domain.Asset;
import com.orangejuice.orangebank_backend.domain.AssetType;
import com.orangejuice.orangebank_backend.domain.User;
import com.orangejuice.orangebank_backend.repository.AssetRepository;
import com.orangejuice.orangebank_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
                    
                    // Check if user already exists before creating
                    if (!userService.userExists(user.getEmail(), user.getCpf())) {
                        userService.createUser(user);
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
            JsonNode stocksNode = root.get("stocks");
            
            for (JsonNode stockNode : stocksNode) {
                try {
                    String symbol = stockNode.get("symbol").asText();
                    String name = stockNode.get("name").asText();
                    String sector = stockNode.get("sector").asText();
                    BigDecimal currentPrice = new BigDecimal(stockNode.get("currentPrice").asText());
                    BigDecimal dailyVariation = new BigDecimal(stockNode.get("dailyVariation").asText());
                    
                                    // Check if asset already exists
                if (assetRepository.findBySymbol(symbol).size() == 0) {
                    Asset asset = new Asset();
                    asset.setSymbol(symbol);
                    asset.setName(name);
                    asset.setType(AssetType.STOCK);
                    asset.setSector(sector);
                    asset.setCurrentPrice(currentPrice);
                    asset.setDailyVariation(dailyVariation);
                    // Não associar usuário para ativos globais
                    asset.setUser(null);
                    
                    assetRepository.save(asset);
                }
                } catch (Exception e) {
                    System.err.println("Erro ao criar ativo: " + e.getMessage());
                }
            }
            
            System.out.println("Ativos carregados com sucesso!");
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar ativos: " + e.getMessage());
        }
    }
} 