package br.com.a3.gp.visao;

import br.com.a3.gp.dao.TarefaDAO;
import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Tarefa;
import br.com.a3.gp.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class TarefaFormJanela extends JDialog {

    private JTextField txtTitulo = new JTextField(30);
    private JTextArea txtDescricao = new JTextArea(5, 30);
    private JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"em_execucao","em_qa","aprovado","reprovado"});
    private JComboBox<String> cmbPrioridade = new JComboBox<>(new String[]{"baixa","media","alta"});
    private JTextField txtPrevisao = new JTextField(16);
    private JComboBox<Usuario> cmbResponsavel = new JComboBox<>();

    private final TarefaDAO dao = new TarefaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Tarefa tarefa;
    private final int projetoId;

    public TarefaFormJanela(Window owner, int projetoId, Tarefa t) {
        super(owner, "Tarefa", ModalityType.APPLICATION_MODAL);
        this.tarefa = t;
        this.projetoId = projetoId;
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        c.gridx=0; c.gridy=y; form.add(new JLabel("Título:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtTitulo, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("Descrição:"), c);
        c.gridx=1; c.gridy=y++; c.fill=GridBagConstraints.BOTH; form.add(new JScrollPane(txtDescricao), c);
        c.gridx=0; c.gridy=y; c.fill=GridBagConstraints.HORIZONTAL; form.add(new JLabel("Status:"), c);
        c.gridx=1; c.gridy=y++; form.add(cmbStatus, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("Prioridade:"), c);
        c.gridx=1; c.gridy=y++; form.add(cmbPrioridade, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("Previsão (YYYY-MM-DD HH:MM):"), c);
        c.gridx=1; c.gridy=y++; form.add(txtPrevisao, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("Responsável:"), c);
        c.gridx=1; c.gridy=y++; form.add(cmbResponsavel, c);

        JPanel buttons = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttons.add(btnSalvar);
        buttons.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        carregarUsuarios();

        if (tarefa != null) {
            txtTitulo.setText(tarefa.getTitulo());
            txtDescricao.setText(tarefa.getDescricao());
            cmbStatus.setSelectedItem(tarefa.getStatus());
            cmbPrioridade.setSelectedItem(tarefa.getPrioridade());
            txtPrevisao.setText(tarefa.getDataPrevisao());
            if (tarefa.getResponsavelId()!=null) {
                for (int i=0;i<cmbResponsavel.getItemCount();i++) {
                    if (cmbResponsavel.getItemAt(i).getId().equals(tarefa.getResponsavelId())) {
                        cmbResponsavel.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }

    private void carregarUsuarios() {
        DefaultComboBoxModel<Usuario> model = new DefaultComboBoxModel<>();
        for (Usuario u : usuarioDAO.listarTodos()) model.addElement(u);
        cmbResponsavel.setModel(model);
    }

    private void salvar() {
        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o título da tarefa.");
            return;
        }
        if (tarefa == null) tarefa = new Tarefa();
        tarefa.setProjetoId(projetoId);
        tarefa.setTitulo(titulo);
        tarefa.setDescricao(txtDescricao.getText());
        tarefa.setStatus((String)cmbStatus.getSelectedItem());
        tarefa.setPrioridade((String)cmbPrioridade.getSelectedItem());
        tarefa.setDataPrevisao(txtPrevisao.getText().trim());
        Usuario resp = (Usuario) cmbResponsavel.getSelectedItem();
        tarefa.setResponsavelId(resp!=null ? resp.getId() : null);

        if (tarefa.getId() == null) dao.inserir(tarefa);
        else dao.atualizar(tarefa);
        dispose();
    }
}
