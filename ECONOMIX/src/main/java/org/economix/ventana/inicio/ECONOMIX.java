// Paquete principal donde se lanza la interfaz gráfica del sistema
package org.economix.ventana.inicio;

// Importaciones generales: tema visual, utilidades Hibernate y vistas Swing
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.economix.util.HibernateUtil;
import org.economix.ventana.vista.PantallaInicio;
import org.hibernate.SessionFactory;
import javax.swing.SwingUtilities;

/**
 * Clase principal que inicia ECONOMIX con interfaz gráfica (Swing).
 */
public class ECONOMIX {

    /**
     * Método main: aplica el tema visual, inicializa Hibernate
     * y lanza la pantalla de bienvenida.
     */
    public static void main(String[] args) {
        FlatMacDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            SessionFactory sf = HibernateUtil.getSessionFactory(); // Obtiene la sesión Hibernate
            new PantallaInicio(sf).setVisible(true);               // Muestra la ventana inicial
        });
    }
}
