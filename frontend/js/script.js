// URL do seu Backend (Spring Boot está rodando aqui)
const API_URL = 'http://localhost:8080/api/produtos';

// Variável para armazenar e persistir o carrinho
let cart = JSON.parse(localStorage.getItem('shoppingCart')) || [];

document.addEventListener('DOMContentLoaded', () => {
    // Tenta buscar os produtos assim que a página é carregada
    fetchProducts();
    
    // --- Configuração dos elementos do modal de Login/Cadastro ---
    
    const modal = document.getElementById("login-modal");
    const userDisplay = document.getElementById("open-login-modal"); 
    const closeBtn = document.querySelector("#login-modal .close-button");
    const showRegisterLink = document.getElementById("show-register");
    const showLoginLink = document.getElementById("show-login");
    const registerSection = document.querySelector(".register-section");
    const loginSection = document.querySelector(".auth-section:not(.register-section)");

    const registerForm = document.getElementById("register-form");
    const loginForm = document.getElementById("login-form");
    
    const logoutButtonClient = document.getElementById('logout-button-client');

    // Variável global para guardar o nome após o login
    let loggedInUser = localStorage.getItem('loggedInUserName');
    
    // --- Configuração dos elementos do Carrinho ---
    const cartModal = document.getElementById('cart-modal');
    const cartIcon = document.getElementById('open-cart-modal'); 
    const closeCartBtn = document.getElementById('close-cart-modal');
    const cartItemsContainer = document.getElementById('cart-items');
    const cartTotalElement = document.getElementById('cart-total');
    const checkoutButton = document.getElementById('checkout-button');
    const clearCartButton = document.getElementById('clear-cart-button');

    // Inicializa o contador do carrinho
    updateCartCount();
    
    // --- NOVO: LÓGICA DE PESQUISA ---
    const searchForm = document.getElementById('search-form'); // ID do FORM no seu HTML
    const searchInput = document.getElementById('search-input'); // ID do INPUT no seu HTML

    if (searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault(); // Impede o envio tradicional do formulário
            const searchTerm = searchInput.value;
            fetchProducts(searchTerm); // Chama a função de busca AGORA COM O TERMO
        });
    }

    // --- LÓGICA DE LOGOUT ---
    function handleLogout() {
        // Limpa os dados de sessão do cliente
        localStorage.removeItem('loggedInUserName');
        localStorage.removeItem('isAdmin'); 
        
        // Atualiza a variável local e a exibição
        loggedInUser = null;
        updateUserDisplay();
    }

    // Adiciona o listener de clique ao botão de Sair
    if (logoutButtonClient) {
        logoutButtonClient.addEventListener('click', handleLogout);
    }
    
    // --- Função para Atualizar o Link do Usuário E O BOTÃO SAIR ---

    function updateUserDisplay() {
        // Verifica se é administrador
        const isAdmin = localStorage.getItem('isAdmin') === 'true';

        if (loggedInUser && !isAdmin) {
            // Cliente Logado
            userDisplay.textContent = `Olá, ${loggedInUser.toUpperCase()}`; 
            userDisplay.style.fontWeight = 'bold';
            userDisplay.style.cursor = 'default';
            logoutButtonClient.style.display = 'inline-block'; // MOSTRA o botão Sair
        } else if (isAdmin) {
            // Admin Logado (Redirecionamento já feito no Login)
            userDisplay.textContent = `ADMIN MASTER`; 
            userDisplay.style.fontWeight = 'bold';
            userDisplay.href = 'admin/admin.html';
            logoutButtonClient.style.display = 'none';
        } 
        else {
            // Deslogado
            userDisplay.textContent = 'LOGIN/CADASTRO';
            userDisplay.style.fontWeight = 'normal';
            userDisplay.href = '#'; // Volta a ser um link clicável para abrir o modal
            userDisplay.style.cursor = 'pointer';
            logoutButtonClient.style.display = 'none'; // ESCONDE o botão Sair
        }
    }

    updateUserDisplay();

    // --- Funções de Abertura/Fechamento do Modal de Login ---
    
    if (userDisplay) {
        userDisplay.addEventListener('click', (e) => {
            // Só abre o modal se o usuário não estiver logado
            if (!loggedInUser && !localStorage.getItem('isAdmin')) { 
                e.preventDefault();
                modal.style.display = "block";
            } else if (localStorage.getItem('isAdmin') === 'true') {
                return;
            }
        });
    }

    // Restante das funções do modal (fechar, trocar de aba)
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            modal.style.display = "none";
        });
    }

    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = "none";
        }
    });

    if (showRegisterLink && loginSection && registerSection) {
        showRegisterLink.addEventListener('click', (e) => {
            e.preventDefault();
            loginSection.classList.add('hidden');
            registerSection.classList.remove('hidden');
        });
    }

    if (showLoginLink && loginSection && registerSection) {
        showLoginLink.addEventListener('click', (e) => {
            e.preventDefault();
            registerSection.classList.add('hidden');
            loginSection.classList.remove('hidden');
        });
    }
    
    // --- Lógica de Cadastro (Conectado ao Backend) ---
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => { 
            e.preventDefault();
            
            const nameInput = registerForm.querySelector('input[type="text"]').value;
            const emailInput = registerForm.querySelector('input[type="email"]').value;
            const passwordInput = registerForm.querySelector('input[type="password"]').value;

            const userData = {
                nome: nameInput,
                email: emailInput,
                senha: passwordInput
            };

            try {
                const response = await fetch('http://localhost:8080/api/usuarios/cadastrar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(userData)
                });
                
                let feedbackDiv = modal.querySelector('.feedback-message');
                if (!feedbackDiv) {
                    feedbackDiv = document.createElement('div');
                    feedbackDiv.className = 'feedback-message';
                    modal.querySelector('.modal-header').after(feedbackDiv);
                }

                if (response.ok) {
                    const novoUsuario = await response.json();
                    
                    feedbackDiv.innerHTML = `Cadastro realizado com sucesso, ${novoUsuario.nome}!`;
                    feedbackDiv.style.backgroundColor = '#e6ffe6';
                    feedbackDiv.style.color = '#006600';
                    feedbackDiv.style.display = 'block';

                    registerSection.classList.add('hidden');
                    loginSection.classList.add('hidden');
                    
                    setTimeout(() => {
                        loggedInUser = novoUsuario.nome;
                        localStorage.setItem('loggedInUserName', novoUsuario.nome);
                        localStorage.removeItem('isAdmin');
                        updateUserDisplay(); 
                        modal.style.display = "none";
                        feedbackDiv.style.display = 'none';
                        loginSection.classList.remove('hidden');
                        registerSection.classList.add('hidden');
                        registerForm.reset(); 

                    }, 2500); 

                } else {
                    const errorText = await response.text();
                    feedbackDiv.style.backgroundColor = '#ffcccc'; 
                    feedbackDiv.style.color = '#cc0000';
                    feedbackDiv.innerHTML = `Erro no cadastro: ${errorText || 'Verifique se os dados estão corretos.'}`;
                    feedbackDiv.style.display = 'block';

                    setTimeout(() => {
                        feedbackDiv.style.display = 'none';
                    }, 4000);
                }

            } catch (error) {
                console.error("Erro de conexão ao cadastrar:", error);
                alert("Erro de conexão com o servidor. Verifique o Spring Boot.");
            }
        });
    }

    // --- Lógica de Login (Conectado ao Backend, com Checagem Admin) ---
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => { 
            e.preventDefault();
            
            const emailInput = loginForm.querySelector('input[type="email"]').value;
            const passwordInput = loginForm.querySelector('input[type="password"]').value;
            
            const credentials = {
                email: emailInput,
                senha: passwordInput
            };

            try {
                const response = await fetch('http://localhost:8080/api/usuarios/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(credentials)
                });
                
                if (response.ok) {
                    const usuarioLogado = await response.json();
                    
                    loggedInUser = usuarioLogado.nome; 
                    localStorage.setItem('loggedInUserName', usuarioLogado.nome);
                    
                    if (usuarioLogado.admin === true) { 
                        localStorage.setItem('isAdmin', 'true');
                        modal.style.display = "none";
                        window.location.href = 'admin/admin.html'; 
                        return; 
                    }
                    
                    // LOGIN NORMAL (cliente)
                    localStorage.removeItem('isAdmin');
                    updateUserDisplay(); 
                    modal.style.display = "none";
                
                } else {
                    alert("Erro no Login: Email ou senha inválidos.");
                }

            } catch (error) {
                console.error("Erro de conexão ao logar:", error);
                alert("Erro de conexão com o servidor. Verifique o Spring Boot.");
            }
        });
    }

    // --- LÓGICA DO CARRINHO (NOVA) ---

    // Abre/Fecha o Modal do Carrinho
    cartIcon.addEventListener('click', (e) => {
        e.preventDefault(); // Impede que o link vá para '#'
        renderCart(); // Renderiza antes de abrir para garantir que está atualizado
        cartModal.style.display = 'block';
    });
    
    closeCartBtn.addEventListener('click', () => {
        cartModal.style.display = 'none';
    });
    
    // Permite fechar clicando fora
    window.addEventListener('click', (e) => {
        if (e.target === cartModal) {
            cartModal.style.display = 'none';
        }
    });

    // Listener de eventos delegado para Adicionar, Remover e Atualizar Quantidade
    document.addEventListener('click', (e) => {
        // ADICIONAR AO CARRINHO
        if (e.target.classList.contains('add-to-cart')) {
            const productId = e.target.dataset.id;
            handleAddToCart(productId); 
        }
        // REMOVER DO CARRINHO
        if (e.target.classList.contains('remove-from-cart')) {
            const productId = e.target.dataset.id;
            removeFromCart(productId); 
        }
    });

    // Listener delegado para atualização de quantidade (em inputs)
    document.addEventListener('change', (e) => {
        if (e.target.classList.contains('update-qty')) {
             const productId = e.target.dataset.id;
             // Garante que o valor seja um número inteiro positivo
             const newQty = Math.max(1, parseInt(e.target.value) || 1); 
             e.target.value = newQty; // Corrige o valor exibido no input
             updateQuantity(productId, newQty);
        }
    });
    
    // Limpar Carrinho
    clearCartButton.addEventListener('click', () => {
        if (confirm('Tem certeza que deseja limpar o carrinho?')) {
            cart = [];
            localStorage.setItem('shoppingCart', JSON.stringify(cart));
            renderCart();
            updateCartCount();
        }
    });

    // Finalizar Compra (Checkout)
    checkoutButton.addEventListener('click', () => {
        handleCheckout();
    });
});

// --- FUNÇÕES GLOBAIS (FORA DO DOMContentLoaded) ---

// Função de busca de produtos (AGORA ACEITA UM TERMO)
async function fetchProducts(searchTerm = '') {
    const productListDiv = document.getElementById('product-list');
    
    productListDiv.innerHTML = '<p>Carregando produtos...</p>'; 

    try {
        // Constrói a URL: se houver termo, adiciona "?termo=..."
        const url = searchTerm 
            ? `${API_URL}?termo=${encodeURIComponent(searchTerm)}` 
            : API_URL; 
            
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error(`Erro ao buscar dados: ${response.status}`);
        }
        
        const products = await response.json();
        
        if (products.length === 0) {
            // Mensagem diferente se for uma pesquisa que não encontrou resultados
            const message = searchTerm 
                ? `<p>Nenhum produto encontrado para "${searchTerm}".</p>`
                : '<p>Nenhum produto cadastrado no momento.</p>';
            productListDiv.innerHTML = message;
            productListDiv.className = ''; 
        } else {
            renderProducts(products);
        }
        
    } catch (error) {
        console.error("Erro ao conectar ou buscar produtos:", error);
        productListDiv.innerHTML = 
            '<p style="color: #AA4444; font-weight: bold;">[ERRO] Não foi possível carregar os produtos. Verifique se o backend (porta 8080) está rodando e se há erro no console.</p>';
    }
}

// Função de renderização
function renderProducts(products) {
    const productListDiv = document.getElementById('product-list');
    productListDiv.innerHTML = ''; 

    productListDiv.className = 'product-grid';

    products.forEach(produto => {
        const card = document.createElement('div');
        card.className = 'product-card';
        
        const imageUrl = produto.imagemUrl || 'placeholder-pb.jpg'; 
        
        // CORREÇÃO: Adicionando Tamanho e Estoque ao HTML do card
        card.innerHTML = `
            <img src="${imageUrl}" alt="${produto.nome}">
            <h3>${produto.nome}</h3>
            
            <p>Tamanho: <strong>${produto.tamanho || 'N/A'}</strong></p>
            <p>Estoque: <strong>${produto.quantidadeEmEstoque || '0'}</strong></p>
            
            <p class="price">R$ ${produto.preco ? produto.preco.toFixed(2).replace('.', ',') : 'N/A'}</p>
            <button class="add-to-cart" data-id="${produto.id}">ADICIONAR</button>
        `;
        
        productListDiv.appendChild(card);
    });
}


// --- FUNÇÕES DE LÓGICA DO CARRINHO ---

// Atualiza o contador de itens no ícone do carrinho
function updateCartCount() {
    const cartCountElement = document.querySelector('.cart-count');
    // Calcula o total de unidades de todos os produtos no carrinho
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0); 
    cartCountElement.textContent = totalItems;
}

// Função que gerencia a ação de Adicionar ao Carrinho
async function handleAddToCart(productId) {
    try {
        const response = await fetch(`http://localhost:8080/api/produtos/${productId}`);
        if (!response.ok) {
            throw new Error('Produto não encontrado');
        }
        const produto = await response.json();

        const existingItem = cart.find(item => item.id === productId);

        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            cart.push({
                id: productId,
                name: produto.nome,
                price: produto.preco,
                tamanho: produto.tamanho,
                imagemUrl: produto.imagemUrl,
                quantity: 1
            });
        }

        localStorage.setItem('shoppingCart', JSON.stringify(cart));
        updateCartCount();
        alert(`${produto.nome} (x1) adicionado ao carrinho!`);

    } catch (error) {
        console.error("Erro ao adicionar produto:", error);
        alert("Erro ao adicionar o produto. Verifique a conexão com o servidor.");
    }
}

// Remove um item completamente do carrinho
function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    localStorage.setItem('shoppingCart', JSON.stringify(cart));
    renderCart(); 
    updateCartCount();
}

// Atualiza a quantidade de um item
function updateQuantity(productId, newQty) {
    const item = cart.find(item => item.id === productId);
    if (item && newQty > 0) {
        item.quantity = newQty;
        localStorage.setItem('shoppingCart', JSON.stringify(cart));
        renderCart(); 
        updateCartCount();
    } else if (newQty === 0) {
        removeFromCart(productId);
    }
}


// Renderiza o conteúdo do modal do carrinho
function renderCart() {
    let total = 0;
    const cartItemsContainer = document.getElementById('cart-items');
    const cartTotalElement = document.getElementById('cart-total');

    cartItemsContainer.innerHTML = ''; 

    if (cart.length === 0) {
        cartItemsContainer.innerHTML = '<p style="text-align: center; color: #555; padding: 20px;">Seu carrinho está vazio.</p>';
    } else {
        cart.forEach(item => {
            const itemTotal = item.price * item.quantity;
            total += itemTotal;

            const cartItemDiv = document.createElement('div');
            cartItemDiv.className = 'cart-item-detail';
            
            cartItemDiv.innerHTML = `
                <img src="${item.imagemUrl || 'placeholder-pb.jpg'}" alt="${item.name}">
                <div class="item-info">
                    <p class="item-name">${item.name} (${item.tamanho})</p>
                    <p class="item-price">R$ ${item.price ? item.price.toFixed(2).replace('.', ',') : 'N/A'}</p>
                </div>
                <div class="item-quantity-control">
                    <input type="number" class="update-qty" data-id="${item.id}" value="${item.quantity}" min="1" max="99">
                    <button class="remove-from-cart" data-id="${item.id}">X</button>
                </div>
                <p class="item-subtotal">Subtotal: R$ ${itemTotal ? itemTotal.toFixed(2).replace('.', ',') : 'N/A'}</p>
            `;
            
            cartItemsContainer.appendChild(cartItemDiv);
        });
    }

    // Atualiza o total
    cartTotalElement.textContent = `R$ ${total.toFixed(2).replace('.', ',')}`;
}

// Lógica de Checkout (VERIFICAÇÃO DE LOGIN E ENVIO PARA O BACKEND)
async function handleCheckout() {
    const loggedInUser = localStorage.getItem('loggedInUserName');
    const cartModal = document.getElementById('cart-modal');
    const loginModal = document.getElementById("login-modal");
    const checkoutButton = document.getElementById('checkout-button');

    if (cart.length === 0) {
        alert("Seu carrinho está vazio. Adicione produtos para continuar.");
        return;
    }
    
    if (!loggedInUser) {
        alert("Você precisa estar logado para finalizar a compra.");
        
        // Esconde o modal do carrinho
        cartModal.style.display = 'none';
        
        // Mostra o modal de login automaticamente
        loginModal.style.display = "block";
        
        // Garante que a aba de login está aberta
        document.querySelector(".register-section").classList.add('hidden');
        document.querySelector(".auth-section:not(.register-section)").classList.remove('hidden');
        return;
    }
    
    // --- PASSO 1: Preparar os dados para o Backend ---
    const orderItems = cart.map(item => ({
        id: item.id, 
        nome: item.name, 
        quantidade: item.quantity
    }));
    
    // Desabilitar o botão para evitar cliques duplicados
    checkoutButton.disabled = true;
    checkoutButton.textContent = "Finalizando...";

    try {
        // --- PASSO 2: Enviar pedido para o Backend para Baixa de Estoque ---
        const response = await fetch('http://localhost:8080/api/produtos/checkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderItems)
        });

        if (!response.ok) {
            // Se o backend retornou um erro (ex: estoque insuficiente)
            const errorText = await response.text();
            throw new Error(errorText || "Erro desconhecido ao processar o checkout.");
        }
        
        // --- PASSO 3: Sucesso no processamento ---
        const totalText = document.getElementById('cart-total').textContent;
        alert(`🎉 Pedido finalizado com sucesso por ${loggedInUser}! Total: ${totalText}. 
        
O estoque foi atualizado no sistema.`);
        
        // Limpa o carrinho após a finalização
        cart = [];
        localStorage.removeItem('shoppingCart');
        renderCart();
        updateCartCount();
        cartModal.style.display = 'none';

    } catch (error) {
        console.error("Erro no Checkout:", error);
        alert(`❌ Não foi possível finalizar o pedido. Motivo: ${error.message || 'Verifique o console ou se o backend está rodando.'}`);
        
    } finally {
        // Habilitar o botão novamente
        checkoutButton.disabled = false;
        checkoutButton.textContent = "FINALIZAR COMPRA";
        // Recarrega a lista de produtos na página principal para mostrar o estoque atualizado
        fetchProducts(); 
    }
}