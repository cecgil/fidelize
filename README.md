# Fidelize – SaaS de Fidelização com QR Code

Fidelize é um **SaaS de fidelização de clientes** simples, moderno e mobile-first, focado em **barbearias, oficinas e restaurantes**.

O objetivo do sistema é **aumentar a recorrência de clientes** por meio de um fluxo intuitivo de **acúmulo de visitas, recompensas e resgate via QR Code**, sem necessidade de aplicativo ou login do cliente.

---

## ✨ Funcionalidades

### 👤 Cliente
- Registro de visita via **QR Code da empresa**
- Identificação simples (nome + telefone)
- Acúmulo automático de visitas
- Progresso visual (cartão fidelidade digital)
- Liberação automática de recompensa ao atingir o limite
- Resgate da recompensa via **QR Code temporário**
- Proteção contra abuso (1 visita a cada 24h)

### 🧑‍🔧 Dono / Estabelecimento
- Mini painel administrativo
- Visualização de clientes e total de visitas
- Visualização de resgates pendentes
- Validação de resgate via QR Code
- Confirmação visual de sucesso após resgate

---

## 🛡️ Regras Anti-Abuso (Anti-Calote)

O sistema implementa proteções simples e eficazes:

- ✅ **1 visita por cliente a cada 24 horas**
- ✅ Identificação única por telefone (por empresa)
- ✅ QR Code de resgate com token único e expiração
- ✅ Validação do resgate obrigatoriamente pelo dono
- ✅ Reset automático do contador após cada resgate

Essas regras equilibram **segurança e boa experiência do usuário**.

---

## 🧠 Modelo de Fidelização

- Cada visita registrada soma **1 ponto**
- Ao atingir o número definido pela recompensa:
  - o cliente pode resgatar
  - o contador reinicia automaticamente
- Histórico é mantido para análise futura

Modelo inspirado em cartões fidelidade físicos, porém digital e escalável.

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21**
- **Spring Boot**
- Spring MVC
- Spring Data JPA
- Hibernate
- H2 (ambiente de desenvolvimento)

### Frontend
- **Thymeleaf**
- HTML5
- CSS mobile-first (sem frameworks pesados)

### Outros
- QR Code gerado dinamicamente
- Arquitetura orientada a domínio (package by feature)

---

## 📂 Estrutura do Projeto (simplificada)

src/main/java
└── com.cecgil.fidelize
├── cliente
├── empresa
├── fidelidade
│ ├── visita
│ ├── recompensa
│ ├── resgate
│ └── qrcode
├── admin
└── FidelizeApplication.java

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Java 21
- Maven

### Executar
mvn spring-boot:run
A aplicação ficará disponível em:

Copiar código
http://localhost:8080
🗄️ Banco de Dados (H2)
Console H2:

Copiar código
http://localhost:8080/h2
Configuração:

JDBC URL: jdbc:h2:mem:loyaltydb

Usuário: sa

Senha: (vazio)

📱 Fluxos Principais
QR Code da Empresa
bash
Copiar código
/c/{empresaId}
Painel do Dono
bash
Copiar código
/admin/{empresaId}
Validação de Resgate
bash
Copiar código
/validar/{token}

📌 Status do Projeto

✔ MVP funcional
✔ Fluxo completo de fidelização
✔ Pronto para testes com usuários reais
✔ Base sólida para evolução como SaaS

🔜 Próximos Passos (Planejados)

Múltiplas recompensas por empresa

Configuração de regras por empresa

Histórico de ciclos e relatórios

Persistência em PostgreSQL

Dockerização para produção

📄 Licença

Projeto em desenvolvimento.
Uso livre para fins educacionais e comerciais conforme evolução do projeto.
