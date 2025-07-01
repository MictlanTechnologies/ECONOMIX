package org.economix.ventana.model.usuario.personal;

// Importaciones generales para entidades, vistas, sesiones y componentes gráficos
import org.economix.model.usuario.Contacto;
import org.economix.model.usuario.Persona;
import org.economix.model.usuario.Usuario;
import org.economix.ventana.vista.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel gráfico Swing que permite la gestión de entidades {@link Contacto} relacionadas a un {@link Usuario}.
 * Este panel está diseñado para realizar operaciones CRUD (crear, leer, actualizar y eliminar) sobre los contactos,
 * incluyendo número celular y correo electrónico, y asociándolos a una persona existente.
 */
public class ContactoPanel extends GestorCatalogosSwing<Contacto> {

    /**
     * ComboBox para seleccionar la {@link Persona} asociada al contacto.
     */
    private final JComboBox<Persona> personaCmb = new JComboBox<>();

    /**
     * Campo de entrada para capturar el número de celular del contacto.
     */
    private final JTextField celularTxt = new JTextField(15);

    /**
     * Campo de entrada para capturar el correo electrónico del contacto.
     */
    private final JTextField correoTxt = new JTextField(20);

    /**
     * Constructor que inicializa el panel de contacto, construye el formulario y carga los datos iniciales.
     * @param sf fábrica de sesiones de Hibernate para acceso a datos.
     * @param usuario usuario autenticado, base para filtrar entidades relacionadas.
     */
    public ContactoPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario, new String[]{"Persona", "Celular", "Correo"});
        add(construirFormulario(), BorderLayout.EAST);
        cargarPersonas();
        cargarTabla();
    }

    /**
     * Carga todas las personas asociadas al usuario en el ComboBox para poder vincularlas a un contacto.
     */
    public void cargarPersonas() {
        personaCmb.removeAllItems();
        try(Session s = sf.openSession()) {
            List<Persona> personas = s.createQuery("from Persona where usuario.id = :uid", Persona.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            personas.forEach(personaCmb::addItem);
        }
    }

    @Override
    protected Class<Contacto> getEntityClass() {
        return Contacto.class;
    }

    @Override
    protected void agregarFilaATabla(Contacto c) {
        String persona = c.getPersona() != null ? c.getPersona().getNombreP() : "";
        modelo.addRow(new Object[]{c.getId(), persona, c.getNumCelular(), c.getCorreo()});
    }

    @Override
    public void cargarTabla() {
        try(Session s = sf.openSession()) {
            List<Contacto> lista = s.createQuery(
                            "select c from Contacto c join c.persona p where p.usuario.id = :uid",
                            Contacto.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            refrescarTabla(lista);
        }
    }

    @Override
    public void cargarSeleccion(Contacto c) {
        idSeleccionado = c.getId();
        personaCmb.setSelectedItem(c.getPersona());
        celularTxt.setText(c.getNumCelular());
        correoTxt.setText(c.getCorreo());
    }

    @Override
    public void guardar() {
        Persona per = (Persona) personaCmb.getSelectedItem();
        String cel = celularTxt.getText().trim();
        String cor = correoTxt.getText().trim();

        if (per == null || cel.isEmpty() || cor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dentroDeTransaccion(s -> {
            Contacto c;
            if (idSeleccionado == null) {
                c = new Contacto();
                c.setPersona(per);
            } else {
                c = s.get(Contacto.class, idSeleccionado);
                c.setPersona(per);
            }
            c.setNumCelular(cel);
            c.setCorreo(cor);
            s.persist(c);
        });

        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void eliminar() {
        if (idSeleccionado == null) return;
        dentroDeTransaccion(s -> {
            Contacto c = s.get(Contacto.class, idSeleccionado);
            if (c != null) s.remove(c);
        });
        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void limpiarCampos() {
        celularTxt.setText("");
        correoTxt.setText("");
        idSeleccionado = null;
        cargarPersonas();
    }

    /**
     * Construye visualmente el formulario del panel para captura de datos del contacto.
     * Se utilizan GridBagLayout para un control preciso del posicionamiento de los elementos.
     * Incluye campos de entrada y una botonera con las acciones disponibles.
     *
     * devuelve JPanel totalmente construido con etiquetas, campos de texto y botones.
     */
    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);                      // Margen entre componentes
        gc.anchor = GridBagConstraints.WEST;                    // Alineación a la izquierda
        gc.fill = GridBagConstraints.HORIZONTAL;                // Estira horizontalmente
        gc.weightx = 1;                                         // Usa espacio disponible
        int y = 0;

        // Primera fila: selector de Persona
        gc.gridx = 0; gc.gridy = y;
        p.add(new JLabel("Persona:"), gc);
        gc.gridx = 1;
        p.add(personaCmb, gc); y++;

        // Segunda fila: campo de celular
        gc.gridx = 0; gc.gridy = y;
        p.add(new JLabel("Celular:"), gc);
        gc.gridx = 1;
        p.add(celularTxt, gc); y++;

        // Tercera fila: campo de correo
        gc.gridx = 0; gc.gridy = y;
        p.add(new JLabel("Correo:"), gc);
        gc.gridx = 1;
        p.add(correoTxt, gc); y++;

        // Panel auxiliar para los botones principales
        JPanel btns = new JPanel();

        // Botón que al hacer clic guarda la información del contacto
        JButton g = new JButton("Guardar");
        g.addActionListener(ev -> guardar());

        // Botón que elimina el contacto seleccionado de la tabla y BD
        JButton e = new JButton("Eliminar");
        e.addActionListener(ev -> eliminar());

        // Botón que limpia los campos de entrada del formulario
        JButton l = new JButton("Limpiar");
        l.addActionListener(ev -> limpiarCampos());

        // Agrega los botones al subpanel horizontal
        btns.add(g);
        btns.add(e);
        btns.add(l);

        // Coloca el subpanel de botones en la última fila del formulario
        gc.gridx = 0;
        gc.gridy = y;
        gc.gridwidth = 2;                          // Ocupa ambas columnas
        gc.anchor = GridBagConstraints.CENTER;    // Centra la botonera
        p.add(btns, gc);

        return p;
    }
}