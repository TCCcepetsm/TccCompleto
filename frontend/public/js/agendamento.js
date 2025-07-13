// Verificar autenticação ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    checkAuthOnProtectedPage();
    
    // Configurar formulário de agendamento
    const agendamentoForm = document.getElementById('agendamentoForm');
    if (agendamentoForm) {
        agendamentoForm.addEventListener('submit', handleCreateAgendamento);
    }
    
    // Carregar agendamentos existentes
    loadAgendamentos();
    
    // Configurar filtros
    setupFilters();
    
    // Atualizar informações do usuário na interface
    updateUserInfo();
});

// Função para atualizar informações do usuário
function updateUserInfo() {
    const userInfo = getUserInfo();
    if (userInfo) {
        const userNameElement = document.getElementById('userName');
        if (userNameElement) {
            userNameElement.textContent = userInfo.nome;
        }
    }
}

// Função para criar agendamento
async function handleCreateAgendamento(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    
    const agendamento = {
        titulo: formData.get('titulo').trim(),
        descricao: formData.get('descricao').trim(),
        dataHora: formData.get('dataHora'),
        local: formData.get('local').trim(),
        tipoEvento: formData.get('tipoEvento'),
        capacidadeMaxima: parseInt(formData.get('capacidadeMaxima')) || null
    };
    
    // Validações
    if (!agendamento.titulo) {
        showError('Título é obrigatório.');
        return;
    }
    
    if (!agendamento.dataHora) {
        showError('Data e hora são obrigatórias.');
        return;
    }
    
    if (!agendamento.local) {
        showError('Local é obrigatório.');
        return;
    }
    
    if (!agendamento.tipoEvento) {
        showError('Tipo de evento é obrigatório.');
        return;
    }
    
    // Verificar se a data não é no passado
    const agendamentoDate = new Date(agendamento.dataHora);
    const now = new Date();
    if (agendamentoDate <= now) {
        showError('A data e hora devem ser no futuro.');
        return;
    }
    
    // Mostrar indicador de carregamento
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Criando...';
    submitBtn.disabled = true;
    
    try {
        const result = await apiRequest(API_CONFIG.ENDPOINTS.AGENDAMENTOS_CRIAR, {
            method: 'POST',
            body: JSON.stringify(agendamento)
        });
        
        if (result) {
            showSuccess('Agendamento criado com sucesso!');
            form.reset();
            loadAgendamentos(); // Recarregar lista
        }
        
    } catch (error) {
        console.error('Erro ao criar agendamento:', error);
        showError('Erro ao criar agendamento: ' + error.message);
        
    } finally {
        // Restaurar botão
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}

// Função para carregar agendamentos
async function loadAgendamentos() {
    const container = document.getElementById('agendamentosList');
    if (!container) return;
    
    try {
        container.innerHTML = '<div class="loading">Carregando agendamentos...</div>';
        
        const agendamentos = await apiRequest(API_CONFIG.ENDPOINTS.AGENDAMENTOS_LISTAR);
        
        container.innerHTML = '';
        
        if (!agendamentos || agendamentos.length === 0) {
            container.innerHTML = '<div class="no-agendamentos">Nenhum agendamento encontrado.</div>';
            return;
        }
        
        // Ordenar por data
        agendamentos.sort((a, b) => new Date(a.dataHora) - new Date(b.dataHora));
        
        agendamentos.forEach(agendamento => {
            const agendamentoElement = createAgendamentoElement(agendamento);
            container.appendChild(agendamentoElement);
        });
        
    } catch (error) {
        console.error('Erro ao carregar agendamentos:', error);
        container.innerHTML = `
            <div class="error">
                <h3>Erro ao carregar agendamentos</h3>
                <p>${error.message}</p>
                <button onclick="loadAgendamentos()">Tentar novamente</button>
            </div>
        `;
    }
}

// Função para criar elemento de agendamento
function createAgendamentoElement(agendamento) {
    const div = document.createElement('div');
    div.className = 'agendamento-item';
    
    const dataHora = new Date(agendamento.dataHora);
    const isPassado = dataHora < new Date();
    const userInfo = getUserInfo();
    const isOwner = userInfo && userInfo.id === agendamento.usuarioId;
    const isAdmin = userInfo && userInfo.role === 'ADMIN';
    
    if (isPassado) {
        div.classList.add('passado');
    }
    
    div.innerHTML = `
        <div class="agendamento-header">
            <h3>${agendamento.titulo}</h3>
            <span class="tipo-evento">${agendamento.tipoEvento}</span>
        </div>
        <div class="agendamento-body">
            <p class="descricao">${agendamento.descricao || 'Sem descrição'}</p>
            <div class="agendamento-info">
                <p><strong>📅 Data:</strong> ${formatDateTime(agendamento.dataHora)}</p>
                <p><strong>📍 Local:</strong> ${agendamento.local}</p>
                ${agendamento.capacidadeMaxima ? 
                    `<p><strong>👥 Capacidade:</strong> ${agendamento.participantesCount || 0}/${agendamento.capacidadeMaxima}</p>` : 
                    ''
                }
                <p><strong>👤 Organizador:</strong> ${agendamento.organizadorNome || 'N/A'}</p>
            </div>
        </div>
        <div class="agendamento-actions">
            ${!isPassado && (!agendamento.capacidadeMaxima || (agendamento.participantesCount || 0) < agendamento.capacidadeMaxima) ? 
                `<button onclick="participarAgendamento(${agendamento.id})" class="btn-participar">
                    Participar
                </button>` : ''
            }
            ${(isOwner || isAdmin) && !isPassado ? 
                `<button onclick="editarAgendamento(${agendamento.id})" class="btn-editar">
                    Editar
                </button>
                <button onclick="deletarAgendamento(${agendamento.id})" class="btn-deletar">
                    Deletar
                </button>` : ''
            }
        </div>
    `;
    
    return div;
}

// Função para formatar data e hora
function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleString('pt-BR', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Função para participar de agendamento
async function participarAgendamento(agendamentoId) {
    try {
        const result = await apiRequest(`${API_CONFIG.ENDPOINTS.AGENDAMENTOS_LISTAR}/${agendamentoId}/participar`, {
            method: 'POST'
        });
        
        if (result) {
            showSuccess('Participação confirmada!');
            loadAgendamentos(); // Recarregar lista
        }
        
    } catch (error) {
        console.error('Erro ao participar:', error);
        showError('Erro ao confirmar participação: ' + error.message);
    }
}

// Função para editar agendamento
async function editarAgendamento(agendamentoId) {
    // Implementar modal de edição ou redirecionar para página de edição
    alert('Funcionalidade de edição será implementada em breve.');
}

// Função para deletar agendamento
async function deletarAgendamento(agendamentoId) {
    if (!confirm('Tem certeza que deseja deletar este agendamento?')) {
        return;
    }
    
    try {
        await apiRequest(`${API_CONFIG.ENDPOINTS.AGENDAMENTOS_DELETAR}/${agendamentoId}`, {
            method: 'DELETE'
        });
        
        showSuccess('Agendamento deletado com sucesso!');
        loadAgendamentos(); // Recarregar lista
        
    } catch (error) {
        console.error('Erro ao deletar agendamento:', error);
        showError('Erro ao deletar agendamento: ' + error.message);
    }
}

// Função para configurar filtros
function setupFilters() {
    const tipoEventoFilter = document.getElementById('tipoEventoFilter');
    const dataFilter = document.getElementById('dataFilter');
    
    if (tipoEventoFilter) {
        tipoEventoFilter.addEventListener('change', applyFilters);
    }
    
    if (dataFilter) {
        dataFilter.addEventListener('change', applyFilters);
    }
}

// Função para aplicar filtros
async function applyFilters() {
    const tipoEvento = document.getElementById('tipoEventoFilter')?.value;
    const data = document.getElementById('dataFilter')?.value;
    
    const container = document.getElementById('agendamentosList');
    if (!container) return;
    
    try {
        container.innerHTML = '<div class="loading">Filtrando agendamentos...</div>';
        
        let endpoint = API_CONFIG.ENDPOINTS.AGENDAMENTOS_LISTAR;
        const params = new URLSearchParams();
        
        if (tipoEvento && tipoEvento !== 'todos') {
            params.append('tipoEvento', tipoEvento);
        }
        
        if (data) {
            params.append('data', data);
        }
        
        if (params.toString()) {
            endpoint += '?' + params.toString();
        }
        
        const agendamentos = await apiRequest(endpoint);
        
        container.innerHTML = '';
        
        if (!agendamentos || agendamentos.length === 0) {
            container.innerHTML = '<div class="no-agendamentos">Nenhum agendamento encontrado com os filtros aplicados.</div>';
            return;
        }
        
        // Ordenar por data
        agendamentos.sort((a, b) => new Date(a.dataHora) - new Date(b.dataHora));
        
        agendamentos.forEach(agendamento => {
            const agendamentoElement = createAgendamentoElement(agendamento);
            container.appendChild(agendamentoElement);
        });
        
    } catch (error) {
        console.error('Erro ao filtrar agendamentos:', error);
        container.innerHTML = `
            <div class="error">
                <h3>Erro ao filtrar agendamentos</h3>
                <p>${error.message}</p>
                <button onclick="loadAgendamentos()">Voltar</button>
            </div>
        `;
    }
}

// Função para mostrar mensagem de erro
function showError(message) {
    const errorDiv = document.getElementById('errorMessage') || createMessageDiv('errorMessage', 'error-message');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    
    setTimeout(() => {
        errorDiv.style.display = 'none';
    }, 5000);
}

// Função para mostrar mensagem de sucesso
function showSuccess(message) {
    const successDiv = document.getElementById('successMessage') || createMessageDiv('successMessage', 'success-message');
    successDiv.textContent = message;
    successDiv.style.display = 'block';
    
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
    
    const container = document.querySelector('.container') || document.body;
    container.insertBefore(div, container.firstChild);
    
    return div;
}

