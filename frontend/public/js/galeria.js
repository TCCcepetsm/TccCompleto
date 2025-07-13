// Carrega as imagens do backend
document.addEventListener('DOMContentLoaded', async () => {
    const container = document.getElementById('gravacao');

    try {
        // Mostra indicador de carregamento
        container.innerHTML = '<div class="loading">Carregando imagens...</div>';

        // Busca as imagens do backend usando a configuração centralizada
        const imagens = await apiRequest(API_CONFIG.ENDPOINTS.GALERIA_LISTAR);

        // Limpa o container
        container.innerHTML = '';

        if (!imagens || imagens.length === 0) {
            container.innerHTML = '<div class="no-images">Nenhuma imagem encontrada.</div>';
            return;
        }

        // Cria os elementos para cada imagem
        imagens.forEach(imagem => {
            const box = document.createElement('div');
            box.className = 'box';

            // Cria a estrutura HTML para cada imagem
            box.innerHTML = `
                <img src="${imagem.urlGravacao}" 
                     alt="${imagem.titulo}" 
                     onerror="this.src='../public/images/placeholder.jpg'; this.onerror=null;"
                     loading="lazy">
                <h3>${imagem.titulo}</h3>
                <div class="image-info">
                    <p class="event-type">${imagem.tipoEvento || 'Evento'}</p>
                    <p class="event-date">${formatDate(imagem.dataEvento)}</p>
                    <p class="upload-date">Enviado em: ${formatDate(imagem.dataUpload)}</p>
                </div>
                <div class="image-actions">
                    <button onclick="visualizarGravacao('${imagem.urlGravacao}', '${imagem.titulo}')" 
                            class="btn-visualizar">
                        Ver Gravacao
                    </button>
                    ${getUserInfo() && getUserInfo().role === 'ADMIN' ?
                    `<button onclick="deletarGravacao(${imagem.id})" 
                                 class="btn-deletar">
                            Deletar
                         </button>` : ''
                }
                </div>
            `;

            container.appendChild(box);
        });

    } catch (error) {
        console.error('Erro ao carregar imagens:', error);
        container.innerHTML = `
            <div class="error">
                <h3>Erro ao carregar imagens</h3>
                <p>Não foi possível conectar ao servidor: ${error.message}</p>
                <button onclick="location.reload()">Tentar novamente</button>
            </div>
        `;
    }
});

// Função para formatar data
function formatDate(dateString) {
    if (!dateString) return '';

    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    } catch (error) {
        return '';
    }
}

// Função para filtrar imagens por tipo
async function filtrarPorTipo(tipoEvento) {
    const container = document.getElementById('gravacao');

    try {
        container.innerHTML = '<div class="loading">Filtrando imagens...</div>';

        let imagens;
        if (!tipoEvento || tipoEvento === 'todos') {
            // Busca todas as imagens
            imagens = await apiRequest(API_CONFIG.ENDPOINTS.GALERIA_LISTAR);
        } else {
            // Busca imagens por tipo
            imagens = await apiRequest(`${API_CONFIG.ENDPOINTS.GALERIA_POR_TIPO}/${encodeURIComponent(tipoEvento)}`);
        }

        container.innerHTML = '';

        if (!imagens || imagens.length === 0) {
            container.innerHTML = `<div class="no-images">Nenhuma imagem encontrada${tipoEvento && tipoEvento !== 'todos' ? ` para "${tipoEvento}"` : ''}.</div>`;
            return;
        }

        imagens.forEach(imagem => {
            const box = document.createElement('div');
            box.className = 'box';

            box.innerHTML = `
                <img src="${imagem.urlGravacao}" 
                     alt="${imagem.titulo}" 
                     onerror="this.src='../public/images/placeholder.jpg'; this.onerror=null;"
                     loading="lazy">
                <h3>${imagem.titulo}</h3>
                <div class="image-info">
                    <p class="event-type">${imagem.tipoEvento || 'Evento'}</p>
                    <p class="event-date">${formatDate(imagem.dataEvento)}</p>
                    <p class="upload-date">Enviado em: ${formatDate(imagem.dataUpload)}</p>
                </div>
                <div class="image-actions">
                    <button onclick="visualizarGravacao('${imagem.urlGravacao}', '${imagem.titulo}')" 
                            class="btn-visualizar">
                        Ver Gravacao
                    </button>
                    ${getUserInfo() && getUserInfo().role === 'ADMIN' ?
                    `<button onclick="deletarGravacao(${imagem.id})" 
                                 class="btn-deletar">
                            Deletar
                         </button>` : ''
                }
                </div>
            `;

            container.appendChild(box);
        });

    } catch (error) {
        console.error('Erro ao filtrar imagens:', error);
        container.innerHTML = `
            <div class="error">
                <h3>Erro ao filtrar imagens</h3>
                <p>Não foi possível carregar as imagens filtradas: ${error.message}</p>
                <button onclick="location.reload()">Voltar</button>
            </div>
        `;
    }
}

// Função para visualizar imagem em modal
function visualizarGravacao(url, titulo) {
    // Cria modal se não existir
    let modal = document.getElementById('imageModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'imageModal';
        modal.className = 'modal';
        modal.innerHTML = `
            <div class="modal-content">
                <span class="close" onclick="fecharModal()">&times;</span>
                <img id="modalImage" src="" alt="">
                <h3 id="modalTitle"></h3>
            </div>
        `;
        document.body.appendChild(modal);
    }

    // Atualiza conteúdo do modal
    document.getElementById('modalImage').src = url;
    document.getElementById('modalTitle').textContent = titulo;
    modal.style.display = 'block';
}

// Função para fechar modal
function fecharModal() {
    const modal = document.getElementById('imageModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Função para deletar imagem (apenas para admins)
async function deletarGravacao(imagemId) {
    if (!confirm('Tem certeza que deseja deletar esta imagem?')) {
        return;
    }

    try {
        await apiRequest(`${API_CONFIG.ENDPOINTS.GALERIA_DELETAR}/${imagemId}`, {
            method: 'DELETE'
        });

        alert('Gravacao deletada com sucesso!');
        location.reload(); // Recarrega a página para atualizar a gravacao

    } catch (error) {
        console.error('Erro ao deletar imagem:', error);
        alert('Erro ao deletar imagem: ' + error.message);
    }
}

// Fechar modal ao clicar fora dele
window.onclick = function (event) {
    const modal = document.getElementById('imageModal');
    if (event.target === modal) {
        fecharModal();
    }
}

// Gerencia os botões de filtro
document.addEventListener('DOMContentLoaded', () => {
    const filterButtons = document.querySelectorAll('.filter-button');

    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Remove classe active de todos os botões
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // Adiciona classe active ao botão clicado
            button.classList.add('active');
        });
    });
});