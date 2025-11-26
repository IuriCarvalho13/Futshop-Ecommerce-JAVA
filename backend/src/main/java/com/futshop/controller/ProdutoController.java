package com.futshop.controller;

import com.futshop.model.Produto;
import com.futshop.repository.ProdutoRepository;

// Importar o Service para acessar a lógica de negócio
import com.futshop.service.ProdutoService; 

// Importar as classes para o Checkout
import com.futshop.dto.CheckoutItemDTO; // Importe o DTO do carrinho
import com.futshop.exception.EstoqueInsuficienteException; // Importe a exceção de negócio

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500") 
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService; 
    
    @Autowired
    private ProdutoRepository produtoRepository;


    // 1. GET: Listar todos os produtos ou FILTRAR por nome (READ)
    // Aceita um parâmetro 'termo' opcional para pesquisa
    @GetMapping
    public List<Produto> listarProdutos(@RequestParam(required = false) String termo) {
        if (termo != null && !termo.trim().isEmpty()) {
            // Se houver um termo, chama o método de busca por nome no Service
            return produtoService.buscarPorNome(termo);
        }
        // Se não houver termo, retorna todos os produtos
        return produtoService.buscarTodos(); 
    }

    // 2. GET: Buscar produto por ID (READ)
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        return produto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // 3. POST: Adicionar novo produto (CREATE)
    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody Produto produto) {
        // Usamos o Service para aplicar quaisquer regras de negócio antes de salvar
        produto.setId(null); 
        Produto novoProduto = produtoService.salvar(produto);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }
    
    // 4. PUT: Atualizar produto existente (UPDATE)
    // Manteve-se a lógica de atualização com a inclusão de todos os campos (descrição, estoque, tamanho)
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id, @RequestBody Produto produtoDetalhes) {
        Optional<Produto> produtoOpt = produtoRepository.findById(id);

        if (produtoOpt.isPresent()) {
            Produto produtoExistente = produtoOpt.get();
            
            produtoExistente.setNome(produtoDetalhes.getNome());
            produtoExistente.setPreco(produtoDetalhes.getPreco());
            produtoExistente.setImagemUrl(produtoDetalhes.getImagemUrl());
            
            // Campos adicionados/ajustados para serem atualizados (necessário devido à sua modelagem)
            produtoExistente.setDescricao(produtoDetalhes.getDescricao());
            produtoExistente.setQuantidadeEmEstoque(produtoDetalhes.getQuantidadeEmEstoque());
            produtoExistente.setTamanho(produtoDetalhes.getTamanho());

            Produto produtoAtualizado = produtoService.salvar(produtoExistente); // Usando service.salvar
            return ResponseEntity.ok(produtoAtualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 5. DELETE: Remover produto (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 6. NOVO ENDPOINT: Finalizar Compra e Baixa de Estoque (UPDATE)
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody List<CheckoutItemDTO> items) {
        try {
            // Chama o Service para executar a baixa de estoque transacional
            produtoService.processCheckout(items); 
            
            // Retorna sucesso 200 OK
            return ResponseEntity.ok("Estoque atualizado com sucesso!");
            
        } catch (EstoqueInsuficienteException e) {
            // Se o Service lançar a exceção de estoque, retorna erro 400 (Bad Request)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            
        } catch (Exception e) {
            // Captura outros erros inesperados (ex: produto não encontrado) e retorna erro 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao processar o pedido.");
        }
    }
}