// Verificar autenticação ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    checkAuthOnProtectedPage();
    
    // Configurar o formulário de upload
    const form = document.getElementById('uploadForm');
    if (form) {
        form.addEventListener('submit', handleUpload);
    }
    
    // Configurar preview de imagens
    const fileInput = document.getElementById('imagens');
    if (fileInput) {
        fileInput.addEventListener('change', previewImages);
    }
});

// Função para fazer upload das imagens
async function handleUpload(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData();
    
    // Obter dados do formulário
    const files = document.getElementById('imagens').files;
    const tipoEvento = document.getElementById('tipoEvento').value;
    const dataEvento = document.getElementById('dataEvento').value;
    
    // Validações
    if (files.length === 0) {
        alert('Por favor, selecione pelo menos uma imagem.');
        return;
    }
    
    if (!tipoEvento) {
        alert('Por favor, selecione o tipo de evento.');
        return;
    }
    
    if (!dataEvento) {
        alert('Por favor, selecione a data do evento.');
        return;
    }
    
    // Adicionar arquivos ao FormData
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }
    
    // Adicionar outros dados
    formData.append('tipoEvento', tipoEvento);
    formData.append('dataEvento', dataEvento);
    
    // Mostrar indicador de carregamento
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Enviando...';
    submitBtn.disabled = true;
    
    try {
        // Fazer upload usando a função utilitária
        const result = await apiUpload(API_CONFIG.ENDPOINTS.GALERIA_UPLOAD, formData);
        
        if (result && result.sucesso) {
            alert(`Upload realizado com sucesso! ${result.mensagem}`);
            
            // Limpar formulário
            form.reset();
            clearPreview();
            
            // Redirecionar para a gravacao após 2 segundos
            setTimeout(() => {
                window.location.href = '/views/gravacao.html';
            }, 2000);
            
        } else {
            throw new Error(result?.mensagem || 'Erro desconhecido no upload');
        }
        
    } catch (error) {
        console.error('Erro no upload:', error);
        alert('Erro ao fazer upload das imagens: ' + error.message);
        
    } finally {
        // Restaurar botão
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}

// Função para preview das imagens selecionadas
function previewImages(event) {
    const files = event.target.files;
    const previewContainer = document.getElementById('imagePreview');
    
    if (!previewContainer) {
        // Criar container de preview se não existir
        const container = document.createElement('div');
        container.id = 'imagePreview';
        container.className = 'image-preview-container';
        event.target.parentNode.appendChild(container);
    }
    
    clearPreview();
    
    if (files.length === 0) return;
    
    const previewDiv = document.getElementById('imagePreview');
    previewDiv.innerHTML = '<h4>Preview das Gravacoes:</h4>';
    
    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        
        // Verificar se é uma imagem
        if (!file.type.startsWith('image/')) {
            continue;
        }
        
        const reader = new FileReader();
        reader.onload = function(e) {
            const previewItem = document.createElement('div');
            previewItem.className = 'preview-item';
            
            previewItem.innerHTML = `
                <img src="${e.target.result}" alt="Preview" class="preview-image">
                <div class="preview-info">
                    <p class="file-name">${file.name}</p>
                    <p class="file-size">${formatFileSize(file.size)}</p>
                </div>
            `;
            
            previewDiv.appendChild(previewItem);
        };
        
        reader.readAsDataURL(file);
    }
}

// Função para limpar preview
function clearPreview() {
    const previewContainer = document.getElementById('imagePreview');
    if (previewContainer) {
        previewContainer.innerHTML = '';
    }
}

// Função para formatar tamanho do arquivo
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// Função para validar arquivos antes do upload
function validateFiles(files) {
    const maxSize = 10 * 1024 * 1024; // 10MB
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    const errors = [];
    
    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        
        // Verificar tipo
        if (!allowedTypes.includes(file.type)) {
            errors.push(`${file.name}: Tipo de arquivo não permitido. Use apenas JPEG, PNG, GIF ou WebP.`);
        }
        
        // Verificar tamanho
        if (file.size > maxSize) {
            errors.push(`${file.name}: Arquivo muito grande. Máximo permitido: 10MB.`);
        }
    }
    
    return errors;
}

// Adicionar validação ao input de arquivo
document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('imagens');
    if (fileInput) {
        fileInput.addEventListener('change', (event) => {
            const files = event.target.files;
            const errors = validateFiles(files);
            
            if (errors.length > 0) {
                alert('Erros encontrados:\n\n' + errors.join('\n'));
                event.target.value = ''; // Limpar seleção
                clearPreview();
                return;
            }
            
            previewImages(event);
        });
    }
});

