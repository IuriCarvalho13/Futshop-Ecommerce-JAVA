// Pacote onde o controller está localizado
package com.futshop.controller;

// Importa a classe Produto do pacote model
import com.futshop.model.Produto;
// Importa o repositório que faz a comunicação com o banco de dados
import com.futshop.repository.ProdutoRepository;

// Importa o Service com as regras de negócio
import com.futshop.service.ProdutoService; 

// Importa os itens usados no processo de checkout
import com.futshop.dto.CheckoutItemDTO; 
import com.futshop.exception.EstoqueInsuficienteException; 

// Importações do Spring necessárias para o Controller funcionar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Permite requisições vindas do frontend hospedado no endereço informado
@CrossOrigin(origins = "http://127.0.0.1:5500") 
// Indica que esta classe é um controller REST
@RestController
// Define o caminho base para os endpoints deste controller
@RequestMapping("/api/produtos")
public class ProdutoController {

    // Injeta o Service para acessar regras de negócio
    @Autowired
    private ProdutoService produtoService; 
    
    // Injeta o repositório para operações diretas com o banco
    @Autowired
    private ProdutoRepository produtoRepository;


    // ---------------------------
    // 1. GET: Listar produtos (com filtro opcional)
    // ---------------------------

    @GetMapping
    public List<Produto> listarProdutos(@RequestParam(required = false) String termo) {
        // Verifica se foi enviado um termo de pesquisa
        if (termo != null && !termo.trim().isEmpty()) {
            // Retorna produtos que contenham o termo no nome
            return produtoService.buscarPorNome(termo);
        }
        // Caso não tenha termo, retorna todos os produtos
        return produtoService.buscarTodos(); 
    }


    // ---------------------------
    // 2. GET: Buscar produto por ID
    // ---------------------------

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id) {
        // Tenta buscar o produto pelo ID
        Optional<Produto> produto = produtoRepository.findById(id);

        // Se existir, retorna 200 OK; senão, retorna 404 Not Found
        return produto.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }
    

    // ---------------------------
    // 3. POST: Criar novo produto
    // ---------------------------

    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody Produto produto) {
        // Garante que será criado um novo registro no banco
        produto.setId(null); 
        
        // Salva o novo produto usando o Service
        Produto novoProduto = produtoService.salvar(produto);

        // Retorna o produto criado com status 201 Created
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }
    

    // ---------------------------
    // 4. PUT: Atualizar produto existente
    // ---------------------------

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id, @RequestBody Produto produtoDetalhes) {
        
        // Busca o produto existente
        Optional<Produto> produtoOpt = produtoRepository.findById(id);

        // Se existir, atualiza os dados
        if (produtoOpt.isPresent()) {
            Produto produtoExistente = produtoOpt.get();
            
            // Atualiza campos básicos
            produtoExistente.setNome(produtoDetalhes.getNome());
            produtoExistente.setPreco(produtoDetalhes.getPreco());
            produtoExistente.setImagemUrl(produtoDetalhes.getImagemUrl());

            // Atualiza campos adicionais do produto
            produtoExistente.setDescricao(produtoDetalhes.getDescricao());
            produtoExistente.setQuantidadeEmEstoque(produtoDetalhes.getQuantidadeEmEstoque());
            produtoExistente.setTamanho(produtoDetalhes.getTamanho());

            // Salva o produto atualizado
            Produto produtoAtualizado = produtoService.salvar(produtoExistente);

            // Retorna o produto atualizado
            return ResponseEntity.ok(produtoAtualizado);
        } 
        else {
            // Se não existir, retorna 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }
    

    // ---------------------------
    // 5. DELETE: Excluir produto
    // ---------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        
        // Verifica se o produto existe
        if (produtoRepository.existsById(id)) {
            
            // Deleta o produto
            produtoRepository.deleteById(id);

            // Retorna 204 No Content
            return ResponseEntity.noContent().build();

        } else {
            // Se não existir, retorna 404
            return ResponseEntity.notFound().build();
        }
    }
    

    // ---------------------------
    // 6. POST: Checkout (baixa de estoque)
    // ---------------------------

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody List<CheckoutItemDTO> items) {
        try {
            // Chama o Service para processar a baixa de estoque
            produtoService.processCheckout(items); 
            
            // Se tudo der certo, retorna 200 OK
            return ResponseEntity.ok("Estoque atualizado com sucesso!");
            
        } catch (EstoqueInsuficienteException e) {
            // Quando falta estoque, retorna erro 400 com a mensagem
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(e.getMessage());
            
        } catch (Exception e) {
            // Captura qualquer outro erro inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Erro interno ao processar o pedido.");
        }
    }
}
