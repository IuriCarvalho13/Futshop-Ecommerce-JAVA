// Pacote onde a exceção está localizada (corrigido conforme indicado)
package com.futshop.exception; // PACOTE CORRIGIDO

// Exceção personalizada para sinalizar que não há quantidade suficiente em estoque
// Estende RuntimeException para ser uma exceção não verificada (unchecked)
public class EstoqueInsuficienteException extends RuntimeException {
    
    // Construtor que recebe uma mensagem descritiva do erro
    // Ex.: new EstoqueInsuficienteException("Produto X sem estoque suficiente")
    public EstoqueInsuficienteException(String message) {
        // Chama o construtor da classe pai (RuntimeException) com a mensagem
        super(message);
    }
}
