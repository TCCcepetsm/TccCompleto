# Backend - Sistema Gravacao

Backend Java Spring Boot para o sistema de agendamentos esportivos.

## 🏗️ Arquitetura

- **Framework:** Spring Boot 3.2.5
- **Linguagem:** Java 17
- **Banco de Dados:** PostgreSQL (Supabase)
- **Autenticação:** JWT
- **Build:** Maven
- **Hospedagem:** Render

## 📁 Estrutura

```
backend/
├── src/main/java/com/gravacao/
│   ├── auth/                    # Módulo de autenticação
│   │   ├── controller/         # Controllers de auth
│   │   ├── service/           # Serviços de auth
│   │   ├── model/             # Entidades User, Role
│   │   ├── repository/        # Repositórios JPA
│   │   ├── dto/               # DTOs de auth
│   │   ├── config/            # Configurações de segurança
│   │   └── util/              # Utilitários JWT
│   ├── agendamento/            # Módulo de agendamentos
│   │   ├── controller/        # Controllers de agendamento
│   │   ├── service/           # Serviços de agendamento
│   │   ├── model/             # Entidade Agendamento
│   │   └── repository/        # Repositórios JPA
│   ├── galeria/                # Módulo de galeria
│   │   ├── controller/        # Controllers de galeria
│   │   ├── service/           # Serviços de galeria
│   │   ├── model/             # Entidade Imagem
│   │   ├── repository/        # Repositórios JPA
│   │   ├── dto/               # DTOs de galeria
│   │   └── config/            # Configurações Supabase
│   ├── config/                 # Configurações gerais
│   ├── controller/            # Controllers gerais
│   └── GravacaoApplication.java # Classe principal
├── src/main/resources/
│   ├── application.properties      # Configurações desenvolvimento
│   └── application-prod.properties # Configurações produção
├── pom.xml                    # Dependências Maven
└── render.yaml               # Configuração Render
```

## 🚀 Executar Localmente

### Pré-requisitos
- Java 17+
- Maven 3.6+
- PostgreSQL (ou usar Supabase)

### Configuração
1. Clone o repositório
2. Configure as variáveis de ambiente:
   ```bash
   export DATABASE_URL=jdbc:postgresql://localhost:5432/gravacao
   export DATABASE_USERNAME=seu_usuario
   export DATABASE_PASSWORD=sua_senha
   export SUPABASE_URL=https://seu-projeto.supabase.co
   export SUPABASE_ANON_KEY=sua_anon_key
   export SUPABASE_SERVICE_ROLE_KEY=sua_service_role_key
   export JWT_SECRET=seu_jwt_secret_muito_seguro
   ```

### Executar
```bash
cd backend
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## 🔧 Build para Produção

```bash
mvn clean package -DskipTests
```

O arquivo WAR será gerado em `target/gravacao-0.0.1-SNAPSHOT.war`

## 📡 API Endpoints

### Autenticação
- `POST /api/auth/register` - Registrar usuário
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Renovar token

### Agendamentos
- `GET /api/agendamentos` - Listar agendamentos
- `POST /api/agendamentos` - Criar agendamento
- `PUT /api/agendamentos/{id}` - Atualizar agendamento
- `DELETE /api/agendamentos/{id}` - Deletar agendamento
- `GET /api/agendamentos/data/{data}` - Agendamentos por data
- `GET /api/agendamentos/usuario/{userId}` - Agendamentos do usuário

### Galeria
- `GET /api/galeria/imagens` - Listar imagens
- `POST /api/galeria/upload` - Upload de imagem
- `GET /api/galeria/imagens/tipo/{tipo}` - Imagens por tipo
- `DELETE /api/galeria/imagens/{id}` - Deletar imagem

### Health Check
- `GET /api/health` - Status da aplicação
- `GET /api/health/ping` - Ping simples

## 🔒 Segurança

- **JWT:** Tokens com expiração de 24 horas
- **CORS:** Configurado para frontend específico
- **Validação:** Bean Validation em todos os endpoints
- **Autorização:** Role-based access control (RBAC)

## 🗄️ Banco de Dados

### Entidades Principais

#### User
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Agendamento
```sql
CREATE TABLE agendamentos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_evento DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    local VARCHAR(255) NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    capacidade_maxima INTEGER,
    organizador_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Imagem
```sql
CREATE TABLE imagens (
    id BIGSERIAL PRIMARY KEY,
    nome_arquivo VARCHAR(255) NOT NULL,
    url_imagem VARCHAR(500) NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    data_evento DATE,
    descricao TEXT,
    uploaded_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🚀 Deploy no Render

### Configuração Automática
O arquivo `render.yaml` já está configurado. Apenas:

1. Conecte o repositório no Render
2. Configure as variáveis de ambiente
3. O deploy será automático

### Variáveis de Ambiente Necessárias
- `DATABASE_URL` - URL do banco Supabase
- `DATABASE_USERNAME` - Usuário do banco
- `DATABASE_PASSWORD` - Senha do banco
- `SUPABASE_URL` - URL do projeto Supabase
- `SUPABASE_ANON_KEY` - Chave anônima do Supabase
- `SUPABASE_SERVICE_ROLE_KEY` - Chave de serviço do Supabase
- `JWT_SECRET` - Segredo para JWT (gerado automaticamente)
- `CORS_ALLOWED_ORIGINS` - URL do frontend

## 🔧 Otimizações para Render

### Keep-Alive
- Ping automático a cada 14 minutos para evitar sleep
- Health check endpoint para monitoramento

### Performance
- Pool de conexões otimizado para 512MB RAM
- Batch processing para operações de banco
- Logs otimizados para produção

### Monitoramento
- Logs estruturados
- Métricas de memória
- Health checks automáticos

## 🐛 Troubleshooting

### Problemas Comuns

#### Erro de Conexão com Banco
```
Caused by: org.postgresql.util.PSQLException: Connection refused
```
**Solução:** Verificar variáveis de ambiente do banco

#### Erro de CORS
```
Access to fetch at 'https://backend.onrender.com' from origin 'https://frontend.onrender.com' has been blocked by CORS policy
```
**Solução:** Verificar `CORS_ALLOWED_ORIGINS`

#### Aplicação em Sleep
```
Application Error: Service Unavailable
```
**Solução:** O keep-alive automático deve resolver. Se persistir, fazer uma requisição manual.

### Logs
```bash
# Ver logs no Render Dashboard
# Ou usar curl para health check
curl https://gravacao-backend.onrender.com/api/health
```

## 📚 Dependências Principais

```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Security + JWT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
</dependency>

<!-- JPA + PostgreSQL -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

## 📄 Licença

Este projeto está sob a licença MIT.

