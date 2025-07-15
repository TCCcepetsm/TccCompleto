// Verificar se já está logado ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    if (isAuthenticated()) {
        // Se já está logado, redirecionar para a página principal
        window.location.href = '/views/inicial.html';
        return;
    }
    
    // Configurar formulário de registro
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
    
    // Configurar link para login
    const loginLink = document.getElementById('loginLink');
    if (loginLink) {
        loginLink.addEventListener('click', (e) => {
            e.preventDefault();
            window.location.href = '/views/login.html';
        });
    }
    
    // Configurar validação em tempo real
    setupRealTimeValidation();
});

// Função para fazer registro
async function handleRegister(event) {
    event.preventDefault();
    
    const form = event.target;
    const nome = document.getElementById('nome').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    // Validações
    const validationErrors = validateForm(nome, email, password, confirmPassword);
    if (validationErrors.length > 0) {
        showError(validationErrors.join('\n'));
        return;
    }
    
    // Mostrar indicador de carregamento
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Criando conta...';
    submitBtn.disabled = true;
    
    try {
        // Fazer requisição de registro
        const response = await fetch(API_CONFIG.BASE_URL + API_CONFIG.ENDPOINTS.REGISTER, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                nome: nome,
                email: email,
                password: password
            })
        });
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Erro ao criar conta');
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
            
            showSuccess('Conta criada com sucesso! Redirecionando...');
            
            // Redirecionar após 2 segundos
            setTimeout(() => {
                window.location.href = '/views/inicial.html';
            }, 2000);
            
        } else {
            showSuccess('Conta criada com sucesso! Redirecionando para login...');
            
            // Redirecionar para login após 2 segundos
            setTimeout(() => {
                window.location.href = '/views/login.html';
            }, 2000);
        }
        
    } catch (error) {
        console.error('Erro no registro:', error);
        showError('Erro ao criar conta: ' + error.message);
        
    } finally {
        // Restaurar botão
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}

// Função para validar formulário
function validateForm(nome, email, password, confirmPassword) {
    const errors = [];
    
    // Validar nome
    if (!nome || nome.length < 2) {
        errors.push('Nome deve ter pelo menos 2 caracteres.');
    }
    
    // Validar email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email) {
        errors.push('Email é obrigatório.');
    } else if (!emailRegex.test(email)) {
        errors.push('Email deve ter um formato válido.');
    }
    
    // Validar senha
    if (!password) {
        errors.push('Senha é obrigatória.');
    } else if (password.length < 6) {
        errors.push('Senha deve ter pelo menos 6 caracteres.');
    }
    
    // Validar confirmação de senha
    if (!confirmPassword) {
        errors.push('Confirmação de senha é obrigatória.');
    } else if (password !== confirmPassword) {
        errors.push('Senhas não coincidem.');
    }
    
    return errors;
}

// Função para configurar validação em tempo real
function setupRealTimeValidation() {
    const nome = document.getElementById('nome');
    const email = document.getElementById('email');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    
    // Validação do nome
    if (nome) {
        nome.addEventListener('blur', () => {
            const value = nome.value.trim();
            if (value && value.length < 2) {
                showFieldError(nome, 'Nome deve ter pelo menos 2 caracteres.');
            } else {
                clearFieldError(nome);
            }
        });
    }
    
    // Validação do email
    if (email) {
        email.addEventListener('blur', () => {
            const value = email.value.trim();
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (value && !emailRegex.test(value)) {
                showFieldError(email, 'Email deve ter um formato válido.');
            } else {
                clearFieldError(email);
            }
        });
    }
    
    // Validação da senha
    if (password) {
        password.addEventListener('input', () => {
            const value = password.value;
            if (value && value.length < 6) {
                showFieldError(password, 'Senha deve ter pelo menos 6 caracteres.');
            } else {
                clearFieldError(password);
            }
            
            // Revalidar confirmação se já foi preenchida
            if (confirmPassword && confirmPassword.value) {
                validatePasswordConfirmation();
            }
        });
    }
    
    // Validação da confirmação de senha
    if (confirmPassword) {
        confirmPassword.addEventListener('input', validatePasswordConfirmation);
    }
    
    function validatePasswordConfirmation() {
        const passwordValue = password.value;
        const confirmValue = confirmPassword.value;
        
        if (confirmValue && passwordValue !== confirmValue) {
            showFieldError(confirmPassword, 'Senhas não coincidem.');
        } else {
            clearFieldError(confirmPassword);
        }
    }
}

// Função para mostrar erro em campo específico
function showFieldError(field, message) {
    clearFieldError(field);
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'field-error';
    errorDiv.textContent = message;
    
    field.parentNode.appendChild(errorDiv);
    field.classList.add('error');
}

// Função para limpar erro de campo específico
function clearFieldError(field) {
    const existingError = field.parentNode.querySelector('.field-error');
    if (existingError) {
        existingError.remove();
    }
    field.classList.remove('error');
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
    const form = document.getElementById('registerForm');
    if (form) {
        form.parentNode.insertBefore(div, form);
    }
    
    return div;
}

// Função para alternar visibilidade da senha
function togglePassword(fieldId) {
    const passwordInput = document.getElementById(fieldId);
    const toggleBtn = passwordInput.parentNode.querySelector('.toggle-password');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleBtn.textContent = '🙈';
    } else {
        passwordInput.type = 'password';
        toggleBtn.textContent = '👁️';
    }
}

// Função para limpar mensagens quando o usuário começar a digitar
document.addEventListener('DOMContentLoaded', () => {
    const inputs = document.querySelectorAll('#registerForm input');
    inputs.forEach(input => {
        input.addEventListener('input', () => {
            const errorDiv = document.getElementById('errorMessage');
            const successDiv = document.getElementById('successMessage');
            
            if (errorDiv) errorDiv.style.display = 'none';
            if (successDiv) successDiv.style.display = 'none';
        });
    });
});

