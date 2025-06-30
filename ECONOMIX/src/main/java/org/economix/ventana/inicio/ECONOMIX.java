package org.economix.ventana.inicio;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.economix.util.HibernateUtil;
import org.economix.ventana.vista.Login;
import org.economix.ventana.vista.Registro;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.economix.ventana.vista.PantallaInicio;

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
            new PantallaInicio(sf).setVisible(true);
        });
    }
}





