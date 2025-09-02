package br.com.a3.gp.config;

import java.sql.*;

public class Database {
    private static final String DEFAULT_URL = "jdbc:sqlite:db/gp_a3_pronto.db";

    public static Connection getConnection() throws SQLException {
        String url = System.getProperty("DB_URL", DEFAULT_URL);
        Connection con = DriverManager.getConnection(url);
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (Exception ignored) {}
        return con;
    }
}
