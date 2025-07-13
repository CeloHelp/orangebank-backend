package com.orangejuice.orangebank_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OrangeBank API")
                        .description("API de mini banco de investimentos - OrangeBank")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("OrangeBank Team")
                                .email("contato@orangebank.com")
                                .url("https://github.com/CeloHelp/orangebank-backend"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.orangebank.com")
                                .description("Servidor de Produção")
                ));
    }
} 