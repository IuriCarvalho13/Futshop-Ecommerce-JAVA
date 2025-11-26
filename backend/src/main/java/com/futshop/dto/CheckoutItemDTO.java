// Pacote onde a classe DTO está localizada (corrigido conforme indicado)
package com.futshop.dto; // PACOTE CORRIGIDO

// Observação sobre Lombok:
// Se você estiver usando Lombok, pode descomentar os imports/annotations abaixo
// e remover os getters/setters manuais.
// import lombok.Getter;
// import lombok.Setter;
// @Getter
// @Setter

// Classe DTO (Data Transfer Object) que representa um item do checkout/carrinho
public class CheckoutItemDTO {
    
    // Identificador do produto — corresponde ao ID do produto no banco
    private Long id; // ID do Produto

    // Nome do produto — útil para exibir no frontend sem precisar buscar o produto inteiro
    private String nome; // Nome do produto

    // Quantidade do produto que o cliente deseja comprar
    private Integer quantidade; // Quantidade comprada

    // ---------------------------
    // Getters e Setters (manuais)
    // ---------------------------
    // Se usar Lombok, estes métodos podem ser removidos

    // Retorna o ID do produto
    public Long getId() { 
        return id; 
    }

    // Define o ID do produto
    public void setId(Long id) { 
        this.id = id; 
    }
    
    // Retorna o nome do produto
    public String getNome() { 
        return nome; 
    }

    // Define o nome do produto
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    
    // Retorna a quantidade desejada pelo usuário
    public Integer getQuantidade() { 
        return quantidade; 
    }

    // Define a quantidade desejada pelo usuário
    public void setQuantidade(Integer quantidade) { 
        this.quantidade = quantidade; 
    }
}
