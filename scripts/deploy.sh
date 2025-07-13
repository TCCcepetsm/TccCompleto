#!/bin/bash

# Script de Deploy Automatizado - Sistema Gravacao
# Este script facilita o deploy do projeto no Render

set -e  # Parar em caso de erro

echo "🚀 Iniciando deploy do Sistema Gravacao..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se estamos no diretório correto
if [ ! -f "README.md" ] || [ ! -d "backend" ] || [ ! -d "frontend" ]; then
    print_error "Execute este script a partir do diretório raiz do projeto Gravacao_Estruturado"
    exit 1
fi

print_status "Verificando estrutura do projeto..."

# Verificar arquivos essenciais
REQUIRED_FILES=(
    "backend/pom.xml"
    "backend/render.yaml"
    "backend/src/main/java/com/gravacao/GravacaoApplication.java"
    "frontend/render.yaml"
    "frontend/public/js/config.js"
    "frontend/index.html"
)

for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        print_error "Arquivo obrigatório não encontrado: $file"
        exit 1
    fi
done

print_success "Estrutura do projeto verificada!"

# Verificar se Git está configurado
if ! command -v git &> /dev/null; then
    print_error "Git não está instalado. Instale o Git para continuar."
    exit 1
fi

# Verificar se estamos em um repositório Git
if [ ! -d ".git" ]; then
    print_warning "Não é um repositório Git. Inicializando..."
    git init
    git add .
    git commit -m "Initial commit - Sistema Gravacao"
    print_success "Repositório Git inicializado!"
else
    print_status "Repositório Git encontrado."
fi

# Verificar se há mudanças não commitadas
if ! git diff-index --quiet HEAD --; then
    print_warning "Há mudanças não commitadas. Fazendo commit..."
    git add .
    git commit -m "Deploy: $(date '+%Y-%m-%d %H:%M:%S')"
    print_success "Mudanças commitadas!"
fi

# Verificar se há um remote configurado
if ! git remote | grep -q "origin"; then
    print_warning "Nenhum remote 'origin' configurado."
    echo "Configure um repositório GitHub/GitLab e execute:"
    echo "git remote add origin <URL_DO_SEU_REPOSITORIO>"
    echo "git push -u origin main"
    echo ""
    print_status "Continuando com verificações locais..."
else
    print_status "Remote 'origin' configurado. Fazendo push..."
    git push origin main
    print_success "Código enviado para o repositório!"
fi

# Verificar configuração do backend
print_status "Verificando configuração do backend..."

if grep -q "seu-backend.onrender.com" frontend/public/js/config.js; then
    print_warning "URL do backend ainda não foi configurada no frontend!"
    echo "Edite frontend/public/js/config.js e substitua:"
    echo "  'https://seu-backend.onrender.com/api'"
    echo "Por:"
    echo "  'https://gravacao-backend.onrender.com/api'"
    echo "(ou a URL real do seu backend no Render)"
fi

# Verificar se Maven está disponível (para teste local)
if command -v mvn &> /dev/null; then
    print_status "Testando build do backend..."
    cd backend
    if mvn clean compile -q; then
        print_success "Backend compila corretamente!"
    else
        print_error "Erro na compilação do backend. Verifique os logs acima."
        exit 1
    fi
    cd ..
else
    print_warning "Maven não encontrado. Pulando teste de compilação local."
fi

# Gerar checklist de deploy
print_status "Gerando checklist de deploy..."

cat > deploy-checklist.md << EOF
# Checklist de Deploy - Sistema Gravacao

## ✅ Pré-requisitos
- [ ] Conta no Render criada
- [ ] Conta no Supabase criada
- [ ] Repositório GitHub configurado
- [ ] Código enviado para GitHub

## 🗄️ Configurar Supabase
- [ ] Criar novo projeto no Supabase
- [ ] Executar scripts SQL para criar tabelas
- [ ] Criar bucket 'gravacao-images' no Storage
- [ ] Configurar políticas RLS (Row Level Security)
- [ ] Anotar credenciais:
  - [ ] Database URL
  - [ ] Database Username
  - [ ] Database Password
  - [ ] Supabase URL
  - [ ] Anon Key
  - [ ] Service Role Key

## 🖥️ Deploy Backend (Render)
- [ ] Criar novo Web Service no Render
- [ ] Conectar repositório GitHub
- [ ] Configurar:
  - [ ] Root Directory: \`backend\`
  - [ ] Build Command: \`mvn clean package -DskipTests\`
  - [ ] Start Command: \`java -Dserver.port=\$PORT -jar target/gravacao-0.0.1-SNAPSHOT.war\`
- [ ] Adicionar variáveis de ambiente:
  - [ ] SPRING_PROFILES_ACTIVE=prod
  - [ ] DATABASE_URL=(do Supabase)
  - [ ] DATABASE_USERNAME=(do Supabase)
  - [ ] DATABASE_PASSWORD=(do Supabase)
  - [ ] SUPABASE_URL=(do Supabase)
  - [ ] SUPABASE_ANON_KEY=(do Supabase)
  - [ ] SUPABASE_SERVICE_ROLE_KEY=(do Supabase)
  - [ ] SUPABASE_STORAGE_BUCKET=gravacao-images
  - [ ] JWT_SECRET=(gerar automaticamente)
  - [ ] CORS_ALLOWED_ORIGINS=https://gravacao-frontend.onrender.com
- [ ] Aguardar deploy e anotar URL do backend

## 🌐 Deploy Frontend (Render)
- [ ] Criar novo Static Site no Render
- [ ] Conectar mesmo repositório GitHub
- [ ] Configurar:
  - [ ] Root Directory: \`frontend\`
  - [ ] Build Command: \`echo "No build needed"\`
  - [ ] Publish Directory: \`.\`
- [ ] Aguardar deploy e anotar URL do frontend

## 🔧 Configuração Final
- [ ] Atualizar URL do backend em \`frontend/public/js/config.js\`
- [ ] Atualizar CORS_ALLOWED_ORIGINS no backend com URL real do frontend
- [ ] Fazer commit e push das mudanças
- [ ] Aguardar redeploy automático

## 🧪 Testes
- [ ] Acessar frontend e verificar se carrega
- [ ] Testar registro de usuário
- [ ] Testar login
- [ ] Testar criação de agendamento
- [ ] Testar upload de imagem na galeria
- [ ] Verificar se imagens aparecem corretamente

## 🎉 Finalização
- [ ] Configurar domínio personalizado (opcional)
- [ ] Configurar monitoramento
- [ ] Documentar URLs finais
- [ ] Compartilhar com usuários

---

**URLs Finais:**
- Frontend: https://gravacao-frontend.onrender.com
- Backend: https://gravacao-backend.onrender.com
- API Docs: https://gravacao-backend.onrender.com/api/swagger-ui.html

**Credenciais de Teste:**
- Email: admin@gravacao.com
- Senha: admin123

EOF

print_success "Checklist gerado em deploy-checklist.md"

# Resumo final
echo ""
echo "🎉 Preparação para deploy concluída!"
echo ""
echo "📋 Próximos passos:"
echo "1. Siga o checklist em deploy-checklist.md"
echo "2. Configure Supabase (banco + storage)"
echo "3. Deploy backend no Render"
echo "4. Deploy frontend no Render"
echo "5. Configure URLs finais"
echo ""
echo "📚 Documentação completa:"
echo "- README.md (geral)"
echo "- backend/README.md (backend específico)"
echo "- frontend/README.md (frontend específico)"
echo "- docs/hospedagem.md (guia detalhado)"
echo ""
echo "🆓 Custo total: R$ 0,00/mês"
echo ""
print_success "Boa sorte com o deploy! 🚀"

