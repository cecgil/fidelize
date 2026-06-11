# Fidelize – SaaS de Fidelizacao com QR Code

Fidelize e um **SaaS de fidelizacao de clientes** simples, moderno e mobile-first, focado em **barbearias, oficinas e restaurantes**.

O sistema **aumenta a recorrencia de clientes** por meio de um fluxo intuitivo de **acumulo de visitas, recompensas e resgate via QR Code**, sem necessidade de aplicativo ou login do cliente.

---

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Backend | Java 21 + Spring Boot 4.0.1 + Spring Security |
| Persistencia | Spring Data JPA + PostgreSQL (prod) / H2 (dev) |
| Frontend | Thymeleaf + Tailwind CSS + Google Fonts (Inter) |
| Email | Spring Mail + Gmail SMTP |
| Build | Maven |

---

## Funcionalidades

### Cliente (sem login)
- Registro de visita via QR Code da empresa
- Verificacao por OTP enviado ao e-mail (6 digitos, expira em 5 min)
- Rate limiting: max 3 solicitacoes de OTP por telefone a cada 15 min
- Cartao fidelidade digital com progresso visual animado
- Resgate via QR Code temporario (5 min, uso unico)
- Protecao anti-abuso (intervalo minimo entre visitas)

### Admin (painel)
- Dashboard com metricas (clientes, visitas, resgates)
- QR Code da loja para impressao
- CRUD de recompensas (com protecao contra exclusao de recompensas vinculadas a resgates)
- Gerenciamento de equipe (multi-usuario com roles ADMIN/USUARIO)
- Configuracoes do programa (visitas para recompensa, intervalo minimo, ativar/desativar)
- Validacao de resgate via QR Code do cliente
- Protecao: admin nao pode desativar a si mesmo

### Seguranca
- Spring Security com CSRF habilitado
- Isolamento multi-tenant (cada empresa ve apenas seus dados)
- Validacao de inputs (servidor + cliente)
- QR Code de resgate com token UUID, expiracao e uso unico

---

## Como Executar

### Pre-requisitos
- Java 21
- Maven

### Desenvolvimento (H2 em memoria)

```bash
./mvnw spring-boot:run
```

Acesse: http://localhost:8080
Login: `admin` / `123456`

### Producao (PostgreSQL)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod \
  -DDB_USERNAME=fidelize \
  -DDB_PASSWORD=sua_senha \
  -DMAIL_USERNAME=seu@gmail.com \
  -DMAIL_PASSWORD=app_password
```

### Console H2 (apenas dev)
- URL: http://localhost:8080/h2
- JDBC URL: `jdbc:h2:mem:loyaltydb`
- Usuario: `sa` / Senha: (vazio)

---

## Rotas Principais

| Metodo | Rota | Descricao |
|--------|------|-----------|
| GET | `/` | Landing page |
| GET | `/cadastro` | Cadastro de empresa |
| GET | `/login` | Login |
| GET | `/c/{empresaId}` | Registro de visita (cliente) |
| POST | `/c/{empresaId}/solicitar` | Solicitar OTP |
| POST | `/c/{empresaId}/verificar` | Verificar OTP e registrar visita |
| POST | `/resgate/{clienteId}/{recompensaId}` | Solicitar resgate |
| GET | `/validar/{token}` | Validar QR de resgate (admin) |
| POST | `/validar/{token}/confirmar` | Confirmar resgate |
| GET | `/admin/painel` | Dashboard admin |
| GET | `/admin/recompensas` | Gerenciar recompensas |
| GET | `/admin/usuarios` | Gerenciar equipe |
| GET | `/admin/config` | Configuracoes |

---

## Estrutura do Projeto

```
src/main/java/com/cecgil/fidelize/
├── admin/          # Controllers do painel admin
├── cliente/        # Entidade e controller do cliente
├── config/         # SecurityConfig, DataInitializer
├── empresa/        # Entidade e repository da empresa
├── fidelidade/
│   ├── qrcode/     # QR Code de resgate (token + expiracao)
│   ├── recompensa/ # Entidade e repository
│   ├── resgate/    # Entidade, service e repository
│   └── visita/     # Entidade, service e repository
├── usuario/        # Entidade, repository, UserDetailsService
├── verificacao/    # OtpService + EmailService
└── web/            # Controllers publicos (resgate, landing)
```

---

## Progresso do Planejamento

| Semana | Foco | Status |
|--------|------|--------|
| 1 | Infraestrutura (PostgreSQL, SMTP, CSRF, validacao) | Concluida |
| 2 | Correcao de bugs e robustez | Concluida |
| 3 | Redesign visual completo (UI/UX) | Concluida |
| 4 | Funcionalidades de negocio (multiplas recompensas, filtros) | Pendente |
| 5 | Qualidade e testes | Pendente |
| 6 | Docker e deploy | Pendente |
| 7 | SEO, LGPD e lancamento | Pendente |

---

## Licenca

Projeto em desenvolvimento. Uso livre para fins educacionais e comerciais.
