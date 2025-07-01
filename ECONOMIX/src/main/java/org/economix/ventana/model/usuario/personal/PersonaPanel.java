package org.economix.ventana.model.usuario.personal;

// Importaciones necesarias para manipular entidades de Persona y construir interfaces gráficas
import org.economix.model.usuario.Persona;
import org.economix.model.usuario.Usuario;
import org.economix.ventana.vista.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel gráfico de Swing que permite gestionar la entidad {@link Persona} vinculada a un {@link Usuario}.
 * Se pueden realizar operaciones CRUD (crear, leer, actualizar, eliminar) sobre los registros de personas.
 */
public class PersonaPanel extends GestorCatalogosSwing<Persona> {

    /** Campo para capturar el nombre de la persona. */
    private final JTextField nombreTxt   = new JTextField(15);
    /** Campo para capturar el apellido paterno. */
    private final JTextField paternoTxt  = new JTextField(15);
    /** Campo para capturar el apellido materno. */
    private final JTextField maternoTxt  = new JTextField(15);

    /** Runnable que puede ejecutarse tras guardar o eliminar, útil para refrescar otros componentes. */
    private Runnable cambioListener;

    /**
     * Constructor que configura el panel y carga la información inicial.
     * @param sf fábrica de sesiones de Hibernate.
     * @param usuario usuario actualmente autenticado.
     */
    public PersonaPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario, new String[]{"Nombre", "Apellido P", "Apellido M"});
        add(construirFormulario(), BorderLayout.EAST);
        cargarTabla();
    }

    /** Permite establecer una acción personalizada cuando se realiza un cambio. */
    public void setCambioListener(Runnable r) { this.cambioListener = r; }

    @Override
    protected Class<Persona> getEntityClass() { return Persona.class; }

    @Override
    protected void agregarFilaATabla(Persona p) {
        modelo.addRow(new Object[]{p.getId(), p.getNombreP(), p.getApellidoP(), p.getApellidoM()});
    }

    /**
     * Carga las personas asociadas al usuario autenticado desde la base de datos y las muestra en la tabla.
     */
    @Override
    public void cargarTabla() {
        try(Session s = sf.openSession()){
            List<Persona> lista = s.createQuery("from Persona where usuario.id = :uid", Persona.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            refrescarTabla(lista);
        }
    }

    /**
     * Carga los datos de una persona seleccionada en los campos del formulario.
     * @param p la entidad Persona seleccionada.
     */
    @Override
    public void cargarSeleccion(Persona p) {
        idSeleccionado = p.getId();
        nombreTxt.setText(p.getNombreP());
        paternoTxt.setText(p.getApellidoP());
        maternoTxt.setText(p.getApellidoM());
    }

    /**
     * Guarda una nueva persona o actualiza una existente según el contexto.
     * Valida que todos los campos estén llenos antes de persistir la entidad.
     */
    @Override
    public void guardar() {
        String nom = nombreTxt.getText().trim();
        String pat = paternoTxt.getText().trim();
        String mat = maternoTxt.getText().trim();

        if(nom.isEmpty() || pat.isEmpty() || mat.isEmpty()){
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dentroDeTransaccion(s -> {
            Persona p;
            if(idSeleccionado == null){
                p = new Persona();
                p.setUsuario(usuario);
            }else{
                p = s.get(Persona.class, idSeleccionado);
            }
            p.setNombreP(nom);
            p.setApellidoP(pat);
            p.setApellidoM(mat);
            s.persist(p);
        });

        limpiarCampos();
        cargarTabla();
        if(cambioListener != null) cambioListener.run();
    }

    /**
     * Elimina la persona seleccionada de la base de datos.
     * No hace nada si no hay persona seleccionada.
     */
    @Override
    public void eliminar() {
        if(idSeleccionado == null) return;

        dentroDeTransaccion(s -> {
            Persona p = s.get(Persona.class, idSeleccionado);
            if(p != null) s.remove(p);
        });

        limpiarCampos();
        cargarTabla();
        if(cambioListener != null) cambioListener.run();
    }

    /**
     * Limpia los campos del formulario y deselecciona el registro actual.
     */
    @Override
    public void limpiarCampos() {
        nombreTxt.setText("");
        paternoTxt.setText("");
        maternoTxt.setText("");
        idSeleccionado = null;
    }

    /**
     * Construye visualmente el formulario del panel para captura de datos de Persona.
     * Utiliza GridBagLayout para posicionar los campos con precisión.
     *
     * @return JPanel que contiene los campos de entrada y la botonera de acciones.
     */
    private JPanel construirFormulario(){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);                // Espaciado entre componentes
        gc.anchor = GridBagConstraints.WEST;            // Alineación a la izquierda
        gc.fill = GridBagConstraints.HORIZONTAL;        // Expandir horizontalmente
        gc.weightx = 1;                                 // Ocupar todo el ancho disponible
        int y = 0;

        // Fila 1: Nombre
        gc.gridx=0; gc.gridy=y;
        p.add(new JLabel("Nombre:"),gc);
        gc.gridx=1;
        p.add(nombreTxt,gc);
        y++;

        // Fila 2: Apellido Paterno
        gc.gridx=0; gc.gridy=y;
        p.add(new JLabel("Apellido P:"),gc);
        gc.gridx=1;
        p.add(paternoTxt,gc);
        y++;

        // Fila 3: Apellido Materno
        gc.gridx=0; gc.gridy=y;
        p.add(new JLabel("Apellido M:"),gc);
        gc.gridx=1;
        p.add(maternoTxt,gc);
        y++;

        // Fila 4: Botonera inferior
        JPanel btns = new JPanel();                      // Panel contenedor de botones
        JButton g = new JButton("Guardar");             // Botón para guardar registro
        JButton e = new JButton("Eliminar");            // Botón para eliminar registro
        JButton l = new JButton("Limpiar");             // Botón para limpiar campos

        // Asignar acciones a cada botón
        g.addActionListener(ev->guardar());
        e.addActionListener(ev->eliminar());
        l.addActionListener(ev->limpiarCampos());

        // Agregar botones al panel
        btns.add(g); btns.add(e); btns.add(l);

        // Posicionar panel de botones en el formulario
        gc.gridx=0; gc.gridy=y; gc.gridwidth=2;
        gc.anchor=GridBagConstraints.CENTER;
        p.add(btns,gc);

        return p;
    }
}
