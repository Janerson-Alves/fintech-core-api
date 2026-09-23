package br.com.jaanalves.fintechcoreapi.exceptions;

import br.com.jaanalves.fintechcoreapi.dto.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata exceções de regra de negócio (CPF DUPLICADO, SALDO INICIAL INSUFICIENTE)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handlerIllegalArgument(IllegalArgumentException e,
                                                                HttpServletRequest request)
    {
        // STATUS BAD REQUEST de regra de negocio
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Regra de negócio Violada",
                e.getMessage(),
                request.getRequestURI()
        );
        // retonar a exceção com a mensagem
        return ResponseEntity.status(status).body(err);
    }
    // Trata Exceções do tipo ResponseStatusException(Conta não encontrada - 404, Saldo insuficiente - 400)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<StandardError> handlerResponseStatus(ResponseStatusException e,
                                                               HttpServletRequest request)
    {
        // STATUS 400 - 404
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                e.getReason() != null ? e.getReason() : "Erro de Requisição",
                e.getReason(),
                request.getRequestURI()
        );

        // Retorna a Exceção com a mensagem
        return ResponseEntity.status(status).body(err);
    }
    // trata erros de validação de DTO (CPF em branco, valores negativos)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handlerValidation(MethodArgumentNotValidException e,
                                                           HttpServletRequest request)
    {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Pega a primeira mensagem de erro dos campos anotados
        String erromessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Erro na validação nos campos.");
        // Instancia um erro para mostrar na tela.
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Erro de Validação de Dados",
                erromessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }




}
