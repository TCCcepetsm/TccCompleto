# Sistema Gravacao

Sistema completo de agendamentos esportivos com funcionalidade de gravação de eventos (galeria de imagens), desenvolvido em Java Spring Boot (backend) e HTML/CSS/JavaScript (frontend).

## 🏗️ Arquitetura

- **Backend:** Java Spring Boot + PostgreSQL (Supabase)
- **Frontend:** HTML/CSS/JavaScript estático
- **Hospedagem:** Render (backend + frontend)
- **Banco de Dados:** Supabase PostgreSQL (gratuito)
- **Armazenamento:** Supabase Storage (gratuito)

## 📁 Estrutura do Projeto

```
Gravacao_Estruturado/
├── backend/                 # Backend Java Spring Boot
│   ├── src/                # Código fonte
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── gravacao/ # Pacote raiz do projeto
│   │                   ├── auth/          # Módulo de autenticação
│   │                   ├── agendamento/   # Módulo de agendamentos
│   │                   ├── gravacao/      # Módulo de gravação (antiga galeria)
│   │                   ├── config/        # Configurações gerais
│   │                   ├── controller/    # Controllers gerais e Health Check
│   │                   └── GravacaoApplication.java # Classe principal
│   ├── pom.xml            # Dependências Maven
│   ├── render.yaml        # Configuração Render
│   └── README.md          # Documentação do backend
├── frontend/               # Frontend estático
│   ├── views/             # Páginas HTML (login, register, agendamento, gravacao, etc.)
│   ├── public/            # Assets estáticos
│   │   ├── css/           # Estilos CSS
│   │   ├── js/            # Scripts JavaScript
│   │   └── images/        # Imagens estáticas
│   ├── render.yaml        # Configuração Render
│   └── README.md          # Documentação do frontend
├── docs/                   # Documentação
│   ├── hospedagem_render_completo.md # Guia de hospedagem detalhado
│   └── api.md             # Documentação da API (a ser criada/detalhada)
├── scripts/                # Scripts utilitários
│   └── deploy.sh          # Script de deploy automatizado
└── README.md              # Este arquivo (documentação principal)
```

## 🚀 Deploy Rápido

### 1. Preparação
1. Faça fork/clone deste repositório
2. Crie conta no [Render](https://render.com)
3. Crie conta no [Supabase](https://supabase.com)

### 2. Configurar Supabase
1. Crie um novo projeto no Supabase
2. Execute os scripts SQL em `docs/database.sql` (você precisará criar este arquivo com o script SQL fornecido no guia de hospedagem)
3. Configure o Storage bucket `gravacao-images`
4. Anote as credenciais (URL, anon key, service role key)

### 3. Deploy Backend
1. No Render, crie um novo Web Service
2. Conecte ao repositório GitHub
3. Configure:
   - **Build Command:** `mvn clean package -DskipTests`
   - **Start Command:** `java -Dserver.port=$PORT -jar target/gravacao-0.0.1-SNAPSHOT.war`
   - **Root Directory:** `backend`
4. Adicione as variáveis de ambiente do Supabase

### 4. Deploy Frontend
1. No Render, crie um novo Static Site
2. Conecte ao mesmo repositório GitHub
3. Configure:
   - **Build Command:** `echo "No build needed"`
   - **Publish Directory:** `frontend`
4. Atualize a URL do backend em `frontend/public/js/config.js` (após o deploy do backend)

## 🔧 Desenvolvimento Local

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
# Servir com qualquer servidor HTTP estático
python -m http.server 8000
```

## 📚 Documentação

- [Guia de Hospedagem Completo](docs/hospedagem_render_completo.md)
- [Backend README](backend/README.md)
- [Frontend README](frontend/README.md)

## 🆓 Custos

Este projeto foi projetado para ser **100% gratuito**:

- **Render:** 750 horas/mês (suficiente para 24/7)
- **Supabase:** 500MB DB + 1GB Storage + 50k usuários/mês
- **Total:** R$ 0,00/mês

## 🛠️ Tecnologias

### Backend
- Java 17
- Spring Boot 3.2.5
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Maven

### Frontend
- HTML5
- CSS3
- JavaScript ES6+
- Leaflet (mapas)
- Fetch API

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

Para suporte, abra uma issue no GitHub ou entre em contato através do email: suporte@gravacao.com

---

**Desenvolvido com ❤️ para a comunidade esportiva**

