package br.com.jaanalves.fintechcoreapi.entities;

import br.com.jaanalves.fintechcoreapi.enums.StatusConta;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_conta")
public class Conta {
    // ID Gerado automaticamente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Coluna não nula
    @Column(nullable = false)
    private String titular;
    // Coluna não nula e unica
    @Column(nullable = false, unique = true)
    private String cpf;
    // Coluna não nula e unica
    @Column(nullable = false, unique = true)
    private String numeroConta;
    // Coluna não nula
    @Column(nullable = false)
    private BigDecimal saldo;
    // Coluna Enumerada e não nula
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConta status;
}
