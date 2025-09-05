package br.com.a3.gp.config;

import java.sql.*;

public class Database {

    // Caminho padrão do banco de dados SQLite utilizado no projeto.
    // Pode ser sobrescrito por uma variável de sistema chamada "DB_URL".
    private static final String DEFAULT_URL = "jdbc:sqlite:db/gp_a3_pronto.db";

    /**
     * Obtém uma conexão com o banco de dados SQLite.
     * Caso exista a propriedade do sistema "DB_URL", ela será usada como caminho do banco.
     * Habilita a verificação de integridade de chaves estrangeiras (PRAGMA foreign_keys = ON).
     * @return Conexão ativa com o banco de dados.
     * @throws SQLException Se ocorrer erro ao tentar conectar.
     */
    public static Connection getConnection() throws SQLException {
        // Usa DB_URL da JVM se definida, senão usa o caminho padrão.
        String url = System.getProperty("DB_URL", DEFAULT_URL);

        // Cria a conexão com o SQLite.
        Connection con = DriverManager.getConnection(url);

        // Ativa suporte a chave estrangeira (por padrão, desabilitado no SQLite).
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (Exception ignored) {
            // Em caso de erro nessa configuração, ignora (não impede a conexão).
        }

        return con;
    }
}
