# Gestão de Produto A3

[![CI - Build (Maven)](https://github.com/<seu-usuario>/<seu-repositorio>/actions/workflows/maven.yml/badge.svg)](https://github.com/<seu-usuario>/<seu-repositorio>/actions/workflows/maven.yml)

Aplicação Java Swing com SQLite e BCrypt. Padrão MVC. CRUDs de **Usuários**, **Equipes**, **Projetos** e **Tarefas** com fluxo **DEV → QA → Aprovado/Reprovado**.

## Como executar
```bash
mvn -q -DskipTests exec:java
```
O banco será criado automaticamente em `db/gp_a3.db` no primeiro run.

## Login inicial
- Usuário: `admin`
- Senha: `admin123`

## Estrutura do projeto
- `config/` (Database, SchemaInit)
- `modelo/` (POJOs)
- `dao/` (CRUDs, PreparedStatement)
- `servico/` (Login e Sessão)
- `visao/` (Telas Swing)

## Fluxo de Tarefas
- `em_execucao` → `em_qa` → `aprovado` ou `reprovado`.
A tela de tarefas dispara as ações do DAO e registra histórico.

## Publicar no GitHub
Consulte `PUBLISH.md` ou execute `./push.sh`.
