package br.com.jaanalves.fintechcoreapi.controllers;

import br.com.jaanalves.fintechcoreapi.dto.*;
import br.com.jaanalves.fintechcoreapi.enums.StatusConta;
import br.com.jaanalves.fintechcoreapi.services.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Contas", description = "Endpoints para gerenciamento de contas bancárias e movimentações")
@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    // Rotas

    // DOCUMENTA O POST no SWAGGER.
    @Operation(summary = "Criar nova conta", description = "Abre uma nova conta bancária com saldo mínimo de R$ 50.00")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos, ou CPF já cadastrado")
    })
    // POST -> Criar a conta
    @PostMapping
    public ResponseEntity<ContaResponseDTO> criarConta(@Valid @RequestBody ContaRequestDTO dto) {
        // Chama o Metodo de criar a conta e se for com sucesso, ele retorna created 201
        ContaResponseDTO response = contaService.criarConta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET - > Buscar Numero da conta
    @GetMapping("/{numeroConta}")
    public ResponseEntity<ContaResponseDTO> buscarNumeroConta(@PathVariable String numeroConta) {
        // Passa o numero da conta para verificar se existe.
        ContaResponseDTO responseDTO = contaService.buscarPorNumeroConta(numeroConta);
        // Se existir a conta, retorna ok
        return ResponseEntity.ok(responseDTO);
    }

    // PUT -> depositar valores a conta ATIVA.
    @PutMapping("/{numeroConta}/deposito")
    public ResponseEntity<ContaResponseDTO> depositar(@PathVariable String numeroConta,
                                                      @Valid @RequestBody DepositoRequestDTO dto) {
        // Chama o Método para Depositar, se for com sucesso, ele retorna OK.
        ContaResponseDTO deposito = contaService.depositar(numeroConta, dto);
        return ResponseEntity.ok(deposito);
    }

    // POST -> Transferência entre contas
    @PostMapping("/transferencia")
    public ResponseEntity<String> transferencia(@Valid @RequestBody TransferenciaRequestDTO dto) {
        contaService.transferir(dto);
        return ResponseEntity.ok("Transferência realizada com sucesso.");
    }

    // GET -> Extrato da Conta
    @GetMapping("/{numeroConta}/extrato")
    public ResponseEntity<Page<TransacaoResponseDTO>> obterExtrato(
            @PathVariable String numeroConta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime dataFim,
            @PageableDefault(page = 0, size = 10, sort = "dataHora", direction = Sort.Direction.DESC)Pageable pageable) {
        // Lista de extrato filtrado pela conta
        //List<TransacaoResponseDTO> extrato = contaService.obterExtrato(numeroConta);

        // Lista de extrato filtrado pela conta com paginação
        Page<TransacaoResponseDTO> extrato = contaService.obterExtrato(numeroConta, dataInicio, dataFim, pageable);
        // retorna ok com corpo de todas as transações.
        return ResponseEntity.ok(extrato);
    }

    // Alterar Status da conta (ATIVA, BLOQUEADA, DESATIVADA)
    @PatchMapping("/{numeroConta}/status")
    public ResponseEntity<ContaResponseDTO> alterarStatus(@PathVariable Long numeroConta,
                                                          @RequestParam StatusConta novoStatus)
    {
        ContaResponseDTO contaAtualizada = contaService.alterarStatus(numeroConta, novoStatus);
        return ResponseEntity.ok(contaAtualizada);
    }


}
