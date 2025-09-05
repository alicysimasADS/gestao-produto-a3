package br.com.a3.gp.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe utilitária para operações de segurança relacionadas a senhas.
 * Usa o algoritmo BCrypt para gerar e verificar hashes de forma segura.
 */
public class SenhaUtil {

    /**
     * Gera um hash seguro (BCrypt) para a senha informada.
     * Utiliza salt aleatório e fator de custo padrão (log rounds).
     *
     * @param senha senha em texto puro
     * @return hash gerado (com salt embutido)
     */
    public static String hash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    /**
     * Verifica se uma senha informada confere com um hash existente.
     * A comparação já considera o salt embutido no hash.
     *
     * @param senha senha em texto puro (entrada do usuário)
     * @param hash hash armazenado no banco (gerado anteriormente)
     * @return true se a senha for válida, false caso contrário
     */
    public static boolean conferir(String senha, String hash) {
        return BCrypt.checkpw(senha, hash);
    }
}
