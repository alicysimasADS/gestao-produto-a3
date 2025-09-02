package br.com.a3.gp.visao;

import br.com.a3.gp.servico.LoginServico;
import br.com.a3.gp.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginJanela extends JFrame {

    private final JTextField txtLogin = new JTextField(20);
    private final JPasswordField txtSenha = new JPasswordField(20);
    private final LoginServico loginServico = new LoginServico();

    public LoginJanela() {
        super("Login - Gestão de Produto A3");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; form.add(new JLabel("Login:"), c);
        c.gridx=1; c.gridy=0; form.add(txtLogin, c);
        c.gridx=0; c.gridy=1; form.add(new JLabel("Senha:"), c);
        c.gridx=1; c.gridy=1; form.add(txtSenha, c);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(e -> entrar());

        add(form, BorderLayout.CENTER);
        add(btnEntrar, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void entrar() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());
        Usuario u = loginServico.autenticar(login, senha);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Login ou senha inválidos.");
            return;
        }
        dispose();
        new PrincipalJanela().setVisible(true);
    }
}
