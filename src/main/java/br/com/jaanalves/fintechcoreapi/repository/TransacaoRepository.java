package br.com.jaanalves.fintechcoreapi.repository;

import br.com.jaanalves.fintechcoreapi.entities.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    // Faz a custa da conta, dara e hora e descrição da transação
    List<Transacao> findByContaNumeroContaOrderByDataHoraDesc(String numeroConta);

    // Busca paginada filtrando por número da conta e período de datas
    Page<Transacao> findByContaNumeroContaAndDataHoraBetween(
            // Informações da paginação.
            String numeroConta,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable
    );
}
