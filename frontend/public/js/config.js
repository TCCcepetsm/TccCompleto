// Configuração centralizada da API
const API_CONFIG = {
    // URL base da API - será definida automaticamente baseada no ambiente
    BASE_URL: (() => {
        // Se estiver em produção (Render), usar a URL do backend no Render
        if (window.location.hostname.includes('onrender.com') || 
            window.location.protocol === 'https:') {
            return 'https://gravacao-backend.onrender.com/api';
        }
        // Se estiver em desenvolvimento local
        return 'http://localhost:8080/api';
    })(),
    
    // Endpoints da API
    ENDPOINTS: {
        // Autenticação
        LOGIN: '/auth/login',
        REGISTER: '/auth/register',
        REFRESH_TOKEN: '/auth/refresh',
        
        // Galeria
        GALERIA_UPLOAD: '/gravacao/upload',
        GALERIA_LISTAR: '/gravacao/imagens',
        GALERIA_POR_TIPO: '/gravacao/imagens/tipo',
        GALERIA_DELETAR: '/gravacao/imagens',
        
        // Agendamentos
        AGENDAMENTOS_LISTAR: '/agendamentos',
        AGENDAMENTOS_CRIAR: '/agendamentos',
        AGENDAMENTOS_ATUALIZAR: '/agendamentos',
        AGENDAMENTOS_DELETAR: '/agendamentos',
        AGENDAMENTOS_POR_DATA: '/agendamentos/data',
        AGENDAMENTOS_POR_USUARIO: '/agendamentos/usuario'
    },
    
    // Headers padrão
    getHeaders: () => {
        const headers = {
            'Content-Type': 'application/json'
        };
        
        const token = localStorage.getItem('authToken');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        
        return headers;
    },
    
    // Headers para upload de arquivos
    getUploadHeaders: () => {
        const headers = {};
        
        const token = localStorage.getItem('authToken');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        
        return headers;
    }
};

// Função utilitária para fazer requisições à API
const apiRequest = async (endpoint, options = {}) => {
    const url = API_CONFIG.BASE_URL + endpoint;
    
    const defaultOptions = {
        headers: API_CONFIG.getHeaders(),
        ...options
    };
    
    try {
        const response = await fetch(url, defaultOptions);
        
        // Se o token expirou, redirecionar para login
        if (response.status === 401) {
            localStorage.removeItem('authToken');
            localStorage.removeItem('userInfo');
            window.location.href = '/views/login.html';
            return null;
        }
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Erro HTTP: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('Erro na requisição:', error);
        throw error;
    }
};

// Função utilitária para upload de arquivos
const apiUpload = async (endpoint, formData) => {
    const url = API_CONFIG.BASE_URL + endpoint;
    
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: API_CONFIG.getUploadHeaders(),
            body: formData
        });
        
        if (response.status === 401) {
            localStorage.removeItem('authToken');
            localStorage.removeItem('userInfo');
            window.location.href = '/views/login.html';
            return null;
        }
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Erro HTTP: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('Erro no upload:', error);
        throw error;
    }
};

// Função para verificar se o usuário está autenticado
const isAuthenticated = () => {
    const token = localStorage.getItem('authToken');
    const userInfo = localStorage.getItem('userInfo');
    return !!(token && userInfo);
};

// Função para obter informações do usuário
const getUserInfo = () => {
    const userInfo = localStorage.getItem('userInfo');
    return userInfo ? JSON.parse(userInfo) : null;
};

// Função para logout
const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userInfo');
    window.location.href = '/views/login.html';
};

// Verificar autenticação em páginas protegidas
const checkAuthOnProtectedPage = () => {
    if (!isAuthenticated()) {
        window.location.href = '/views/login.html';
    }
};

// Exportar para uso global
window.API_CONFIG = API_CONFIG;
window.apiRequest = apiRequest;
window.apiUpload = apiUpload;
window.isAuthenticated = isAuthenticated;
window.getUserInfo = getUserInfo;
window.logout = logout;
window.checkAuthOnProtectedPage = checkAuthOnProtectedPage;

