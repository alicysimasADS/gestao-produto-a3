package br.com.a3.gp.visao;

import javax.swing.*;
import java.awt.*;

public class PrincipalJanela extends JFrame {
    public PrincipalJanela() {
        super("Gestão de Produto A3");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JButton btnUsuarios = new JButton("Usuários");
        JButton btnEquipes = new JButton("Equipes");
        JButton btnProjetos = new JButton("Projetos");

        btnUsuarios.addActionListener(e -> new UsuarioListaJanela().setVisible(true));
        btnEquipes.addActionListener(e -> new EquipeListaJanela().setVisible(true));
        btnProjetos.addActionListener(e -> new ProjetoListaJanela().setVisible(true));

        add(btnUsuarios);
        add(btnEquipes);
        add(btnProjetos);

        setSize(480, 150);
        setLocationRelativeTo(null);
    }
}
