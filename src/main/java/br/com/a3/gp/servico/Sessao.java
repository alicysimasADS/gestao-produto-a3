package br.com.a3.gp.servico;

import br.com.a3.gp.modelo.Usuario;

/**
 * Classe utilitária responsável por manter o usuário autenticado durante a sessão.
 * Atua como um "singleton estático" de sessão para toda a aplicação.
 */
public class Sessao {

    // Armazena o usuário autenticado atualmente na aplicação
    private static Usuario usuario;

    /**
     * Retorna o usuário atualmente autenticado.
     * @return objeto Usuario da sessão atual (ou null, se ninguém estiver logado)
     */
    public static Usuario getUsuario() {
        return usuario;
    }

    /**
     * Define o usuário da sessão atual (após login bem-sucedido).
     * @param u objeto Usuario autenticado
     */
    public static void setUsuario(Usuario u) {
        usuario = u;
    }
}
