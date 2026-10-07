package br.com.jaanalves.fintechcoreapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Serviço responsável pelo JWT
@Service
public class TokenService {

    // Chave secreta do JWT
    @Value("${api.security.token.secret:MinhaChaveSecretaSuperSeguraParaFintechCoreApi123456}")
    private String secret;

    // Cria a chave de assinatura
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Gera um token para o CPF
    public String gerarToken(String cpf) {
        long expiracaoMillis = 2 * 60 * 60 * 1000; // Expira em 2 horas

        return Jwts.builder()
                .subject(cpf)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracaoMillis))
                .signWith(getSigningKey())
                .compact();
    }

    // Valida o token e retorna o CPF
    public String validarToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();

        } catch (Exception e) {
            return null; // Token inválido
        }
    }
}
