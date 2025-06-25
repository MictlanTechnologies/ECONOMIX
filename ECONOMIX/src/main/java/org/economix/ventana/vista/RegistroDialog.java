package org.economix.ventana.vista;

import org.economix.model.usuario.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.swing.*;
import java.awt.*;

/* --------------------------------------------------
 *  DIÁLOGO DE REGISTRO (PRIMERA EJECUCIÓN)
 * -------------------------------------------------- */
public class RegistroDialog extends JDialog {
    private final JTextField usuarioTxt  = new JTextField(20);
    private final JPasswordField passTxt = new JPasswordField(20);
    private final SessionFactory sf;

    public RegistroDialog(Frame owner, SessionFactory sf) {
        super(owner, "Registro de usuario", true);
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

        JButton registrar = new JButton("Registrar y entrar");
        registrar.addActionListener(e -> registrarAction());
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        add(registrar, gc);

        pack();
        setLocationRelativeTo(null);
    }

    private void registrarAction() {
        String user = usuarioTxt.getText().trim();
        String pass = new String(passTxt.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario y contraseña obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setPerfilUsuario(user);
        nuevo.setContraseñaUsuario(pass);

        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            s.persist(nuevo);
            tx.commit();
        }

        dispose();
        new PrincipalVentana(sf, nuevo).setVisible(true);
    }
}

