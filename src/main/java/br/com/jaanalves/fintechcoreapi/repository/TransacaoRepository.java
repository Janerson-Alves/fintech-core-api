package br.com.jaanalves.fintechcoreapi.repository;

import br.com.jaanalves.fintechcoreapi.entities.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    // Faz a custa da conta, dara e hora e descrição da transação
    List<Transacao> findByContaNumeroContaOrderByDataHoraDesc(String numeroConta);
}
