package br.com.jaanalves.fintechcoreapi.services;

import br.com.jaanalves.fintechcoreapi.dto.ContaRequestDTO;
import br.com.jaanalves.fintechcoreapi.dto.ContaResponseDTO;
import br.com.jaanalves.fintechcoreapi.dto.DepositoRequestDTO;
import br.com.jaanalves.fintechcoreapi.dto.TransferenciaRequestDTO;
import br.com.jaanalves.fintechcoreapi.entities.Conta;
import br.com.jaanalves.fintechcoreapi.enums.StatusConta;
import br.com.jaanalves.fintechcoreapi.repository.ContaRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.engine.TestExecutionResult;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Integra o Mockito ao JUNIT 5, permitindo annotations
// @Mock e @InjectMocks sejam inicializados automaticamente
@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    // Cria um mock falso para teste que não precise acessar o banco de verdade
    @Mock
    private ContaRepository contaRepository;

    // Cria uma instancia de teste que injeta os mocks necessarios.
    @InjectMocks
    private ContaService contaService;


    // TESTE 1 - Cadastrando uma conta dentro das regras de negócio
    @Test
    void deveCadastrarContaComSucesso() {

        // Instanciando o ContaRequestDTO valido para cadastro
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setTitular("João Mario");
        dto.setCpf("111.222.333-44");
        dto.setSaldoInicial(new BigDecimal("100.00"));

        // Verifica se o CPF ja existe, se não existir, ele retorna false.
        when(contaRepository.existsByCpf(dto.getCpf()))
                .thenReturn(false);
        // Simula o salvamento dos dados no banco, vamos simular esse comportamento colocando o ID 1L
        when(contaRepository.save(any(Conta.class)))
                .thenAnswer(invocation -> {
                    // Recupera a entidade que o service enviou
                    Conta conta = invocation.getArgument(0);
                    // Simula um id
                    conta.setId(1L);
                    // Inserindo a conta como ativa
                    conta.setStatus(StatusConta.ATIVA);
                    // Retorna a entidade simulando o comportamento após o salvamento
                    return conta;
                });

        // ACT
        ContaResponseDTO response = contaService.criarConta(dto);

        // ASSERT
        Assertions.assertNotNull(response);
        Assertions.assertEquals("João Mario", response.getTitular());
        Assertions.assertEquals("111.222.333-44", response.getCpf());
        Assertions.assertEquals(StatusConta.ATIVA, response.getStatus());
        Assertions.assertNotNull(response.getNumeroConta());
        Assertions.assertEquals(6, response.getNumeroConta().length());
        Assertions.assertTrue(response.getNumeroConta().matches("\\d{6}"));

    }

    @Test
    void naoDeveCriarContaComCpfDuplicado() {
        // Instanciando o ContaRequestDTO valido para cadastro
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setTitular("João Mario");
        dto.setCpf("111.222.333-44");
        dto.setSaldoInicial(new BigDecimal("100.00"));

        // Verifica se o CPF ja existe, se não existir, ele retorna false.
        when(contaRepository.existsByCpf(dto.getCpf()))
                .thenReturn(true);

        // Executa o metodo de cadastrar e verificar se ele lança a exceção
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> contaService.criarConta(dto)
        );

        // VEIFY

        // Garante que o metodo SAVE nunca foi chamado
        // pois o cadastro deve ser interrompido quando ele encontra o cpf duplicado
        Mockito.verify(contaRepository, never())
                .save(any());
    }

    @Test
    void naoDeveCriarContaComSaldoInicialMenorQueMinimo() {
        // Instanciando o ContaRequestDTO valido para cadastro
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setTitular("João Mario");
        dto.setCpf("111.222.333-44");
        dto.setSaldoInicial(new BigDecimal("40.00"));

        // Executa o metodo de cadastrar e verificar se ele lança a exceção
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> contaService.criarConta(dto)
        );

        // VEIFY

        // Garante que o metodo SAVE nunca foi chamado
        // pois o cadastro deve ser interrompido quando ele encontra o cpf duplicado
        Mockito.verify(contaRepository, never())
                .save(any());
    }

    // Teste de realizar Deposito
    @Test
    void deveRealizarDepositoComSucesso() {
        // Instanciando uma nova conta
        Conta conta = new Conta();
        conta.setId(1L);
        conta.setTitular("Joao Mario");
        conta.setCpf("111.222.333-44");
        conta.setNumeroConta("123456");
        conta.setSaldo(new BigDecimal("500.00"));
        conta.setStatus(StatusConta.ATIVA);

        // Instanciando um novo deposito
        DepositoRequestDTO dto = new DepositoRequestDTO();
        dto.setValor(new BigDecimal("100.00"));

        // Verifica se o numero da conta está igual ao que passamos
        when(contaRepository.findByNumeroConta("123456"))
                .thenReturn(Optional.of(conta));

        // Simulando o salvamento no banco
        when(contaRepository.save(conta))
                .thenReturn(conta);

        // ACT deposito
        ContaResponseDTO response = contaService.depositar("123456", dto);

        // ASSERTS
        // response nao pode ser vazio e o get saldo precisa ser igual a 600
        Assertions.assertNotNull(response);
        Assertions.assertEquals(new BigDecimal("600.00"), response.getSaldo());

        // Verifica se a conta existe e se foi salva no simulador
        verify(contaRepository).findByNumeroConta("123456");
        verify(contaRepository).save(conta);

    }

    @Test
    void naoDeveDepositarEmContaInexistenteOuInativa () {
        // Instanciando uma nova conta
        Conta conta = new Conta();
        conta.setId(1L);
        conta.setTitular("Joao Mario");
        conta.setCpf("111.222.333-44");
        conta.setNumeroConta("123456");
        conta.setSaldo(new BigDecimal("500.00"));
        conta.setStatus(StatusConta.BLOQUEADA);

        // Instanciando um novo deposito
        DepositoRequestDTO dto = new DepositoRequestDTO();
        dto.setValor(new BigDecimal("100.00"));

        when(contaRepository.findByNumeroConta("123456"))
                .thenReturn(Optional.of(conta));

        // Executa o metodo de depositar e verificar se ele lança a exceção
        Assertions.assertThrows(
                ResponseStatusException.class,
                () -> contaService.depositar("123456", dto)
        );

        // Garante que o metodo SAVE nunca foi chamado
        // pois o cadastro deve ser interrompido quando ele encontra o cpf duplicado
        Mockito.verify(contaRepository, never())
                .save(any());
    }

    @Test
    void deveRealizarTransferenciaComSucesso() {
        // Instanciando uma nova conta de origem
        Conta contaOrigem = new Conta();
        contaOrigem.setId(1L);
        contaOrigem.setTitular("Joao Mario");
        contaOrigem.setCpf("111.222.333-44");
        contaOrigem.setNumeroConta("123456");
        contaOrigem.setSaldo(new BigDecimal("500.00"));
        contaOrigem.setStatus(StatusConta.ATIVA);

        // Instanciando uma nova conta de destino
        Conta contaDestino = new Conta();
        contaDestino.setId(1L);
        contaDestino.setTitular("Joao Mario");
        contaDestino.setCpf("111.222.333-44");
        contaDestino.setNumeroConta("654321");
        contaDestino.setSaldo(new BigDecimal("300.00"));
        contaDestino.setStatus(StatusConta.ATIVA);

        // Transferencia
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setNumeroContaOrigem(contaOrigem.getNumeroConta());
        dto.setNumeroContaDestino(contaDestino.getNumeroConta());
        dto.setValor(new BigDecimal("50.00"));

        // Verifica se a origem e destino está correto
        when(contaRepository.findByNumeroConta(contaOrigem.getNumeroConta()))
                .thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByNumeroConta(contaDestino.getNumeroConta()))
                .thenReturn(Optional.of(contaDestino));
        // ACT
        // Trasnfere
        contaService.transferir(dto);
        // ASSERT
        // Verifica se o saldo foi transferido para destino e se foi debitado de origem.
        Assertions.assertEquals(new BigDecimal("450.00"), contaOrigem.getSaldo());
        Assertions.assertEquals(new BigDecimal("350.00"), contaDestino.getSaldo());
        verify(contaRepository).saveAll(List.of(contaOrigem, contaDestino));

    }

    @Test
    void naoDeveTransferirComSaldoInsuficiente() {
        // Instanciando uma nova conta de origem
        Conta contaOrigem = new Conta();
        contaOrigem.setId(1L);
        contaOrigem.setTitular("Joao Mario");
        contaOrigem.setCpf("111.222.333-44");
        contaOrigem.setNumeroConta("123456");
        contaOrigem.setSaldo(new BigDecimal("100.00"));
        contaOrigem.setStatus(StatusConta.ATIVA);
        // Instanciando uma nova conta de destino
        Conta contaDestino = new Conta();
        contaDestino.setId(1L);
        contaDestino.setTitular("Joao Mario");
        contaDestino.setCpf("111.222.333-44");
        contaDestino.setNumeroConta("654321");
        contaDestino.setSaldo(new BigDecimal("300.00"));
        contaDestino.setStatus(StatusConta.ATIVA);
        // Transferencia
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setNumeroContaOrigem(contaOrigem.getNumeroConta());
        dto.setNumeroContaDestino(contaDestino.getNumeroConta());
        dto.setValor(new BigDecimal("150.00"));
        // Verifica se a origem e destino está correto
        when(contaRepository.findByNumeroConta(contaOrigem.getNumeroConta()))
                .thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByNumeroConta(contaDestino.getNumeroConta()))
                .thenReturn(Optional.of(contaDestino));

        // Executa o metodo de transferir e verificar se ele lança a exceção de saldo insuficiente
        Assertions.assertThrows(
                ResponseStatusException.class,
                () -> contaService.transferir(dto)
        );

        // Garante que o metodo SAVE nunca foi chamado
        // pois o cadastro deve ser interrompido quando ele encontra o cpf duplicado
        Mockito.verify(contaRepository, never())
                .saveAll(any());
    }
    @Test
    void naoDeveTransferirContaInativaOuInexistente() {
        // Instanciando uma nova conta de origem
        Conta contaOrigem = new Conta();
        contaOrigem.setId(1L);
        contaOrigem.setTitular("Joao Mario");
        contaOrigem.setCpf("111.222.333-44");
        contaOrigem.setNumeroConta("123456");
        contaOrigem.setSaldo(new BigDecimal("500.00"));
        contaOrigem.setStatus(StatusConta.ATIVA);

        // Instanciando uma nova conta de destino
        Conta contaDestino = new Conta();
        contaDestino.setId(1L);
        contaDestino.setTitular("Joao Mario");
        contaDestino.setCpf("111.222.333-44");
        contaDestino.setNumeroConta("654321");
        contaDestino.setSaldo(new BigDecimal("300.00"));
        contaDestino.setStatus(StatusConta.BLOQUEADA);

        // Transferencia
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setNumeroContaOrigem(contaOrigem.getNumeroConta());
        dto.setNumeroContaDestino(contaDestino.getNumeroConta());
        dto.setValor(new BigDecimal("150.00"));
        // Verifica se a origem e destino está correto
        when(contaRepository.findByNumeroConta(contaOrigem.getNumeroConta()))
                .thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByNumeroConta(contaDestino.getNumeroConta()))
                .thenReturn(Optional.of(contaDestino));
        // Executa o metodo de transferir e verificar se ele lança a exceção de saldo insuficiente
        Assertions.assertThrows(
                ResponseStatusException.class,
                () -> contaService.transferir(dto)
        );

        // Garante que o metodo SAVE nunca foi chamado
        // pois o cadastro deve ser interrompido quando ele encontra o cpf duplicado
        Mockito.verify(contaRepository, never())
                .saveAll(any());
    }

    @Test
    void naoDeveTransferirParaPropriaConta () {
        // Instanciando uma nova conta de origem
        Conta contaOrigem = new Conta();
        contaOrigem.setId(1L);
        contaOrigem.setTitular("Joao Mario");
        contaOrigem.setCpf("111.222.333-44");
        contaOrigem.setNumeroConta("123456");
        contaOrigem.setSaldo(new BigDecimal("500.00"));
        contaOrigem.setStatus(StatusConta.ATIVA);

        // Transferencia
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setNumeroContaOrigem(contaOrigem.getNumeroConta());
        dto.setNumeroContaDestino(contaOrigem.getNumeroConta());
        dto.setValor(new BigDecimal("150.00"));

        // Executa o metodo de transferir e verificar se ele lança a exceção de saldo insuficiente
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> contaService.transferir(dto)
        );

        // Garante que o metodo SAVE nunca foi chamado
        // pois o cadastro deve ser interrompido quando ele encontra o cpf duplicado
        Mockito.verify(contaRepository, never())
                .saveAll(any());
    }

}
