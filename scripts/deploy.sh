#!/bin/bash

# Script de Deploy Manual via GitHub Actions
# Dispara o workflow CD sem expor dados sensíveis
# Pré-requisito: GitHub CLI instalado (gh)
# Instalação: https://cli.github.com/

set -e

echo "=================================================="
echo "🚀 DEPLOY MANUAL - Inventory Notification System"
echo "=================================================="
echo ""

# Verificar se gh CLI está instalado
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) não está instalado!"
    echo ""
    echo "Instale com:"
    echo "  macOS/Linux: brew install gh"
    echo "  Windows: winget install --id GitHub.cli"
    echo "  Ou: https://cli.github.com/"
    echo ""
    exit 1
fi

# Verificar se está autenticado
if ! gh auth status &> /dev/null; then
    echo "❌ Você não está autenticado no GitHub CLI!"
    echo ""
    echo "Execute: gh auth login"
    echo ""
    exit 1
fi

echo "✅ GitHub CLI está instalado e autenticado"
echo ""

# Confirmar execução
echo "⚠️  Isso irá disparar o workflow de deploy no GitHub Actions"
echo "   O deploy será feito usando os secrets configurados no repositório"
echo ""
read -p "Deseja continuar? (s/N): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Ss]$ ]]; then
    echo "❌ Deploy cancelado"
    exit 0
fi

echo ""
echo "🚀 Disparando workflow de deploy..."
echo ""

# Disparar workflow
gh workflow run cd.yml

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Workflow disparado com sucesso!"
    echo ""
    echo "📊 Acompanhe o progresso em:"
    echo "   https://github.com/$(gh repo view --json nameWithOwner -q .nameWithOwner)/actions"
    echo ""
    echo "Ou execute: gh run watch"
    echo ""
else
    echo ""
    echo "❌ Falha ao disparar workflow"
    exit 1
fi

