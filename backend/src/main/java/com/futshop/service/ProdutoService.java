// Pacote onde o service está localizado
package com.futshop.service;

import java.util.List;

// Indica que esta classe é um Service do Spring (componente de lógica de negócio)
import org.springframework.stereotype.Service;
// Anotação para controlar transações (commit/rollback automático)
import org.springframework.transaction.annotation.Transactional;

import com.futshop.model.Produto;
// Repositório responsável por operações de persistência para Produto
import com.futshop.repository.ProdutoRepository;
import com.futshop.dto.CheckoutItemDTO;
// Exceção personalizada lançada quando não há estoque suficiente
import com.futshop.exception.EstoqueInsuficienteException;

@Service
public class ProdutoService {

    // Repositório usado para acessar o banco de dados (CRUD de Produto)
    private final ProdutoRepository produtoRepository;

    // -----------------------------
    // Injeção de dependência via construtor
    // -----------------------------
    // Usar injeção por construtor é recomendado para facilitar testes e garantir imutabilidade do campo
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // -----------------------------
    // Salvar/Atualizar produto
    // -----------------------------
    // Método simples que delega ao produtoRepository.save()
    // Se o produto tiver id nulo, cria; se tiver id, atualiza.
    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    // -----------------------------
    // Buscar todos os produtos
    // -----------------------------
    // Retorna a lista completa de produtos do banco (usado pelo Controller)
    public List<Produto> buscarTodos() {
        return produtoRepository.findAll();
    }
    
    /**
     * NOVO MÉTODO: Buscar produtos por termo (parte do nome).
     * @param termo O texto a ser procurado no nome do produto.
     * @return Uma lista filtrada de produtos.
     */
    public List<Produto> buscarPorNome(String termo) {
        // Se o termo for nulo ou vazio, evita filtrar e retorna todos os produtos
        if (termo == null || termo.trim().isEmpty()) {
            return produtoRepository.findAll();
        }
        // Caso contrário, delega ao repositório que implementa a busca por nome (contendo, ignore case)
        return produtoRepository.findByNomeContainingIgnoreCase(termo);
    }

    /**
     * Processa o checkout, realizando a baixa de estoque dos produtos comprados.
     * @param items Lista de CheckoutItemDTO com ID e quantidade comprada.
     */
    @Transactional // Garante que todas as operações dentro do método sejam atômicas (commit ou rollback)
    public void processCheckout(List<CheckoutItemDTO> items) {
        // Percorre cada item enviado no checkout (carrinho)
        for (CheckoutItemDTO item : items) {
            
            // 1. Busca o produto no banco pelo ID informado no DTO.
            //    Se não encontrar, lança RuntimeException (poderia ser customizada).
            Produto produto = produtoRepository.findById(item.getId())
                                .orElseThrow(() -> new RuntimeException("Produto ID " + item.getId() + " não encontrado."));

            // 2. Verifica se o estoque atual é suficiente para a quantidade comprada
            int quantidadeComprada = item.getQuantidade();
            int estoqueAtual = produto.getQuantidadeEmEstoque();

            if (estoqueAtual < quantidadeComprada) {
                // 3. Se não houver estoque suficiente, lança a exceção de negócio específica.
                //    Como o método é @Transactional, ao lançar a exceção a transação será revertida.
                String nomeProduto = item.getNome() != null ? item.getNome() : "ID: " + item.getId();
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + nomeProduto + ". Em estoque: " + estoqueAtual);
            }

            // 4. Atualiza a quantidade em estoque subtraindo a quantidade comprada
            produto.setQuantidadeEmEstoque(estoqueAtual - quantidadeComprada);
            
            // 5. Persiste a alteração no banco. Mesmo dentro de uma transação, é bom salvar explicitamente.
            produtoRepository.save(produto);
        }
        // Ao final do método (se nenhuma exceção ocorreu), a transação será comitada automaticamente.
    }
}
