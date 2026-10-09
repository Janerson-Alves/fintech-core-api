package br.com.jaanalves.fintechcoreapi.controllers;

import br.com.jaanalves.fintechcoreapi.dto.LoginRequestDTO;
import br.com.jaanalves.fintechcoreapi.dto.LoginResponseDTO;
import br.com.jaanalves.fintechcoreapi.repository.ContaRepository;
import br.com.jaanalves.fintechcoreapi.security.TokenService;
import br.com.jaanalves.fintechcoreapi.services.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Endpoint para geração do token JWT")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ContaRepository contaRepository;

    @Operation(summary = "Realiza Login", description = "Gera o Toke JWT a partir do CPF do titular da conta")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        // Garante que a conta existe para o CPF antes de gerar o token
        contaRepository.existsByCpf(dto.getCpf());
        // Recupera o Token para mostrar na tela
        String token = tokenService.gerarToken(dto.getCpf());
        // Retorna o Token.
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
