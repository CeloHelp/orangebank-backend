# 🍊 OrangeJuiceBank - Backend API

Este é o backend da aplicação **OrangeJuiceBank**, desenvolvido para o hackathon da comunidade Orange Juice Tech. A aplicação simula um mini banco de investimentos com operações financeiras completas.

---

## 🧪 Testes e Cobertura

### Rodando os testes
```bash
./gradlew test
```

### Gerando o relatório de cobertura (JaCoCo)
```bash
./gradlew test jacocoTestReport
```
O relatório HTML será gerado em:
```
build/jacocoHtml/index.html
```
Abra esse arquivo no navegador para visualizar a cobertura detalhada.

**Cobertura da camada de service:**
- ✅ **85% de cobertura de código** (meta superada!)
- TransactionService: 98%
- CustomUserDetailsService: 100%
- UserService: 81%
- DataLoaderService: 91%
- AuthService: 100%

---

## 📖 Swagger / OpenAPI

A documentação interativa da API está disponível em:
```
http://localhost:8080/swagger-ui.html
```

---

## 🚀 Funcionalidades Implementadas

### ✅ Funcionalidades Obrigatórias
- [x] **Criar contas de usuários** - Sistema completo de cadastro
- [x] **Consultar saldo** - Saldos de conta corrente e investimento
- [x] **Depósitos e saques** - Operações na conta corrente
- [x] **Transferências** - Internas (entre contas do mesmo usuário) e externas (entre usuários)
- [x] **Investir em ativos** - Compra e venda de ações fictícias
- [x] **Cálculo de taxas e tributos** - Taxas de transferência (0.5%) e corretagem (1%), impostos sobre rendimentos

### 🎯 Funcionalidades Extras
- [x] **Dashboard completo** - Resumo financeiro do usuário
- [x] **Histórico de transações** - Todas as movimentações financeiras
- [x] **Portfólio de investimentos** - Acompanhamento de ativos
- [x] **Cálculo de lucro/prejuízo** - Análise de performance dos investimentos
- [x] **Dados mock pré-carregados** - 10 usuários e 30+ ações fictícias

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **H2 Database** (banco em memória)
- **Gradle**
- **Jackson** (JSON processing)

## 📋 Pré-requisitos

- Java 17 ou superior
- Gradle 7.0 ou superior

## 🚀 Como Executar

### 1. Clone o repositório
```bash
git clone <seu-repositorio>
cd orangebank-backend
```

### 2. Execute a aplicação
```bash
./gradlew bootRun
```

### 3. Acesse a aplicação
- **API Base URL**: http://localhost:8080/api
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:orangebank`
  - Username: `sa`
  - Password: `password`

## 🌐 Deploy em Produção (AWS Elastic Beanstalk)

A aplicação está disponível publicamente em:

- **URL base:**  
  [http://orange-env.eba-iqe4dcr4.us-east-2.elasticbeanstalk.com](http://orange-env.eba-iqe4dcr4.us-east-2.elasticbeanstalk.com)

### Documentação Swagger
- [Swagger UI](http://orange-env.eba-iqe4dcr4.us-east-2.elasticbeanstalk.com/swagger-ui.html)

### Endpoints principais (produção)

- **Cadastro de usuário:**  
  `POST /api/auth/register`
- **Login:**  
  `POST /api/auth/login`

#### Exemplo de requisição para cadastro de usuário (produção):
```json
{
  "name": "João da Silva",
  "email": "joao@email.com",
  "cpf": "12345678900",
  "birthDate": "1990-01-01",
  "password": "senha123"
}
```

#### Testando endpoints em produção
Você pode testar os endpoints diretamente pelo Swagger UI ou usando ferramentas como Postman/curl:

```bash
curl -X POST http://orange-env.eba-iqe4dcr4.us-east-2.elasticbeanstalk.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João da Silva",
    "email": "joao@email.com",
    "cpf": "12345678900",
    "birthDate": "1990-01-01",
    "password": "senha123"
  }'
```

---

## 📚 Documentação da API

### 👥 Usuários

#### Listar todos os usuários
```http
GET /api/users
```

#### Buscar usuário por ID
```http
GET /api/users/{id}
```

#### Buscar usuário por email/CPF
```http
GET /api/users/search?emailOrCpf={emailOuCpf}
```

#### Criar novo usuário
```http
POST /api/users
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "cpf": "123.456.789-00",
  "birthDate": "1990-01-15"
}
```

#### Consultar saldos do usuário
```http
GET /api/users/{id}/balances
```

### 💰 Transações

#### Realizar depósito
```http
POST /api/transactions/{userId}/deposit
Content-Type: application/json

{
  "amount": 1000.00
}
```

#### Realizar saque
```http
POST /api/transactions/{userId}/withdraw
Content-Type: application/json

{
  "amount": 500.00
}
```

#### Realizar transferência
```http
POST /api/transactions/{userId}/transfer
Content-Type: application/json

{
  "amount": 200.00,
  "transferType": "INTERNAL"
}
```

#### Transferência externa
```http
POST /api/transactions/{userId}/transfer
Content-Type: application/json

{
  "amount": 300.00,
  "transferType": "EXTERNAL",
  "destinationAccountNumber": "123456"
}
```

#### Consultar transações do usuário
```http
GET /api/transactions/{userId}
```

### 📈 Ativos

#### Listar ativos disponíveis
```http
GET /api/assets/available
```

#### Consultar ativos do usuário
```http
GET /api/assets/{userId}
```

#### Comprar ativo
```http
POST /api/assets/{userId}/buy
Content-Type: application/json

{
  "symbol": "BOIB3",
  "quantity": 10
}
```

#### Vender ativo
```http
POST /api/assets/{userId}/sell
Content-Type: application/json

{
  "symbol": "BOIB3",
  "quantity": 5
}
```

### 📊 Dashboard

#### Dashboard completo do usuário
```http
GET /api/dashboard/{userId}
```

#### Resumo financeiro
```http
GET /api/dashboard/{userId}/summary
```

## 💡 Regras de Negócio Implementadas

### 💳 Tipos de Conta
- **Conta Corrente**: Para movimentações diárias (depósitos, saques, transferências)
- **Conta Investimento**: Exclusiva para operações de compra e venda de ativos

### 💸 Taxas e Tributos
- **Transferências externas**: 0.5% do valor transferido
- **Corretagem de ações**: 1% do valor da operação
- **Imposto de Renda sobre rendimentos**:
  - Ações: 15% sobre o lucro
  - Renda fixa: 22% sobre o rendimento

### 🔄 Transferências
- **Internas**: Entre contas do mesmo usuário (sem taxa)
- **Externas**: Entre usuários diferentes (com taxa de 0.5%)

### 📈 Ativos
- **Ações fictícias**: 30+ ações disponíveis
- **Preços dinâmicos**: Simulação de variação de preços
- **Cálculo de preço médio**: Para múltiplas compras do mesmo ativo

## 🧪 Dados de Teste

A aplicação já vem com dados mock pré-carregados:

### 👥 Usuários (10 usuários)
- João Silva (joao.silva@email.com)
- Maria Santos (maria.santos@email.com)
- Pedro Oliveira (pedro.oliveira@email.com)
- E mais 7 usuários...

### 📈 Ações Disponíveis (30+ ações)
- **Agro**: BOIB3, BOIN3, SOJA3, CAFE3, etc.
- **Tecnologia**: NUV3, CHIP3, SOFT3, DADOS3, etc.
- **Serviços**: AGUA3, ENER3, LIXO3, GAS3, etc.

## 🏗️ Arquitetura

```
src/main/java/com/orangejuice/orangebank_backend/
├── controller/          # Controllers REST
├── domain/             # Entidades JPA
├── dto/                # Data Transfer Objects
├── repository/         # Repositórios JPA
└── service/            # Lógica de negócio
```

### 📁 Estrutura de Pacotes
- **Controller**: Expõe endpoints REST
- **Domain**: Entidades do domínio (User, Account, Transaction, Asset)
- **DTO**: Objetos de transferência de dados
- **Repository**: Acesso a dados
- **Service**: Regras de negócio

## 🚀 Próximos Passos

Para completar o desafio, você pode:

1. **Desenvolver o Frontend**:
   - Interface web responsiva
   - Dashboard interativo
   - Formulários de operações

2. **Funcionalidades Extras**:
   - Simulação de mercado em tempo real
   - Notificações de preços
   - Relatórios detalhados
   - Gamificação

3. **Melhorias Técnicas**:
   - Autenticação e autorização
   - Testes unitários e de integração
   - Documentação com Swagger
   - Deploy em cloud

## 📝 Licença

Este projeto foi desenvolvido para o hackathon da Orange Juice Tech.

---

**🍊 OrangeJuiceBank** - Transformando investimentos em realidade! 🚀 

## ⚙️ CI/CD Automático

A aplicação possui **deploy automático (CI/CD)** configurado com GitHub Actions:
- A cada push na branch `main`, o pipeline executa testes, build e faz o deploy automático para a AWS Elastic Beanstalk.
- O workflow está em `.github/workflows/deploy.yml`.
- As credenciais de deploy são gerenciadas com secrets do GitHub.

### Link de produção atualizado
- **Acesse a API em produção:**
  [http://orange-env.eba-iqe4dcr4.us-east-2.elasticbeanstalk.com](http://orange-env.eba-iqe4dcr4.us-east-2.elasticbeanstalk.com)
- **Swagger UI:**
  [http://orange-env.eba-iqe4dcr4.us-east-2.elasticbeanstalk.com/swagger-ui.html](http://orange-env.eba-iqe4dcr4.us-east-2.elasticbeanstalk.com/swagger-ui.html)

--- 