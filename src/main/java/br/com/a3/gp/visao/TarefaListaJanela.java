package br.com.a3.gp.visao;

import br.com.a3.gp.dao.TarefaDAO;
import br.com.a3.gp.modelo.Tarefa;
import br.com.a3.gp.servico.Sessao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela de listagem e ações sobre as tarefas vinculadas a um projeto específico.
 * Permite CRUD de tarefas e transições de status no fluxo QA.
 */
public class TarefaListaJanela extends JFrame {

    private final int projetoId;              // ID do projeto ao qual as tarefas pertencem
    private final TarefaDAO dao = new TarefaDAO(); // DAO para persistência
    private JTable tabela;                    // Tabela que exibe as tarefas
    private DefaultTableModel modelo;         // Modelo da JTable

    public TarefaListaJanela(int projetoId) {
        super("Tarefas do Projeto " + projetoId);
        this.projetoId = projetoId;

        // Configurações da janela
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // Modelo da tabela com colunas fixas
        modelo = new DefaultTableModel(new Object[]{"ID","Titulo","Status","Responsável"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; } // desabilita edição direta
        };

        // Inicializa tabela
        tabela = new JTable(modelo);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Barra de botões (ações)
        JPanel barra = new JPanel();
        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnEnviarQA = new JButton("Enviar p/ QA");
        JButton btnAprovar = new JButton("Aprovar QA");
        JButton btnReprovar = new JButton("Reprovar QA");

        // Adiciona botões na barra superior
        barra.add(btnNovo);
        barra.add(btnEditar);
        barra.add(btnExcluir);
        barra.add(btnEnviarQA);
        barra.add(btnAprovar);
        barra.add(btnReprovar);
        add(barra, BorderLayout.NORTH);

        // -------------------------------
        // Ações dos botões
        // -------------------------------

        // Botão "Novo": abre o formulário de nova tarefa
        btnNovo.addActionListener(e -> {
            new TarefaFormJanela(this, projetoId, null).setVisible(true);
            carregar(); // recarrega tabela
        });

        // Botão "Editar": abre o formulário com a tarefa selecionada
        btnEditar.addActionListener(e -> {
            Tarefa t = selecionada();
            if (t == null) return;
            new TarefaFormJanela(this, projetoId, t).setVisible(true);
            carregar();
        });

        // Botão "Excluir": confirma e remove a tarefa selecionada
        btnExcluir.addActionListener(e -> {
            Tarefa t = selecionada();
            if (t == null) return;
            int op = JOptionPane.showConfirmDialog(this, "Excluir tarefa " + t.getTitulo() + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dao.excluir(t.getId());
                carregar();
            }
        });

        // Botão "Enviar p/ QA": muda status da tarefa de em_execucao → em_qa
        btnEnviarQA.addActionListener(e -> {
            Tarefa t = selecionada();
            if (t == null) return;
            try {
                dao.enviarParaQA(t.getId(), Sessao.getUsuario() != null ? Sessao.getUsuario().getId() : null);
                carregar();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage()); // exibe erro se status inválido
            }
        });

        // Botão "Aprovar QA": muda status de em_qa → aprovado
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

        // Botão "Reprovar QA": muda status de em_qa → reprovado com observação
        btnReprovar.addActionListener(e -> {
            Tarefa t = selecionada();
            if (t == null) return;
            String obs = JOptionPane.showInputDialog(this, "Motivo da reprovação:");
            if (obs == null) return; // usuário cancelou
            try {
                dao.reprovarQA(t.getId(), obs, Sessao.getUsuario() != null ? Sessao.getUsuario().getId() : null);
                carregar();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        // Carrega dados na tabela ao abrir
        carregar();

        // Configurações finais da janela
        setSize(820, 420);
        setLocationRelativeTo(null); // centraliza na tela
    }

    /**
     * Retorna a tarefa selecionada na tabela.
     * Se nada estiver selecionado, exibe alerta.
     */
    private Tarefa selecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa.");
            return null;
        }
        Integer id = (Integer) modelo.getValueAt(row, 0); // pega o ID da linha selecionada
        return dao.buscarPorId(id);
    }

    /**
     * Carrega todas as tarefas do projeto na tabela.
     */
    private void carregar() {
        List<Tarefa> dados = dao.listarPorProjeto(projetoId);
        modelo.setRowCount(0); // limpa a tabela
        for (Tarefa t : dados) {
            String resp = t.getResponsavelId() == null ? "" : String.valueOf(t.getResponsavelId());
            modelo.addRow(new Object[]{ t.getId(), t.getTitulo(), t.getStatus(), resp });
        }
    }
}
