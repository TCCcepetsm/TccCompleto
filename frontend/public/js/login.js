// Verificar se já está logado ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    if (isAuthenticated()) {
        // Se já está logado, redirecionar para a página principal
        window.location.href = '/views/inicial.html';
        return;
    }
    
    // Configurar formulário de login
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    // Configurar link para registro
    const registerLink = document.getElementById('registerLink');
    if (registerLink) {
        registerLink.addEventListener('click', (e) => {
            e.preventDefault();
            window.location.href = '/views/register.html';
        });
    }
});

// Função para fazer login
async function handleLogin(event) {
    event.preventDefault();
    
    const form = event.target;
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    
    // Validações básicas
    if (!email) {
        showError('Por favor, digite seu email.');
        return;
    }
    
    if (!password) {
        showError('Por favor, digite sua senha.');
        return;
    }
    
    // Mostrar indicador de carregamento
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Entrando...';
    submitBtn.disabled = true;
    
    try {
        // Fazer requisição de login
        const response = await fetch(API_CONFIG.BASE_URL + API_CONFIG.ENDPOINTS.LOGIN, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Erro ao fazer login');
        }
        
        const result = await response.json();
        
        if (result.token) {
            // Salvar token e informações do usuário
            localStorage.setItem('authToken', result.token);
            localStorage.setItem('userInfo', JSON.stringify({
                id: result.userId,
                email: result.email,
                nome: result.nome,
                role: result.role
            }));
            
            showSuccess('Login realizado com sucesso! Redirecionando...');
            
            // Redirecionar após 1 segundo
            setTimeout(() => {
                window.location.href = '/views/inicial.html';
            }, 1000);
            
        } else {
            throw new Error('Token não recebido do servidor');
        }
        
    } catch (error) {
        console.error('Erro no login:', error);
        showError('Erro ao fazer login: ' + error.message);
        
    } finally {
        // Restaurar botão
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}

// Função para mostrar mensagem de erro
function showError(message) {
    const errorDiv = document.getElementById('errorMessage') || createMessageDiv('errorMessage', 'error-message');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    
    // Esconder após 5 segundos
    setTimeout(() => {
        errorDiv.style.display = 'none';
    }, 5000);
}

// Função para mostrar mensagem de sucesso
function showSuccess(message) {
    const successDiv = document.getElementById('successMessage') || createMessageDiv('successMessage', 'success-message');
    successDiv.textContent = message;
    successDiv.style.display = 'block';
    
    // Esconder após 3 segundos
    setTimeout(() => {
        successDiv.style.display = 'none';
    }, 3000);
}

// Função para criar div de mensagem
function createMessageDiv(id, className) {
    const div = document.createElement('div');
    div.id = id;
    div.className = className;
    div.style.display = 'none';
    
    // Inserir antes do formulário
    const form = document.getElementById('loginForm');
    if (form) {
        form.parentNode.insertBefore(div, form);
    }
    
    return div;
}

// Função para alternar visibilidade da senha
function togglePassword() {
    const passwordInput = document.getElementById('password');
    const toggleBtn = document.querySelector('.toggle-password');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleBtn.textContent = '🙈';
    } else {
        passwordInput.type = 'password';
        toggleBtn.textContent = '👁️';
    }
}

// Adicionar funcionalidade de Enter para submeter o formulário
document.addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        const loginForm = document.getElementById('loginForm');
        if (loginForm && document.activeElement.form === loginForm) {
            event.preventDefault();
            loginForm.dispatchEvent(new Event('submit'));
        }
    }
});

// Função para limpar mensagens de erro/sucesso quando o usuário começar a digitar
document.addEventListener('DOMContentLoaded', () => {
    const inputs = document.querySelectorAll('#loginForm input');
    inputs.forEach(input => {
        input.addEventListener('input', () => {
            const errorDiv = document.getElementById('errorMessage');
            const successDiv = document.getElementById('successMessage');
            
            if (errorDiv) errorDiv.style.display = 'none';
            if (successDiv) successDiv.style.display = 'none';
        });
    });
});

