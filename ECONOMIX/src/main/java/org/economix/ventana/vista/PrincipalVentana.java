package org.economix.ventana.vista;

import org.economix.model.usuario.Usuario;
import org.economix.ventana.model.gastos.GastosPanel;
import org.economix.ventana.model.ingresos.IngresosPanel;
import org.economix.ventana.model.usuario.UsuarioPanel;
import org.economix.ventana.model.presupuesto.PresupuestoPanel;
import org.hibernate.SessionFactory;
import java.awt.*;
import javax.swing.*;

/* --------------------------------------------------
 *  VENTANA PRINCIPAL CON CARDLAYOUT
 * -------------------------------------------------- */
public class PrincipalVentana extends JFrame {
    private final SessionFactory sf;
    private final Usuario usuarioActual;
    private final CardLayout card      = new CardLayout();
    private final JPanel cardPanel     = new JPanel(card);

    public PrincipalVentana(SessionFactory sf, Usuario usuarioActual) {
        super("ECONOMIX – Bienvenido " + usuarioActual.getPerfilUsuario());
        this.sf            = sf;
        this.usuarioActual = usuarioActual;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        construirUI();
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        /* ---------- Barra de navegación ---------- */
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        JButton gastosBtn   = new JButton("Gastos");
        JButton ingresosBtn = new JButton("Ingresos");
        JButton presupBtn   = new JButton("Presupuestos");
        JButton usuarioBtn  = new JButton("Usuario");
        barra.add(gastosBtn);
        barra.add(ingresosBtn);
        barra.add(presupBtn);
        barra.add(usuarioBtn);
        add(barra, BorderLayout.NORTH);

        /* ---------- Tarjetas ---------- */
        cardPanel.add(new GastosPanel(sf, usuarioActual), "Gastos");
        cardPanel.add(new IngresosPanel(sf,usuarioActual), "Ingresos");
        cardPanel.add(new PresupuestoPanel(sf, usuarioActual), "Presupuestos");
        cardPanel.add(new UsuarioPanel(sf,usuarioActual), "Usuario");
        add(cardPanel, BorderLayout.CENTER);

        /* ---------- Eventos ---------- */
        gastosBtn.addActionListener(e -> card.show(cardPanel, "Gastos"));
        ingresosBtn.addActionListener(e -> card.show(cardPanel, "Ingresos"));
        presupBtn.addActionListener(e -> card.show(cardPanel, "Presupuestos"));
        usuarioBtn.addActionListener(e -> card.show(cardPanel, "Usuario"));
    }
}


