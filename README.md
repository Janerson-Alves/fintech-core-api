# 🏦 Fintech Core API

API RESTful para gestão de contas digitais e processamento de transações financeiras de alta performance, desenvolvida em Java 21 com Spring Boot 3.

---

## 🛠️ Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3.x** (Spring Data JPA, Spring Web, Bean Validation)
* **PostgreSQL** (Via Docker Compose)
* **Hibernate / JPA**
* **JUnit 5 & Mockito**
* **Maven**

---

## 🛢️ Infraestrutura e Modelo de Dados

* **Docker Compose:** Ambiente local com PostgreSQL containerizado mapeado para a porta `5435`.
* **Entidade Conta (`tb_conta`):** Mapeamento JPA com chave primária auto-incrementável, atributos cadastrais (titular, número da conta, CPF, saldo) e o enum `StatusConta` (`ATIVA`, `BLOQUEADA`, `ENCERRADA`) persistido como String.

---

## ⚙️ Regras de Negócio e Serviços

* **Abertura de Conta Digital:** Validação de CPF único e depósito inicial mínimo de R$ 50,00.
* **Geração de Número de Conta:** Algoritmo randômico de 6 dígitos com verificação de colisão no banco de dados.
* **Operação de Depósito:** Acréscimo atômico de saldo via `@Transactional` com validação de status de conta `ATIVA`.
* **Testes Unitários:** Cobertura de regras de negócio de abertura, busca e depósito com JUnit 5 e Mockito.

---



## 📍 Endpoints da API

### Contas (`/api/contas`)
* `POST /api/contas` - Realiza a abertura de uma nova conta digital (Exige titular, CPF válido e saldo inicial $\ge 50.00$). Retorna `201 Created`.
* `GET /api/contas/{numeroConta}` - Consulta os dados cadastrais e saldo da conta pelo número de 6 dígitos. Retorna `200 OK`.
* `PUT /api/contas/{numeroConta}/deposito` - Realiza o depósito de valores em uma conta ativa. Retorna `200 OK`.

### 💸 Operações Financeiras
* **POST `/api/contas/transferencia`:** Realiza transferência de saldo entre contas ativas. Requer payload com `numeroContaOrigem`, `numeroContaDestino` e `valor`.