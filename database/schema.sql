-- 1. Comando para criar o banco de dados (Schema)
--    SE já existir, ele não fará nada (IF NOT EXISTS)
CREATE SCHEMA IF NOT EXISTS futshop_db DEFAULT CHARACTER SET utf8mb4;

-- 2. Seleciona o banco de dados que acabamos de criar/confirmar
USE futshop_db;
---
-- 3. Tabela de Produtos
--    Cria a tabela para armazenar os itens da loja
---
CREATE TABLE IF NOT EXISTS produto (
    -- ID e Chave Primária
    id BIGINT NOT NULL AUTO_INCREMENT,

    -- Campos do Produto
    descricao VARCHAR(2000),
    imagem_url VARCHAR(255),
    nome VARCHAR(255) NOT NULL,
    preco DOUBLE PRECISION NOT NULL,
    quantidade_em_estoque INTEGER,
    tamanho VARCHAR(255),

    PRIMARY KEY (id)
) ENGINE=InnoDB;

---
-- 4. Tabela de Usuários
--    Cria a tabela para armazenar dados de login/cadastro
---
CREATE TABLE usuario (
    -- ID e Chave Primária
    id BIGINT NOT NULL AUTO_INCREMENT,

    -- Campos do Usuário
    nome VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE, 
    senha VARCHAR(255) NOT NULL, -- Garante que a senha não seja nula

    -- Campo de Administração (NOVO)
    -- TINYINT(1) é comumente usado em MySQL para representar booleanos (1=TRUE, 0=FALSE)
    is_admin TINYINT(1) NOT NULL DEFAULT 0, 

    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Use o comando SQL para inserir um novo usuário com privilégio de administrador
INSERT INTO usuario (nome, email, senha, is_admin) 
VALUES ('Administrador Master', 'admin@futshop.com', 'admin123', 1);

INSERT INTO usuario (nome, email, senha, is_admin)
VALUES ('teste','teste@teste.com','123456',0);

-- Inserindo Produtos
INSERT INTO produto (id, descricao, imagem_url, nome, preco, quantidade_em_estoque, tamanho) VALUES
(5, '', 'https://imgcentauro-a.akamaihd.net/660x660/99673104A14.jpg', 'Camisa do Cruzeiro III 25/26 Torcedor adidas Masculina', 379.99, 20, 'G'),
(6, '', 'https://imgcentauro-a.akamaihd.net/800x800/99358904A2.jpg', 'Camisa do Cruzeiro I 25 adidas Masculina Torcedor', 299.99, 20, 'M'),
(7, '', 'https://imgcentauro-a.akamaihd.net/800x800/99367319A2.jpg', 'Camisa do Flamengo 25/26 adidas Masculina Fanshirt', 189.99, 20, 'G'),
(8, '', 'https://imgcentauro-a.akamaihd.net/800x800/99673212A15.jpg', 'Camisa do Flamengo III 25/26 Torcedor adidas Masculina Originais', 379.99, 20, 'G'),
(9, '', 'https://imgcentauro-a.akamaihd.net/800x800/9971LY08A4.jpg', 'Camisa do Palmeiras III 25/26 Puma Masculina Torcedor', 379.99, 20, 'M'),
(10, '', 'https://imgcentauro-a.akamaihd.net/800x800/99437707A2.jpg', 'Camisa do Palmeiras I 25 Puma Masculina Torcedor', 349.99, 20, 'G'),
(11, '', 'https://imgcentauro-a.akamaihd.net/800x800/99437801A2.jpg', 'Camisa do Palmeiras II 25 Puma Masculina Torcedor', 349.99, 20, 'M'),
(12, '', 'https://imgcentauro-a.akamaihd.net/800x800/9971LI02A2.jpg', 'Camisa do Manchester City II 25/26 Puma Torcedor Masculina', 399.99, 20, 'M'),
(13, '', 'https://imgcentauro-a.akamaihd.net/800x800/99086204A5.jpg', 'Camisa do Manchester City I 24/25 Puma Masculina Torcedor', 249.99, 20, 'G'),
(14, '', 'https://imgcentauro-a.akamaihd.net/800x800/99030401A4.jpg', 'Camisa do Real Madrid I 24/25 adidas Masculina Torcedor', 249.99, 20, 'M'),
(15, '', 'https://imgcentauro-a.akamaihd.net/800x800/9971A4TKA2.jpg', 'Camisa do Barcelona I 25/26 Nike Masculina Torcedor', 329.99, 20, 'M'),
(16, '', 'https://imgcentauro-a.akamaihd.net/660x660/9972P004A2.jpg', 'Camisa Seleção da Itália I 26/27 Torcedor adidas Masculina', 379.99, 20, 'G');