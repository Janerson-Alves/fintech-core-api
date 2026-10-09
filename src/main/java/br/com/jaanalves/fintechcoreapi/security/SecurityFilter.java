package br.com.jaanalves.fintechcoreapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Recupera o Token
        var token = recuperarToken(request);

        // Verifica se o Token Não e nulo
        if (token != null) {
            var cpf = tokenService.validarToken(token);

            // Verifica se o CPF nao e nulo
            if (cpf != null) {
                // Se o Token e valido, cria o objeto de autenticação sem senhas/autorização por enquanto
                var authentication = new UsernamePasswordAuthenticationToken(cpf, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        // Pega o Token gerado Após o Authorization
        var authHeader = request.getHeader("Authorization");
        // Verifica se o Authorization está vazio ou que o filtro não seja Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        // Altera a nomeclatura pegando somente o token
        return authHeader.replace("Bearer ", "");
    }
}
