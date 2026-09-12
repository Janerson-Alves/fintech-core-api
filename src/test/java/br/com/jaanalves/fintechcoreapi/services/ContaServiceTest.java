package br.com.jaanalves.fintechcoreapi.services;

import br.com.jaanalves.fintechcoreapi.dto.ContaRequestDTO;
import br.com.jaanalves.fintechcoreapi.dto.ContaResponseDTO;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

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
}
