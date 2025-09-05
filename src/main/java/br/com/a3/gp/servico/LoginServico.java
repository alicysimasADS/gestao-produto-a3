package br.com.a3.gp.servico;

import br.com.a3.gp.dao.UsuarioDAO;
import br.com.a3.gp.modelo.Usuario;
import br.com.a3.gp.util.SenhaUtil;

/**
 * Serviço responsável pela autenticação de usuários.
 * Verifica login, senha e status ativo, e inicia a sessão se válido.
 */
public class LoginServico {

    // DAO responsável por buscar dados do usuário no banco
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Realiza o processo de autenticação do usuário.
     * Verifica:
     * - se o usuário existe com o login informado
     * - se está ativo
     * - se a senha informada confere com o hash armazenado (via BCrypt)
     *
     * Se válido, registra o usuário na sessão (`Sessao.setUsuario()`).
     *
     * @param login login informado
     * @param senha senha em texto puro (será comparada via BCrypt)
     * @return objeto Usuario autenticado, ou null se falhar
     */
    public Usuario autenticar(String login, String senha) {
        // Busca o usuário pelo login
        Usuario u = usuarioDAO.buscarPorLogin(login);

        // Se encontrado, estiver ativo e a senha for válida...
        if (u != null && u.isAtivo() && SenhaUtil.conferir(senha, u.getSenhaHash())) {
            // Armazena o usuário na sessão global
            Sessao.setUsuario(u);
            return u;
        }

        // Caso não atenda às condições, autenticação falha
        return null;
    }
}
