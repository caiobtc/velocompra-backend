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
     *
     * ALTERAÇÃO AQUI: Adicionar `mappedBy` e `cascade = CascadeType.ALL`
     * `mappedBy` aponta para o campo `cliente` na entidade `EnderecoFaturamento`,
     * indicando que `EnderecoFaturamento` é o lado "dono" da relação.
     * `cascade = CascadeType.ALL` garante que operações (PERSIST, MERGE, REMOVE, etc.) no Cliente
     * se propaguem para o EnderecoFaturamento.
     */
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    // `orphanRemoval = true` é útil para garantir que se um endereço de faturamento for removido de um cliente,
    // ele seja excluído do banco de dados (o que é o comportamento esperado para faturamento).
    private EnderecoFaturamento enderecoFaturamento;

    /**
     * Lista de endereços de entrega associados ao cliente.
     * Relacionamento de um para muitos (one-to-many) com a entidade {@link EnderecoEntrega}.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EnderecoEntrega> enderecosEntrega = new ArrayList<>();

    public void setEnderecoFaturamento(EnderecoFaturamento enderecoFaturamento) {
        // 1. Define o endereço de faturamento no lado do Cliente
        // Aqui, o campo 'enderecoFaturamento' da entidade Cliente está sendo atualizado
        // com o novo objeto EnderecoFaturamento que foi passado como argumento.
        this.enderecoFaturamento = enderecoFaturamento;

        // 2. Garante a consistência bidirecional
        // Verifica se o novo endereço de faturamento não é nulo.
        if (enderecoFaturamento != null) {
            // Se não for nulo, define o cliente (this) no lado do EnderecoFaturamento.
            // Isso é fundamental para que o relacionamento seja completo:
            // Cliente aponta para EnderecoFaturamento, e EnderecoFaturamento aponta de volta para Cliente.
            // Sem esta linha, o Hibernate pode ter dificuldades em entender a associação completa,
            // especialmente ao salvar em cascata ou ao buscar dados.
            enderecoFaturamento.setCliente(this);
        }
    }

    public void setEnderecosEntrega(List<EnderecoEntrega> enderecosEntrega) {
        // 1. Limpa e desassocia os endereços antigos (se existirem)
        // Este bloco garante que, antes de associar uma nova lista de endereços,
        // os endereços que estavam anteriormente ligados a este cliente sejam "desligados".
        // Isso é importante para evitar que endereços antigos continuem apontando para um cliente
        // que não os possui mais, ou para lidar com a remoção de "órfãos" (orphanRemoval = true).
        if (this.enderecosEntrega != null) {
            for (EnderecoEntrega endereco : this.enderecosEntrega) {
                // Para cada endereço antigo, define seu campo 'cliente' como nulo.
                // Isso remove a referência do endereço para este cliente, desassociando-o.
                endereco.setCliente(null); // Desassocia do cliente anterior
            }
        }

        // 2. Define a nova lista de endereços de entrega no lado do Cliente
        // O campo 'enderecosEntrega' da entidade Cliente é atualizado com a nova lista.
        this.enderecosEntrega = enderecosEntrega;
        if (enderecosEntrega != null) {
            // Itera sobre cada endereço na nova lista.
            for (EnderecoEntrega endereco : enderecosEntrega) {
                // Para cada novo endereço, define o cliente (this) no lado do EnderecoEntrega.
                // Assim como no EnderecoFaturamento, isso completa o relacionamento bidirecional:
                // Cliente aponta para a lista de EnderecosEntrega, e cada EnderecoEntrega
                // na lista aponta de volta para o Cliente.
                // Isso é essencial para que as operações em cascata (CascadeType.ALL) funcionem corretamente
                // e para que o Hibernate possa persistir, atualizar ou remover esses endereços
                // junto com o cliente.
                endereco.setCliente(this); // associa o cliente a cada novo endereco
            }
        }
    }
}
