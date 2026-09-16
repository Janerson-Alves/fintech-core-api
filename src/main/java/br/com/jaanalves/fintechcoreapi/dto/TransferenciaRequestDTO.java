package br.com.jaanalves.fintechcoreapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferenciaRequestDTO {

    @NotBlank(message = "Número da conta de origem é obrigatório")
    private String numeroContaOrigem;

    @NotBlank(message = "Número da conta de destino é obrigatório")
    private String numeroContaDestino;

    @NotNull(message = "Valor da transferência é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor da transferência deve ser maior que zero.")
    private BigDecimal valor;

    // Construtor padrão necessário para a desserialização do JSON
    public TransferenciaRequestDTO() {
    }

    public TransferenciaRequestDTO(String numeroContaOrigem, String numeroContaDestino, BigDecimal valor) {
        this.numeroContaOrigem = numeroContaOrigem;
        this.numeroContaDestino = numeroContaDestino;
        this.valor = valor;
    }

    // GETTERS E SETTERS MANDATÓRIOS
    public String getNumeroContaOrigem() { return numeroContaOrigem; }
    public void setNumeroContaOrigem(String numeroContaOrigem) { this.numeroContaOrigem = numeroContaOrigem; }
    public String getNumeroContaDestino() { return numeroContaDestino; }
    public void setNumeroContaDestino(String numeroContaDestino) { this.numeroContaDestino = numeroContaDestino; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}