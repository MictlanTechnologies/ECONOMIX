package org.economix.ventana.inicio;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.economix.util.HibernateUtil;
import org.economix.ventana.vista.Login;
import org.economix.ventana.vista.Registro;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;

/**
 * Lanzador de la aplicación.
 * Comprueba si existe al menos un usuario; si no, fuerza el registro.
 */
public class ECONOMIX {
    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(() -> {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            long totalUsuarios;
            try (Session s = sf.openSession()) {
                totalUsuarios = s.createQuery("select count(u) from Usuario u", Long.class)
                        .uniqueResult();
            }

            if (totalUsuarios == 0) {
                new Registro(null, sf).setVisible(true);
            } else {
                new Login(null, sf).setVisible(true);
            }
        });
    }
}





