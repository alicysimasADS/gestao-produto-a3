package br.com.a3.gp.config;

import java.sql.*;

/**
 * Responsável por garantir que todo o schema mínimo exista antes da aplicação iniciar.
 * Cria tabelas idempotentes (CREATE TABLE IF NOT EXISTS), ativa FKs,
 * povoa papéis básicos e um usuário admin inicial com o papel ADMIN.
 */
public class SchemaInit {

    /**
     * Executa a verificação/criação do schema e dados de bootstrap.
     * É seguro chamar múltiplas vezes (operações são idempotentes).
     */
    public static void ensure() {
        try (
            Connection con = Database.getConnection();
            Statement st = con.createStatement()
        ) {
            // Garante integridade referencial no SQLite (por padrão vem OFF)
            st.execute("PRAGMA foreign_keys = ON");

            // -------------------------
            // TABELAS DE IDENTIDADE (Usuário e Papéis)
            // -------------------------
            st.execute("CREATE TABLE IF NOT EXISTS usuario (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "nome TEXT NOT NULL," +
                       "login TEXT UNIQUE NOT NULL," +
                       "senha_hash TEXT NOT NULL," +
                       "ativo INTEGER NOT NULL DEFAULT 1," +
                       "cpf TEXT," +
                       "email TEXT," +
                       "cargo TEXT," +
                       "perfil TEXT," +
                       "criado_em TEXT DEFAULT CURRENT_TIMESTAMP" +
                       ")");

            st.execute("CREATE TABLE IF NOT EXISTS papel (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "nome TEXT UNIQUE NOT NULL" +
                       ")");

            // Tabela de junção N:N entre usuário e papel
            st.execute("CREATE TABLE IF NOT EXISTS usuario_papel (" +
                       "usuario_id INTEGER NOT NULL," +
                       "papel_id INTEGER NOT NULL," +
                       "PRIMARY KEY(usuario_id, papel_id)," +
                       "FOREIGN KEY(usuario_id) REFERENCES usuario(id) ON DELETE CASCADE," +
                       "FOREIGN KEY(papel_id) REFERENCES papel(id) ON DELETE CASCADE" +
                       ")");

            // -------------------------
            // TABELAS DE TIMES/EQUIPES
            // -------------------------
            st.execute("CREATE TABLE IF NOT EXISTS equipe (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "nome TEXT NOT NULL," +
                       "descricao TEXT" +
                       ")");

            // Usuário ↔ Equipe (N:N)
            st.execute("CREATE TABLE IF NOT EXISTS usuario_equipe (" +
                       "usuario_id INTEGER NOT NULL," +
                       "equipe_id INTEGER NOT NULL," +
                       "PRIMARY KEY(usuario_id, equipe_id)," +
                       "FOREIGN KEY(usuario_id) REFERENCES usuario(id) ON DELETE CASCADE," +
                       "FOREIGN KEY(equipe_id) REFERENCES equipe(id) ON DELETE CASCADE" +
                       ")");

            // -------------------------
            // TABELAS DE PROJETO E VÍNCULOS
            // -------------------------
            st.execute("CREATE TABLE IF NOT EXISTS projeto (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "nome TEXT NOT NULL UNIQUE," +
                       "descricao TEXT," +
                       "data_inicio TEXT," +
                       "data_prev_fim TEXT," +
                       "status TEXT DEFAULT 'planejado'," +
                       "gerente_id INTEGER," +
                       "ativo INTEGER NOT NULL DEFAULT 1," +
                       "criado_em TEXT DEFAULT CURRENT_TIMESTAMP," +
                       "FOREIGN KEY(gerente_id) REFERENCES usuario(id)" +
                       ")");

            // Projeto ↔ Equipe (N:N) com datas de alocação
            st.execute("CREATE TABLE IF NOT EXISTS projeto_equipe (" +
                       "projeto_id INTEGER NOT NULL," +
                       "equipe_id INTEGER NOT NULL," +
                       "data_inicio TEXT," +
                       "data_fim TEXT," +
                       "PRIMARY KEY(projeto_id, equipe_id)," +
                       "FOREIGN KEY(projeto_id) REFERENCES projeto(id) ON DELETE CASCADE," +
                       "FOREIGN KEY(equipe_id) REFERENCES equipe(id) ON DELETE CASCADE" +
                       ")");

            // -------------------------
            // TAREFAS E HISTÓRICO
            // -------------------------
            st.execute("CREATE TABLE IF NOT EXISTS tarefa (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "projeto_id INTEGER NOT NULL," +
                       "responsavel_id INTEGER," +
                       "titulo TEXT NOT NULL," +
                       "descricao TEXT," +
                       "status TEXT NOT NULL DEFAULT 'em_execucao'," +   // fluxo: em_execucao → em_qa → aprovado|reprovado
                       "prioridade TEXT DEFAULT 'media'," +
                       "data_criacao TEXT DEFAULT CURRENT_TIMESTAMP," +
                       "data_previsao TEXT," +
                       "data_conclusao TEXT," +
                       "FOREIGN KEY(projeto_id) REFERENCES projeto(id) ON DELETE CASCADE," +
                       "FOREIGN KEY(responsavel_id) REFERENCES usuario(id)" +
                       ")");

            st.execute("CREATE TABLE IF NOT EXISTS historico_tarefa (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "tarefa_id INTEGER NOT NULL," +
                       "usuario_id INTEGER," +
                       "acao TEXT NOT NULL," +                           // ex.: enviar_para_qa, aprovar_qa, reprovar_qa
                       "observacao TEXT," +                              // motivo de reprovação etc.
                       "data_registro TEXT DEFAULT CURRENT_TIMESTAMP," +
                       "FOREIGN KEY(tarefa_id) REFERENCES tarefa(id) ON DELETE CASCADE," +
                       "FOREIGN KEY(usuario_id) REFERENCES usuario(id)" +
                       ")");

            // -------------------------
            // AUDITORIA (opcional, para rastreabilidade)
            // -------------------------
            st.execute("CREATE TABLE IF NOT EXISTS audit_log (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "usuario_id INTEGER," +
                       "operacao TEXT NOT NULL," +                       // INSERT/UPDATE/DELETE etc.
                       "origem TEXT," +                                  // tabela/origem da operação
                       "registro_id INTEGER," +                          // id do registro afetado
                       "data_hora TEXT DEFAULT CURRENT_TIMESTAMP," +
                       "obs TEXT," +
                       "FOREIGN KEY(usuario_id) REFERENCES usuario(id)" +
                       ")");

            // -------------------------
            // DADOS INICIAIS (bootstrap)
            // -------------------------

            // Papéis padrão do sistema
            String[] papeis = {"ADMIN","GERENTE_PROJETO","DESENVOLVEDOR","CSM","QA"};
            try (PreparedStatement ps = con.prepareStatement("INSERT OR IGNORE INTO papel(nome) VALUES (?)")) {
                for (String p : papeis) {
                    ps.setString(1, p);
                    ps.executeUpdate();
                }
            }

            // Cria usuário admin (se não existir) e associa papel ADMIN
            try (
                PreparedStatement check = con.prepareStatement("SELECT id FROM usuario WHERE login = 'admin'");
                ResultSet rs = check.executeQuery()
            ) {
                if (!rs.next()) {
                    // Gera hash seguro para senha padrão (ex.: BCrypt via SenhaUtil)
                    String hash = br.com.a3.gp.util.SenhaUtil.hash("admin123");

                    // Insere usuário admin
                    try (PreparedStatement ins = con.prepareStatement(
                            "INSERT INTO usuario(nome,login,senha_hash,ativo) VALUES (?,?,?,1)")
                    ) {
                        ins.setString(1, "Administrador");
                        ins.setString(2, "admin");
                        ins.setString(3, hash);
                        ins.executeUpdate();
                    }

                    // Recupera ID do admin recém-criado
                    int adminId = -1;
                    try (
                        PreparedStatement u = con.prepareStatement("SELECT id FROM usuario WHERE login = 'admin'");
                        ResultSet r = u.executeQuery()
                    ) {
                        if (r.next()) {
                            adminId = r.getInt(1);
                        }
                    }

                    // Vincula papel ADMIN ao usuário admin
                    if (adminId != -1) {
                        try (
                            PreparedStatement psel = con.prepareStatement("SELECT id FROM papel WHERE nome='ADMIN'");
                            ResultSet pr = psel.executeQuery()
                        ) {
                            if (pr.next()) {
                                int papelId = pr.getInt(1);
                                try (PreparedStatement up = con.prepareStatement(
                                        "INSERT OR IGNORE INTO usuario_papel(usuario_id,papel_id) VALUES (?,?)")
                                ) {
                                    up.setInt(1, adminId);
                                    up.setInt(2, papelId);
                                    up.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            // Loga stacktrace para facilitar diagnóstico em ambiente de avaliação
            e.printStackTrace();
            throw new RuntimeException("Erro ao inicializar schema: " + e.getMessage(), e);
        }
    }
}
