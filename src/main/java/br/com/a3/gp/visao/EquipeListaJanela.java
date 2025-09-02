package br.com.a3.gp.visao;

import br.com.a3.gp.dao.EquipeDAO;
import br.com.a3.gp.modelo.Equipe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EquipeListaJanela extends JFrame {

    private final EquipeDAO dao = new EquipeDAO();
    private JTable tabela;
    private DefaultTableModel modelo;

    public EquipeListaJanela() {
        super("Equipes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        modelo = new DefaultTableModel(new Object[]{"ID","Nome"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel barra = new JPanel();
        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        barra.add(btnNovo);
        barra.add(btnEditar);
        barra.add(btnExcluir);
        add(barra, BorderLayout.NORTH);

        btnNovo.addActionListener(e -> {
            new EquipeFormJanela(this, null).setVisible(true);
            carregar();
        });
        btnEditar.addActionListener(e -> {
            Equipe eq = selecionado();
            if (eq == null) return;
            new EquipeFormJanela(this, eq).setVisible(true);
            carregar();
        });
        btnExcluir.addActionListener(e -> {
            Equipe eq = selecionado();
            if (eq == null) return;
            int op = JOptionPane.showConfirmDialog(this, "Excluir equipe " + eq.getNome() + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dao.excluir(eq.getId());
                carregar();
            }
        });

        carregar();
        setSize(600, 380);
        setLocationRelativeTo(null);
    }

    private Equipe selecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipe.");
            return null;
        }
        Integer id = (Integer) modelo.getValueAt(row, 0);
        return dao.buscarPorId(id);
    }

    private void carregar() {
        List<Equipe> dados = dao.listarTodas();
        modelo.setRowCount(0);
        for (Equipe e : dados) {
            modelo.addRow(new Object[]{ e.getId(), e.getNome() });
        }
    }
}
