package br.com.a3.gp.visao;

import br.com.a3.gp.dao.ProjetoDAO;
import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Projeto;
import br.com.a3.gp.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class ProjetoFormJanela extends JDialog {
    private JTextField txtNome = new JTextField(30);
    private JTextArea txtDescricao = new JTextArea(5, 30);
    private JTextField txtDataInicio = new JTextField(10);
    private JTextField txtDataPrevFim = new JTextField(10);
    private JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"planejado","em_andamento","concluido","cancelado"});
    private JCheckBox chkAtivo = new JCheckBox("Ativo", true);
    private JComboBox<Usuario> cmbGerente = new JComboBox<>();

    private final ProjetoDAO dao = new ProjetoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Projeto projeto;

    public ProjetoFormJanela(Window owner, Projeto p) {
        super(owner, "Projeto", ModalityType.APPLICATION_MODAL);
        this.projeto = p;
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; form.add(new JLabel("Nome:"), c);
        c.gridx=1; c.gridy=0; form.add(txtNome, c);

        c.gridx=0; c.gridy=1; form.add(new JLabel("Descrição:"), c);
        c.gridx=1; c.gridy=1; c.fill = GridBagConstraints.BOTH; form.add(new JScrollPane(txtDescricao), c);

        c.gridx=0; c.gridy=2; c.fill = GridBagConstraints.HORIZONTAL; form.add(new JLabel("Data Início (YYYY-MM-DD):"), c);
        c.gridx=1; c.gridy=2; form.add(txtDataInicio, c);

        c.gridx=0; c.gridy=3; form.add(new JLabel("Data Prev. Fim (YYYY-MM-DD):"), c);
        c.gridx=1; c.gridy=3; form.add(txtDataPrevFim, c);

        c.gridx=0; c.gridy=4; form.add(new JLabel("Status:"), c);
        c.gridx=1; c.gridy=4; form.add(cmbStatus, c);

        c.gridx=0; c.gridy=5; form.add(new JLabel("Gerente:"), c);
        c.gridx=1; c.gridy=5; form.add(cmbGerente, c);

        c.gridx=1; c.gridy=6; form.add(chkAtivo, c);

        JPanel buttons = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttons.add(btnSalvar);
        buttons.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        carregarGerentes();

        if (projeto != null) {
            txtNome.setText(projeto.getNome());
            txtDescricao.setText(projeto.getDescricao());
            txtDataInicio.setText(projeto.getDataInicio());
            txtDataPrevFim.setText(projeto.getDataPrevFim());
            cmbStatus.setSelectedItem(projeto.getStatus());
            chkAtivo.setSelected(projeto.isAtivo());
            if (projeto.getGerenteId()!=null) {
                for (int i=0;i<cmbGerente.getItemCount();i++) {
                    if (cmbGerente.getItemAt(i).getId().equals(projeto.getGerenteId())) {
                        cmbGerente.setSelectedIndex(i);
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

    private void carregarGerentes() {
        DefaultComboBoxModel<Usuario> model = new DefaultComboBoxModel<>();
        for (Usuario u : usuarioDAO.listarTodos()) model.addElement(u);
        cmbGerente.setModel(model);
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do projeto.");
            return;
        }
        if (projeto == null) projeto = new Projeto();
        projeto.setNome(nome);
        projeto.setDescricao(txtDescricao.getText());
        projeto.setDataInicio(txtDataInicio.getText().trim());
        projeto.setDataPrevFim(txtDataPrevFim.getText().trim());
        projeto.setStatus((String)cmbStatus.getSelectedItem());
        Usuario gerente = (Usuario) cmbGerente.getSelectedItem();
        projeto.setGerenteId(gerente != null ? gerente.getId() : null);
        projeto.setAtivo(chkAtivo.isSelected());

        if (projeto.getId() == null) dao.inserir(projeto);
        else dao.atualizar(projeto);
        dispose();
    }
}
