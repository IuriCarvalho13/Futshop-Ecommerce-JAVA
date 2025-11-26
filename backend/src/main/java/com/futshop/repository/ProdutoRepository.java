package com.futshop.repository;

import com.futshop.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Novo import

// Interface de acesso a dados (CRUD) para a entidade Produto
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // Métodos CRUD básicos herdados (save, findById, findAll, delete, etc.)
    
    /**
     * NOVO MÉTODO:
     * Busca produtos onde o campo 'nome' (Nome) contém a string fornecida.
     * * - 'Containing': Mapeia para a cláusula SQL LIKE %termo%.
     * - 'IgnoreCase': Garante que a pesquisa seja insensível a maiúsculas/minúsculas.
     * * @param nome O termo de busca para o nome do produto.
     * @return Uma lista de produtos que correspondem ao critério.
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);
}