# payments-api

API REST para controle de despesas pessoais, organizadas por categorias.
Cada usuário se cadastra, faz login (recebendo um token JWT) e gerencia suas
próprias categorias e as transações dentro de cada categoria.

> Projeto de estudo do ciclo completo de desenvolvimento Java com Spring Boot:
> desenvolvimento, testes, cobertura, documentação, containerização e deploy.

## Tecnologias

| Camada          | Tecnologia                                         |
|-----------------|----------------------------------------------------|
| Linguagem       | Java 21                                            |
| Framework       | Spring Boot 4.1.2 (Web MVC, Actuator, Validation)  |
| Persistência    | PostgreSQL + JdbcTemplate                          |
| Migrações       | Flyway                                             |
| Autenticação    | JWT (jjwt 0.12.6) + senhas com BCrypt              |
| Documentação    | OpenAPI 3 com springdoc-openapi 3.2 (Swagger UI)   |
| Testes          | JUnit 5 + Mockito                                  |
| Cobertura       | Jacoco                                             |
| Build           | Maven (wrapper `mvnw` incluso)                     |
| Container       | Docker (multi-stage, eclipse-temurin 21)           |

## Arquitetura

O código segue uma arquitetura em camadas (`src/main/java/com/galimberti/paymentsapi`):

```
resources/     → Controllers REST (entrada HTTP)
dto/           → Objetos de entrada das requisições, com validação (Bean Validation)
services/      → Regras de negócio (interface + implementação)
repositories/  → Acesso a dados com JdbcTemplate (interface + implementação)
domain/        → Entidades: User, Category, Transaction
config/        → Propriedades do JWT e configuração do OpenAPI
exceptions/    → Exceções mapeadas para status HTTP (400, 401, 404)
filters/       → AuthFilter: valida o token JWT nas rotas protegidas
```

## Pré-requisitos

- **JDK 21**
- **PostgreSQL** (porta padrão `5432`)
- **psql** (cliente de linha de comando do PostgreSQL)
- Maven **não** é necessário: use o wrapper `./mvnw` (Linux/macOS) ou `mvnw.cmd` (Windows)

## Montando o ambiente

### 1. Clonar o repositório

```bash
git clone https://github.com/gugagalimberti/payments-api.git
cd payments-api
```

### 2. Criar o banco de dados

O script `paymentsapi_db.sql` cria o usuário, o banco, as tabelas e as sequences.
Execute-o com um usuário administrador do PostgreSQL:

```bash
psql -U postgres -f paymentsapi_db.sql
```

> ⚠️ O script começa com `drop database` / `drop user`: **ele apaga o banco
> existente**. Na primeira execução, os `drop` exibirão erros por não haver
> o que apagar; isso é esperado.

Estrutura criada:

| Tabela            | Descrição                                   |
|-------------------|---------------------------------------------|
| `pa_users`        | Usuários (senha armazenada com hash BCrypt) |
| `pa_categories`   | Categorias de despesa de cada usuário       |
| `pa_transactions` | Transações vinculadas a uma categoria       |

### 3. Variáveis de ambiente

Os dados sensíveis **não ficam no código**: são lidos de variáveis de ambiente
pelo `application.yaml`.

| Variável          | Obrigatória | Padrão                                           | Descrição                                   |
|-------------------|:-----------:|--------------------------------------------------|---------------------------------------------|
| `JWT_SECRET`      | ✅          | —                                                | Chave de assinatura do JWT (Base64, mínimo 32 bytes) |
| `DB_PASSWORD`     | ✅          | —                                                | Senha do usuário do banco                   |
| `DB_URL`          |             | `jdbc:postgresql://localhost:5432/paymentsapidb` | URL JDBC do banco                           |
| `DB_USERNAME`     |             | `paymentsapi`                                    | Usuário do banco                            |
| `JWT_VALIDITY_MS` |             | `7200000` (2 horas)                              | Validade do token, em milissegundos         |

Sem as variáveis obrigatórias, a aplicação **não sobe**. Isso é intencional
(*fail fast*).

Para gerar uma chave para o `JWT_SECRET`:

```bash
openssl rand -base64 32
```

No PowerShell:

```powershell
$b = New-Object byte[] 32; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b)
```

### 4. Executar a aplicação

Linux/macOS:

```bash
export JWT_SECRET="<chave-gerada>"
export DB_PASSWORD="password"
./mvnw spring-boot:run
```

Windows (PowerShell):

```powershell
$env:JWT_SECRET = "<chave-gerada>"
$env:DB_PASSWORD = "password"
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.
Para verificar se está no ar: `GET http://localhost:8080/actuator/health`.

## Documentação da API (Swagger)

Com a aplicação em execução:

| O quê                     | URL                                        |
|---------------------------|--------------------------------------------|
| Swagger UI (interativo)   | http://localhost:8080/swagger-ui.html      |
| Especificação OpenAPI (JSON) | http://localhost:8080/v3/api-docs       |
| Especificação OpenAPI (YAML) | http://localhost:8080/v3/api-docs.yaml  |

O Swagger UI lista todos os endpoints, com os campos de cada requisição,
exemplos e os códigos de resposta possíveis. Também permite testá-los direto
do navegador:

1. Execute `POST /api/users/login` em **Try it out** e copie o `token` da resposta.
2. Clique em **Authorize** 🔒 e cole **somente o token** (sem a palavra `Bearer`).
3. Os endpoints protegidos (com cadeado) passam a enviar o token automaticamente.

## Docker

A aplicação pode ser executada em um container, sem precisar de JDK instalado.

### Construir a imagem

```bash
docker build -t payments-api:0.2 .
```

O `Dockerfile` usa **multi-stage build**: um estágio com JDK compila o projeto,
e a imagem final contém apenas o JRE e o `.jar` (~146 MB compactada).
A aplicação roda com um usuário sem privilégios de root.

> Os testes **não** rodam durante o build da imagem (`-DskipTests`), pois o teste
> de contexto depende do banco. Rode `./mvnw verify` antes de gerar a imagem.

### Executar o container

Crie um arquivo `.env` na raiz (ele está no `.gitignore` e **não** deve ser versionado):

```
JWT_SECRET=<chave-gerada>
DB_PASSWORD=password
DB_URL=jdbc:postgresql://host.docker.internal:5432/paymentsapidb
```

E execute:

```bash
docker run --rm -p 8080:8080 --env-file .env payments-api:0.2
```

## Autenticação

1. Cadastre-se em `POST /api/users/register` ou faça login em `POST /api/users/login`.
2. A resposta traz um token: `{ "token": "eyJhbGciOi..." }`
3. Envie o token em todas as rotas de `/api/categories/**`:

```
Authorization: Bearer <token>
```

Requisições sem token, ou com token inválido/expirado, recebem
**401 Unauthorized**, com o cabeçalho `WWW-Authenticate: Bearer`.

## Endpoints

A documentação completa (campos, validações e exemplos) está no **Swagger UI**.
Resumo:

### Usuários (públicos)

| Método | Rota                  | Corpo                                          | Respostas       |
|--------|-----------------------|------------------------------------------------|-----------------|
| POST   | `/api/users/register` | `firstName`, `lastName`, `email`, `password`   | 200 · 400 · 401 |
| POST   | `/api/users/login`    | `email`, `password`                            | 200 · 400 · 401 |

### Categorias (requer token)

| Método | Rota                           | Corpo                  | Respostas             |
|--------|--------------------------------|------------------------|-----------------------|
| GET    | `/api/categories`              | —                      | 200                   |
| GET    | `/api/categories/{categoryId}` | —                      | 200 · 404             |
| POST   | `/api/categories`              | `title`, `description` | 201 · 400             |
| PUT    | `/api/categories/{categoryId}` | `title`, `description` | 200 · 400             |
| DELETE | `/api/categories/{categoryId}` | —                      | 200 · 404 (remove também as transações da categoria) |

### Transações (requer token)

Base: `/api/categories/{categoryId}/transactions`

| Método | Rota                 | Corpo                                 | Respostas        |
|--------|----------------------|---------------------------------------|------------------|
| GET    | `/`                  | —                                     | 200              |
| GET    | `/{transactionId}`   | —                                     | 200 · 404        |
| POST   | `/`                  | `amount`, `note`, `transactionDate`   | 201 · 400 · 404  |
| PUT    | `/{transactionId}`   | `amount`, `note`, `transactionDate`   | 200 · 400 · 404  |
| DELETE | `/{transactionId}`   | —                                     | 200 · 404        |

Todas as rotas protegidas também podem responder **401** (token ausente, inválido ou expirado).

`transactionDate` é um timestamp em **milissegundos** (epoch), ex.: `1577817000000`.

### Validação de entrada

Os corpos das requisições são validados antes de chegar às regras de negócio.
Campos ausentes, vazios, com formato inválido ou acima do tamanho permitido
retornam **400 Bad Request**, indicando qual campo falhou. Os limites
acompanham as colunas do banco:

| Campo                      | Regra                                      |
|----------------------------|--------------------------------------------|
| `firstName`, `lastName`    | obrigatórios, até 20 caracteres            |
| `email`                    | obrigatório, formato de e-mail, até 30 caracteres |
| `password`                 | obrigatória                                |
| `title`                    | obrigatório, até 20 caracteres             |
| `description`, `note`      | obrigatórios, até 50 caracteres            |
| `amount`                   | obrigatório, positivo, até 8 dígitos inteiros e 2 decimais |
| `transactionDate`          | obrigatório                                |

### Exemplo

```bash
# Login
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@exemplo.com","password":"123456"}'

# Criar categoria
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Mercado","description":"Compras do mês"}'

# Criar transação na categoria 1
curl -X POST http://localhost:8080/api/categories/1/transactions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"amount":150.90,"note":"Supermercado","transactionDate":1577817000000}'
```

## Testes e cobertura

```bash
./mvnw test      # executa os testes
./mvnw verify    # executa os testes e gera o relatório de cobertura
```

Relatório do Jacoco: `target/site/jacoco/index.html`

| Suíte                           | O que cobre                                       |
|---------------------------------|---------------------------------------------------|
| `UserServiceImplTest`           | Cadastro e login (validação de e-mail, duplicidade) |
| `CategoryServiceImplTest`       | CRUD de categorias e remoção em cascata           |
| `TransactionServiceImplTest`    | CRUD de transações e validação da categoria       |
| `PaymentsApiApplicationTests`   | Inicialização do contexto completo do Spring      |

Os testes unitários usam mocks dos repositórios (Mockito) e **não dependem do
banco**. A camada de serviços tem 100% de cobertura de linhas.

> O teste `PaymentsApiApplicationTests` sobe o contexto completo e, por isso,
> precisa do PostgreSQL em execução e da variável `DB_PASSWORD` definida.
> O `jwt.secret` já é fornecido pelo próprio teste.

## Roteiro do projeto

- [x] Desenvolvimento (User, Category, Transaction)
- [x] Testes manuais com Postman
- [x] Testes unitários
- [x] Cobertura com Jacoco
- [x] Documentação OpenAPI (Swagger)
- [x] Dockerfile da aplicação
- [ ] Execução em container / Kubernetes

## Pontos de melhoria conhecidos

- **Status HTTP do cadastro:** `POST /api/users/register` responde **200** (o
  esperado para criação é **201 Created**). Os erros de cadastro respondem
  **401**: e-mail inválido deveria ser **400**, e e-mail já cadastrado, **409 Conflict**.
- **Atualização de categoria inexistente:** `PUT /api/categories/{id}` responde
  200 mesmo quando a categoria não existe. O repositório não verifica a
  quantidade de linhas afetadas, como já faz o de transações.
- **Tratamento genérico de erros nos repositórios:** os blocos `catch (Exception e)`
  convertem qualquer falha (inclusive banco indisponível) em 400/404, em vez de 500.
- **Migrações:** o schema ainda é criado pelo script manual `paymentsapi_db.sql`.
  O Flyway está configurado, mas ainda não há migrações em `db/migration`.
- **Testes de integração:** repositories, controllers e o `AuthFilter` ainda
  não têm testes automatizados (são cobertos apenas pelos testes manuais).
- **Teste de contexto depende do ambiente:** `PaymentsApiApplicationTests` exige
  PostgreSQL rodando e `DB_PASSWORD` definido. O ideal é usar Testcontainers
  para subir um banco descartável durante os testes.