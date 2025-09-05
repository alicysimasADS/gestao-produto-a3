package br.com.a3.gp.visao;

import br.com.a3.gp.dao.EquipeDAO;
import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Equipe;
import br.com.a3.gp.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Janela de criação/edição de Equipe com seleção de membros (usuários).
 */
public class EquipeFormJanela extends JDialog {

    private JTextField txtNome = new JTextField(30);
    private JTextArea txtDescricao = new JTextArea(5, 30);

    /** Lista de usuários (múltipla seleção) para vincular à equipe. */
    private JList<Usuario> lstUsuarios = new JList<>(new DefaultListModel<>());
    private JScrollPane spUsuarios = new JScrollPane(lstUsuarios);

    private final EquipeDAO equipeDAO = new EquipeDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private Equipe equipe;

    public EquipeFormJanela(Window owner, Equipe equipeParam) {
        super(owner, "Equipe", ModalityType.APPLICATION_MODAL);
        this.equipe = equipeParam;

        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;

        // Nome
        c.gridx=0; c.gridy=y; form.add(new JLabel("Nome da Equipe:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtNome, c);

        // Descrição
        c.gridx=0; c.gridy=y; form.add(new JLabel("Descrição:"), c);
        c.gridx=1; c.gridy=y++; c.fill = GridBagConstraints.BOTH; form.add(new JScrollPane(txtDescricao), c);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Membros (Usuários)
        c.gridx=0; c.gridy=y; form.add(new JLabel("Membros (Ctrl para múltiplos):"), c);
        c.gridx=1; c.gridy=y++; 
        spUsuarios.setPreferredSize(new Dimension(300, 140));
        form.add(spUsuarios, c);

        // Barra de botões
        JPanel buttons = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttons.add(btnSalvar);
        buttons.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // Configuração da lista multi-seleção
        lstUsuarios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        carregarUsuariosNaLista();

        // Edição: preenche campos e pré-seleciona membros
        if (equipe != null) {
            txtNome.setText(equipe.getNome());
            txtDescricao.setText(equipe.getDescricao());
            preSelecionarMembros(equipe.getId());
        }

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }

    /** Carrega todos os usuários no JList. */
    private void carregarUsuariosNaLista() {
        DefaultListModel<Usuario> model = (DefaultListModel<Usuario>) lstUsuarios.getModel();
        model.clear();
        for (Usuario u : usuarioDAO.listarTodos()) {
            model.addElement(u); // toString() do Usuario mostra o nome
        }
    }

    /** Pré-seleciona na lista os usuários já vinculados à equipe. */
    private void preSelecionarMembros(int equipeId) {
        List<Integer> membros = equipeDAO.listarUsuarioIdsDaEquipe(equipeId);
        DefaultListModel<Usuario> model = (DefaultListModel<Usuario>) lstUsuarios.getModel();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            Usuario u = model.get(i);
            if (membros.contains(u.getId())) {
                indices.add(i);
            }
        }
        // Converte List<Integer> para int[] e seleciona
        int[] sel = indices.stream().mapToInt(Integer::intValue).toArray();
        lstUsuarios.setSelectedIndices(sel);
    }

    /** Valida, salva equipe e atualiza vínculos na usuario_equipe. */
    private void salvar() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da equipe.");
            return;
        }

        if (equipe == null) equipe = new Equipe();
        equipe.setNome(nome);
        equipe.setDescricao(txtDescricao.getText());

        // Insere ou atualiza equipe
        if (equipe.getId() == null) {
            equipeDAO.inserir(equipe); // agora preenche o ID gerado no objeto
        } else {
            equipeDAO.atualizar(equipe);
        }

        // Coleta IDs dos usuários selecionados
        List<Integer> selecionados = new ArrayList<>();
        for (Usuario u : lstUsuarios.getSelectedValuesList()) {
            selecionados.add(u.getId());
        }

        // Atualiza vínculos na tabela de junção
        equipeDAO.atualizarMembrosEquipe(equipe.getId(), selecionados);

        dispose();
    }
}
