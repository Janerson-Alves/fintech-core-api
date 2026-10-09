package br.com.jaanalves.fintechcoreapi.dto;

public class LoginResponseDTO {
    private String token;
    // Construtor
    public LoginResponseDTO(String token) { this.token = token; }
    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
