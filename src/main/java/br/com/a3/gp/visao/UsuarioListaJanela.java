package br.com.a3.gp.visao;

import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioListaJanela extends JFrame {

    private final UsuarioDAO dao = new UsuarioDAO();
    private JTable tabela;
    private DefaultTableModel modelo;

    public UsuarioListaJanela() {
        super("Usuários");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        modelo = new DefaultTableModel(new Object[]{"ID","Nome","Login","Ativo"}, 0) {
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
            new UsuarioFormJanela(this, null).setVisible(true);
            carregar();
        });

        btnEditar.addActionListener(e -> {
            Usuario u = selecionado();
            if (u == null) return;
            new UsuarioFormJanela(this, u).setVisible(true);
            carregar();
        });

        btnExcluir.addActionListener(e -> {
            Usuario u = selecionado();
            if (u == null) return;
            int op = JOptionPane.showConfirmDialog(this, "Excluir usuário " + u.getNome() + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dao.excluir(u.getId());
                carregar();
            }
        });

        carregar();
        setSize(700, 400);
        setLocationRelativeTo(null);
    }

    private Usuario selecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário.");
            return null;
        }
        Integer id = (Integer) modelo.getValueAt(row, 0);
        return dao.buscarPorId(id);
    }

    private void carregar() {
        List<Usuario> dados = dao.listarTodos();
        modelo.setRowCount(0);
        for (Usuario u : dados) {
            modelo.addRow(new Object[]{ u.getId(), u.getNome(), u.getLogin(), u.isAtivo() });
        }
    }
}
