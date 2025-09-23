package br.com.a3.gp.visao;

import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Usuario;
import br.com.a3.gp.util.SenhaUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Formulário modal para criação/edição de Usuário.
 * - Campos básicos (nome, login, email, cpf, cargo, perfil, ativo)
 * - Definição/atualização de senha (hash com BCrypt)
 * - Atribuição de papéis (múltipla seleção) persistidos via tabela usuario_papel
 *
 * Observações:
 * - Para novo usuário a senha é obrigatória; para edição, é opcional (só atualiza se preenchida).
 * - Ao salvar papéis: remove todos os possíveis e reatribui o que estiver selecionado (idempotente).
 */
public class UsuarioFormJanela extends JDialog {

    // Campos de entrada de dados
    private JTextField txtNome   = new JTextField(30);
    private JTextField txtLogin  = new JTextField(20);
    private JPasswordField txtSenha = new JPasswordField(20); // nunca armazenar senha em texto plano
    private JTextField txtEmail  = new JTextField(25);
    private JTextField txtCpf    = new JTextField(14);
    private JTextField txtCargo  = new JTextField(20);
    private JTextField txtPerfil = new JTextField(20);
    private JCheckBox chkAtivo   = new JCheckBox("Ativo", true);

    // Lista de papéis (multi-seleção)
    private JList<String> listaPapeis;
    private final DefaultListModel<String> modeloPapeis = new DefaultListModel<>();
    // Papéis disponíveis no sistema (deve bater com a tabela 'papel')
    private final List<String> papeisDisponiveis =
            Arrays.asList("ADMIN","GERENTE_PROJETO","DESENVOLVEDOR","CSM","QA");

    // Persistência
    private final UsuarioDAO dao = new UsuarioDAO();

    // Modelo em edição; null indica criação de novo registro
    private Usuario usuario;

    /**
     * @param owner Janela pai (para modal e centralização)
     * @param u     Usuário a editar ou null para criação
     */
    public UsuarioFormJanela(Window owner, Usuario u) {
        super(owner, "Usuário", ModalityType.APPLICATION_MODAL);
        this.usuario = u;

        setLayout(new BorderLayout(10,10));

        // ---------- Layout do formulário ----------
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y=0;
        // Nome
        c.gridx=0; c.gridy=y; form.add(new JLabel("Nome:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtNome, c);

        // Login
        c.gridx=0; c.gridy=y; form.add(new JLabel("Login:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtLogin, c);

        // Senha (obrigatória apenas para novo usuário)
        c.gridx=0; c.gridy=y; form.add(new JLabel("Senha:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtSenha, c);

        // Email
        c.gridx=0; c.gridy=y; form.add(new JLabel("Email:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtEmail, c);

        // CPF
        c.gridx=0; c.gridy=y; form.add(new JLabel("CPF:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtCpf, c);

        // Cargo (papel organizacional/descrição do posto)
        c.gridx=0; c.gridy=y; form.add(new JLabel("Cargo:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtCargo, c);

        // Perfil (campo livre para classificação interna; não confundir com "papel")
        c.gridx=0; c.gridy=y; form.add(new JLabel("Perfil:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtPerfil, c);

        // Ativo
        c.gridx=1; c.gridy=y++; form.add(chkAtivo, c);

        // Monta o modelo da lista de papéis
        for (String p : papeisDisponiveis) modeloPapeis.addElement(p);
        listaPapeis = new JList<>(modeloPapeis);
        listaPapeis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Papéis (multi-seleção)
        c.gridx=0; c.gridy=y; form.add(new JLabel("Papéis:"), c);
        c.gridx=1; c.gridy=y++; c.fill = GridBagConstraints.BOTH; c.weightx=1; c.weighty=1;
        form.add(new JScrollPane(listaPapeis), c);

        // ---------- Barra de botões ----------
        JPanel buttons = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttons.add(btnSalvar);
        buttons.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // --------- Edição: pré-carrega dados e papéis ----------
        if (usuario != null) {
            // Preenche campos com dados existentes
            txtNome.setText(usuario.getNome());
            txtLogin.setText(usuario.getLogin());
            txtEmail.setText(usuario.getEmail());
            txtCpf.setText(usuario.getCpf());
            txtCargo.setText(usuario.getCargo());
            txtPerfil.setText(usuario.getPerfil());
            chkAtivo.setSelected(usuario.isAtivo());

            // Marca na JList os papéis atuais do usuário
            List<String> atuais = dao.listarPapeisDoUsuario(usuario.getId());
            // Converte cada papel para o índice correspondente na lista de disponíveis
            int[] idx = atuais.stream()
                    .mapToInt(papeisDisponiveis::indexOf)
                    .filter(i -> i >= 0)
                    .toArray();
            listaPapeis.setSelectedIndices(idx);
        }

        // ---------- Ações ----------
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        // Tamanho + centralização
        setSize(520, 520);
        setLocationRelativeTo(owner);
    }

    /**
     * Validação, montagem do objeto Usuario e persistência.
     * Regras principais:
     * - Nome, login e (se for novo) senha são obrigatórios.
     * - Senha só é atualizada se campo preenchido.
     * - Papéis: remove todos os possíveis e reatribui os selecionados (simplifica sync).
     */
    private void salvar() {
        String nome  = txtNome.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();

        // Validação mínima
        if (nome.isEmpty() || login.isEmpty() || (usuario == null && senha.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Preencha nome, login e senha para novo usuário.");
            return;
        }

        boolean novo = (usuario == null);
        if (novo) usuario = new Usuario();

        // Copia campos para o modelo
        usuario.setNome(nome);
        usuario.setLogin(login);

        // Apenas define/atualiza hash de senha se usuário for novo ou se o campo estiver preenchido
        if (!senha.isEmpty()) {
            // BCrypt com salt, via SenhaUtil
            usuario.setSenhaHash(SenhaUtil.hash(senha));
        }

        usuario.setEmail(txtEmail.getText().trim());
        usuario.setCpf(txtCpf.getText().trim());
        usuario.setCargo(txtCargo.getText().trim());
        usuario.setPerfil(txtPerfil.getText().trim());
        usuario.setAtivo(chkAtivo.isSelected());

        // Persistência: insert ou update
        if (novo) dao.inserir(usuario);
        else dao.atualizar(usuario);

        // Garante que temos o ID (em alguns fluxos de insert ele pode não estar preenchido no objeto)
        if (usuario.getId() == null) {
            Usuario u = new UsuarioDAO().buscarPorLogin(login);
            if (u != null) usuario.setId(u.getId());
        }

        // Sincroniza papéis:
        // Estratégia simples e robusta: remove todos os papéis conhecidos e reatribui os selecionados.
        List<String> selecionados = listaPapeis.getSelectedValuesList();

        // Remove papéis conhecidos (evita duplicidade e facilita troca)
        for (String p : Arrays.asList("ADMIN","GERENTE_PROJETO","DESENVOLVEDOR","CSM","QA")) {
            dao.removerPapel(usuario.getId(), p);
        }
        // Atribui apenas os escolhidos
        for (String p : selecionados) {
            dao.atribuirPapel(usuario.getId(), p);
        }

        // Fecha o diálogo após salvar
        dispose();
    }
}
