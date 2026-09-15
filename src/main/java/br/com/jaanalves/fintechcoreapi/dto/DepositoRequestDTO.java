package br.com.jaanalves.fintechcoreapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// Classe de Deposito
public class DepositoRequestDTO {
    // Não pode ser nula e o valor de deposito tem que ser > 0
    @NotNull
    @DecimalMin(value = "0.01", message = "O valor do depósito deve ser maior que zero.")
    private BigDecimal valor;

    // Construtor sem parâmetros
    public DepositoRequestDTO() {}

    // Construtor com parâmetros
    public DepositoRequestDTO(BigDecimal valor) {
        this.valor = valor;
    }

    // Getters e Setters
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
