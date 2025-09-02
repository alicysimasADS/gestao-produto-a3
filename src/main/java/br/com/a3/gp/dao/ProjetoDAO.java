package br.com.a3.gp.dao;

import br.com.a3.gp.config.Database;
import br.com.a3.gp.modelo.Projeto;
import java.sql.*;
import java.util.*;

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
        p.setAtivo(rs.getInt("ativo")==1);
        return p;
    }

    public List<Projeto> listarTodos() {
        List<Projeto> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,nome,descricao,data_inicio,data_prev_fim,status,gerente_id,ativo FROM projeto ORDER BY id DESC")) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }

    public Projeto buscarPorId(int id) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id,nome,descricao,data_inicio,data_prev_fim,status,gerente_id,ativo FROM projeto WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public void inserir(Projeto p) {
        String sql = "INSERT INTO projeto(nome,descricao,data_inicio,data_prev_fim,status,gerente_id,ativo) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setString(3, p.getDataInicio());
            ps.setString(4, p.getDataPrevFim());
            ps.setString(5, p.getStatus());
            if (p.getGerenteId()==null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, p.getGerenteId());
            ps.setInt(7, p.isAtivo()?1:0);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void atualizar(Projeto p) {
        String sql = "UPDATE projeto SET nome=?, descricao=?, data_inicio=?, data_prev_fim=?, status=?, gerente_id=?, ativo=? WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setString(3, p.getDataInicio());
            ps.setString(4, p.getDataPrevFim());
            ps.setString(5, p.getStatus());
            if (p.getGerenteId()==null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, p.getGerenteId());
            ps.setInt(7, p.isAtivo()?1:0);
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
}
