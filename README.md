# Gestão de Produto A3
Aplicação Java Swing com SQLite e BCrypt, padrão MVC, contemplando login, usuários com papéis, equipes, projetos, tarefas e fluxo DEV → QA → aprovado/reprovado.

## Requisitos
- Java 17+
- Maven 3.8+

## Executar
```bash
mvn -q -DskipTests exec:java
```
O banco SQLite é criado no primeiro run em `db/gp_a3.db`.

## Login inicial
- Usuário: `admin`
- Senha: `admin123`

## Estrutura
- `config` Database e SchemaInit
- `modelo` POJOs de domínio
- `dao` DAOs com CRUDs
- `servico` Login e Sessão
- `visao` Telas Swing para CRUDs

## Publicar no GitHub
1. Crie um repositório vazio no GitHub
2. Neste diretório, rode:
```bash
git init
git add .
git commit -m "Projeto A3: CRUDs completos e fluxo QA"
git branch -M main
git remote add origin https://github.com/<seu-usuario>/<seu-repositorio>.git
git push -u origin main
```
