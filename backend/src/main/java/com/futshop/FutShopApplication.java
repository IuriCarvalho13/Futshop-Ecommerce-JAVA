package com.futshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Classe principal para iniciar o aplicativo Spring Boot
@SpringBootApplication
public class FutShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(FutShopApplication.class, args);
        System.out.println("FutShop Backend rodando na porta 8080!");
    }
}