package br.com.a3.gp.servico;

import br.com.a3.gp.modelo.Usuario;

public class Sessao {
    private static Usuario usuario;

    public static Usuario getUsuario() {
        return usuario;
    }
    public static void setUsuario(Usuario u) {
        usuario = u;
    }
}
