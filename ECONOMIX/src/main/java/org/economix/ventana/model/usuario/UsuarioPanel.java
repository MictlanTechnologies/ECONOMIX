package org.economix.ventana.model.usuario;

/**
 * Panel para visualizar y editar los datos de Usuario (perfil y contraseña).
 * Extiende {@link GestorCatalogosSwing} reutilizando la lógica CRUD genérica.
 */
import org.economix.model.usuario.Usuario;
import org.economix.ventana.vista.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UsuarioPanel extends GestorCatalogosSwing<Usuario> {
    /* ===== Campos del formulario ===== */
    private final JTextField perfilTxt   = new JTextField(15);
    private final JPasswordField passTxt = new JPasswordField(15);


    public UsuarioPanel(SessionFactory sf, Usuario usuarioActual) {
        super(sf, usuarioActual, new String[]{"Nombre de usuario"});
        add(construirFormulario(), BorderLayout.SOUTH);
        cargarTabla();
        cargarSeleccion(usuario);
    }

    /* =====================================================
     *      Implementación de métodos abstractos del gestor
     * ===================================================== */

    @Override
    protected Class<Usuario> getEntityClass() { return Usuario.class; }

    @Override
    protected void agregarFilaATabla(Usuario u) {
        modelo.addRow(new Object[]{u.getId(), u.getPerfilUsuario()});
    }

    /** Lista solo el usuario actual */
    @Override
    public void cargarTabla() {
        try (Session s = sf.openSession()) {
            Usuario u = s.get(Usuario.class, usuario.getId());
            refrescarTabla(java.util.List.of(u));
        }
    }

    /**
     * Carga la fila seleccionada en el formulario para edición.
     * La contraseña no se precarga por seguridad.
     */
    @Override
    public void cargarSeleccion(Usuario u) {
        idSeleccionado = u.getId();
        perfilTxt.setText(u.getPerfilUsuario());
        passTxt.setText(""); // obligar a introducir nueva si se desea cambiar
    }

    /** Actualiza los datos del usuario actual. */
    @Override
    public void guardar() {
        String perfil = perfilTxt.getText().trim();
        String pass   = new String(passTxt.getPassword()).trim();

        if (perfil.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre de usuario es obligatorio",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
            dentroDeTransaccion(session -> {
            Usuario u = session.get(Usuario.class, usuario.getId());
            u.setPerfilUsuario(perfil);
            if (!pass.isEmpty()) {
                u.setContraseñaUsuario(pass);
            }
            session.persist(u);
        });

        usuario.setPerfilUsuario(perfil);
        if (!pass.isEmpty()) usuario.setContraseñaUsuario(pass);

        limpiarCampos();
        cargarTabla();
    }

     /** No se permite eliminar el usuario desde este panel */
    @Override
    public void eliminar() {
        JOptionPane.showMessageDialog(this,
                "No tienes permisos para eliminar usuarios",
                "Operación no permitida", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void limpiarCampos() {
        perfilTxt.setText("");
        passTxt.setText("");
        idSeleccionado = null;
    }

    private void mostrarDialogoPersonal() {
        var personaPanel   = new org.economix.ventana.model.usuario.personal.PersonaPanel(sf, usuario);
        var domicilioPanel = new org.economix.ventana.model.usuario.personal.DomicilioPanel(sf, usuario);
        var contactoPanel  = new org.economix.ventana.model.usuario.personal.ContactoPanel(sf, usuario);

        personaPanel.setCambioListener(() -> {
            domicilioPanel.cargarPersonas();
            contactoPanel.cargarPersonas();
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Persona", personaPanel);
        tabs.addTab("Domicilio", domicilioPanel);
        tabs.addTab("Contacto", contactoPanel);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Datos personales", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.getContentPane().add(tabs);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    /* =====================================================
     *                    UI Formulario
     * ===================================================== */
    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        int y = 0;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Usuario:"), gc);
        gc.gridx = 1; p.add(perfilTxt, gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Contraseña:"), gc);
        gc.gridx = 1; p.add(passTxt, gc); y++;

        JPanel botones = new JPanel();
        JButton guardarBtn = new JButton("Guardar");
        JButton limpiarBtn  = new JButton("Limpiar");
        JButton infoBtn     = new JButton("Información personal");
        JButton ayudaBtn    = new JButton("Ayuda");

        guardarBtn.addActionListener(e -> guardar());
        limpiarBtn .addActionListener(e -> limpiarCampos());
        infoBtn   .addActionListener(e -> mostrarDialogoPersonal());

        botones.add(guardarBtn);
        botones.add(limpiarBtn);
        botones.add(infoBtn);
        botones.add(ayudaBtn);

        gc.gridx = 0; gc.gridy = y; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        p.add(botones, gc);

        ayudaBtn.addActionListener(e -> mostrarInfo());


        return p;
    }
        /** Indica cómo gestionar los datos de usuario desde este panel. */
        private void mostrarInfo() {
            JOptionPane.showMessageDialog(this,
                "Desde aquí puedes modificar tu nombre de usuario y contraseña.\n" +
                        "Usa el botón 'Información personal' para editar tus datos de contacto.",
                "Ayuda",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

