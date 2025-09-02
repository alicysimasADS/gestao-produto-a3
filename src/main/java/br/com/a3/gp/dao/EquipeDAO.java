package br.com.a3.gp.dao;

import br.com.a3.gp.config.Database;
import br.com.a3.gp.modelo.Equipe;
import java.sql.*;
import java.util.*;

public class EquipeDAO {

    private Equipe map(ResultSet rs) throws SQLException {
        Equipe e = new Equipe();
        e.setId(rs.getInt("id"));
        e.setNome(rs.getString("nome"));
        e.setDescricao(rs.getString("descricao"));
        return e;
    }

    public List<Equipe> listarTodas() {
        List<Equipe> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,nome,descricao FROM equipe ORDER BY id DESC")) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }

    public Equipe buscarPorId(int id) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id,nome,descricao FROM equipe WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public void inserir(Equipe e) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO equipe(nome,descricao) VALUES (?,?)")) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getDescricao());
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    public void atualizar(Equipe e) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE equipe SET nome=?, descricao=? WHERE id=?")) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getDescricao());
            ps.setInt(3, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    public void excluir(int id) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM equipe WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
}
