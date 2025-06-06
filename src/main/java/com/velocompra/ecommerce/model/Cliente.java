package com.velocompra.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um cliente no sistema.
 * Esta classe contém informações pessoais do cliente, incluindo nome completo, e-mail, CPF, data de nascimento,
 * gênero e senha. Além disso, ela possui relacionamentos com o endereço de faturamento e os endereços de entrega.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    /**
     * ID único do cliente gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome completo do cliente, que deve conter pelo menos duas palavras com no mínimo 3 letras cada.
     */
    @NotBlank(message = "O nome completo é obrigatório")
    @Pattern(regexp = "^(?=.{3,}\\s.{3,}).+$", message = "O nome deve conter pelo menos duas palavras com no mínimo 3 letras cada")
    private String nomeCompleto;

    /**
     * Endereço de e-mail do cliente, que deve ser único e válido.
     */
    @Email(message = "Email inválido")
    @NotBlank(message = "O email é obrigatório")
    @Column(unique = true)
    private String email;

    /**
     * CPF do cliente, que deve ser único.
     */
    @NotBlank(message = "O CPF é obrigatório")
    @Column(unique = true)
    private String cpf;

    /**
     * Data de nascimento do cliente.
     */
    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    /**
     * Gênero do cliente.
     */
    @NotBlank(message = "O gênero é obrigatório")
    private String genero;

    /**
     * Senha do cliente.
     */
    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    /**
     * Endereço de faturamento do cliente.
     * Relacionamento de um para um (one-to-one).
     */
    @OneToOne()
    @JoinColumn(name = "cliente_id")
    private EnderecoFaturamento enderecoFaturamento;

    /**
     * Lista de endereços de entrega associados ao cliente.
     * Relacionamento de um para muitos (one-to-many) com a entidade {@link EnderecoEntrega}.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EnderecoEntrega> enderecosEntrega = new ArrayList<>();
}
