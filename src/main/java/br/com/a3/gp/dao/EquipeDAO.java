package br.com.a3.gp.dao;

import br.com.a3.gp.config.Database;
import br.com.a3.gp.modelo.Equipe;
import br.com.a3.gp.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    private Equipe map(ResultSet rs) throws SQLException {
        Equipe e = new Equipe();
        e.setId(rs.getInt("id"));
        e.setNome(rs.getString("nome"));
        e.setDescricao(rs.getString("descricao"));
        return e;
    }

    // -------- CRUD Equipe --------

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

    /** Insere e PREENCHE o id gerado dentro do objeto (necessário para salvar vínculos depois). */
    public void inserir(Equipe e) {
        String sql = "INSERT INTO equipe(nome,descricao) VALUES (?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getDescricao());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getInt(1));
            }
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

    // -------- VÍNCULOS USUÁRIO ↔ EQUIPE (tabela usuario_equipe) --------

    /** Retorna os IDs dos usuários já vinculados à equipe (para pré-selecionar no formulário). */
    public List<Integer> listarUsuarioIdsDaEquipe(int equipeId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT usuario_id FROM usuario_equipe WHERE equipe_id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, equipeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return ids;
    }

    /**
     * Substitui os vínculos da equipe pelos informados (DELETE ALL + INSERT).
     * Use após inserir/atualizar a equipe.
     */
    public void atualizarMembrosEquipe(int equipeId, List<Integer> usuarioIds) {
        String del = "DELETE FROM usuario_equipe WHERE equipe_id=?";
        String ins = "INSERT OR IGNORE INTO usuario_equipe(usuario_id, equipe_id) VALUES (?,?)";
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psDel = con.prepareStatement(del)) {
                psDel.setInt(1, equipeId);
                psDel.executeUpdate();
            }
            if (usuarioIds != null && !usuarioIds.isEmpty()) {
                try (PreparedStatement psIns = con.prepareStatement(ins)) {
                    for (Integer uid : usuarioIds) {
                        psIns.setInt(1, uid);
                        psIns.setInt(2, equipeId);
                        psIns.addBatch();
                    }
                    psIns.executeBatch();
                }
            }
            con.commit();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
