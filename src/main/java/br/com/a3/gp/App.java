package br.com.a3.gp;

import br.com.a3.gp.config.SchemaInit;
import br.com.a3.gp.visao.LoginJanela;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SchemaInit.ensure();
        SwingUtilities.invokeLater(() -> new LoginJanela().setVisible(true));
    }
}
