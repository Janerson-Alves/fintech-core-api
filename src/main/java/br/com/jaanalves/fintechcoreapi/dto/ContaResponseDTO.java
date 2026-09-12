package br.com.jaanalves.fintechcoreapi.dto;

import br.com.jaanalves.fintechcoreapi.enums.StatusConta;
import jakarta.persistence.*;

import java.math.BigDecimal;

public class ContaResponseDTO {

    private Long id;
    private String titular;
    private String cpf;
    private String numeroConta;
    private BigDecimal saldo;
    private StatusConta status;

    // Construtor sem parametros
    public ContaResponseDTO() {}

    // Construtor com Parâmetros
    public ContaResponseDTO(Long id, String titular, String cpf, String numeroConta,
                            BigDecimal saldo, StatusConta status) {
        this.id = id;
        this.titular = titular;
        this.cpf = cpf;
        this.numeroConta = numeroConta;
        this.saldo = saldo;
        this.status = status;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public StatusConta getStatus() { return status; }
    public void setStatus(StatusConta status) { this.status = status; }
}
