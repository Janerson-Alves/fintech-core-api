package br.com.jaanalves.fintechcoreapi.controllers;

import br.com.jaanalves.fintechcoreapi.dto.ContaRequestDTO;
import br.com.jaanalves.fintechcoreapi.dto.ContaResponseDTO;
import br.com.jaanalves.fintechcoreapi.dto.DepositoRequestDTO;
import br.com.jaanalves.fintechcoreapi.dto.TransferenciaRequestDTO;
import br.com.jaanalves.fintechcoreapi.services.ContaService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contas")
public class ContaController {
    @Autowired
    private ContaService contaService;

    // Rotas
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


}
