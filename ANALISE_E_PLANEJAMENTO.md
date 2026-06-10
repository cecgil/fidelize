# Fidelize - Analise Completa e Planejamento Semanal

## 1. Visao Geral do Projeto

**Fidelize** e um sistema SaaS de programa de fidelidade para pequenos negocios (barbearias, oficinas, restaurantes). O cliente registra visitas via QR Code, acumula pontos e resgata recompensas.

**Stack**: Java 21 + Spring Boot 4.0.1 + Thymeleaf + H2 (in-memory) + Spring Security

---

## 2. Analise de Botoes e Funcionalidades

### 2.1 Fluxo do Cliente (sem autenticacao)

| Botao/Acao | Tela | Status | Observacoes |
|---|---|---|---|
| Formulario nome/telefone/email | `cliente/registro.html` | OK | Valida se fidelidade esta ativa |
| Enviar codigo OTP | POST `/c/{id}/solicitar` | PARCIAL | Funciona, mas email cai em fallback (log no console). SMTP nao configurado |
| Verificar codigo | POST `/c/{id}/verificar` | OK | Valida OTP, registra visita, mostra cartao |
| Dots de progresso (cartao fidelidade) | `cliente/sucesso.html` | OK | Exibe visitas do ciclo atual vs meta |
| Botao "Resgatar agora" | POST `/resgate/{clienteId}/{recompensaId}` | OK | So aparece quando `totalVisitas >= visitasParaRecompensa`. Protegido contra duplo-clique (reutiliza QR pendente) |
| Link "Registrar outra visita" | `cliente/sucesso.html` | OK | Redireciona para formulario inicial |

### 2.2 Fluxo do QR Code de Resgate

| Botao/Acao | Tela | Status | Observacoes |
|---|---|---|---|
| Gerar QR Code | `cliente/qrcode.html` | OK | Token UUID, expira em 5 minutos |
| Validar QR (admin escaneia) | GET `/validar/{token}` | OK | Mostra dados do resgate para confirmacao |
| Botao "Confirmar resgate" | POST `/validar/{token}/confirmar` | OK | Marca como UTILIZADO, reseta ciclo |
| Erros (expirado/invalido/usado) | `error/qr.html` | OK | GlobalExceptionHandler trata 3 excecoes |

### 2.3 Painel Admin

| Botao/Acao | Tela | Status | Observacoes |
|---|---|---|---|
| Checklist onboarding | `admin/painel.html` | OK | Aparece somente quando nao ha clientes |
| QR Code da loja | `admin/painel.html` | OK | Usa API externa `api.qrserver.com` |
| Botao "Copiar link" | `admin/painel.html` | OK | Clipboard API com feedback visual |
| Cards de resumo (clientes/visitas/resgates) | `admin/painel.html` | OK | Contadores corretos |
| Tabela ultimos resgates | `admin/painel.html` | OK | Top 10 resgates utilizados |
| Tabela clientes com ciclo | `admin/painel.html` | OK | Paginacao de 20 por pagina |
| Botao "Gerenciar recompensas" | `admin/painel.html` | OK | Link para `/admin/recompensas` |
| Link "Configuracoes da empresa" | `admin/painel.html` | OK | Link para `/admin/config` |
| Paginacao (anterior/proxima) | `admin/painel.html` | OK | Funciona com parametro `?pagina=` |

### 2.4 Gerenciamento de Recompensas

| Botao/Acao | Tela | Status | Observacoes |
|---|---|---|---|
| Botao "Nova recompensa" | `admin/recompensas/listar.html` | OK | Abre formulario |
| Formulario criar/editar | `admin/recompensas/form.html` | OK | Apenas campo "nome" |
| Botao "Salvar" (criar) | POST `/admin/recompensas` | OK | Cria com `ativa=true` |
| Botao "Salvar" (editar) | POST `/admin/recompensas/{id}/editar` | OK | Atualiza nome |
| Botao "Ativar/Desativar" | POST `/admin/recompensas/{id}/toggle` | OK | Toggle booleano |
| Botao "Excluir" | POST `/admin/recompensas/{id}/delete` | RISCO | Deleta sem confirmacao. Se houver resgates vinculados, pode dar erro de FK |

### 2.5 Gerenciamento de Usuarios

| Botao/Acao | Tela | Status | Observacoes |
|---|---|---|---|
| Botao "Novo usuario" | `admin/usuarios/listar.html` | OK | Abre formulario |
| Formulario criar | `admin/usuarios/form.html` | OK | Username, senha, role |
| Botao "Salvar" | POST `/admin/usuarios` | OK | Valida username duplicado |
| Botao "Ativar/Desativar" | POST `/admin/usuarios/{id}/toggle` | OK | Verifica mesma empresa |

### 2.6 Configuracoes da Empresa

| Botao/Acao | Tela | Status | Observacoes |
|---|---|---|---|
| Campo "Visitas para recompensa" | `admin/config.html` | OK | Atualiza `visitasParaRecompensa` |
| Campo "Intervalo minimo (horas)" | `admin/config.html` | OK | Anti-abuso |
| Toggle "Fidelidade ativa" | `admin/config.html` | OK | Desativa programa inteiro |
| Botao "Salvar" | POST `/admin/config` | OK | Salva e redireciona ao painel |

### 2.7 Cadastro de Empresa (onboarding)

| Botao/Acao | Tela | Status | Observacoes |
|---|---|---|---|
| Formulario (empresa, segmento, user, senha) | `cadastro.html` | OK | Cria empresa + usuario admin |
| Botao "Criar conta" | POST `/cadastro` | OK | Login automatico apos criacao |
| Validacao username duplicado | POST `/cadastro` | OK | Exibe erro na tela |

### 2.8 Landing Page

| Botao/Acao | Tela | Status | Observacoes |
|---|---|---|---|
| CTA "Comecar gratis" | `landing.html` | OK | Link para `/cadastro` |
| Link "Login" | `landing.html` | OK | Link para `/login` |

---

## 3. Problemas Identificados e Riscos

### CRITICOS

| # | Problema | Arquivo | Impacto |
|---|---|---|---|
| C1 | **Banco H2 in-memory**: todos os dados sao perdidos ao reiniciar o servidor | `application.yml` | Inviavel para producao |
| C2 | **OTP em memoria**: ConcurrentHashMap perde todos os codigos no restart | `OtpService.java` | Codigos de verificacao perdidos |
| C3 | **CSRF desabilitado**: formularios POST ficam vulneraveis a ataques CSRF | `SecurityConfig.java` | Vulnerabilidade de seguranca |
| C4 | **Email sem SMTP**: todos os emails caem em fallback (log no console) | `EmailService.java` | Clientes nao recebem OTP |

### ALTOS

| # | Problema | Arquivo | Impacto |
|---|---|---|---|
| A1 | **Excluir recompensa com resgates vinculados**: pode causar erro de FK | `RecompensaAdminController.java:135` | Erro 500 para o admin |
| A2 | **Sem validacao de input** no cadastro (nome vazio, telefone invalido, email invalido) | `ClienteController.java`, `CadastroController.java` | Dados sujos no banco |
| A3 | **Cliente so pega primeira recompensa ativa**: `findFirst()` ignora as demais | `ClienteController.java:155` | Cliente nao escolhe recompensa |
| A4 | **Sem testes automatizados**: apenas o teste padrao do Spring Boot | `FidelizeApplicationTests.java` | Regressoes silenciosas |
| A5 | **Dados do cliente via query param no redirect**: nome/telefone/email ficam na URL | `ClienteController.java:80-83` | Dados pessoais expostos na URL e historico do navegador |

### MEDIOS

| # | Problema | Arquivo | Impacto |
|---|---|---|---|
| M1 | **Segmentos fixos em enum**: adicionar novo segmento requer deploy | `Segmento.java` | Pouca flexibilidade |
| M2 | **Sem recuperacao de senha** | - | Admin perde acesso |
| M3 | **Sem log de auditoria**: acoes admin nao sao registradas | - | Sem rastreabilidade |
| M4 | **Admin pode desativar a si mesmo** | `UsuarioAdminController.java:67` | Admin se tranca fora |
| M5 | **Campo `custoVisitas` na Recompensa nao e usado** | `Recompensa.java` | Campo morto no banco |
| M6 | **QR Code da loja depende de API externa** (api.qrserver.com) | `admin/painel.html:39` | Se API cair, QR nao aparece |
| M7 | **Sem rate limiting** no envio de OTP | `ClienteController.java:78` | Abuso de envio de email |

---

## 4. Regras de Negocio Validadas

| Regra | Implementacao | Status |
|---|---|---|
| 1 visita por intervalo minimo (default 24h) | `ClienteController.java:139-141` | OK |
| Ciclo reseta apos resgate utilizado | `VisitaService.java:20-25` | OK |
| QR Code expira em 5 minutos | `QRCodeResgateService.java:32` | OK |
| QR Code de uso unico | `QRCodeResgateService.java:41-42` | OK |
| Reutiliza QR pendente (anti duplo-clique) | `ResgateController.java:51-60` | OK |
| Isolamento multi-tenant (empresa) | Todos os controllers admin | OK |
| Roles ADMIN vs USUARIO | `SecurityConfig.java` | OK |
| Login automatico apos cadastro | `CadastroController.java:71-79` | OK |
| Verificacao de pertencimento (recompensa/usuario da mesma empresa) | Controllers admin | OK |

---

## 5. Planejamento Semanal

### SEMANA 1 - Infraestrutura e Seguranca

**Objetivo**: Tornar o projeto viavel para testes reais

- [ ] **Migrar H2 para PostgreSQL** (resolve C1)
  - Adicionar dependencia `postgresql` no `pom.xml`
  - Criar `application-prod.yml` com config PostgreSQL
  - Manter H2 para perfil `dev`
  - Criar script SQL inicial (`schema.sql`)
- [ ] **Configurar SMTP real** (resolve C4)
  - Configurar Gmail SMTP ou SendGrid no `application.yml`
  - Testar envio de OTP para email real
- [ ] **Habilitar CSRF com Thymeleaf** (resolve C3)
  - Remover `.csrf(c -> c.disable())`
  - Thymeleaf ja adiciona tokens CSRF automaticamente em formularios
  - Testar todos os formularios POST
- [ ] **Adicionar validacao de inputs** (resolve A2)
  - Bean Validation (`@NotBlank`, `@Email`, `@Pattern`) nos formularios
  - Validar telefone com regex
  - Exibir mensagens de erro no frontend

---

### SEMANA 2 - Correcao de Bugs e Robustez

**Objetivo**: Eliminar comportamentos inesperados

- [ ] **Proteger exclusao de recompensa** (resolve A1)
  - Verificar se ha resgates vinculados antes de deletar
  - Opcionalmente: soft-delete em vez de delete fisico
  - Adicionar modal de confirmacao no frontend
- [ ] **Impedir admin de desativar a si mesmo** (resolve M4)
  - Verificar `if (u.getId().equals(admin.getId()))` no toggle
- [ ] **Remover campo `custoVisitas`** (resolve M5)
  - Remover da entidade `Recompensa.java`
- [ ] **Mover dados do cliente do query param para sessao** (resolve A5)
  - Usar `HttpSession` ou `RedirectAttributes.addFlashAttribute()`
  - Evitar exposicao de dados pessoais na URL
- [ ] **Adicionar rate limiting no OTP** (resolve M7)
  - Limitar a 3 solicitacoes por telefone a cada 15 minutos
  - Exibir mensagem de espera no frontend

---

### SEMANA 3 - Redesign Visual e UI/UX

**Objetivo**: Transformar o layout atual em um design moderno e profissional

**Diagnostico atual:**
- Tailwind CSS via CDN (nao recomendado para producao, mas funcional)
- CSS customizado basico em `app.css` e `mobile.css` com duplicacoes
- Landing page razoavel mas com aparencia generica
- Telas admin funcionais porem sem identidade visual forte
- Telas do cliente (registro, verificacao, sucesso) muito simples
- Sem animacoes, transicoes ou micro-interacoes
- Sem dark mode ou temas personalizaveis por empresa

#### 3.1 Fundacao de Design

- [ ] **Instalar Tailwind CSS via build** (substituir CDN)
  - Configurar Tailwind como dependencia do projeto (npm/Maven frontend plugin)
  - Gerar CSS otimizado e minificado
  - Remover `<script src="cdn.tailwindcss.com">`
  - Definir design tokens customizados (cores, fontes, espacamentos)
- [ ] **Criar sistema de design unificado**
  - Definir paleta de cores primaria/secundaria/accent com variantes
  - Escolher tipografia moderna (Inter, Plus Jakarta Sans ou similar via Google Fonts)
  - Padronizar botoes, inputs, cards, badges, alertas como componentes Thymeleaf fragments
  - Unificar `app.css` e `mobile.css` em um unico arquivo organizado

#### 3.2 Landing Page (primeira impressao)

- [ ] **Redesign completo da landing**
  - Hero section com ilustracao/animacao (pode usar CSS puro ou SVG animado)
  - Melhorar mockup do cartao fidelidade (glassmorphism, sombras suaves, animacao de preenchimento)
  - Adicionar secao de video/demo mostrando o fluxo real
  - Testimonials com fotos (ou avatares gerados) e layout em carousel/grid
  - FAQ com animacao suave de abertura/fechamento (CSS transitions)
  - CTA fixo mobile mais discreto e elegante (blur background)
  - Footer com links uteis, redes sociais, selo de seguranca

#### 3.3 Telas do Cliente (mobile-first)

- [ ] **Registro do cliente** (`cliente/registro.html`)
  - Branding da empresa no topo (nome + segmento com icone)
  - Inputs com icones inline (telefone, email, nome)
  - Botao com loading state ao enviar
  - Indicador de etapas (Etapa 1 de 2)
- [ ] **Verificacao OTP** (`cliente/verificar.html`)
  - Input de codigo com 6 caixas separadas (estilo bancario)
  - Auto-focus entre campos ao digitar
  - Timer visual de expiracao do codigo (5 min countdown)
  - Link "Reenviar codigo" com cooldown visual
- [ ] **Cartao fidelidade** (`cliente/sucesso.html`)
  - Card com visual premium (gradientes suaves, sombras, bordas arredondadas)
  - Animacao dos dots de progresso ao carregar (preenchimento sequencial)
  - Barra de progresso com gradiente animado
  - Botao "Resgatar" com animacao de destaque (pulse/glow) quando disponivel
  - Confetti/celebracao visual quando atinge a meta
- [ ] **QR Code de resgate** (`cliente/qrcode.html`)
  - QR Code grande e centralizado com borda estilizada
  - Timer de expiracao com contagem regressiva visual (circulo animado)
  - Instrucoes claras com icones

#### 3.4 Painel Admin (dashboard profissional)

- [ ] **Navegacao admin** (`fragments/nav-admin.html`)
  - Sidebar colapsavel em desktop, bottom-nav em mobile
  - Icones nos itens de menu (Lucide Icons ou Heroicons via SVG)
  - Indicador de pagina ativa
  - Avatar/iniciais do usuario logado com dropdown
- [ ] **Dashboard principal** (`admin/painel.html`)
  - Cards de metricas com icones e indicadores de tendencia (seta up/down)
  - Tabelas com hover states, zebra striping, e status badges coloridos
  - QR Code da loja em card destacado com opcao de download como imagem
  - Onboarding redesenhado como stepper horizontal com progresso visual
- [ ] **Telas de CRUD** (recompensas, usuarios, config)
  - Formularios com layout mais espaçado e agrupamento visual
  - Modais de confirmacao para acoes destrutivas (excluir recompensa)
  - Feedback visual de sucesso/erro com toast notifications
  - Empty states com ilustracoes quando nao ha dados

#### 3.5 Micro-interacoes e Polish

- [ ] **Animacoes CSS**
  - Fade-in suave ao carregar paginas
  - Transicoes em hover de botoes e links
  - Skeleton loading para carregamento de dados
  - Smooth scroll em ancoras da landing page
- [ ] **Responsividade avancada**
  - Testar em 320px, 375px, 414px (celulares), 768px (tablet), 1024px+ (desktop)
  - Ajustar tabelas admin para scroll horizontal em mobile
  - Garantir touch targets de 44px minimo em botoes mobile
- [ ] **Acessibilidade basica**
  - Contraste de cores WCAG AA
  - Focus visible em todos os elementos interativos
  - Labels e aria-labels nos formularios
  - Alt text nas imagens

---

### SEMANA 4 - Funcionalidades de Negocio

**Objetivo**: Melhorar a experiencia do cliente e do admin

- [ ] **Permitir cliente escolher recompensa**  (resolve A3)
  - Na tela `sucesso.html`, listar todas as recompensas ativas
  - Cliente clica na que deseja resgatar
  - Atualizar `ClienteController` para passar lista em vez de `.findFirst()`
- [ ] **Adicionar recuperacao de senha** (resolve M2)
  - Tela "Esqueci minha senha"
  - Envio de link/codigo por email
  - Tela de redefinicao
- [ ] **Dashboard: filtros por periodo**
  - Filtrar visitas/resgates por semana, mes, periodo customizado
  - Grafico simples de visitas por dia (pode ser com Chart.js ou similar)
- [ ] **Notificacao ao cliente quando recompensa disponivel**
  - Enviar email automatico quando ciclo completar
  - Template de email HTML

---

### SEMANA 5 - Qualidade e Testes

**Objetivo**: Garantir estabilidade antes de ir para producao

- [ ] **Escrever testes unitarios** (resolve A4)
  - `VisitaServiceTest` - calculo de ciclo
  - `ResgateServiceTest` - validacao de elegibilidade
  - `QRCodeResgateServiceTest` - gerar, validar, confirmar, expirar
  - `OtpServiceTest` - gerar, validar, expirar
- [ ] **Escrever testes de integracao**
  - Fluxo completo: cadastro empresa -> registro cliente -> visita -> resgate
  - Testar regras de intervalo minimo
  - Testar QR expirado e ja usado
- [ ] **Gerar QR Code localmente** (resolve M6)
  - Usar biblioteca `com.google.zxing` para gerar QR server-side
  - Eliminar dependencia da API externa
- [ ] **Adicionar log de auditoria basico** (resolve M3)
  - Registrar acoes: login, resgate confirmado, config alterada
  - Tabela `auditoria` com (usuario, acao, timestamp, detalhes)

---

### SEMANA 6 - Producao e Deploy

**Objetivo**: Preparar para primeiro deploy real

- [ ] **Dockerizar a aplicacao**
  - `Dockerfile` multi-stage (build + runtime)
  - `docker-compose.yml` com PostgreSQL + app
  - Variaveis de ambiente para config sensivel
- [ ] **Configurar profiles Spring**
  - `dev` (H2, logs verbose, DataInitializer ativo)
  - `prod` (PostgreSQL, SMTP real, DataInitializer desativado)
- [ ] **Configurar HTTPS**
  - Certificado SSL (Let's Encrypt ou similar)
  - Redirecionar HTTP para HTTPS
- [ ] **Remover DataInitializer do perfil producao**
  - Garantir que dados demo nao sejam criados em prod
- [ ] **Definir estrategia de backup**
  - Backup automatico do PostgreSQL
  - Rotacao de backups

---

### SEMANA 7 - Polish e Lancamento

**Objetivo**: Refinar detalhes finais e lancar MVP

- [ ] **SEO e performance**
  - Meta tags, Open Graph, favicon
  - CSS minificado e otimizado
  - PWA manifest basico (icone na home do celular)
- [ ] **Ampliar segmentos** (resolve M1)
  - Migrar enum `Segmento` para tabela no banco ou campo texto livre
  - Permitir que o dono cadastre o segmento que quiser
- [ ] **Termos de uso e politica de privacidade**
  - Paginas estaticas obrigatorias (LGPD)
  - Checkbox de aceite no cadastro do cliente
- [ ] **Monitoramento basico**
  - Spring Actuator habilitado
  - Health check endpoint
  - Metricas de uso (total cadastros, visitas/dia)
- [ ] **Revisao final de UX**
  - Testes manuais em dispositivos reais (Android/iOS)
  - Ajustes finos de espacamento, cores e tipografia
  - Validar fluxo completo ponta a ponta com usuarios teste

---

## 6. Prioridade Resumida

```
Semana 1  [INFRA]      PostgreSQL + SMTP + CSRF + Validacao
Semana 2  [BUGS]       Exclusao segura + Rate limit + Dados na URL
Semana 3  [DESIGN]     Redesign completo: landing, cliente, admin, animacoes
Semana 4  [FEATURES]   Multiplas recompensas + Recuperar senha + Filtros
Semana 5  [QUALIDADE]  Testes + QR local + Auditoria
Semana 6  [DEPLOY]     Docker + Profiles + HTTPS + Backup
Semana 7  [LAUNCH]     SEO + LGPD + Monitoramento + Revisao final
```

---

## 7. Metricas de Sucesso por Semana

| Semana | Criterio de conclusao |
|---|---|
| 1 | App roda com PostgreSQL, emails chegam, CSRF ativo, inputs validados |
| 2 | Nenhum erro 500 em fluxos normais, rate limit funcionando |
| 3 | Layout moderno e profissional em todas as telas, responsivo, acessivel |
| 4 | Cliente pode escolher entre recompensas, admin ve graficos |
| 5 | Cobertura de testes > 70% nos services, QR gerado localmente |
| 6 | `docker-compose up` sobe tudo, HTTPS funcionando |
| 7 | MVP publicado e acessivel por usuarios reais |

---

*Documento gerado em 09/06/2026 com base na analise completa do codigo-fonte.*
