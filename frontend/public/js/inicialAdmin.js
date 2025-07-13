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