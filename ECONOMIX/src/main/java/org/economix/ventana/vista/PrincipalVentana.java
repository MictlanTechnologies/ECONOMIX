package org.economix.ventana.vista;

import org.economix.model.usuario.Usuario;
import org.economix.util.IconUtil;
import org.economix.ventana.model.GastosPanel;
import org.economix.ventana.model.IngresosPanel;
import org.economix.ventana.model.usuario.UsuarioPanel;
import org.economix.ventana.model.GraficasPanel;
import org.economix.ventana.model.PresupuestoPanel;
import org.hibernate.SessionFactory;
import java.awt.*;
import javax.swing.*;

/* --------------------------------------------------
 *  VENTANA PRINCIPAL CON CARDLAYOUT
 * -------------------------------------------------- */
public class PrincipalVentana extends JFrame {
    private final SessionFactory sf;
    private final Usuario usuarioActual;
    private final CardLayout card = new CardLayout();
    private final JPanel cardPanel = new JPanel(card);
    private final GraficasPanel graficasPanel;
    private final GastosPanel gastosPanel;
    private final IngresosPanel ingresosPanel;
    private final PresupuestoPanel presupuestoPanel;

    public PrincipalVentana(SessionFactory sf, Usuario usuarioActual) {
        super("ECONOMIX – Bienvenido " + usuarioActual.getPerfilUsuario());
        this.sf = sf;
        setIconImage(IconUtil.getAppImage());
        this.usuarioActual = usuarioActual;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        this.gastosPanel = new GastosPanel(sf, usuarioActual);
        this.ingresosPanel = new IngresosPanel(sf, usuarioActual);
        this.presupuestoPanel = new PresupuestoPanel(sf, usuarioActual);
        this.graficasPanel = new GraficasPanel(sf, usuarioActual);
        construirUI();
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        /* ---------- Barra de navegación ---------- */
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        JButton gastosBtn = new JButton("Gastos");
        JButton ingresosBtn = new JButton("Ingresos");
        JButton presupBtn = new JButton("Presupuestos");
        JButton graficasBtn = new JButton("Gráficas");
        JButton usuarioBtn = new JButton("Usuario");
        JButton ayudaBtn = new JButton("Ayuda");
        barra.add(gastosBtn);
        barra.addSeparator();
        barra.add(ingresosBtn);
        barra.addSeparator();
        barra.add(presupBtn);
        barra.addSeparator();
        barra.add(graficasBtn);
        barra.addSeparator();
        barra.add(usuarioBtn);
        barra.addSeparator();
        barra.add(ayudaBtn);
        add(barra, BorderLayout.NORTH);

        /* ---------- Tarjetas ---------- */
        cardPanel.add(gastosPanel, "Gastos");
        cardPanel.add(ingresosPanel, "Ingresos");
        cardPanel.add(presupuestoPanel, "Presupuestos");
        cardPanel.add(new UsuarioPanel(sf, usuarioActual), "Usuario");
        cardPanel.add(graficasPanel, "Graficas");
        add(cardPanel, BorderLayout.CENTER);

        /* --- Sincronizar paneles --- */
        Runnable refrescar = () -> {
            graficasPanel.actualizar();
            presupuestoPanel.actualizar();
        };
        gastosPanel.setCambioListener(refrescar);
        ingresosPanel.setCambioListener(refrescar);
        presupuestoPanel.setCambioListener(refrescar);

        /* ---------- Eventos ---------- */
        gastosBtn.addActionListener(e -> {
            gastosPanel.cargarTabla();
            card.show(cardPanel, "Gastos");
        });
        ingresosBtn.addActionListener(e -> {
            ingresosPanel.cargarTabla();
            card.show(cardPanel, "Ingresos");
        });
        presupBtn.addActionListener(e -> {
            presupuestoPanel.actualizar();
            card.show(cardPanel, "Presupuestos");
        });
        usuarioBtn.addActionListener(e -> card.show(cardPanel, "Usuario"));
        ayudaBtn.addActionListener(e -> mostrarInfo());
        graficasBtn.addActionListener(e -> {graficasPanel.actualizar();
            card.show(cardPanel, "Graficas");
        });
    }

    /**
     * Mensaje de ayuda general para la navegación.
     */
    private void mostrarInfo() {
        JOptionPane.showMessageDialog(
                this,
                "Usa la barra superior para acceder a Gastos, Ingresos, Presupuestos, Gráficas y Usuario.",
                "Ayuda",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}


