package br.com.a3.gp.util;

import org.mindrot.jbcrypt.BCrypt;

public class SenhaUtil {
    public static String hash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }
    public static boolean conferir(String senha, String hash) {
        return BCrypt.checkpw(senha, hash);
    }
}
