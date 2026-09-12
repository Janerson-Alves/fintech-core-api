package br.com.jaanalves.fintechcoreapi.services;

import br.com.jaanalves.fintechcoreapi.dto.ContaRequestDTO;
import br.com.jaanalves.fintechcoreapi.dto.ContaResponseDTO;
import br.com.jaanalves.fintechcoreapi.entities.Conta;
import br.com.jaanalves.fintechcoreapi.enums.StatusConta;
import br.com.jaanalves.fintechcoreapi.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

// classe componente que gerencia as regras de negócio
@Service
public class ContaService {
    // Instanciando automaticamente a classe repositorio
    @Autowired
    ContaRepository contaRepository;

    // Método para gerar o numero da conta
    public String gerarNumeroConta() {
        String numeroConta;

        // Gera o Numero da conta, caso não exista esse numero cadastrado no banco
        do {
            numeroConta = String.valueOf(
                    ThreadLocalRandom.current().nextInt(100000, 1000000)
            );
        } while (contaRepository.findByNumeroConta(numeroConta).isPresent());

        return numeroConta;
    }

    // Método para Criar a conta
    public ContaResponseDTO criarConta(ContaRequestDTO dto) {

        // Verifica se o cpf do cliente já está cadastrado
        if (contaRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("Já existe uma conta com esse CPF.");
        }
        // Verifica se o saldo inicial e menor que 50 reais
        if (dto.getSaldoInicial().compareTo(new BigDecimal("50.00")) < 0) {
            throw new IllegalArgumentException(
                    "O saldo inicial para abertura de conta deve ser maior ou igual a R$ 50.00"
            );
        }

        // Instancia uma nova conta
        Conta contaCorrente = new Conta();
        // Converte o DTO para Entities
        contaCorrente.setCpf(dto.getCpf());
        contaCorrente.setTitular(dto.getTitular());
        contaCorrente.setSaldo(dto.getSaldoInicial());
        contaCorrente.setStatus(StatusConta.ATIVA);

        // Geração do numero da conta e passando para o atributo da Conta
        String numeroConta = gerarNumeroConta();
        contaCorrente.setNumeroConta(numeroConta);
        // Salva a conta corrente criada
        Conta contaSalva = contaRepository.save(contaCorrente);

        // Criando o ResponseDTO para retorno
        return new ContaResponseDTO(
                contaSalva.getId(),
                contaSalva.getTitular(),
                contaSalva.getCpf(),
                contaSalva.getNumeroConta(),
                contaSalva.getSaldo(),
                contaSalva.getStatus()
        );
    }

}
