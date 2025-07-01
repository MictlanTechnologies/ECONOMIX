package org.economix.ventana.vista;

import org.economix.ventana.vista.Login;
import org.economix.ventana.vista.Registro;
import org.economix.util.IconUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
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

    public PantallaInicio(SessionFactory sf) {
        super("ECONOMIX");
        this.sf = sf;
        setIconImage(IconUtil.getAppImage());
        // Mostrar el logo por defecto en el fondo y en la etiqueta
        construirUI();
    }

    private void construirUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        background.setLayout(new GridBagLayout());
        add(background);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.CENTER;

        JLabel titulo = new JLabel("ECONOMIX");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 48));
        titulo.setForeground(Color.WHITE);
        background.add(titulo, gc);

        gc.gridy++;
        JLabel subtitulo = new JLabel("Mictlan Technologies");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitulo.setForeground(Color.WHITE);
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

    /** Establece un GIF como fondo de la pantalla. */
    public void setBackgroundGif(String path) {
        background.setIcon(new ImageIcon(path));
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