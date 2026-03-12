# API de Gestão Financeira

API de gestão financeira desenvolvida com **Spring Boot**.

Permite **registro e login de usuários**, criação de **categorias** e controle de **transações financeiras**.

---

## Funcionalidades

* Registro de usuário
* Login com autenticação **JWT**
* CRUD de **transações**
* CRUD de **categorias**
* Autenticação com **Spring Security**
* Documentação automática com **Swagger**

---

## Tecnologias utilizadas

* Java
* Spring Boot
* Spring Security
* JWT
* JPA / Hibernate
* PostgreSQL
* Docker
* Maven

---

## Documentação da API

Após iniciar o projeto, a documentação estará disponível em:

http://localhost:8080/swagger-ui.html

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

## Estrutura do projeto

```
src/main/java/com/financas/projeto
│
├── auth
├── user
├── transaction
├── category
├── config
└── security
```

---

## Melhorias futuras

* Filtros por data e categoria
* Deploy em cloud
* Testes automatizados
