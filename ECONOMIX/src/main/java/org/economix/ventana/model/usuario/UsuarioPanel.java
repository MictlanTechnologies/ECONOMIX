package org.economix.ventana.model.usuario;

/**
 * Panel para visualizar y editar los datos de Usuario (perfil y contraseña).
 * Extiende {@link GestorCatalogosSwing} reutilizando la lógica CRUD genérica.
 */
import org.economix.model.usuario.Usuario;
import org.economix.ventana.model.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UsuarioPanel extends GestorCatalogosSwing<Usuario> {
    /* ===== Campos del formulario ===== */
    private final JTextField perfilTxt = new JTextField(15);
    private final JPasswordField passTxt = new JPasswordField(15);

    public UsuarioPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario, new String[]{"ID", "Nombre de usuario"});
        add(construirFormulario(), BorderLayout.SOUTH);
        cargarTabla();
    }

    /* =====================================================
     *           Implementación de métodos abstractos
     * ===================================================== */

    @Override
    protected Class<Usuario> getEntityClass() {
        return Usuario.class;
    }

    @Override
    protected void agregarFilaATabla(Usuario u) {
        modelo.addRow(new Object[]{u.getId(), u.getPerfilUsuario()});
    }

    @Override
    public void cargarTabla() {
        // Solo muestra el usuario autenticado
        try (Session s = sf.openSession()) {
            List<Usuario> lista = s.createQuery(
                            "from Usuario where id = :id", Usuario.class)
                    .setParameter("id", usuario.getId())
                    .list();
            refrescarTabla(lista);
        }
    }

    @Override
    public void cargarSeleccion(Usuario u) {
        idSeleccionado = u.getId();
        perfilTxt.setText(u.getPerfilUsuario());
        passTxt.setText(u.getContraseñaUsuario());
    }

    @Override
    public void guardar() {
        String perfil = perfilTxt.getText().trim();
        String pass = new String(passTxt.getPassword()).trim();

        if (perfil.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Usuario y contraseña son obligatorios",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dentroDeTransaccion(session -> {
            Usuario u = session.get(Usuario.class, usuario.getId());
            u.setPerfilUsuario(perfil);
            u.setContraseñaUsuario(pass);
            session.persist(u);
        });

        JOptionPane.showMessageDialog(this,
                "Datos actualizados.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        cargarTabla();
    }

    @Override
    public void eliminar() {
        JOptionPane.showMessageDialog(this,
                "No se permite eliminar el usuario actual.",
                "Operación no soportada", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void limpiarCampos() {
        perfilTxt.setText("");
        passTxt.setText("");
        idSeleccionado = null;
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

        JButton guardarBtn = new JButton("Guardar cambios");
        guardarBtn.addActionListener(e -> guardar());

        gc.gridx = 0; gc.gridy = y; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        p.add(guardarBtn, gc);

        return p;
    }
}

