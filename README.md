# 🏦 Fintech Core API

API RESTful para gestão de contas digitais e processamento de transações financeiras de alta performance, desenvolvida em Java 21 com Spring Boot 3.

---

## 🛠️ Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3.x** (Spring Data JPA, Spring Web, Validation)
* **PostgreSQL** (Via Docker Compose)
* **Hibernate / JPA**
* **Maven**

---

## 🛢️ Infraestrutura e Modelo de Dados

* **Docker Compose:** Ambiente local com PostgreSQL containerizado mapeado para a porta `5435`.
* **Entidade Conta (`tb_conta`):** Mapeamento JPA com chave primária auto-incrementável, atributos cadastrais (titular, número da conta, CPF, saldo) e o enum `StatusConta` (`ATIVA`, `BLOQUEADA`, `ENCERRADA`) persistido como String.

---
