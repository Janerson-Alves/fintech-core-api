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

## ⚙️ Regras de Negócio e Serviços

* **Abertura de Conta Digital:** Validação de CPF único e depósito inicial mínimo de R$ 50,00.
* **Geração de Número de Conta:** Algoritmo randômico de 6 dígitos com verificação de colisão.
* **Testes Unitários:** Cobertura das regras do `ContaService` utilizando JUnit 5 e Mockito.
