package br.com.a3.gp.visao;

import br.com.a3.gp.dao.EquipeDAO;
import br.com.a3.gp.modelo.Equipe;

import javax.swing.*;
import java.awt.*;

public class EquipeFormJanela extends JDialog {

    private JTextField txtNome = new JTextField(30);
    private JTextArea txtDescricao = new JTextArea(5, 30);
    private final EquipeDAO dao = new EquipeDAO();
    private Equipe equipe;

    public EquipeFormJanela(Window owner, Equipe equipeParam) {
    super(owner, "Equipe", ModalityType.APPLICATION_MODAL);
    this.equipe = equipeParam;
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; form.add(new JLabel("Nome:"), c);
        c.gridx=1; c.gridy=0; form.add(txtNome, c);

        c.gridx=0; c.gridy=1; form.add(new JLabel("Descrição:"), c);
        c.gridx=1; c.gridy=1; c.fill = GridBagConstraints.BOTH; form.add(new JScrollPane(txtDescricao), c);

        JPanel buttons = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttons.add(btnSalvar);
        buttons.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        if (equipe != null) {
            txtNome.setText(equipe.getNome());
            txtDescricao.setText(equipe.getDescricao());
        }

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da equipe.");
            return;
        }
        if (equipe == null) equipe = new Equipe();
        equipe.setNome(nome);
        equipe.setDescricao(txtDescricao.getText());

        if (equipe.getId() == null) dao.inserir(equipe);
        else dao.atualizar(equipe);
        dispose();
    }
}
