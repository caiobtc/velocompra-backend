package com.velocompra.ecommerce.model;

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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome completo é obrigatório")
    @Pattern(regexp = "^(?=.{3,}\\s.{3,}).+$", message = "O nome deve conter pelo menos duas palavras com no mínimo 3 letras cada")
    private String nomeCompleto;

    @Email(message = "Email inválido")
    @NotBlank(message = "O email é obrigatório")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "O CPF é obrigatório")
    @Column(unique = true)
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    @NotBlank(message = "O gênero é obrigatório")
    private String genero;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    // Endereço de Faturamento separado
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_faturamento_id")
    private EnderecoFaturamento enderecoFaturamento;

    // Endereços de entrega (mantém como lista embutida)
    @ElementCollection
    @CollectionTable(name = "enderecos_entrega", joinColumns = @JoinColumn(name = "cliente_id"))
    private List<Endereco> enderecosEntrega = new ArrayList<>();



}
