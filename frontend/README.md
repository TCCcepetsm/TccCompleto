# Frontend - Sistema Gravacao

Frontend estático para o sistema de agendamentos esportivos, desenvolvido em HTML, CSS e JavaScript vanilla.

## 🏗️ Arquitetura

- **Tecnologia:** HTML5, CSS3, JavaScript ES6+
- **Estilo:** CSS Grid, Flexbox, Responsive Design
- **Mapas:** Leaflet.js
- **Hospedagem:** Render Static Site
- **CDN:** Global via Render

## 📁 Estrutura

```
frontend/
├── views/                      # Páginas HTML
│   ├── index.html             # Página inicial (será movida para raiz)
│   ├── login.html             # Login
│   ├── register.html          # Registro
│   ├── agendamento.html       # Criar agendamento
│   ├── galeria.html           # Galeria de imagens
│   ├── uploadGaleria.html     # Upload de imagens
│   └── ...                    # Outras páginas
├── public/                     # Assets estáticos
│   ├── css/                   # Estilos
│   │   ├── login.css
│   │   ├── galeria.css
│   │   ├── agendamento.css
│   │   └── ...
│   ├── js/                    # Scripts
│   │   ├── config.js          # Configuração da API
│   │   ├── login.js
│   │   ├── galeria.js
│   │   ├── agendamento.js
│   │   └── ...
│   └── images/                # Imagens estáticas
│       ├── logo.png
│       └── ...
├── index.html                 # Página inicial (raiz)
├── render.yaml               # Configuração Render
└── README.md                 # Este arquivo
```

## 🚀 Executar Localmente

### Pré-requisitos
- Navegador moderno
- Servidor HTTP (Python, Node.js, ou qualquer outro)

### Executar
```bash
cd frontend

# Opção 1: Python
python -m http.server 8000

# Opção 2: Node.js (se tiver http-server instalado)
npx http-server -p 8000

# Opção 3: PHP
php -S localhost:8000
```

Acesse `http://localhost:8000`

## 🔧 Configuração

### API Configuration
O arquivo `public/js/config.js` contém a configuração da API:

```javascript
const API_CONFIG = {
    BASE_URL: (() => {
        // Produção (Render)
        if (window.location.hostname.includes('onrender.com')) {
            return 'https://gravacao-backend.onrender.com/api';
        }
        // Desenvolvimento local
        return 'http://localhost:8080/api';
    })(),
    // ... endpoints
};
```

### Atualizar URL do Backend
Para apontar para seu backend específico, edite `config.js`:

```javascript
return 'https://SEU-BACKEND.onrender.com/api';
```

## 📱 Páginas e Funcionalidades

### 🏠 Página Inicial (`index.html`)
- Dashboard principal
- Navegação para outras seções
- Resumo de agendamentos

### 🔐 Autenticação
- **Login** (`login.html`): Autenticação de usuários
- **Registro** (`register.html`): Cadastro de novos usuários
- **Logout**: Disponível em todas as páginas autenticadas

### 📅 Agendamentos
- **Criar** (`agendamento.html`): Formulário com mapa interativo
- **Listar** (`meusAgendamentos.html`): Agendamentos do usuário
- **Detalhes** (`agendamentoDetalhes.html`): Visualização detalhada

### 📸 Galeria
- **Visualizar** (`galeria.html`): Grid responsivo de imagens
- **Upload** (`uploadGaleria.html`): Upload múltiplo com preview
- **Filtros**: Por tipo de evento e data

### 👥 Administração
- **Dashboard Admin** (`inicialAdmin.html`): Painel administrativo
- **Gerenciar Agendamentos** (`agendamentoAdmin.html`): CRUD completo

## 🎨 Design System

### Cores
```css
:root {
    --primary-color: #007bff;
    --secondary-color: #6c757d;
    --success-color: #28a745;
    --danger-color: #dc3545;
    --warning-color: #ffc107;
    --info-color: #17a2b8;
    --light-color: #f8f9fa;
    --dark-color: #343a40;
}
```

### Tipografia
- **Fonte Principal:** 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif
- **Tamanhos:** 14px (base), 16px (body), 18px+ (headings)

### Layout
- **Grid System:** CSS Grid + Flexbox
- **Breakpoints:**
  - Mobile: < 768px
  - Tablet: 768px - 1024px
  - Desktop: > 1024px

## 📱 Responsividade

### Mobile First
Todos os componentes são desenvolvidos mobile-first:

```css
/* Mobile (padrão) */
.container {
    padding: 1rem;
}

/* Tablet */
@media (min-width: 768px) {
    .container {
        padding: 2rem;
    }
}

/* Desktop */
@media (min-width: 1024px) {
    .container {
        padding: 3rem;
        max-width: 1200px;
        margin: 0 auto;
    }
}
```

### Componentes Responsivos
- **Navigation:** Hamburger menu em mobile
- **Cards:** Stack vertical em mobile, grid em desktop
- **Forms:** Campos full-width em mobile
- **Galeria:** 1 coluna (mobile) → 2 colunas (tablet) → 3+ colunas (desktop)

## 🗺️ Mapas (Leaflet)

### Configuração
```javascript
// Inicializar mapa
const map = L.map('map').setView([-23.5505, -46.6333], 13);

// Adicionar tiles
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

// Adicionar marcador
const marker = L.marker([-23.5505, -46.6333]).addTo(map);
```

### Funcionalidades
- **Seleção de Local:** Click no mapa para definir local do evento
- **Geocoding:** Busca por endereço
- **Marcadores:** Visualização de locais de eventos

## 🔒 Segurança

### Autenticação
```javascript
// Verificar se está logado
const isAuthenticated = () => {
    const token = localStorage.getItem('authToken');
    return !!token;
};

// Redirecionar se não autenticado
const checkAuthOnProtectedPage = () => {
    if (!isAuthenticated()) {
        window.location.href = '/views/login.html';
    }
};
```

### Headers de Segurança
```javascript
// Headers automáticos
const getHeaders = () => {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    const token = localStorage.getItem('authToken');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    return headers;
};
```

## 🚀 Deploy no Render

### Configuração Automática
O arquivo `render.yaml` já está configurado:

```yaml
services:
  - type: static_site
    name: gravacao-frontend
    env: static
    plan: free
    buildCommand: echo "No build needed for static site"
    staticPublishPath: .
```

### Deploy Manual
1. Conecte o repositório no Render
2. Selecione "Static Site"
3. Configure:
   - **Build Command:** `echo "No build needed"`
   - **Publish Directory:** `frontend`
4. Deploy automático a cada push

### Domínio Personalizado
1. No dashboard do Render, vá em "Settings"
2. Em "Custom Domains", adicione seu domínio
3. Configure DNS conforme instruções

## 🔧 Otimizações

### Performance
- **Minificação:** CSS e JS minificados para produção
- **Compressão:** Gzip automático via Render
- **CDN:** Distribuição global automática
- **Lazy Loading:** Imagens carregadas sob demanda

### SEO
```html
<!-- Meta tags essenciais -->
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Sistema de agendamentos esportivos">
<meta name="keywords" content="esportes, agendamento, eventos">
<title>Gravacao - Agendamentos Esportivos</title>
```

### PWA (Futuro)
Estrutura preparada para Progressive Web App:
- Service Worker ready
- Manifest.json preparado
- Offline-first strategy

## 🐛 Troubleshooting

### Problemas Comuns

#### CORS Error
```
Access to fetch at 'backend-url' has been blocked by CORS policy
```
**Solução:** Verificar se backend está configurado com CORS correto

#### 404 em Rotas
```
Cannot GET /views/pagina.html
```
**Solução:** Verificar se arquivo existe e caminho está correto

#### JavaScript não carrega
```
Uncaught ReferenceError: API_CONFIG is not defined
```
**Solução:** Verificar se `config.js` está sendo carregado antes dos outros scripts

### Debug
```javascript
// Habilitar logs detalhados
localStorage.setItem('debug', 'true');

// Ver configuração atual
console.log('API Config:', API_CONFIG);

// Testar conectividade
fetch(API_CONFIG.BASE_URL + '/health')
    .then(r => r.json())
    .then(console.log);
```

## 📚 Dependências Externas

### CDN Libraries
```html
<!-- Leaflet (Mapas) -->
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

<!-- Font Awesome (Ícones) -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
```

### Sem Build Process
- **Vantagem:** Deploy simples, sem dependências
- **Desvantagem:** Sem transpilação, bundling automático
- **Compatibilidade:** ES6+ (navegadores modernos)

## 🔄 Atualizações Futuras

### Melhorias Planejadas
- [ ] Service Worker para offline
- [ ] Push notifications
- [ ] Dark mode
- [ ] Internacionalização (i18n)
- [ ] Testes automatizados
- [ ] Bundle optimization

### Migração para Framework
Se necessário migrar para React/Vue:
1. Manter estrutura de pastas
2. Converter componentes gradualmente
3. Manter compatibilidade de API

## 📄 Licença

Este projeto está sob a licença MIT.

