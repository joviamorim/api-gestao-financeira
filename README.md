![Java](https://img.shields.io/badge/Java-21-blue)
![Spring](https://img.shields.io/badge/Spring-Boot-green)
![Status](https://img.shields.io/badge/status-active-success)

# 💰 Financial Management API

API REST para gestão financeira pessoal, permitindo controle de transações, categorias e relatórios.

Projeto desenvolvido com foco em boas práticas de backend, incluindo arquitetura em camadas, autenticação JWT, testes automatizados e deploy em nuvem.

---

## 🔗 Links

- 📄 Documentação (Swagger): [<link>](https://api-gestao-financeira.onrender.com/swagger-ui/index.html)
- 💻 Deploy do app: [<link>](https://gestaofinanceira-one.vercel.app/)

---

## 🔐 Como autenticar

1. Faça login em `/auth/login`
2. Copie o token retornado
3. No Swagger, clique em **Authorize**
4. Insira:

Bearer {seu_token}

---

## 📌 Funcionalidades

- Cadastro de usuários
- Autenticação
- Gestão de transações
- Filtros por data
- Relatórios básicos

---

## 🛠️ Tecnologias

- Java 21
- Spring Boot
- Spring Security
- JPA / Hibernate
- JUnit / Mockito
- PostgreSQL
- Docker
- Maven
- Swagger

---

## 🔐 Segurança

- Autenticação via JWT
- Validação de dados com Bean Validation
- Tratamento global de exceções

---

## 🧪 Testes

- Testes unitários com JUnit e Mockito

---

## Como rodar o projeto

### 1. Clone o repositório

```bash
git clone https://github.com/joviamorim/api-gestao-financeira.git
cd api-gestao-financeira
```

---

### 2. Subir o banco de dados

O projeto utiliza **PostgreSQL via Docker**.

```bash
docker compose up -d
```

---

### 3. Rodar a API

```bash
mvn spring-boot:run
```

A API estará disponível em:

```
http://localhost:8080
```

---

## Documentação da API

Após iniciar o projeto, a documentação estará disponível em:

http://localhost:8080/swagger-ui/index.html

---

## Estrutura do projeto

A aplicação segue uma organização baseada em separação por domínio e responsabilidade:

src/main/java/com/financas/projeto
│
├── auth # Autenticação e geração de tokens JWT
├── user # Gestão de usuários
├── transaction # Regras de negócio de transações financeiras
├── category # Gerenciamento de categorias
├── balance # Cálculo e controle de saldo
├── security # Configurações de segurança (Spring Security, filtros JWT)
├── exception # Tratamento global de exceções
├── config # Configurações gerais da aplicação (Swagger, beans, etc.)
├── common # Classes utilitárias e componentes compartilhados
│
└── ProjetoApplication.java # Classe principal da aplicação

---

## 🧠 Decisões técnicas

- Utilizei Spring Boot pela produtividade e ecossistema robusto
- Arquitetura em camadas para separação clara de responsabilidades
- JWT para autenticação stateless, facilitando escalabilidade
- Uso de DTOs para desacoplamento entre camada de domínio e API

---

## 🚀 Melhorias futuras

- Implementação de refresh token
- Controle de acesso por roles (RBAC)
- Testes de integração com Testcontainers
- Cache com Redis
- Rate limiting
- Dashboard analítico
