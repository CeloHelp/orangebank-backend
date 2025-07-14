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
                        .description("<b>API RESTful para o mini banco de investimentos OrangeBank.</b><br><br>" +
                                "Permite cadastro, autenticação, movimentações financeiras, investimentos e muito mais.\n" +
                                "\n<b>Principais recursos:</b>\n<ul>\n" +
                                "<li>Cadastro e autenticação de usuários</li>\n" +
                                "<li>Operações de conta corrente e investimento</li>\n" +
                                "<li>Compra e venda de ativos</li>\n" +
                                "<li>Histórico de transações</li>\n" +
                                "<li>Dashboard financeiro</li>\n" +
                                "</ul>\n" +
                                "\n<b>Documentação interativa disponível no Swagger UI.</b>")
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
                                .url("http://orange-env.eba-iqe4dcr4.us-east-2.elasticbeanstalk.com")
                                .description("Servidor de Produção (AWS)")
                ));
    }
} 