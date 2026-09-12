package br.com.jaanalves.fintechcoreapi.dto;

import java.math.BigDecimal;

public class ContaRequestDTO {
    // Atributos a serem passados
    private String titular;
    private String cpf;
    private BigDecimal saldoInicial;

    // Construtor sem parâmetros
    public ContaRequestDTO() {}

    // Construtor com parâmetros
    public ContaRequestDTO(String titular, String cpf, BigDecimal saldoInicial) {
        this.titular = titular;
        this.cpf = cpf;
        this.saldoInicial = saldoInicial;
    }

    // Getters e Setters
    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }
}
