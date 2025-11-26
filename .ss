# 🛍️ FutShop - E-commerce de Produtos Esportivos

Este projeto é um sistema de E-commerce para venda de produtos esportivos (API Backend e Frontend estático), desenvolvido com Spring Boot (Java) para o backend e persistência de dados com MySQL.

---

## 💻 Arquitetura e Tecnologias

A aplicação segue uma arquitetura baseada em **API REST** com separação de camadas (Controller, Service, Repository).

### ⚙️ Backend (API REST)

| Tecnologia | Função |
| :--- | :--- |
| **Java 17+** | Linguagem de programação principal. |
| **Spring Boot** | Framework para construção da API. |
| **Spring Data JPA** | Persistência de dados e mapeamento Objeto-Relacional (ORM). |
| **MySQL** | Banco de dados relacional. |
| **Maven** | Gerenciador de dependências e automação de build. |

### 🌐 Frontend

| Tecnologia | Função |
| :--- | :--- |
| **HTML, CSS, JavaScript** | Interface de usuário e lógica de manipulação do DOM. |
| **Live Server (VS Code)** | Servidor local para o frontend. |

---

## 🛠️ Requisitos e Configuração

Para rodar este projeto localmente, você precisará ter instalado:

1.  **JDK (Java Development Kit)**: Versão 17 ou superior.
2.  **MySQL Server**: Rodando na porta padrão (3306).
3.  **Maven**: Para gerenciar o projeto.
4.  **IDE**: (Recomendado) IntelliJ IDEA ou VS Code com as extensões Java.

### 1. Configuração do Banco de Dados

1.  Crie um banco de dados no seu MySQL Server com o nome `futshop_db`.
    ```sql
    CREATE DATABASE futshop_db;
    ```
2.  Atualize as credenciais no arquivo `backend/src/main/resources/application.properties`:

    ```properties
    spring.datasource.username=root
    spring.datasource.password=SUA_SENHA_MYSQL
    ```
    *(Substitua `SUA_SENHA_MYSQL` pela sua senha real)*

### 2. Inicialização dos Dados

O projeto está configurado para inicializar a tabela `produto` e inserir os dados iniciais automaticamente usando o arquivo `data.sql` na primeira vez que o Spring Boot rodar no modo `ddl-auto=update`.

---

## ▶️ Como Executar a Aplicação

### Passo 1: Iniciar o Backend (API)

Abra o terminal na pasta raiz do projeto (`FutShop-Ecommerce-Java`) e inicie a aplicação Spring Boot:

**Opção A: Via Maven (Recomendado para desenvolvimento)**

```bash
cd backend
mvn spring-boot:run
```
O backend estará ativo em ```http://localhost:8080/api/produtos```

Abra o arquivo ```index.html``` usando a extensão Live Server do VS Code.