// Aguarda todo o HTML ser carregado antes de executar o script
document.addEventListener('DOMContentLoaded', () => {

    // URL base da API de produtos
    const API_URL = 'http://localhost:8080/api/produtos';
    
    // Recupera nome do usuário logado salvo no navegador
    const loggedInUser = localStorage.getItem('loggedInUserName');

    // Verifica se o usuário é admin (salvo no login)
    const isAdmin = localStorage.getItem('isAdmin');

    // Elemento HTML onde aparece o nome do administrador
    const adminNameSpan = document.getElementById('admin-name');

    // Botão de logout
    const logoutButton = document.getElementById('logout-button');


    // ---------- ELEMENTOS DO FORMULÁRIO E TABELA ----------

    const productForm = document.getElementById('product-form'); // Formulário
    const submitButton = document.getElementById('submit-button'); // Botão de adicionar/salvar
    const cancelButton = document.getElementById('cancel-button'); // Botão de cancelar edição

    const productIdInput = document.getElementById('product-id'); // Campo oculto de ID
    const productNameInput = document.getElementById('product-name'); // Nome
    const productDescriptionInput = document.getElementById('product-description'); // Descrição
    const productPriceInput = document.getElementById('product-price'); // Preço
    const productSizeInput = document.getElementById('product-size'); // Tamanho
    const productStockInput = document.getElementById('product-stock'); // Quantidade em estoque
    const productImageInput = document.getElementById('product-image'); // URL ou Base64 da imagem

    const productTableBody = document.getElementById('product-table-body'); // Corpo da tabela de produtos
    const adminMessage = document.getElementById('admin-message'); // Campo de mensagens


    // ---------- (1) SISTEMA DE SEGURANÇA ----------

    // Se não for admin, bloqueia o acesso
    if (isAdmin !== 'true') {
        window.location.href = '../index.html'; // Redireciona para página inicial
        return; // Impede execução do restante
    }

    // Exibe o nome do administrador na interface
    if (adminNameSpan && loggedInUser) {
        adminNameSpan.textContent = loggedInUser;
    }
    
    // Evento de logout
    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('isAdmin'); // Remove flag de admin
        localStorage.removeItem('loggedInUserName'); // Remove nome
        window.location.href = '../index.html'; // Redireciona
    });


    // ---------- (2) FUNÇÃO PARA MOSTRAR MENSAGENS ----------

    function showMessage(text, type) {
        adminMessage.textContent = text; // Define o texto
        adminMessage.className = `admin-message ${type}`; // Define estilo (success/error)

        // Remove mensagem após 5 segundos
        setTimeout(() => {
            adminMessage.textContent = '';
            adminMessage.className = 'admin-message';
        }, 5000);
    }


    // ---------- (3) FUNÇÕES CRUD (CREATE / READ / UPDATE / DELETE) ----------

    // A) CARREGAR LISTA DE PRODUTOS
    async function loadProducts() {

        // Mostra mensagem de carregamento
        productTableBody.innerHTML = '<tr><td colspan="6">Carregando produtos...</td></tr>';

        try {
            const response = await fetch(API_URL); // Consulta API
            const products = await response.json(); // Converte para JSON
            
            // Limpa tabela
            productTableBody.innerHTML = '';

            // Caso não tenha produtos cadastrados
            if (products.length === 0) {
                productTableBody.innerHTML = '<tr><td colspan="6">Nenhum produto cadastrado.</td></tr>';
                return;
            }

            // Monta cada linha da tabela
            products.forEach(product => {
                const row = productTableBody.insertRow(); // Nova linha
                row.innerHTML = `
                    <td>${product.id}</td>
                    <td>${product.nome}</td>
                    <td>R$ ${product.preco.toFixed(2)}</td>
                    <td>${product.quantidadeEmEstoque}</td>
                    <td>${product.tamanho}</td>
                    <td>
                        <button class="btn-action edit" data-id="${product.id}">Editar</button>
                        <button class="btn-action delete" data-id="${product.id}">Excluir</button>
                    </td>
                `;
            });
            
            // Liga botões "Editar"
            document.querySelectorAll('.btn-action.edit').forEach(button => {
                button.addEventListener('click', () => editProduct(button.dataset.id));
            });

            // Liga botões "Excluir"
            document.querySelectorAll('.btn-action.delete').forEach(button => {
                button.addEventListener('click', () => deleteProduct(button.dataset.id));
            });

        } catch (error) {
            console.error('Erro ao carregar produtos:', error);

            // Mensagem de erro direto na tabela
            productTableBody.innerHTML =
                '<tr><td colspan="6" class="admin-message error">Falha ao carregar produtos. Verifique o Backend.</td></tr>';
        }
    }


    // B) SUBMISSÃO DO FORMULÁRIO (CRIAR OU ATUALIZAR PRODUTO)
    productForm.addEventListener('submit', async (e) => {
        e.preventDefault(); // Impede reload da página
        
        // Verifica se estamos editando
        const isEditing = productIdInput.value !== '';

        // Define método HTTP
        const method = isEditing ? 'PUT' : 'POST';

        // Define URL correta
        const url = isEditing ? `${API_URL}/${productIdInput.value}` : API_URL;

        // Objeto enviado para API
        const productData = {
            id: isEditing ? parseInt(productIdInput.value) : undefined,
            nome: productNameInput.value,
            descricao: productDescriptionInput.value,
            preco: parseFloat(productPriceInput.value),
            tamanho: productSizeInput.value,
            quantidadeEmEstoque: parseInt(productStockInput.value),
            imagemUrl: productImageInput.value
        };

        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(productData)
            });

            if (response.ok) {
                showMessage(
                    `Produto ${productData.nome} ${isEditing ? 'atualizado' : 'criado'} com sucesso!`,
                    'success'
                );

                productForm.reset(); // Limpa formulário
                productIdInput.value = ''; // Sai do modo edição
                submitButton.textContent = 'Adicionar Produto'; // Ajusta botão
                cancelButton.style.display = 'none'; // Esconde cancelar

                loadProducts(); // Atualiza tabela
            } else {
                const errorText = await response.text();
                console.error(`Erro na API (${method} ${url}):`, errorText);

                showMessage(
                    `Erro ao ${isEditing ? 'atualizar' : 'criar'} produto: ${errorText}`,
                    'error'
                );
            }

        } catch (error) {
            console.error('Erro de rede/conexão:', error);
            showMessage('Erro de conexão. Verifique se o backend está rodando.', 'error');
        }
    });


    // C) EDITAR PRODUTO
    async function editProduct(id) {
        try {
            const response = await fetch(`${API_URL}/${id}`); // Busca dados
            const product = await response.json(); // Converte para JSON

            // Preenche formulário para edição
            productIdInput.value = product.id;
            productNameInput.value = product.nome;
            productDescriptionInput.value = product.descricao || '';
            productPriceInput.value = product.preco;
            productSizeInput.value = product.tamanho;
            productStockInput.value = product.quantidadeEmEstoque;
            productImageInput.value = product.imagemUrl;

            submitButton.textContent = 'Salvar Alterações'; // Muda texto
            cancelButton.style.display = 'inline-block'; // Mostra botão cancelar

            // Rola a página para cima
            window.scrollTo(0, 0);

        } catch (error) {
            console.error('Erro ao buscar produto para edição:', error);
            showMessage('Erro ao buscar dados do produto.', 'error');
        }
    }


    // D) CANCELAR MODO EDIÇÃO
    cancelButton.addEventListener('click', () => {
        productForm.reset(); // Limpa campos
        productIdInput.value = ''; // Sai do modo edição
        submitButton.textContent = 'Adicionar Produto'; // Restaura nome do botão
        cancelButton.style.display = 'none'; // Esconde o botão
    });


    // E) EXCLUIR PRODUTO
    async function deleteProduct(id) {

        // Confirma a exclusão com o usuário
        if (!confirm(`Tem certeza que deseja excluir o produto ID ${id}?`)) {
            return;
        }

        try {
            const response = await fetch(`${API_URL}/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                showMessage(`Produto ID ${id} excluído com sucesso!`, 'success');
                loadProducts();
            } else {
                showMessage(`Erro ao excluir produto ID ${id}.`, 'error');
            }

        } catch (error) {
            console.error('Erro ao excluir produto:', error);
            showMessage('Erro de conexão ao excluir produto.', 'error');
        }
    }


    // Carrega lista de produtos assim que a página abrir
    loadProducts();
});
