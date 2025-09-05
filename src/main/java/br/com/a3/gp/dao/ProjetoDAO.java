package br.com.a3.gp.dao;

import br.com.a3.gp.config.Database;
import br.com.a3.gp.modelo.Projeto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    private Projeto map(ResultSet rs) throws SQLException {
        Projeto p = new Projeto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setDataInicio(rs.getString("data_inicio"));
        p.setDataPrevFim(rs.getString("data_prev_fim"));
        p.setStatus(rs.getString("status"));
        int gid = rs.getInt("gerente_id");
        p.setGerenteId(rs.wasNull() ? null : gid);
        p.setAtivo(rs.getInt("ativo") == 1);
        return p;
    }

    // ---------------------------
    // Regras de negócio (papéis)
    // ---------------------------

    /**
     * Verifica se o usuário tem o papel GERENTE_PROJETO.
     * Retorna false para gerenteId null.
     */
    private boolean isGerenteProjeto(Connection con, Integer gerenteId) throws SQLException {
        if (gerenteId == null) return false;
        String sql =
            "SELECT 1 " +
            "FROM usuario_papel up " +
            "JOIN papel p ON p.id = up.papel_id " +
            "WHERE up.usuario_id = ? AND p.nome = 'GERENTE_PROJETO' " +
            "LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, gerenteId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // (Opcional) útil para popular a UI com apenas gerentes
    public List<Integer> listarGerentesIds() {
        List<Integer> ids = new ArrayList<>();
        String sql =
            "SELECT DISTINCT up.usuario_id " +
            "FROM usuario_papel up " +
            "JOIN papel p ON p.id = up.papel_id " +
            "WHERE p.nome = 'GERENTE_PROJETO' " +
            "ORDER BY up.usuario_id DESC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ids.add(rs.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ids;
    }

    // ---------------------------
    // CRUD
    // ---------------------------

    public List<Projeto> listarTodos() {
        List<Projeto> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT id,nome,descricao,data_inicio,data_prev_fim,status,gerente_id,ativo " +
                 "FROM projeto ORDER BY id DESC")) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }

    public Projeto buscarPorId(int id) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT id,nome,descricao,data_inicio,data_prev_fim,status,gerente_id,ativo " +
                 "FROM projeto WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }
    
    // Lista IDs de equipes vinculadas ao projeto (para pré-selecionar na UI)
public List<Integer> listarEquipeIdsDoProjeto(int projetoId) {
    List<Integer> ids = new ArrayList<>();
    String sql = "SELECT equipe_id FROM projeto_equipe WHERE projeto_id=?";
    try (Connection con = Database.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, projetoId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ids.add(rs.getInt(1));
        }
    } catch (SQLException e) { throw new RuntimeException(e); }
    return ids;
}
// Atualiza os vínculos M:N: apaga os antigos e insere os novos (transacional)
public void atualizarEquipesDoProjeto(int projetoId, List<Integer> equipeIds) {
    String del = "DELETE FROM projeto_equipe WHERE projeto_id=?";
    String ins = "INSERT OR IGNORE INTO projeto_equipe(projeto_id, equipe_id) VALUES (?,?)";
    try (Connection con = Database.getConnection()) {
        con.setAutoCommit(false);
        try (PreparedStatement d = con.prepareStatement(del)) {
            d.setInt(1, projetoId);
            d.executeUpdate();
        }
        if (equipeIds != null && !equipeIds.isEmpty()) {
            try (PreparedStatement i = con.prepareStatement(ins)) {
                for (Integer eid : equipeIds) {
                    i.setInt(1, projetoId);
                    i.setInt(2, eid);
                    i.addBatch();
                }
                i.executeBatch();
            }
        }
        con.commit();
    } catch (SQLException e) { throw new RuntimeException(e); }
}


// CHAME com RETURN_GENERATED_KEYS para obter o ID na inserção
public void inserir(Projeto p) {
    String sql = "INSERT INTO projeto(nome,descricao,data_inicio,data_prev_fim,status,gerente_id,ativo) VALUES (?,?,?,?,?,?,?)";
    try (Connection con = Database.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        // Valida papel de gerente (se houver)
        if (p.getGerenteId() != null && !isGerenteProjeto(con, p.getGerenteId())) {
            throw new RuntimeException("Somente usuários com papel GERENTE_PROJETO podem ser gerente do projeto.");
        }

        ps.setString(1, p.getNome());
        ps.setString(2, p.getDescricao());
        ps.setString(3, p.getDataInicio());
        ps.setString(4, p.getDataPrevFim());
        ps.setString(5, p.getStatus());
        if (p.getGerenteId()==null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, p.getGerenteId());
        ps.setInt(7, p.isAtivo()?1:0);
        ps.executeUpdate();

        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) p.setId(keys.getInt(1));
        }
    } catch (SQLException e) { throw new RuntimeException(e); }
}


    public void atualizar(Projeto p) {
        String sql = "UPDATE projeto SET nome=?, descricao=?, data_inicio=?, data_prev_fim=?, status=?, gerente_id=?, ativo=? WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Valida papel de gerente (se houver gerente definido)
            if (p.getGerenteId() != null && !isGerenteProjeto(con, p.getGerenteId())) {
                throw new RuntimeException("Somente usuários com papel GERENTE_PROJETO podem ser gerente do projeto.");
            }

            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setString(3, p.getDataInicio());
            ps.setString(4, p.getDataPrevFim());
            ps.setString(5, p.getStatus());
            if (p.getGerenteId() == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, p.getGerenteId());
            ps.setInt(7, p.isAtivo() ? 1 : 0);
            ps.setInt(8, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void excluir(int id) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM projeto WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    // Retorna o nome do gerente a partir do ID; lida com null/ausente
public String buscarNomeGerente(Integer gerenteId) {
    if (gerenteId == null) return "";
    String sql = "SELECT nome FROM usuario WHERE id=?";
    try (Connection con = Database.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, gerenteId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString(1);
        }
    } catch (SQLException e) { throw new RuntimeException(e); }
    return "";
}

// Retorna nomes das equipes vinculadas ao projeto, separados por vírgula
public String listarNomesEquipes(int projetoId) {
    StringBuilder sb = new StringBuilder();
    String sql = "SELECT e.nome " +
                 "FROM projeto_equipe pe " +
                 "JOIN equipe e ON e.id = pe.equipe_id " +
                 "WHERE pe.projeto_id = ? " +
                 "ORDER BY e.nome";
    try (Connection con = Database.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, projetoId);
        try (ResultSet rs = ps.executeQuery()) {
            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(", ");
                sb.append(rs.getString(1));
                first = false;
            }
        }
    } catch (SQLException e) { throw new RuntimeException(e); }
    return sb.toString();
}

}
