// Pacote raiz da aplicação
package com.futshop;

 // Importa a classe utilitária do Spring Boot para iniciar a aplicação
import org.springframework.boot.SpringApplication;
// Importa anotação que ativa auto-configuração, component scanning e configuração Spring Boot
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Comentário de topo: classe principal responsável por inicializar toda a aplicação Spring Boot
// Anotação que indica que esta é a aplicação Spring Boot (combina @Configuration, @EnableAutoConfiguration e @ComponentScan)
@SpringBootApplication
public class FutShopApplication {

    // Método main — ponto de entrada da aplicação Java
    public static void main(String[] args) {
        // Inicia o contexto Spring e levanta o servidor embutido (por padrão Tomcat na porta 8080)
        SpringApplication.run(FutShopApplication.class, args);
        
        // Mensagem simples para o console confirmando que a aplicação foi iniciada
        // (útil durante desenvolvimento; em produção prefira logs estruturados)
        System.out.println("FutShop Backend rodando na porta 8080!");
    }
}
