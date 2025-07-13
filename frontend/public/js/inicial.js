// Verifica se o usuário está logado ao carregar a página
const token = localStorage.getItem("jwtToken");
if (!token) {
    window.location.href = "login.html"; // Redireciona se não houver token
}

// Opcional: Verifica se o token é válido (mesma lógica do login)
fetch("http://localhost:8080/api/auth/validate-token", {
    headers: { "Authorization": `Bearer ${token}` }
})
.then(response => {
    if (!response.ok) {
        localStorage.clear();
        window.location.href = "login.html";
    }
});
// Função para navegação
function navigateTo(page) {
    window.location.href = page;
}

// Efeitos interativos
document.addEventListener('DOMContentLoaded', function () {
    const cards = document.querySelectorAll('.service-card');

    cards.forEach(card => {
        // Efeito ao passar o mouse
        card.addEventListener('mouseenter', function () {
            this.style.transform = 'translateY(-5px)';
            this.style.boxShadow = '0 10px 20px rgba(0,0,0,0.15)';
        });

        // Efeito ao remover o mouse
        card.addEventListener('mouseleave', function () {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 5px 15px rgba(0,0,0,0.1)';
        });

        // Efeito de clique
        card.addEventListener('click', function () {
            this.style.transform = 'translateY(2px)';
            setTimeout(() => {
                this.style.transform = 'translateY(-5px)';
            }, 100);
        });
    });
});