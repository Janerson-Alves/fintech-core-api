package br.com.jaanalves.fintechcoreapi.dto;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;


public class ContaRequestDTO {
    // Atributos a serem passados
    @NotBlank
    private String titular;

    @NotBlank
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "O CPF deve estar no formato 000.000.000-00") // Valida o formato do CPF
    private String cpf;

    @NotNull
    @DecimalMin(value = "50.00",
            message = "O saldo inicial deve ser maior ou igual a R% 50.00") // Rejeita payloads com valor abaixo do minimo direto na API.
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
