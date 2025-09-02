package br.com.a3.gp.dao;

import br.com.a3.gp.config.Database;
import br.com.a3.gp.modelo.Usuario;
import java.sql.*;
import java.util.*;

public class UsuarioDAO {

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setLogin(rs.getString("login"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setAtivo(rs.getInt("ativo")==1);
        u.setCpf(rs.getString("cpf"));
        u.setEmail(rs.getString("email"));
        u.setCargo(rs.getString("cargo"));
        u.setPerfil(rs.getString("perfil"));
        return u;
    }

    public Usuario buscarPorLogin(String login) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id,nome,login,senha_hash,ativo,cpf,email,cargo,perfil FROM usuario WHERE login=?")) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public Usuario buscarPorId(int id) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id,nome,login,senha_hash,ativo,cpf,email,cargo,perfil FROM usuario WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,nome,login,senha_hash,ativo,cpf,email,cargo,perfil FROM usuario ORDER BY id DESC")) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }

    public void inserir(Usuario u) {
        String sql = "INSERT INTO usuario(nome,login,senha_hash,ativo,cpf,email,cargo,perfil) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getLogin());
            ps.setString(3, u.getSenhaHash());
            ps.setInt(4, u.isAtivo() ? 1 : 0);
            ps.setString(5, u.getCpf());
            ps.setString(6, u.getEmail());
            ps.setString(7, u.getCargo());
            ps.setString(8, u.getPerfil());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void atualizar(Usuario u) {
        String sql = "UPDATE usuario SET nome=?, login=?, senha_hash=?, ativo=?, cpf=?, email=?, cargo=?, perfil=? WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getLogin());
            ps.setString(3, u.getSenhaHash());
            ps.setInt(4, u.isAtivo() ? 1 : 0);
            ps.setString(5, u.getCpf());
            ps.setString(6, u.getEmail());
            ps.setString(7, u.getCargo());
            ps.setString(8, u.getPerfil());
            ps.setInt(9, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void excluir(int id) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM usuario WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // Papéis
    public List<String> listarPapeisDoUsuario(int usuarioId) {
        List<String> papeis = new ArrayList<>();
        String sql = "SELECT p.nome FROM papel p INNER JOIN usuario_papel up ON up.papel_id=p.id WHERE up.usuario_id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) papeis.add(rs.getString(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return papeis;
    }

    public void atribuirPapel(int usuarioId, String nomePapel) {
        try (Connection con = Database.getConnection()) {
            int papelId;
            try (PreparedStatement psel = con.prepareStatement("SELECT id FROM papel WHERE nome=?")) {
                psel.setString(1, nomePapel);
                try (ResultSet rs = psel.executeQuery()) {
                    if (!rs.next()) throw new RuntimeException("Papel não existe: " + nomePapel);
                    papelId = rs.getInt(1);
                }
            }
            try (PreparedStatement ins = con.prepareStatement("INSERT OR IGNORE INTO usuario_papel(usuario_id,papel_id) VALUES (?,?)")) {
                ins.setInt(1, usuarioId);
                ins.setInt(2, papelId);
                ins.executeUpdate();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void removerPapel(int usuarioId, String nomePapel) {
        try (Connection con = Database.getConnection()) {
            int papelId;
            try (PreparedStatement psel = con.prepareStatement("SELECT id FROM papel WHERE nome=?")) {
                psel.setString(1, nomePapel);
                try (ResultSet rs = psel.executeQuery()) {
                    if (!rs.next()) return;
                    papelId = rs.getInt(1);
                }
            }
            try (PreparedStatement del = con.prepareStatement("DELETE FROM usuario_papel WHERE usuario_id=? AND papel_id=?")) {
                del.setInt(1, usuarioId);
                del.setInt(2, papelId);
                del.executeUpdate();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
