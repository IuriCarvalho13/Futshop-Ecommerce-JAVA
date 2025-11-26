package com.futshop.dto; // PACOTE CORRIGIDO

// Importe Lombok (se estiver usando) ou crie getters e setters manualmente
// import lombok.Getter;
// import lombok.Setter;
// @Getter
// @Setter
public class CheckoutItemDTO {
    
    private Long id; // ID do Produto
    private String nome; // Nome do produto
    private Integer quantidade; // Quantidade comprada

    // Se NÃO usar Lombok, adicione os Getters e Setters aqui:
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}