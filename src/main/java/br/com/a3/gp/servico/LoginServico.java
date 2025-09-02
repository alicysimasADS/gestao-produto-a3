package br.com.a3.gp.servico;

import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Usuario;
import br.com.a3.gp.util.SenhaUtil;

public class LoginServico {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String login, String senha) {
        Usuario u = usuarioDAO.buscarPorLogin(login);
        if (u != null && u.isAtivo() && SenhaUtil.conferir(senha, u.getSenhaHash())) {
            Sessao.setUsuario(u);
            return u;
        }
        return null;
    }
}
