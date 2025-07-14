package org.economix.ventana.vista;

import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.economix.model.usuario.Usuario;
import org.economix.util.IconUtil;
import org.economix.ventana.model.*;
import org.economix.ventana.model.usuario.UsuarioPanel;
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
    private final AhorroPanel ahorroPanel;
    private final PresupuestoPanel presupuestoPanel;
    private final JButton themeBtn = new JButton("\uD83C\uDF19"); // 🌙 por defecto
    private boolean darkMode = true;
    private int themeMode = 0; // 0 oscuro, 1 claro, 2 paleta

    public PrincipalVentana(SessionFactory sf, Usuario usuarioActual) {
        super("ECONOMIX – Bienvenido " + usuarioActual.getPerfilUsuario());
        this.sf = sf;
        setIconImage(IconUtil.getAppImage());
        this.usuarioActual = usuarioActual;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        this.gastosPanel = new GastosPanel(sf, usuarioActual);
        this.ingresosPanel = new IngresosPanel(sf, usuarioActual);
        this.ahorroPanel = new AhorroPanel(sf, usuarioActual);
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
        JButton ahorroBtn  = new JButton("Ahorro");
        JButton presupBtn = new JButton("Presupuestos");
        JButton graficasBtn = new JButton("Gráficas");
        JButton usuarioBtn = new JButton("Usuario");
        JButton ayudaBtn = new JButton("Ayuda");
        barra.add(gastosBtn);
        barra.addSeparator();
        barra.add(ingresosBtn);
        barra.addSeparator();
        barra.add(ahorroBtn);
        barra.addSeparator();
        barra.add(presupBtn);
        barra.addSeparator();
        barra.add(graficasBtn);
        barra.addSeparator();
        barra.add(usuarioBtn);
        barra.addSeparator();
        barra.add(ayudaBtn);
        barra.add(Box.createHorizontalGlue());
        barra.add(themeBtn);
        add(barra, BorderLayout.NORTH);

        /* ---------- Tarjetas ---------- */
        cardPanel.add(gastosPanel, "Gastos");
        cardPanel.add(ingresosPanel, "Ingresos");
        cardPanel.add(ahorroPanel, "Ahorro");
        cardPanel.add(presupuestoPanel, "Presupuestos");
        cardPanel.add(new UsuarioPanel(sf, usuarioActual), "Usuario");
        cardPanel.add(graficasPanel, "Graficas");
        add(cardPanel, BorderLayout.CENTER);

        /* --- Sincronizar paneles --- */
        Runnable refrescar = () -> {
            graficasPanel.actualizar();
            presupuestoPanel.actualizar();
            ingresosPanel.cargarTabla();
            ahorroPanel.actualizar();
        };
        gastosPanel.setCambioListener(refrescar);
        ingresosPanel.setCambioListener(refrescar);
        presupuestoPanel.setCambioListener(refrescar);
        ahorroPanel.setCambioListener(refrescar);

        /* ---------- Eventos ---------- */
        gastosBtn.addActionListener(e -> {
            gastosPanel.cargarTabla();
            card.show(cardPanel, "Gastos");
        });
        ingresosBtn.addActionListener(e -> {
            ingresosPanel.cargarTabla();
            card.show(cardPanel, "Ingresos");
        });
        ahorroBtn.addActionListener(e -> {
            ahorroPanel.actualizar();
            card.show(cardPanel, "Ahorro");
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
        themeBtn.addActionListener(e -> toggleTheme());
    }

    private void toggleTheme() {
        themeMode = (themeMode + 1) % 4;
        switch (themeMode) {
            case 0 -> {
                FlatMacDarkLaf.setup();
                themeBtn.setText("\uD83C\uDF19"); // 🌙
            }
            case 1 -> {
                FlatMacLightLaf.setup();
                themeBtn.setText("\u2600"); // ☀
            }
            case 2 -> {
                FlatGradiantoDarkFuchsiaIJTheme.setup();
                themeBtn.setText("\uD83D\uDC7E"); // 👾
            }
            default -> {
                FlatGradiantoNatureGreenIJTheme.setup();
                themeBtn.setText("\uD83C\uDF43"); // 🍃
            }
        }
        darkMode = !darkMode;
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
        }
    }

    /**
     * Mensaje de ayuda general para la navegación.
     */
    private void mostrarInfo() {
        JOptionPane.showMessageDialog(
                this,
                "Usa la barra superior para acceder a Gastos, Ingresos, Ahorro, Presupuestos, Gráficas y Usuario.",
                "Ayuda",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}


