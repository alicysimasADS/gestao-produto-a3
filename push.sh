#!/usr/bin/env bash
set -euo pipefail
if [ $# -ne 1 ]; then
  echo "Uso: ./push.sh <seu-usuario/seu-repositorio>"
  exit 1
fi
REPO="$1"
git init
git add .
git commit -m "Projeto A3: CRUDs completos e fluxo QA"
git branch -M main
git remote remove origin 2>/dev/null || true
git remote add origin "https://github.com/${REPO}.git"
git push -u origin main
echo "Pronto! Repositório publicado em: https://github.com/${REPO}"
