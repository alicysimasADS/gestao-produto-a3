package br.com.a3.gp.visao;

import br.com.a3.gp.dao.TarefaDAO;
import br.com.a3.gp.modelo.Tarefa;
import br.com.a3.gp.servico.Sessao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TarefaListaJanela extends JFrame {

    private final int projetoId;
    private final TarefaDAO dao = new TarefaDAO();
    private JTable tabela;
    private DefaultTableModel modelo;

    public TarefaListaJanela(int projetoId) {
        super("Tarefas do Projeto " + projetoId);
        this.projetoId = projetoId;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        modelo = new DefaultTableModel(new Object[]{"ID","Titulo","Status","Responsável"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel barra = new JPanel();
        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnEnviarQA = new JButton("Enviar p/ QA");
        JButton btnAprovar = new JButton("Aprovar QA");
        JButton btnReprovar = new JButton("Reprovar QA");
        barra.add(btnNovo);
        barra.add(btnEditar);
        barra.add(btnExcluir);
        barra.add(btnEnviarQA);
        barra.add(btnAprovar);
        barra.add(btnReprovar);
        add(barra, BorderLayout.NORTH);

        btnNovo.addActionListener(e -> {
            new TarefaFormJanela(this, projetoId, null).setVisible(true);
            carregar();
        });
        btnEditar.addActionListener(e -> {
            Tarefa t = selecionada();
            if (t == null) return;
            new TarefaFormJanela(this, projetoId, t).setVisible(true);
            carregar();
        });
        btnExcluir.addActionListener(e -> {
            Tarefa t = selecionada();
            if (t == null) return;
            int op = JOptionPane.showConfirmDialog(this, "Excluir tarefa " + t.getTitulo() + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dao.excluir(t.getId());
                carregar();
            }
        });
        btnEnviarQA.addActionListener(e -> {
            Tarefa t = selecionada();
            if (t == null) return;
            try {
                dao.enviarParaQA(t.getId(), Sessao.getUsuario() != null ? Sessao.getUsuario().getId() : null);
                carregar();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        btnAprovar.addActionListener(e -> {
            Tarefa t = selecionada();
            if (t == null) return;
            try {
                dao.aprovarQA(t.getId(), Sessao.getUsuario() != null ? Sessao.getUsuario().getId() : null);
                carregar();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        btnReprovar.addActionListener(e -> {
            Tarefa t = selecionada();
            if (t == null) return;
            String obs = JOptionPane.showInputDialog(this, "Motivo da reprovação:");
            if (obs == null) return;
            try {
                dao.reprovarQA(t.getId(), obs, Sessao.getUsuario() != null ? Sessao.getUsuario().getId() : null);
                carregar();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        carregar();
        setSize(820, 420);
        setLocationRelativeTo(null);
    }

    private Tarefa selecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa.");
            return null;
        }
        Integer id = (Integer) modelo.getValueAt(row, 0);
        return dao.buscarPorId(id);
    }

    private void carregar() {
        List<Tarefa> dados = dao.listarPorProjeto(projetoId);
        modelo.setRowCount(0);
        for (Tarefa t : dados) {
            String resp = t.getResponsavelId()==null ? "" : String.valueOf(t.getResponsavelId());
            modelo.addRow(new Object[]{ t.getId(), t.getTitulo(), t.getStatus(), resp });
        }
    }
}
