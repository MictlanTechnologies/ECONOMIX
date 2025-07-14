package org.economix.ventana.vista;

import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.economix.util.IconUtil;
import org.economix.ventana.vista.Login;
import org.economix.ventana.vista.Registro;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

/**
 * Pantalla de inicio con logo y botones para acceder o registrarse.
 * Permite establecer una imagen GIF o un video de fondo utilizando vlcj.
 */
public class PantallaInicio extends JFrame {
    private final SessionFactory sf;
    private final JLabel background = new JLabel();
    private final JLabel logoLabel  = new JLabel();
    private EmbeddedMediaPlayerComponent mediaPlayer; // para video opcional
    private final JButton themeBtn = new JButton("\uD83C\uDF19"); // 🌙 por defecto
    private boolean darkMode = true;
    private int themeMode = 0; // 0 oscuro, 1 claro, 2 paleta


    public PantallaInicio(SessionFactory sf) {
        super("ECONOMIX");
        this.sf = sf;
        setIconImage(IconUtil.getAppImage());
        // Mostrar el logo por defecto en el fondo y en la etiqueta
        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        background.setLayout(new GridBagLayout());
        add(background, BorderLayout.CENTER);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(themeBtn);
        add(top, BorderLayout.NORTH);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.CENTER;

        ImageIcon logo = new ImageIcon(
                Objects.requireNonNull(PantallaInicio.class.getResource("/archivosGraficos/ECONOMIXL_LOGO.png")));
        // Aumentar tamaño del logo para que se aprecie mejor en la pantalla
        Image scaled = logo.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaled));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gc.gridy++;
        JLabel subtitulo = new JLabel("Mictlan Technologies");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitulo.setForeground(Color.LIGHT_GRAY);
        background.add(subtitulo, gc);

        gc.gridy++;
        background.add(logoLabel, gc); // espacio para el logo

        JPanel botones = new JPanel();
        JButton iniciarBtn = new JButton("Iniciar Sesión");
        JButton registrarBtn = new JButton("Registrarse");
        botones.add(iniciarBtn);
        botones.add(registrarBtn);
        gc.gridy++;
        background.add(botones, gc);

        iniciarBtn.addActionListener(e -> mostrarLogin());
        registrarBtn.addActionListener(e -> abrirRegistro());
        themeBtn.addActionListener(e -> toggleTheme());

        setLocationRelativeTo(null);
    }

    private void mostrarLogin() {
        dispose();
        long total;
        try (Session s = sf.openSession()) {
            total = s.createQuery("select count(u) from Usuario u", Long.class)
                    .uniqueResult();
        }
        if (total == 0) {
            new Registro(null, sf).setVisible(true);
        } else {
            new Login(null, sf).setVisible(true);
        }
    }

    private void abrirRegistro() {
        dispose();
        new Registro(null, sf).setVisible(true);
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
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
        }
    }
    /** Establece un GIF como fondo de la pantalla. */
    public void setBackgroundGif(String path) {
        background.setIcon(new ImageIcon("ECONOMIX_background.gif"));
    }

    /** Establece un video de fondo utilizando vlcj. */
    public void setBackgroundVideo(String path) {
        if (mediaPlayer != null) {
            background.remove(mediaPlayer);
        }
        mediaPlayer = new EmbeddedMediaPlayerComponent();
        background.add(mediaPlayer, BorderLayout.CENTER);
        mediaPlayer.mediaPlayer().media().play(path);
    }

    /** Espacio para definir la imagen del logo. */
    public void setLogoIcon(ImageIcon icon) {
        logoLabel.setIcon(icon);
    }
}