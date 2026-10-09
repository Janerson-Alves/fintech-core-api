package br.com.jaanalves.fintechcoreapi.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;
    // Construtor
    public LoginRequestDTO() {}
    public LoginRequestDTO(String cpf) { this.cpf = cpf; }
    // Getters e Setters
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
