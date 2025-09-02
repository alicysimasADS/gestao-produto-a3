package br.com.a3.gp.config;

import java.sql.*;

public class SchemaInit {
    public static void ensure() {
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement()) {

            st.execute("PRAGMA foreign_keys = ON");

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

            st.execute("CREATE TABLE IF NOT EXISTS usuario_papel (" +
                       "usuario_id INTEGER NOT NULL," +
                       "papel_id INTEGER NOT NULL," +
                       "PRIMARY KEY(usuario_id, papel_id)," +
                       "FOREIGN KEY(usuario_id) REFERENCES usuario(id) ON DELETE CASCADE," +
                       "FOREIGN KEY(papel_id) REFERENCES papel(id) ON DELETE CASCADE" +
                       ")");

            st.execute("CREATE TABLE IF NOT EXISTS equipe (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "nome TEXT NOT NULL," +
                       "descricao TEXT" +
                       ")");

            st.execute("CREATE TABLE IF NOT EXISTS usuario_equipe (" +
                       "usuario_id INTEGER NOT NULL," +
                       "equipe_id INTEGER NOT NULL," +
                       "PRIMARY KEY(usuario_id, equipe_id)," +
                       "FOREIGN KEY(usuario_id) REFERENCES usuario(id) ON DELETE CASCADE," +
                       "FOREIGN KEY(equipe_id) REFERENCES equipe(id) ON DELETE CASCADE" +
                       ")");

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

            st.execute("CREATE TABLE IF NOT EXISTS projeto_equipe (" +
                       "projeto_id INTEGER NOT NULL," +
                       "equipe_id INTEGER NOT NULL," +
                       "data_inicio TEXT," +
                       "data_fim TEXT," +
                       "PRIMARY KEY(projeto_id, equipe_id)," +
                       "FOREIGN KEY(projeto_id) REFERENCES projeto(id) ON DELETE CASCADE," +
                       "FOREIGN KEY(equipe_id) REFERENCES equipe(id) ON DELETE CASCADE" +
                       ")");

            st.execute("CREATE TABLE IF NOT EXISTS tarefa (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "projeto_id INTEGER NOT NULL," +
                       "responsavel_id INTEGER," +
                       "titulo TEXT NOT NULL," +
                       "descricao TEXT," +
                       "status TEXT NOT NULL DEFAULT 'em_execucao'," +
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
                       "acao TEXT NOT NULL," +
                       "observacao TEXT," +
                       "data_registro TEXT DEFAULT CURRENT_TIMESTAMP," +
                       "FOREIGN KEY(tarefa_id) REFERENCES tarefa(id) ON DELETE CASCADE," +
                       "FOREIGN KEY(usuario_id) REFERENCES usuario(id)" +
                       ")");

            st.execute("CREATE TABLE IF NOT EXISTS audit_log (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                       "usuario_id INTEGER," +
                       "operacao TEXT NOT NULL," +
                       "origem TEXT," +
                       "registro_id INTEGER," +
                       "data_hora TEXT DEFAULT CURRENT_TIMESTAMP," +
                       "obs TEXT," +
                       "FOREIGN KEY(usuario_id) REFERENCES usuario(id)" +
                       ")");

            // Inserção de papéis padrão
            String[] papeis = {"ADMIN","GERENTE_PROJETO","DESENVOLVEDOR","TESTADOR","QA"};
            try (PreparedStatement ps = con.prepareStatement("INSERT OR IGNORE INTO papel(nome) VALUES (?)")) {
                for (String p : papeis) {
                    ps.setString(1, p);
                    ps.executeUpdate();
                }
            }

            // Usuário admin padrão
            try (PreparedStatement check = con.prepareStatement("SELECT id FROM usuario WHERE login = 'admin'");
                 ResultSet rs = check.executeQuery()) {
                if (!rs.next()) {
                    String hash = br.com.a3.gp.util.SenhaUtil.hash("admin123");
                    try (PreparedStatement ins = con.prepareStatement("INSERT INTO usuario(nome,login,senha_hash,ativo) VALUES (?,?,?,1)")) {
                        ins.setString(1, "Administrador");
                        ins.setString(2, "admin");
                        ins.setString(3, hash);
                        ins.executeUpdate();
                    }
                    try (PreparedStatement u = con.prepareStatement("SELECT id FROM usuario WHERE login = 'admin'");
                         ResultSet r = u.executeQuery()) {
                        if (r.next()) {
                            int adminId = r.getInt(1);
                            try (PreparedStatement psel = con.prepareStatement("SELECT id FROM papel WHERE nome='ADMIN'");
                                 ResultSet pr = psel.executeQuery()) {
                                if (pr.next()) {
                                    int papelId = pr.getInt(1);
                                    try (PreparedStatement up = con.prepareStatement("INSERT OR IGNORE INTO usuario_papel(usuario_id,papel_id) VALUES (?,?)")) {
                                        up.setInt(1, adminId);
                                        up.setInt(2, papelId);
                                        up.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao inicializar schema: " + e.getMessage(), e);
        }
    }
}
