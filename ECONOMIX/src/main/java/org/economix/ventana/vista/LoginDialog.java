package org.economix.ventana.vista;

import org.economix.model.usuario.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;


/* --------------------------------------------------
 *  DIÁLOGO DE LOGIN (EJECUCIONES SUBSIGUIENTES)
 * -------------------------------------------------- */



public class LoginDialog extends JDialog {
    private final JTextField usuarioTxt  = new JTextField(20);
    private final JPasswordField passTxt = new JPasswordField(20);
    private final SessionFactory sf;

    public LoginDialog(Frame owner, SessionFactory sf) {
        super(owner, "Login", true);
        this.sf = sf;
        construirUI();
    }

    private void construirUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; add(new JLabel("Usuario:"), gc);
        gc.gridx = 1; add(usuarioTxt, gc);

        gc.gridx = 0; gc.gridy = 1; add(new JLabel("Contraseña:"), gc);
        gc.gridx = 1; add(passTxt, gc);

        JButton entrar = new JButton("Entrar");
        entrar.addActionListener(e -> loginAction());
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        add(entrar, gc);

        pack();
        setLocationRelativeTo(null);
    }

    private void loginAction() {
        String user = usuarioTxt.getText().trim();
        String pass = new String(passTxt.getPassword());

        Usuario encontrado;
        try (Session s = sf.openSession()) {
            encontrado = s.createQuery("from Usuario where perfilUsuario = :u", Usuario.class)
                    .setParameter("u", user)
                    .uniqueResult();
        }

        if (encontrado == null || !encontrado.getContraseñaUsuario().equals(pass)) {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
        new PrincipalVentana(sf, encontrado).setVisible(true);
    }
}
