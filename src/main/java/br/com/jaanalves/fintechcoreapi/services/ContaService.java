package br.com.jaanalves.fintechcoreapi.services;

import br.com.jaanalves.fintechcoreapi.dto.*;
import br.com.jaanalves.fintechcoreapi.entities.Conta;
import br.com.jaanalves.fintechcoreapi.entities.Transacao;
import br.com.jaanalves.fintechcoreapi.enums.StatusConta;
import br.com.jaanalves.fintechcoreapi.enums.TipoTransacao;
import br.com.jaanalves.fintechcoreapi.repository.ContaRepository;
import br.com.jaanalves.fintechcoreapi.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

// classe componente que gerencia as regras de negócio
@Service
public class ContaService {
    // Instanciando automaticamente a classe repositorio
    @Autowired
    private ContaRepository contaRepository;
    // Instanciando o repositorio de Transação
    @Autowired
    private TransacaoRepository transacaoRepository;

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

    // Método para buscar informações pelo numero da conta.
    public ContaResponseDTO buscarPorNumeroConta(String numeroConta) {
        Optional<Conta> contaEncontrada = contaRepository.findByNumeroConta(numeroConta);

        // Se não existir a conta, retorna uma exceção de NOT FOUND
        if (contaEncontrada.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conta não encontrada"
            );
        }
        // CASO A CONTA SEJA ENCONTRADA
        Conta conta = contaEncontrada.get();

        // Instancia um Response com as informações da conta
        ContaResponseDTO responseDTO = new ContaResponseDTO();
        responseDTO.setId(conta.getId());
        responseDTO.setTitular(conta.getTitular());
        responseDTO.setCpf(conta.getCpf());
        responseDTO.setNumeroConta(conta.getNumeroConta());
        responseDTO.setSaldo(conta.getSaldo());
        responseDTO.setStatus(conta.getStatus());

        // retorna as info da conta
        return responseDTO;
    }

    // Método para realizar o deposito
    @Transactional // Garante Atomicidade na alteração do saldo no Banco.
    public ContaResponseDTO depositar(String numeroConta, DepositoRequestDTO dto) {
        Optional<Conta> contaEncontrada = contaRepository.findByNumeroConta(numeroConta);
        // Se não existir a conta, retorna uma exceção de NOT FOUND
        if (contaEncontrada.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conta não encontrada"
            );
        }
        // Caso a conta exista, ela vai trazer as informações
        Conta conta = contaEncontrada.get();

        // Caso a conta esteja BLOQUEADA/ENCERRADA
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Conta desativada ou bloqueada."
            );
        }

        // CASO A CONTA ESTEJA ATIVA.
        // Adiciona o valor a saldo
        conta.setSaldo(conta.getSaldo().add(dto.getValor()));
        // salvando a conta no banco de dados com as suas alterações de saldo.
        Conta contaSalva = contaRepository.save(conta);

        // Registrando a transação na tabela de transações
        Transacao transacao = new Transacao(conta, TipoTransacao.DEPOSITO, dto.getValor(), "Depósito em conta");
        transacaoRepository.save(transacao);

        // Instancia um Response com as informações da conta
        ContaResponseDTO responseDTO = new ContaResponseDTO();
        responseDTO.setId(conta.getId());
        responseDTO.setTitular(conta.getTitular());
        responseDTO.setCpf(conta.getCpf());
        responseDTO.setNumeroConta(conta.getNumeroConta());
        responseDTO.setSaldo(conta.getSaldo());
        responseDTO.setStatus(conta.getStatus());

        // retorna as info da conta
        return responseDTO;

    }

     // Método de transferência entre contas
    @Transactional // Garante Atomicidade na alteração do saldo no Banco.
    public void transferir(TransferenciaRequestDTO dto) {

        // Verifica se as duas contas são iguais.
        if (dto.getNumeroContaOrigem().equalsIgnoreCase(dto.getNumeroContaDestino())) {
            throw new IllegalArgumentException(
                    "A conta de origem não pode ser igual a de destino."
            );
        }

        // Buscar as duas contas no repositório
        Optional<Conta> contaOrigemEncontrada = contaRepository.findByNumeroConta(dto.getNumeroContaOrigem());
        Optional<Conta> contaDestinoEncontrada = contaRepository.findByNumeroConta(dto.getNumeroContaDestino());

        // Verifica se as contas existem.
        if (contaOrigemEncontrada.isEmpty() || (contaDestinoEncontrada.isEmpty())){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conta de Destino ou Origem não encontrada"
            );
        }
        // Informações da conta.
        Conta contaOrigem = contaOrigemEncontrada.get();
        Conta contaDestino = contaDestinoEncontrada.get();

        // Verifica se a conta está em estado ativa.
        if (contaOrigem.getStatus() != StatusConta.ATIVA || contaDestino.getStatus() != StatusConta.ATIVA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A conta de Origem ou Destino está BLOQUEADA/ENCERRADA."
            );
        }

        // Verifica se a conta de destino possui saldo suficiente.
        if (contaOrigem.getSaldo().compareTo(dto.getValor()) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Saldo Insuficiente na conta de origem para transferência."
            );
        }
        // Realiza o debito na origem.
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(dto.getValor()));
        // Realiza o Crédito no destino
        contaDestino.setSaldo(contaDestino.getSaldo().add(dto.getValor()));

        // Salva em ambas as contas
        contaRepository.saveAll(List.of(contaOrigem, contaDestino));

        // Registrando a transação para a tabela das transações
        // ORIGEM
        Transacao transacaoOrigem = new Transacao(
                contaOrigem,
                TipoTransacao.TRANSFERENCIA_ENVIADA,
                dto.getValor(),
                "Transferência para conta " + contaDestino.getNumeroConta()
        );

        // DESTINO
        Transacao transacaoDestino = new Transacao(
                contaDestino,
                TipoTransacao.TRANSFERENCIA_RECEBIDA,
                dto.getValor(),
                "Tranferência recebida da conta " + contaOrigem.getNumeroConta()
        );

        // Salvando as transações.
        transacaoRepository.saveAll(List.of(transacaoOrigem, transacaoDestino));
    }

    // Método para buscar o extrato da conta.
    public List<TransacaoResponseDTO> obterExtrato(String numeroConta) {
        buscarPorNumeroConta(numeroConta); // Garante que a conta existe
        // Faz a busca das transações filtrada pelo numero da conta
        List<Transacao> transacoes = transacaoRepository.findByContaNumeroContaOrderByDataHoraDesc(numeroConta);

        // Retorna todas as transações da conta em uma lista mapeando com map
        return transacoes.stream()
                .map(t -> new TransacaoResponseDTO(t.getId(), t.getTipo(), t.getValor(),
                        t.getDataHora(), t.getDescricao()))
                .toList();
    }


}
