package br.com.a3.gp.visao;

import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Usuario;
import br.com.a3.gp.util.SenhaUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class UsuarioFormJanela extends JDialog {

    private JTextField txtNome = new JTextField(30);
    private JTextField txtLogin = new JTextField(20);
    private JPasswordField txtSenha = new JPasswordField(20);
    private JTextField txtEmail = new JTextField(25);
    private JTextField txtCpf = new JTextField(14);
    private JTextField txtCargo = new JTextField(20);
    private JTextField txtPerfil = new JTextField(20);
    private JCheckBox chkAtivo = new JCheckBox("Ativo", true);

    private JList<String> listaPapeis;
    private final DefaultListModel<String> modeloPapeis = new DefaultListModel<>();
    private final List<String> papeisDisponiveis = Arrays.asList("ADMIN","GERENTE_PROJETO","DESENVOLVEDOR","TESTADOR","QA");

    private final UsuarioDAO dao = new UsuarioDAO();
    private Usuario usuario; // nulo para novo

    public UsuarioFormJanela(Window owner, Usuario u) {
        super(owner, "Usuário", ModalityType.APPLICATION_MODAL);
        this.usuario = u;
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y=0;
        c.gridx=0; c.gridy=y; form.add(new JLabel("Nome:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtNome, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("Login:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtLogin, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("Senha:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtSenha, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("Email:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtEmail, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("CPF:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtCpf, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("Cargo:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtCargo, c);
        c.gridx=0; c.gridy=y; form.add(new JLabel("Perfil:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtPerfil, c);
        c.gridx=1; c.gridy=y++; form.add(chkAtivo, c);

        for (String p : papeisDisponiveis) modeloPapeis.addElement(p);
        listaPapeis = new JList<>(modeloPapeis);
        listaPapeis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        c.gridx=0; c.gridy=y; form.add(new JLabel("Papéis:"), c);
        c.gridx=1; c.gridy=y++; c.fill = GridBagConstraints.BOTH; c.weightx=1; c.weighty=1; form.add(new JScrollPane(listaPapeis), c);

        JPanel buttons = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttons.add(btnSalvar);
        buttons.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        if (usuario != null) {
            txtNome.setText(usuario.getNome());
            txtLogin.setText(usuario.getLogin());
            txtEmail.setText(usuario.getEmail());
            txtCpf.setText(usuario.getCpf());
            txtCargo.setText(usuario.getCargo());
            txtPerfil.setText(usuario.getPerfil());
            chkAtivo.setSelected(usuario.isAtivo());
            List<String> atuais = dao.listarPapeisDoUsuario(usuario.getId());
            int[] idx = atuais.stream().mapToInt(papeisDisponiveis::indexOf).filter(i -> i >= 0).toArray();
            listaPapeis.setSelectedIndices(idx);
        }

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        setSize(520, 520);
        setLocationRelativeTo(owner);
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();

        if (nome.isEmpty() || login.isEmpty() || (usuario == null && senha.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Preencha nome, login e senha para novo usuário.");
            return;
        }

        boolean novo = usuario == null;
        if (novo) usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setLogin(login);
        if (!senha.isEmpty()) usuario.setSenhaHash(SenhaUtil.hash(senha));
        usuario.setEmail(txtEmail.getText().trim());
        usuario.setCpf(txtCpf.getText().trim());
        usuario.setCargo(txtCargo.getText().trim());
        usuario.setPerfil(txtPerfil.getText().trim());
        usuario.setAtivo(chkAtivo.isSelected());

        if (novo) dao.inserir(usuario);
        else dao.atualizar(usuario);

        if (usuario.getId() == null) {
            Usuario u = new UsuarioDAO().buscarPorLogin(login);
            if (u != null) usuario.setId(u.getId());
        }
        List<String> selecionados = listaPapeis.getSelectedValuesList();
        for (String p : Arrays.asList("ADMIN","GERENTE_PROJETO","DESENVOLVEDOR","TESTADOR","QA")) {
            dao.removerPapel(usuario.getId(), p);
        }
        for (String p : selecionados) dao.atribuirPapel(usuario.getId(), p);

        dispose();
    }
}
