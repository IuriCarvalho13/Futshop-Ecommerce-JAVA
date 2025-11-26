package com.futshop.exception; // PACOTE CORRIGIDO

public class EstoqueInsuficienteException extends RuntimeException {
    
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
}