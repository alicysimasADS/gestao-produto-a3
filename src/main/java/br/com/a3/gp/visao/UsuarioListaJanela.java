package br.com.a3.gp.visao;

import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela de listagem de usuários com ações de CRUD.
 * Mostra colunas essenciais e delega criação/edição ao formulário UsuarioFormJanela.
 */
public class UsuarioListaJanela extends JFrame {

    // DAO responsável por ler/persistir usuários
    private final UsuarioDAO dao = new UsuarioDAO();

    // Componentes visuais principais
    private JTable tabela;                 // tabela que exibe os usuários
    private DefaultTableModel modelo;      // modelo da tabela (dados + definição de colunas)

    public UsuarioListaJanela() {
        super("Usuários");

        // Fecha apenas esta janela quando o usuário clicar no X (não encerra a aplicação)
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Layout base com espaçamento
        setLayout(new BorderLayout(10,10));

        // Define colunas e bloqueia edição direta na tabela
        modelo = new DefaultTableModel(new Object[]{"ID","Nome","Login","Ativo"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // Cria a tabela e a coloca dentro de um scroll
        tabela = new JTable(modelo);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Barra superior com as ações
        JPanel barra = new JPanel();
        JButton btnNovo    = new JButton("Novo");
        JButton btnEditar  = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        barra.add(btnNovo);
        barra.add(btnEditar);
        barra.add(btnExcluir);
        add(barra, BorderLayout.NORTH);

        // ---------- Ações dos botões ----------

        // Novo: abre o formulário em modo criação (usuario == null)
        btnNovo.addActionListener(e -> {
            new UsuarioFormJanela(this, null).setVisible(true);
            carregar(); // recarrega a lista após possível inserção
        });

        // Editar: obtém a linha selecionada, carrega o usuário e abre formulário de edição
        btnEditar.addActionListener(e -> {
            Usuario u = selecionado();
            if (u == null) return; // Nenhuma linha selecionada
            new UsuarioFormJanela(this, u).setVisible(true);
            carregar(); // recarrega a lista após possível atualização
        });

        // Excluir: confirma, remove no DAO e recarrega a lista
        btnExcluir.addActionListener(e -> {
            Usuario u = selecionado();
            if (u == null) return;
            int op = JOptionPane.showConfirmDialog(
                    this,
                    "Excluir usuário " + u.getNome() + "?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (op == JOptionPane.YES_OPTION) {
                dao.excluir(u.getId());
                carregar();
            }
        });

        // Carrega dados ao abrir
        carregar();

        // Tamanho e centralização da janela
        setSize(700, 400);
        setLocationRelativeTo(null);
    }

    /**
     * Retorna o usuário correspondente à linha selecionada na tabela.
     * Se nada estiver selecionado, exibe aviso e retorna null.
     */
    private Usuario selecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário.");
            return null;
        }
        // A primeira coluna do modelo é o ID (Integer)
        Integer id = (Integer) modelo.getValueAt(row, 0);
        // Busca o objeto completo no DAO (evita trabalhar com dados parciais da linha)
        return dao.buscarPorId(id);
    }

    /**
     * Carrega todos os usuários do DAO e preenche a tabela.
     * Limpa as linhas existentes e adiciona a grade atualizada.
     */
    private void carregar() {
        List<Usuario> dados = dao.listarTodos();
        modelo.setRowCount(0); // limpa a tabela
        for (Usuario u : dados) {
            modelo.addRow(new Object[]{
                    u.getId(),
                    u.getNome(),
                    u.getLogin(),
                    u.isAtivo()
            });
        }
    }
}
