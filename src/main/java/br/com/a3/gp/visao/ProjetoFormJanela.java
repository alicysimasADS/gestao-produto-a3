package br.com.a3.gp.visao;

import br.com.a3.gp.dao.ProjetoDAO;
import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.dao.EquipeDAO;
import br.com.a3.gp.modelo.Projeto;
import br.com.a3.gp.modelo.Usuario;
import br.com.a3.gp.modelo.Equipe;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Janela modal (JDialog) para criação/edição de Projetos.
 * Regras:
 * - Apenas usuários com papel GERENTE_PROJETO podem ser gerente.
 * - É possível selecionar várias equipes para o projeto (relacionamento M:N).
 */
public class ProjetoFormJanela extends JDialog {

    // ----------------------------
    // Componentes do formulário
    // ----------------------------
    private final JTextField txtNome = new JTextField(30);
    private final JTextArea  txtDescricao = new JTextArea(5, 30);
    private final JTextField txtDataInicio = new JTextField(10);
    private final JTextField txtDataPrevFim = new JTextField(10);
    private final JComboBox<String> cmbStatus =
            new JComboBox<>(new String[]{"planejado","em_andamento","concluido","cancelado"});
    private final JCheckBox chkAtivo = new JCheckBox("Ativo", true);

    // Gerente (filtrado por papel GERENTE_PROJETO)
    private final JComboBox<Usuario> cmbGerente = new JComboBox<>();

    // Equipes (multi-seleção)
    private final JList<Equipe> lstEquipes = new JList<>(new DefaultListModel<>());
    private final JScrollPane spEquipes = new JScrollPane(lstEquipes);

    // ----------------------------
    // DAOs
    // ----------------------------
    private final ProjetoDAO projetoDAO = new ProjetoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EquipeDAO  equipeDAO  = new EquipeDAO();

    // Projeto em edição (null para criação)
    private Projeto projeto;

    /**
     * @param owner janela pai (para modal e centralização)
     * @param p projeto a editar (ou null para novo)
     */
    public ProjetoFormJanela(Window owner, Projeto p) {
        super(owner, "Projeto", ModalityType.APPLICATION_MODAL);
        this.projeto = p;

        setLayout(new BorderLayout(10,10));

        // ============================
        // Montagem do formulário
        // ============================
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // Nome
        c.gridx=0; c.gridy=y; form.add(new JLabel("Nome:"), c);
        c.gridx=1; c.gridy=y++; form.add(txtNome, c);

        // Descrição
        c.gridx=0; c.gridy=y; form.add(new JLabel("Descrição:"), c);
        c.gridx=1; c.gridy=y++; c.fill = GridBagConstraints.BOTH; form.add(new JScrollPane(txtDescricao), c);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Datas
        c.gridx=0; c.gridy=y; form.add(new JLabel("Data Início (YYYY-MM-DD):"), c);
        c.gridx=1; c.gridy=y++; form.add(txtDataInicio, c);

        c.gridx=0; c.gridy=y; form.add(new JLabel("Data Prev. Fim (YYYY-MM-DD):"), c);
        c.gridx=1; c.gridy=y++; form.add(txtDataPrevFim, c);

        // Status
        c.gridx=0; c.gridy=y; form.add(new JLabel("Status:"), c);
        c.gridx=1; c.gridy=y++; form.add(cmbStatus, c);

        // Gerente
        c.gridx=0; c.gridy=y; form.add(new JLabel("Gerente (apenas GERENTE_PROJETO):"), c);
        c.gridx=1; c.gridy=y++; form.add(cmbGerente, c);

        // Equipes (multi)
        c.gridx=0; c.gridy=y; form.add(new JLabel("Equipes (Ctrl/Cmd para múltiplos):"), c);
        c.gridx=1; c.gridy=y++; 
        spEquipes.setPreferredSize(new Dimension(320, 140));
        form.add(spEquipes, c);

        // Ativo
        c.gridx=1; c.gridy=y++; form.add(chkAtivo, c);

        // Barra de botões
        JPanel buttons = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttons.add(btnSalvar);
        buttons.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // Configura seleções
        lstEquipes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Carrega dados de apoio
        carregarSomenteGerentes();
        carregarEquipes();

        // Se for edição, preencher campos e pré-selecionar gerente/equipes
        if (projeto != null) {
            preencherFormularioCom(projeto);
            preSelecionarEquipes(projeto.getId());
        }

        // Eventos
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }

    // ============================
    // Suporte: carregar listas
    // ============================

    /** Popula o combo de gerente apenas com usuários que possuam o papel GERENTE_PROJETO. */
    private void carregarSomenteGerentes() {
        DefaultComboBoxModel<Usuario> model = new DefaultComboBoxModel<>();
        for (Usuario u : usuarioDAO.listarTodos()) {
            if (usuarioDAO.listarPapeisDoUsuario(u.getId()).contains("GERENTE_PROJETO")) {
                model.addElement(u);
            }
        }
        cmbGerente.setModel(model);

        if (model.getSize() == 0) {
            cmbGerente.setEnabled(false);
            JOptionPane.showMessageDialog(
                this,
                "Não há usuários com o papel GERENTE_PROJETO. Cadastre um antes de definir o gerente.",
                "Aviso",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /** Carrega todas as equipes no JList. */
    private void carregarEquipes() {
        DefaultListModel<Equipe> model = (DefaultListModel<Equipe>) lstEquipes.getModel();
        model.clear();
        for (Equipe e : equipeDAO.listarTodas()) {
            model.addElement(e); // toString() da Equipe mostra o nome
        }
    }

    // ============================
    // Suporte: edição (preencher / selecionar)
    // ============================

    /** Preenche os campos do formulário com os dados do projeto. */
    private void preencherFormularioCom(Projeto p) {
        txtNome.setText(p.getNome());
        txtDescricao.setText(p.getDescricao());
        txtDataInicio.setText(p.getDataInicio());
        txtDataPrevFim.setText(p.getDataPrevFim());
        cmbStatus.setSelectedItem(p.getStatus());
        chkAtivo.setSelected(p.isAtivo());

        if (p.getGerenteId() != null && cmbGerente.isEnabled()) {
            for (int i = 0; i < cmbGerente.getItemCount(); i++) {
                Usuario u = cmbGerente.getItemAt(i);
                if (u.getId().equals(p.getGerenteId())) {
                    cmbGerente.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /** Pré-seleciona no JList as equipes já vinculadas ao projeto. */
    private void preSelecionarEquipes(int projetoId) {
        List<Integer> equipeIds = projetoDAO.listarEquipeIdsDoProjeto(projetoId);
        DefaultListModel<Equipe> model = (DefaultListModel<Equipe>) lstEquipes.getModel();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            if (equipeIds.contains(model.get(i).getId())) {
                indices.add(i);
            }
        }
        int[] sel = indices.stream().mapToInt(Integer::intValue).toArray();
        lstEquipes.setSelectedIndices(sel);
    }

    // ============================
    // Salvar (insert/update + vínculos)
    // ============================

    /**
     * Valida campos, aplica dados ao modelo e persiste (insert/update).
     * Após persistir, atualiza os vínculos M:N em projeto_equipe.
     * A regra de gerente é reforçada também no DAO.
     */
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
        projeto.setStatus((String) cmbStatus.getSelectedItem());
        projeto.setAtivo(chkAtivo.isSelected());

        if (cmbGerente.isEnabled() && cmbGerente.getSelectedItem() != null) {
            Usuario gerente = (Usuario) cmbGerente.getSelectedItem();
            projeto.setGerenteId(gerente != null ? gerente.getId() : null);
        } else {
            projeto.setGerenteId(null);
        }

        try {
            // Insere/atualiza projeto (inserir preenche o ID no objeto)
            if (projeto.getId() == null) {
                projetoDAO.inserir(projeto);
            } else {
                projetoDAO.atualizar(projeto);
            }

            // Coleta equipes selecionadas
            List<Integer> equipesSelecionadas = new ArrayList<>();
            for (Equipe e : lstEquipes.getSelectedValuesList()) {
                equipesSelecionadas.add(e.getId());
            }

            // Atualiza vínculos M:N
            projetoDAO.atualizarEquipesDoProjeto(projeto.getId(), equipesSelecionadas);

            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
