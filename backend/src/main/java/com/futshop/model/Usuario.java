// Pacote onde a entidade Usuario está localizada
package com.futshop.model;

// Importa anotação que marca a classe como entidade JPA (mapeada para tabela)
import jakarta.persistence.Entity;
// Importa anotação para indicar que o campo ID terá valor gerado automaticamente
import jakarta.persistence.GeneratedValue;
// Define a estratégia de geração do ID (IDENTITY = auto-increment pelo banco)
import jakarta.persistence.GenerationType;
// Indica qual atributo é a chave primária da entidade
import jakarta.persistence.Id;

// Anota a classe como uma entidade persistente (será mapeada para uma tabela)
@Entity
public class Usuario {

    // Marca o campo como chave primária da tabela
    @Id
    // Indica que o valor do ID será gerado automaticamente pelo banco
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Nome do usuário (ex.: João Silva)
    private String nome;

    // Email do usuário (usado para login)
    private String email;

    // Senha do usuário (armazenar sempre hash na prática)
    private String senha;
    
    // Indicador se o usuário é administrador (true = admin, false = cliente)
    private boolean isAdmin; // NOVIDADE: Campo para admin

    // -----------------------
    // CONSTRUTORES
    // -----------------------

    // Construtor vazio necessário para o JPA instanciar a entidade via reflexão
    public Usuario() {}

    // Construtor conveniente para criar um usuário com nome, email e senha
    // Por padrão setamos isAdmin = false (cliente)
    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.isAdmin = false; // Cliente padrão
    }

    // -----------------------
    // GETTERS E SETTERS
    // -----------------------
    // Métodos necessários para o Spring/JPA serializarem e manipularem a entidade

    // Retorna o ID do usuário
    public Long getId() {
        return id;
    }

    // Define o ID do usuário (normalmente usado pelo JPA)
    public void setId(Long id) {
        this.id = id;
    }

    // Retorna o nome do usuário
    public String getNome() {
        return nome;
    }

    // Define o nome do usuário
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Retorna o email do usuário
    public String getEmail() {
        return email;
    }

    // Define o email do usuário
    public void setEmail(String email) {
        this.email = email;
    }

    // Retorna a senha do usuário (na prática, nunca exponha a senha em respostas)
    public String getSenha() {
        return senha;
    }

    // Define a senha do usuário (armazene sempre o hash, não a senha em texto)
    public void setSenha(String senha) {
        this.senha = senha;
    }

    // -----------------------
    // GETTER E SETTER PARA isAdmin
    // -----------------------

    // Retorna true se o usuário for administrador
    public boolean isAdmin() {
        return isAdmin;
    }

    // Define se o usuário é administrador (use com cuidado em endpoints públicos)
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
