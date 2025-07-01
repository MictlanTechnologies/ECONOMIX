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

    // Campo de texto para capturar/modificar el nombre del usuario
    private final JTextField perfilTxt   = new JTextField(15);

    // Campo para introducir una nueva contraseña (no se muestra la actual por seguridad)
    private final JPasswordField passTxt = new JPasswordField(15);

    /**
     * Constructor que inicializa el panel con el formulario de edición y carga los datos del usuario actual.
     * @param sf Fábrica de sesiones de Hibernate para operaciones de base de datos.
     * @param usuarioActual Usuario actualmente autenticado.
     */
    public UsuarioPanel(SessionFactory sf, Usuario usuarioActual) {
        super(sf, usuarioActual, new String[]{"Nombre de usuario"});
        add(construirFormulario(), BorderLayout.SOUTH);
        cargarTabla();
        cargarSeleccion(usuario);
    }

    @Override
    protected Class<Usuario> getEntityClass() { return Usuario.class; }

    @Override
    protected void agregarFilaATabla(Usuario u) {
        modelo.addRow(new Object[]{u.getId(), u.getPerfilUsuario()});
    }

    /**
     * Carga únicamente el usuario autenticado en la tabla.
     */
    @Override
    public void cargarTabla() {
        try (Session s = sf.openSession()) {
            Usuario u = s.get(Usuario.class, usuario.getId());
            refrescarTabla(List.of(u));
        }
    }

    /**
     * Carga el perfil del usuario seleccionado en el formulario para edición.
     * Por seguridad, el campo de contraseña se mantiene vacío.
     */
    @Override
    public void cargarSeleccion(Usuario u) {
        idSeleccionado = u.getId();
        perfilTxt.setText(u.getPerfilUsuario());
        passTxt.setText("");
    }

    /**
     * Guarda los cambios realizados en el perfil o contraseña del usuario actual.
     * Si la contraseña está vacía, no se actualiza.
     */
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

    /**
     * Esta operación está bloqueada desde el panel: no es posible eliminar usuarios.
     */
    @Override
    public void eliminar() {
        JOptionPane.showMessageDialog(this,
                "No tienes permisos para eliminar usuarios",
                "Operación no permitida", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Limpia todos los campos del formulario y reinicia la selección.
     */
    @Override
    public void limpiarCampos() {
        perfilTxt.setText("");
        passTxt.setText("");
        idSeleccionado = null;
    }

    /**
     * Muestra una ventana emergente con pestañas para editar los datos personales del usuario:
     * Persona, Domicilio y Contacto. Las pestañas están interconectadas para actualizarse entre sí.
     */
    private void mostrarDialogoPersonal() {
        var personaPanel   = new org.economix.ventana.model.usuario.personal.PersonaPanel(sf, usuario);
        var domicilioPanel = new org.economix.ventana.model.usuario.personal.DomicilioPanel(sf, usuario);
        var contactoPanel  = new org.economix.ventana.model.usuario.personal.ContactoPanel(sf, usuario);

        // Si cambia una persona, se actualizan las listas de domicilio y contacto automáticamente
        personaPanel.setCambioListener(() -> {
            domicilioPanel.cargarPersonas();
            contactoPanel.cargarPersonas();
        });

        // Construcción de las pestañas con cada panel respectivo
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Persona", personaPanel);
        tabs.addTab("Domicilio", domicilioPanel);
        tabs.addTab("Contacto", contactoPanel);

        // Diálogo modal para contener los paneles
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Datos personales", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.getContentPane().add(tabs);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    /**
     * Construye visualmente el formulario principal que contiene los campos de nombre y contraseña,
     * así como los botones de acción: Guardar, Limpiar, Información personal y Ayuda.
     *
     * @return JPanel listo para agregarse a la interfaz principal.
     */
    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);                    // Espaciado entre componentes
        gc.anchor = GridBagConstraints.WEST;                  // Alineación a la izquierda
        gc.fill = GridBagConstraints.HORIZONTAL;              // Expansión horizontal
        gc.weightx = 1;                                       // Prioridad horizontal
        int y = 0;

        // Primera fila: campo nombre de usuario
        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Usuario:"), gc);
        gc.gridx = 1; p.add(perfilTxt, gc); y++;

        // Segunda fila: campo contraseña
        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Contraseña:"), gc);
        gc.gridx = 1; p.add(passTxt, gc); y++;

        // Panel de botones
        JPanel botones = new JPanel();
        JButton guardarBtn = new JButton("Guardar");
        JButton limpiarBtn  = new JButton("Limpiar");
        JButton infoBtn     = new JButton("Información personal");
        JButton ayudaBtn    = new JButton("Ayuda");

        // Asignación de acciones a cada botón
        guardarBtn.addActionListener(e -> guardar());
        limpiarBtn .addActionListener(e -> limpiarCampos());
        infoBtn   .addActionListener(e -> mostrarDialogoPersonal());
        ayudaBtn  .addActionListener(e -> mostrarInfo());

        // Agregar botones al panel
        botones.add(guardarBtn);
        botones.add(limpiarBtn);
        botones.add(infoBtn);
        botones.add(ayudaBtn);

        // Ubicar la fila de botones en el formulario
        gc.gridx = 0; gc.gridy = y; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        p.add(botones, gc);

        return p;
    }

    /**
     * Muestra un cuadro de diálogo con información sobre cómo utilizar el panel actual.
     */
    private void mostrarInfo() {
        JOptionPane.showMessageDialog(this,
                "Desde aquí puedes modificar tu nombre de usuario y contraseña.\n" +
                        "Usa el botón 'Información personal' para editar tus datos de contacto.",
                "Ayuda",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
