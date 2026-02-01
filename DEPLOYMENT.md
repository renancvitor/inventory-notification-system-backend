# 🚀 Deployment Guide

## Automação com GitHub Actions

O projeto está configurado com **GitHub Actions** para deploy automático:

1. **CI Pipeline** (`.github/workflows/ci.yml`):
   - Roda em todo push para `main`
   - Compila com `mvn clean package`
   - Roda todos os testes
   - Se passar, dispara o CD Pipeline

2. **CD Pipeline** (`.github/workflows/cd.yml`):
   - Compila novamente o JAR
   - Roda migrations do Flyway
   - Envia o JAR para EC2
   - Faz backup do JAR antigo
   - Reinicia o serviço
   - Valida se o serviço iniciou corretamente
   - Faz health check

### Secrets Necessários no GitHub

Configure estes secrets no repositório (`Settings > Secrets and variables > Actions`):

```
EC2_HOST              = 18.224.70.113
EC2_USER              = ec2-user
EC2_SSH_KEY           = (conteúdo da sua chave PEM privada)
EC2_APP_DIR           = /home/ec2-user
SERVICE_NAME          = inventory-backend
DB_FLYWAY_URL         = jdbc:postgresql://seu-rds:5432/inventory_db
DB_USER               = postgres
DB_PASSWORD           = sua-senha
```

---

## Deploy Manual

Se quiser fazer deploy manualmente (sem esperar o push), você tem 3 opções:

### Opção 1: Via Script (Recomendado)

```bash
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

**Pré-requisito**: GitHub CLI instalado
- macOS/Linux: `brew install gh`
- Windows: `winget install --id GitHub.cli`
- Ou: https://cli.github.com/

### Opção 2: Via GitHub CLI

```bash
gh workflow run cd.yml
gh run watch  # Acompanhar execução
```

### Opção 3: Via GitHub UI

1. Acesse: `Actions` no repositório
2. Selecione: `CD - Deploy to EC2`
3. Clique: `Run workflow` > `Run workflow`

**Todos usam os secrets do GitHub, sem expor dados sensíveis!**

---

## Troubleshooting

### Serviço não inicia após deploy

```bash
# Ver logs do serviço
ssh ec2-user@18.224.70.113 "sudo journalctl -u inventory-backend -n 50 -f"

# Ver status do serviço
ssh ec2-user@18.224.70.113 "sudo systemctl status inventory-backend"
```

### JAR não está sendo atualizado

Verificar:
1. GitHub Actions rodou com sucesso? Checar na aba "Actions" do repo
2. JAR foi criado? Ver logs do workflow "Build JAR"
3. JAR foi enviado? Ver logs "Deploy JAR to EC2"
4. Versão do JAR em produção é a mesma? Rodar `./scripts/deploy.sh`

### Erro de conexão SSH

```bash
# Testar conexão
ssh -i ~/.ssh/ec2.pem ec2-user@18.224.70.113 "echo OK"

# Se falhar, verificar:
# 1. Arquivo de chave PEM tem permissão 600
chmod 600 ~/.ssh/ec2.pem

# 2. IP da EC2 está correto
# 3. Security Group permite SSH na porta 22
```

---

## Fluxo de Desenvolvimento

1. **Fazer mudanças** no código
2. **Fazer commit** e **push** para `main`
3. **CI Pipeline** roda automaticamente
4. Se testes passarem, **CD Pipeline** roda
5. Aplicação é **deployed** e reiniciada na EC2
6. **Health check** valida que está rodando

---

## Verificar Deploy

```bash
# Acessar a aplicação
curl http://18.224.70.113:8080/health

# Ver Swagger UI
http://18.224.70.113:8080/swagger-ui/index.html

# Ver logs da aplicação
ssh ec2-user@18.224.70.113 "sudo journalctl -u inventory-backend -f"
```
