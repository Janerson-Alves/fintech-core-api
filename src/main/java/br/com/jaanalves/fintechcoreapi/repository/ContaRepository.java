package br.com.jaanalves.fintechcoreapi.repository;

import br.com.jaanalves.fintechcoreapi.entities.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Métodos de Consulta no Banco para a Conta
public interface ContaRepository extends JpaRepository<Conta, Long> {
    // Irá verificar se o CPF existe
    boolean existsByCpf(String cpf);
    // Irá verificar se o numero da conta ja existe.
    Optional<Conta> findByNumeroConta(String numeroConta);
}
