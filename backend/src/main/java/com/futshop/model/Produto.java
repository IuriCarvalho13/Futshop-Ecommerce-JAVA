// Pacote onde a classe Produto está localizada
package com.futshop.model;

// Importa anotação para personalizar colunas do banco
import jakarta.persistence.Column;
// Indica que esta classe será uma entidade JPA mapeada no banco de dados
import jakarta.persistence.Entity;
// Indica que o valor do ID será gerado automaticamente
import jakarta.persistence.GeneratedValue;
// Define o tipo de estratégia de geração do ID (IDENTITY = auto incremental)
import jakarta.persistence.GenerationType;
// Indica qual atributo será a chave primária da entidade
import jakarta.persistence.Id;
// Anotação usada para campos grandes (como textos longos), salva como LONGTEXT/TEXT no banco
import jakarta.persistence.Lob; 

// Marca esta classe como uma entidade JPA, ou seja, corresponde a uma tabela no banco
@Entity
public class Produto {

    // Define o campo como a chave primária da tabela
    @Id
    // Define que o ID será gerado automaticamente pelo banco (auto-increment)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Nome do produto (ex.: Camisa Real Madrid)
    private String nome;

    // Descrição completa do produto
    private String descricao;

    // Preço do produto (ex.: 199.99)
    private Double preco;
    
    // Permite armazenar URLs longas da imagem sem limite de caracteres
    @Lob // Indica campo grande
    @Column(name = "imagem_url", columnDefinition = "TEXT") // Força tipo TEXT no banco
    private String imagemUrl;

    // Tamanho da camisa (ex.: P, M, G, GG)
    private String tamanho;

    // Quantidade disponível no estoque
    private Integer quantidadeEmEstoque;
    
    // Construtor vazio obrigatório para o JPA criar objetos via reflexão
    public Produto() {
    }

    // ------------------------
    // GETTERS E SETTERS
    // ------------------------
    // Necessários para o Spring e JPA acessarem/alterarem os valores

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
    
    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public Integer getQuantidadeEmEstoque() {
        return quantidadeEmEstoque;
    }

    public void setQuantidadeEmEstoque(Integer quantidadeEmEstoque) {
        this.quantidadeEmEstoque = quantidadeEmEstoque;
    }
}
