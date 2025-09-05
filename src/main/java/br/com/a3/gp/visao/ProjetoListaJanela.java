package br.com.a3.gp.visao;

import br.com.a3.gp.dao.ProjetoDAO;
import br.com.a3.gp.modelo.Projeto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProjetoListaJanela extends JFrame {

    private final ProjetoDAO dao = new ProjetoDAO();
    private JTable tabela;
    private DefaultTableModel modelo;

    public ProjetoListaJanela() {
        super("Projetos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // Agora com colunas Gerente e Equipes
        modelo = new DefaultTableModel(new Object[]{"ID","Nome","Status","Ativo","Gerente","Equipes"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel barra = new JPanel();
        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnTarefas = new JButton("Tarefas");
        barra.add(btnNovo);
        barra.add(btnEditar);
        barra.add(btnExcluir);
        barra.add(btnTarefas);
        add(barra, BorderLayout.NORTH);

        btnNovo.addActionListener(e -> {
            new ProjetoFormJanela(this, null).setVisible(true);
            carregar();
        });

        btnEditar.addActionListener(e -> {
            Projeto p = selecionado();
            if (p == null) return;
            new ProjetoFormJanela(this, p).setVisible(true);
            carregar();
        });

        btnExcluir.addActionListener(e -> {
            Projeto p = selecionado();
            if (p == null) return;
            int op = JOptionPane.showConfirmDialog(this, "Excluir projeto " + p.getNome() + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dao.excluir(p.getId());
                carregar();
            }
        });

        btnTarefas.addActionListener(e -> {
            Projeto p = selecionado();
            if (p == null) return;
            new TarefaListaJanela(p.getId()).setVisible(true);
        });

        carregar();
        setSize(1000, 480);
        setLocationRelativeTo(null);
    }

    private Projeto selecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto.");
            return null;
        }
        Integer id = (Integer) modelo.getValueAt(row, 0);
        return dao.buscarPorId(id);
    }

    private void carregar() {
        List<Projeto> dados = dao.listarTodos();
        modelo.setRowCount(0);
        for (Projeto p : dados) {
            String gerente = dao.buscarNomeGerente(p.getGerenteId());    // novo helper
            String equipes = dao.listarNomesEquipes(p.getId());          // novo helper
            modelo.addRow(new Object[]{
                p.getId(), p.getNome(), p.getStatus(), p.isAtivo(), gerente, equipes
            });
        }
    }
}
