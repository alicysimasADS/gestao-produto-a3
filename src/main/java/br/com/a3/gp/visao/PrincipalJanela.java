package br.com.a3.gp.visao;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal da aplicação após login.
 * Exibe botões de navegação para os módulos: Usuários, Equipes e Projetos.
 */
public class PrincipalJanela extends JFrame {

    /**
     * Construtor da janela principal.
     * Define layout simples com botões que abrem as telas de listagem.
     */
    public PrincipalJanela() {
        super("Gestão de Produto A3");

        // Fecha a aplicação ao encerrar esta janela
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Layout simples com componentes lado a lado
        setLayout(new FlowLayout());

        // --------------------------
        // Botões de navegação
        // --------------------------
        JButton btnUsuarios = new JButton("Usuários");
        JButton btnEquipes = new JButton("Equipes");
        JButton btnProjetos = new JButton("Projetos");

        // Abre a janela de listagem de usuários
        btnUsuarios.addActionListener(e -> new UsuarioListaJanela().setVisible(true));

        // Abre a janela de listagem de equipes
        btnEquipes.addActionListener(e -> new EquipeListaJanela().setVisible(true));

        // Abre a janela de listagem de projetos
        btnProjetos.addActionListener(e -> new ProjetoListaJanela().setVisible(true));

        // Adiciona os botões na tela
        add(btnUsuarios);
        add(btnEquipes);
        add(btnProjetos);

        // Define tamanho da janela e centraliza
        setSize(480, 150);
        setLocationRelativeTo(null);
    }
}
