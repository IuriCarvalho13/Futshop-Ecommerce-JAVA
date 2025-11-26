package com.futshop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futshop.model.Produto;
import com.futshop.repository.ProdutoRepository;
import com.futshop.dto.CheckoutItemDTO;
import com.futshop.exception.EstoqueInsuficienteException;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    // Injeção de dependência via Construtor
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // Método para salvar ou atualizar um produto no banco de dados
    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    // Método para buscar todos os produtos (usado pelo Controller)
    public List<Produto> buscarTodos() {
        return produtoRepository.findAll();
    }
    
    /**
     * NOVO MÉTODO: Buscar produtos por termo (parte do nome).
     * @param termo O texto a ser procurado no nome do produto.
     * @return Uma lista filtrada de produtos.
     */
    public List<Produto> buscarPorNome(String termo) {
        // Se o termo estiver vazio ou nulo, retorna todos os produtos para não filtrar
        if (termo == null || termo.trim().isEmpty()) {
            return produtoRepository.findAll();
        }
        // Chama o método que o Spring Data JPA criou no Repository
        return produtoRepository.findByNomeContainingIgnoreCase(termo);
    }

    /**
     * Processa o checkout, realizando a baixa de estoque dos produtos comprados.
     * @param items Lista de CheckoutItemDTO com ID e quantidade comprada.
     */
    @Transactional // Se uma falha ocorrer durante o loop, TODAS as alterações são desfeitas (ROLLBACK)
    public void processCheckout(List<CheckoutItemDTO> items) {
        for (CheckoutItemDTO item : items) {
            
            // 1. Busca o produto no banco
            Produto produto = produtoRepository.findById(item.getId())
                                .orElseThrow(() -> new RuntimeException("Produto ID " + item.getId() + " não encontrado."));

            // 2. Verifica se há estoque suficiente
            int quantidadeComprada = item.getQuantidade();
            int estoqueAtual = produto.getQuantidadeEmEstoque();

            if (estoqueAtual < quantidadeComprada) {
                // 3. Lança exceção se não houver estoque, interrompendo a transação
                String nomeProduto = item.getNome() != null ? item.getNome() : "ID: " + item.getId();
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + nomeProduto + ". Em estoque: " + estoqueAtual);
            }

            // 4. Atualiza a quantidade (Baixa de estoque)
            produto.setQuantidadeEmEstoque(estoqueAtual - quantidadeComprada);
            
            // 5. Salva a alteração no banco de dados
            produtoRepository.save(produto);
        }
    }
}