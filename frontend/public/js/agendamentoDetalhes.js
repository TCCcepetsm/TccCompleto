document.addEventListener('DOMContentLoaded', function () {
    // Busca os dados da API
    fetch('https://gravacao-backend.onrender.com/agendamento')
        .then(res => {
            if (!res.ok) {
                throw new Error('Erro ao carregar dados');
            }
            return res.json();
        })
        .then(dados => {
            // Formata a data para exibição
            const dataFormatada = new Date(dados.data).toLocaleDateString('pt-BR');

            // Preenche os dados na página
            document.getElementById('info-nome').textContent = dados.nome || 'Não informado';
            document.getElementById('info-email').textContent = dados.email || 'Não informado';
            document.getElementById('info-telefone').textContent = dados.telefone || 'Não informado';
            document.getElementById('info-plano').textContent = dados.plano || 'Não selecionado';
            document.getElementById('info-endereco').textContent = dados.endereco || 'Não informado';
            document.getElementById('info-data').textContent = dataFormatada;
            document.getElementById('info-horario').textContent = dados.horario || 'Não informado';

            // Atualiza o status se existir nos dados
            if (dados.status) {
                updateStatus(dados.status);
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            document.getElementById('info-nome').textContent = 'Erro ao carregar dados';
        });

    // Configura o botão de confirmação
    document.getElementById('btn-confirmar').addEventListener('click', function () {
        // Aqui você pode adicionar uma chamada para atualizar o status no backend
        updateStatus('Confirmado');

        this.textContent = 'Agendamento Confirmado';
        this.disabled = true;
        document.getElementById('btn-recusar').disabled = true;

        alert('Agendamento confirmado com sucesso!');
    });

    // Configura o botão de recusa
    document.getElementById('btn-recusar').addEventListener('click', function () {
        const motivo = prompt('Por favor, informe o motivo da recusa:');
        if (motivo === null || motivo.trim() === '') {
            alert('O motivo da recusa é obrigatório.');
            return;
        }

        // Aqui você pode enviar o motivo para o backend
        updateStatus('Recusado');

        this.textContent = 'Agendamento Recusado';
        this.disabled = true;
        document.getElementById('btn-confirmar').disabled = true;

        alert('Agendamento recusado com sucesso! Motivo: ' + motivo);
    });

    function updateStatus(newStatus) {
        const statusElement = document.getElementById('info-status');
        statusElement.textContent = newStatus;
        statusElement.className = 'info-value status-' + newStatus.toLowerCase();
    }
});
// Obter o ID do agendamento da URL
const urlParams = new URLSearchParams(window.location.search);
const agendamentoId = urlParams.get('id');

if (!agendamentoId) {
    alert("ID do agendamento não informado.");
} else {
    // Buscar os detalhes do agendamento
    fetch(`https://gravacao-backend.onrender.com/api/agendamentos/${agendamentoId}`)
        .then(response => {
            if (!response.ok) throw new Error("Erro ao buscar agendamento");
            return response.json();
        })
        .then(data => {
            document.getElementById('info-nome').textContent = data.nomeCliente || 'N/A';
            document.getElementById('info-email').textContent = data.email || 'N/A';
            document.getElementById('info-telefone').textContent = data.telefone || 'N/A';
            document.getElementById('info-plano').textContent = data.plano || 'N/A';
            document.getElementById('info-endereco').textContent = data.endereco || 'N/A';
            document.getElementById('info-data').textContent = data.data || 'N/A';
            document.getElementById('info-horario').textContent = data.horario || 'N/A';
            document.getElementById('info-status').textContent = data.status || 'PENDENTE';
        })
        .catch(error => {
            console.error(error);
            alert("Erro ao carregar os dados do agendamento.");
        });
}

// Função para atualizar status
function atualizarStatus(status) {
    fetch(`https://gravacao-backend.onrender.com/api/agendamentos/${agendamentoId}/status`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: status })
    })
        .then(response => {
            if (!response.ok) throw new Error("Erro ao atualizar status");
            alert(`Agendamento ${status.toLowerCase()} com sucesso!`);
            document.getElementById('info-status').textContent = status;
        })
        .catch(error => {
            console.error(error);
            alert("Erro ao atualizar o status.");
        });
}

// Botões
document.getElementById('btn-confirmar').addEventListener('click', () => {
    atualizarStatus('CONFIRMADO');
});

document.getElementById('btn-recusar').addEventListener('click', () => {
    atualizarStatus('RECUSADO');
});