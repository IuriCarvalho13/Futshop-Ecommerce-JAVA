document.addEventListener('DOMContentLoaded', () => {
    const API_URL = 'http://localhost:8080/api/produtos';
    
    // Elementos de Segurança e Controle
    const loggedInUser = localStorage.getItem('loggedInUserName');
    const isAdmin = localStorage.getItem('isAdmin');
    const adminNameSpan = document.getElementById('admin-name');
    const logoutButton = document.getElementById('logout-button');

    // Elementos do CRUD
    const productForm = document.getElementById('product-form');
    const submitButton = document.getElementById('submit-button');
    const cancelButton = document.getElementById('cancel-button');
    const productIdInput = document.getElementById('product-id');
    const productNameInput = document.getElementById('product-name');
    const productDescriptionInput = document.getElementById('product-description'); // Novo campo
    const productPriceInput = document.getElementById('product-price');
    const productSizeInput = document.getElementById('product-size'); // Novo campo
    const productStockInput = document.getElementById('product-stock'); // Novo campo
    const productImageInput = document.getElementById('product-image');
    const productTableBody = document.getElementById('product-table-body');
    const adminMessage = document.getElementById('admin-message');


    // --- 1. LÓGICA DE SEGURANÇA ---
    if (isAdmin !== 'true') {
        window.location.href = '../index.html';
        return;
    }

    if (adminNameSpan && loggedInUser) {
        adminNameSpan.textContent = loggedInUser;
    }
    
    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('isAdmin');
        localStorage.removeItem('loggedInUserName');
        window.location.href = '../index.html';
    });


    // --- 2. LÓGICA DE MENSAGEM ---
    function showMessage(text, type) {
        adminMessage.textContent = text;
        adminMessage.className = `admin-message ${type}`;
        setTimeout(() => {
            adminMessage.textContent = '';
            adminMessage.className = 'admin-message';
        }, 5000);
    }


    // --- 3. FUNÇÕES CRUD ---

    // A. Carregar Produtos
    async function loadProducts() {
        productTableBody.innerHTML = '<tr><td colspan="6">Carregando produtos...</td></tr>';
        try {
            const response = await fetch(API_URL);
            const products = await response.json();
            
            productTableBody.innerHTML = ''; // Limpa a mensagem de carregamento
            
            if (products.length === 0) {
                productTableBody.innerHTML = '<tr><td colspan="6">Nenhum produto cadastrado.</td></tr>';
                return;
            }

            products.forEach(product => {
                const row = productTableBody.insertRow();
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
            
            // Adiciona listeners para os novos botões
            document.querySelectorAll('.btn-action.edit').forEach(button => {
                button.addEventListener('click', () => editProduct(button.dataset.id));
            });
            document.querySelectorAll('.btn-action.delete').forEach(button => {
                button.addEventListener('click', () => deleteProduct(button.dataset.id));
            });

        } catch (error) {
            console.error('Erro ao carregar produtos:', error);
            productTableBody.innerHTML = '<tr><td colspan="6" class="admin-message error">Falha ao carregar produtos. Verifique o Backend.</td></tr>';
        }
    }

    // B. Submeter Formulário (Criar ou Atualizar)
    productForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const isEditing = productIdInput.value !== '';
        const method = isEditing ? 'PUT' : 'POST';
        const url = isEditing ? `${API_URL}/${productIdInput.value}` : API_URL;

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
                showMessage(`Produto ${productData.nome} ${isEditing ? 'atualizado' : 'criado'} com sucesso!`, 'success');
                productForm.reset();
                productIdInput.value = ''; // Limpa o ID para voltar ao modo de criação
                submitButton.textContent = 'Adicionar Produto';
                cancelButton.style.display = 'none';
                loadProducts(); // Recarrega a lista
            } else {
                const errorText = await response.text();
                console.error(`Erro na API (${method} ${url}):`, errorText);
                showMessage(`Erro ao ${isEditing ? 'atualizar' : 'criar'} produto: ${errorText}`, 'error');
            }
        } catch (error) {
            console.error('Erro de rede/conexão:', error);
            showMessage('Erro de conexão. Verifique se o backend está rodando.', 'error');
        }
    });

    // C. Modo Edição
    async function editProduct(id) {
        try {
            const response = await fetch(`${API_URL}/${id}`);
            const product = await response.json();

            // Preenche o formulário
            productIdInput.value = product.id;
            productNameInput.value = product.nome;
            productDescriptionInput.value = product.descricao || '';
            productPriceInput.value = product.preco;
            productSizeInput.value = product.tamanho;
            productStockInput.value = product.quantidadeEmEstoque;
            productImageInput.value = product.imagemUrl;

            submitButton.textContent = 'Salvar Alterações';
            cancelButton.style.display = 'inline-block';
            window.scrollTo(0, 0); // Leva o usuário de volta ao formulário

        } catch (error) {
            console.error('Erro ao buscar produto para edição:', error);
            showMessage('Erro ao buscar dados do produto.', 'error');
        }
    }

    // D. Cancelar Edição
    cancelButton.addEventListener('click', () => {
        productForm.reset();
        productIdInput.value = '';
        submitButton.textContent = 'Adicionar Produto';
        cancelButton.style.display = 'none';
    });

    // E. Excluir Produto
    async function deleteProduct(id) {
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

    // Inicia o carregamento da lista de produtos
    loadProducts();
});