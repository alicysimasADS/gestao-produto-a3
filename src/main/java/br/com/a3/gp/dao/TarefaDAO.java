package br.com.a3.gp.dao;

import br.com.a3.gp.config.Database;
import br.com.a3.gp.modelo.Tarefa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    public void inserir(Tarefa t) {
        String sql = "INSERT INTO tarefa(projeto_id,responsavel_id,titulo,descricao,status,prioridade,data_previsao) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getProjetoId());
            if (t.getResponsavelId() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, t.getResponsavelId());
            ps.setString(3, t.getTitulo());
            ps.setString(4, t.getDescricao());
            ps.setString(5, t.getStatus());
            ps.setString(6, t.getPrioridade());
            ps.setString(7, t.getDataPrevisao());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir tarefa: " + e.getMessage(), e);
        }
    }

    public void atualizar(Tarefa t) {
        String sql = "UPDATE tarefa SET responsavel_id=?, titulo=?, descricao=?, status=?, prioridade=?, data_previsao=?, data_conclusao=? WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (t.getResponsavelId() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, t.getResponsavelId());
            ps.setString(2, t.getTitulo());
            ps.setString(3, t.getDescricao());
            ps.setString(4, t.getStatus());
            ps.setString(5, t.getPrioridade());
            ps.setString(6, t.getDataPrevisao());
            ps.setString(7, t.getDataConclusao());
            ps.setInt(8, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tarefa: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM tarefa WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir tarefa: " + e.getMessage(), e);
        }
    }

    public List<Tarefa> listarPorProjeto(int projetoId) {
        String sql = "SELECT id, projeto_id, responsavel_id, titulo, descricao, status, prioridade, data_criacao, data_previsao, data_conclusao FROM tarefa WHERE projeto_id=? ORDER BY id DESC";
        List<Tarefa> lista = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, projetoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tarefa t = new Tarefa();
                    t.setId(rs.getInt("id"));
                    t.setProjetoId(rs.getInt("projeto_id"));
                    int resp = rs.getInt("responsavel_id");
                    t.setResponsavelId(rs.wasNull() ? null : resp);
                    t.setTitulo(rs.getString("titulo"));
                    t.setDescricao(rs.getString("descricao"));
                    t.setStatus(rs.getString("status"));
                    t.setPrioridade(rs.getString("prioridade"));
                    t.setDataCriacao(rs.getString("data_criacao"));
                    t.setDataPrevisao(rs.getString("data_previsao"));
                    t.setDataConclusao(rs.getString("data_conclusao"));
                    lista.add(t);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas: " + e.getMessage(), e);
        }
        return lista;
    }

    private void historico(int tarefaId, Integer usuarioId, String acao, String obs) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO historico_tarefa(tarefa_id,usuario_id,acao,observacao) VALUES (?,?,?,?)")) {
            ps.setInt(1, tarefaId);
            if (usuarioId == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, usuarioId);
            ps.setString(3, acao);
            ps.setString(4, obs);
            ps.executeUpdate();
        }
    }

    public void enviarParaQA(int tarefaId, Integer usuarioId) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE tarefa SET status='em_qa' WHERE id=? AND status='em_execucao'")) {
            ps.setInt(1, tarefaId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RuntimeException("Transição inválida. A tarefa precisa estar em_execucao.");
            historico(tarefaId, usuarioId, "enviar_para_qa", null);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao enviar para QA: " + e.getMessage(), e);
        }
    }

    public void aprovarQA(int tarefaId, Integer usuarioId) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE tarefa SET status='aprovado', data_conclusao=CURRENT_TIMESTAMP WHERE id=? AND status='em_qa'")) {
            ps.setInt(1, tarefaId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RuntimeException("Transição inválida. A tarefa precisa estar em_qa.");
            historico(tarefaId, usuarioId, "aprovar_qa", null);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao aprovar QA: " + e.getMessage(), e);
        }
    }

    public void reprovarQA(int tarefaId, String observacao, Integer usuarioId) {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE tarefa SET status='reprovado' WHERE id=? AND status='em_qa'")) {
            ps.setInt(1, tarefaId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RuntimeException("Transição inválida. A tarefa precisa estar em_qa.");
            historico(tarefaId, usuarioId, "reprovar_qa", observacao);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao reprovar QA: " + e.getMessage(), e);
        }
    }

    public Tarefa buscarPorId(int id) {
        String sql = "SELECT id, projeto_id, responsavel_id, titulo, descricao, status, prioridade, data_criacao, data_previsao, data_conclusao FROM tarefa WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tarefa t = new Tarefa();
                    t.setId(rs.getInt("id"));
                    t.setProjetoId(rs.getInt("projeto_id"));
                    int resp = rs.getInt("responsavel_id");
                    t.setResponsavelId(rs.wasNull() ? null : resp);
                    t.setTitulo(rs.getString("titulo"));
                    t.setDescricao(rs.getString("descricao"));
                    t.setStatus(rs.getString("status"));
                    t.setPrioridade(rs.getString("prioridade"));
                    t.setDataCriacao(rs.getString("data_criacao"));
                    t.setDataPrevisao(rs.getString("data_previsao"));
                    t.setDataConclusao(rs.getString("data_conclusao"));
                    return t;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefa: " + e.getMessage(), e);
        }
        return null;
    }
}
