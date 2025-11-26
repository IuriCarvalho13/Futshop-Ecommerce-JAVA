// Pacote onde a interface de repositório está localizada
package com.futshop.repository;

 // Importa a entidade Produto para tipagem do repositório
import com.futshop.model.Produto;
// Importa a interface JpaRepository do Spring Data JPA, que fornece operações CRUD prontas
import org.springframework.data.jpa.repository.JpaRepository;
// Importa a anotação Repository (opcional, melhora legibilidade)
import org.springframework.stereotype.Repository;

import java.util.List; // Novo import para retornar listas de produtos

// Marca a interface como um componente de repositório do Spring
// (não é estritamente necessário com Spring Data JPA, mas é uma boa prática)
@Repository
// Define a interface que estende JpaRepository para herdar métodos de CRUD.
// Parametros: <Tipo da Entidade, Tipo da Chave Primária>
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // Os métodos básicos (save, findById, findAll, deleteById, etc.) já vêm do JpaRepository
    
    /**
     * NOVO MÉTODO:
     * Busca produtos cujo campo 'nome' contenha a string fornecida.
     *
     * - "findByNome" indica que a busca será feita pelo campo 'nome'.
     * - "Containing" traduz-se para SQL LIKE %termo% (procura parcial).
     * - "IgnoreCase" torna a busca insensível a maiúsculas/minúsculas.
     *
     * Exemplo de uso: produtoRepository.findByNomeContainingIgnoreCase("camisa")
     *
     * @param nome O termo de busca (parte do nome) — pode ser uma palavra ou fragmento.
     * @return Lista de produtos cujo nome contém o termo (caso nenhum, retorna lista vazia).
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);
}
