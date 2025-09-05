package br.com.a3.gp.visao;

import br.com.a3.gp.dao.TarefaDAO;
import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Tarefa;
import br.com.a3.gp.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Janela modal (JDialog) para criação/edição de Tarefas.
 * Permite definir título, descrição, status, prioridade, previsão e responsável.
 * Persiste os dados via TarefaDAO.
 */
public class TarefaFormJanela extends JDialog {

    // ----------------------------
    // Componentes do formulário
    // ----------------------------
    private JTextField txtTitulo = new JTextField(30);
    private JTextArea txtDescricao = new JTextArea(5, 30);
    private JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"em_execucao","em_qa","aprovado","reprovado"});
    private JComboBox<String> cmbPrioridade = new JComboBox<>(new String[]{"baixa","media","alta"});
    private JTextField txtPrevisao = new JTextField(16); // padrão: YYYY-MM-DD HH:MM
    private JComboBox<Usuario> cmbResponsavel = new JComboBox<>();

    // ----------------------------
    // Acesso a dados
    // ----------------------------
    private final TarefaDAO dao = new TarefaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Tarefa em edição (null para criar nova)
    private Tarefa tarefa;

    // ID do projeto ao qual a tarefa pertence (obrigatório)
    private final int projetoId;

    /**
     * Construtor da janela de Tarefa.
     * @param owner janela pai para modal/centralização
     * @param projetoId projeto ao qual a tarefa será vinculada
     * @param t tarefa a editar (ou null para criar nova)
     */
    public TarefaFormJanela(Window owner, int projetoId, Tarefa t) {
        super(owner, "Tarefa", ModalityType.APPLICATION_MODAL);
        this.tarefa = t;
        this.projetoId = projetoId;

        // Layout principal
        setLayout(new BorderLayout(10,10));

        // ----------------------------
        // Montagem do formulário
        // ----------------------------
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // Título
        c.gridx=0; c.gridy=y; form.add(new JLabel("Título:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtTitulo, c);

        // Descrição
        c.gridx=0; c.gridy=y; form.add(new JLabel("Descrição:"), c);
        c.gridx=1; c.gridy=y++; c.fill=GridBagConstraints.BOTH; form.add(new JScrollPane(txtDescricao), c);
        c.fill=GridBagConstraints.HORIZONTAL;

        // Status
        c.gridx=0; c.gridy=y; form.add(new JLabel("Status:"), c);
        c.gridx=1; c.gridy=y++; form.add(cmbStatus, c);

        // Prioridade
        c.gridx=0; c.gridy=y; form.add(new JLabel("Prioridade:"), c);
        c.gridx=1; c.gridy=y++; form.add(cmbPrioridade, c);

        // Previsão
        c.gridx=0; c.gridy=y; form.add(new JLabel("Previsão (YYYY-MM-DD HH:MM):"), c);
        c.gridx=1; c.gridy=y++; form.add(txtPrevisao, c);

        // Responsável
        c.gridx=0; c.gridy=y; form.add(new JLabel("Responsável:"), c);
        c.gridx=1; c.gridy=y++; form.add(cmbResponsavel, c);

        // ----------------------------
        // Barra de botões
        // ----------------------------
        JPanel buttons = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttons.add(btnSalvar);
        buttons.add(btnCancelar);

        // Adiciona ao dialog
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // Carrega usuários para o combo de responsável
        carregarUsuarios();

        // Se for edição, pré-carrega os campos
        if (tarefa != null) {
            txtTitulo.setText(tarefa.getTitulo());
            txtDescricao.setText(tarefa.getDescricao());
            cmbStatus.setSelectedItem(tarefa.getStatus());
            cmbPrioridade.setSelectedItem(tarefa.getPrioridade());
            txtPrevisao.setText(tarefa.getDataPrevisao());

            if (tarefa.getResponsavelId()!=null) {
                for (int i=0; i<cmbResponsavel.getItemCount(); i++) {
                    if (cmbResponsavel.getItemAt(i).getId().equals(tarefa.getResponsavelId())) {
                        cmbResponsavel.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        // Ações dos botões
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        // Ajustes finais
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Preenche a ComboBox de responsáveis com todos os usuários cadastrados.
     */
    private void carregarUsuarios() {
        DefaultComboBoxModel<Usuario> model = new DefaultComboBoxModel<>();
        for (Usuario u : usuarioDAO.listarTodos()) model.addElement(u);
        cmbResponsavel.setModel(model);
    }

    /**
     * Valida os campos, preenche o modelo Tarefa e persiste (insert/update).
     * Fecha a janela ao final da operação.
     */
    private void salvar() {
        String titulo = txtTitulo.getText().trim();

        // Validação mínima: título é obrigatório
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o título da tarefa.");
            return;
        }

        // Cria o modelo se for novo
        if (tarefa == null) tarefa = new Tarefa();

        // Copia dados da UI para o modelo
        tarefa.setProjetoId(projetoId);
        tarefa.setTitulo(titulo);
        tarefa.setDescricao(txtDescricao.getText());
        tarefa.setStatus((String)cmbStatus.getSelectedItem());
        tarefa.setPrioridade((String)cmbPrioridade.getSelectedItem());
        tarefa.setDataPrevisao(txtPrevisao.getText().trim());

        // Responsável é opcional
        Usuario resp = (Usuario) cmbResponsavel.getSelectedItem();
        tarefa.setResponsavelId(resp!=null ? resp.getId() : null);

        // Decide entre inserir ou atualizar
        if (tarefa.getId() == null) dao.inserir(tarefa);
        else dao.atualizar(tarefa);

        // Fecha a janela após salvar
        dispose();
    }
}
