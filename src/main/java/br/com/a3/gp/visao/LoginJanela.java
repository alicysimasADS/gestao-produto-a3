package br.com.a3.gp.visao;

import br.com.a3.gp.servico.LoginServico;
import br.com.a3.gp.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Janela de login principal do sistema.
 * Permite que o usuário se autentique informando login e senha.
 * Em caso de sucesso, a aplicação avança para a tela principal.
 */
public class LoginJanela extends JFrame {

    // Campos de entrada: login e senha
    private final JTextField txtLogin = new JTextField(20);
    private final JPasswordField txtSenha = new JPasswordField(20);

    // Serviço responsável pela autenticação
    private final LoginServico loginServico = new LoginServico();

    /**
     * Construtor da tela de login.
     * Define layout, campos e botão de ação.
     */
    public LoginJanela() {
        super("Login - Gestão de Produto A3");

        // Fecha a aplicação ao encerrar esta janela
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Layout principal com margem
        setLayout(new BorderLayout(10, 10));

        // -------------------------------
        // Painel do formulário de login
        // -------------------------------
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6); // espaçamento entre campos
        c.fill = GridBagConstraints.HORIZONTAL;

        // Campo: Login
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Login:"), c);

        c.gridx = 1;
        form.add(txtLogin, c);

        // Campo: Senha
        c.gridx = 0; c.gridy = 1;
        form.add(new JLabel("Senha:"), c);

        c.gridx = 1;
        form.add(txtSenha, c);

        // -------------------------------
        // Botão "Entrar"
        // -------------------------------
        JButton btnEntrar = new JButton("Entrar");

        // Ação do botão: tenta autenticar
        btnEntrar.addActionListener(e -> entrar());

        // -------------------------------
        // Adiciona componentes à janela
        // -------------------------------
        add(form, BorderLayout.CENTER);
        add(btnEntrar, BorderLayout.SOUTH);

        // Ajusta tamanho automático e centraliza na tela
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Realiza a autenticação do usuário.
     * Se bem-sucedida, abre a tela principal e fecha a de login.
     * Caso contrário, exibe uma mensagem de erro.
     */
    private void entrar() {
        // Captura os valores digitados
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        // Tenta autenticar com o serviço
        Usuario u = loginServico.autenticar(login, senha);

        if (u == null) {
            JOptionPane.showMessageDialog(this, "Login ou senha inválidos.");
            return;
        }

        // Autenticação bem-sucedida: fecha login e abre principal
        dispose();
        new PrincipalJanela().setVisible(true);
    }
}
