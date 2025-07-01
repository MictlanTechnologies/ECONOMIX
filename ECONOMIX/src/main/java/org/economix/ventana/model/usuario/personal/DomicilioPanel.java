package org.economix.ventana.model.usuario.personal;

// Importación de entidades del modelo, Hibernate y componentes Swing para la interfaz
import org.economix.model.usuario.Domicilio;
import org.economix.model.usuario.Persona;
import org.economix.model.usuario.Usuario;
import org.economix.ventana.vista.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión visual de {@link Domicilio} usando Swing.
 * Permite CRUD completo de domicilios, enlazados a personas asociadas al usuario autenticado.
 */
public class DomicilioPanel extends GestorCatalogosSwing<Domicilio> {

    // ComboBox para seleccionar una persona a la que se asocia el domicilio
    private final JComboBox<Persona> personaCmb = new JComboBox<>();

    // Campos de texto para capturar la dirección del domicilio
    private final JTextField ciudadTxt  = new JTextField(15);
    private final JTextField calleTxt   = new JTextField(15);
    private final JTextField coloniaTxt = new JTextField(15);
    private final JTextField numeroTxt  = new JTextField(10);

    /**
     * Constructor que inicializa el panel, sus componentes y datos.
     * @param sf fábrica de sesiones de Hibernate
     * @param usuario usuario autenticado para filtrar los datos personales
     */
    public DomicilioPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario, new String[]{"Persona", "Ciudad", "Calle", "Colonia", "Número"});
        add(construirFormulario(), BorderLayout.EAST);
        cargarPersonas();
        cargarTabla();
    }

    /**
     * Carga las personas asociadas al usuario actual en el ComboBox.
     */
    public void cargarPersonas(){
        personaCmb.removeAllItems();
        try(Session s = sf.openSession()){
            List<Persona> personas = s.createQuery("from Persona where usuario.id = :uid", Persona.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            personas.forEach(personaCmb::addItem);
        }
    }

    @Override
    protected Class<Domicilio> getEntityClass() { return Domicilio.class; }

    @Override
    protected void agregarFilaATabla(Domicilio d) {
        String persona = d.getPersona()!=null ? d.getPersona().getNombreP() : "";
        modelo.addRow(new Object[]{d.getId(), persona, d.getCiudad(), d.getCalle(), d.getColonia(), d.getNumero()});
    }

    @Override
    public void cargarTabla() {
        try(Session s = sf.openSession()){
            List<Domicilio> lista = s.createQuery(
                            "select d from Domicilio d join d.persona p where p.usuario.id = :uid",
                            Domicilio.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            refrescarTabla(lista);
        }
    }

    @Override
    public void cargarSeleccion(Domicilio d) {
        idSeleccionado = d.getId();
        personaCmb.setSelectedItem(d.getPersona());
        ciudadTxt.setText(d.getCiudad());
        calleTxt.setText(d.getCalle());
        coloniaTxt.setText(d.getColonia());
        numeroTxt.setText(d.getNumero());
    }

    @Override
    public void guardar() {
        Persona per = (Persona) personaCmb.getSelectedItem();
        String ciudad = ciudadTxt.getText().trim();
        String calle  = calleTxt.getText().trim();
        String col    = coloniaTxt.getText().trim();
        String num    = numeroTxt.getText().trim();

        if(per==null || ciudad.isEmpty() || calle.isEmpty() || col.isEmpty() || num.isEmpty()){
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dentroDeTransaccion(s -> {
            Domicilio d;
            if(idSeleccionado == null){
                d = new Domicilio();
                d.setPersona(per);
            }else{
                d = s.get(Domicilio.class, idSeleccionado);
                d.setPersona(per);
            }
            d.setCiudad(ciudad);
            d.setCalle(calle);
            d.setColonia(col);
            d.setNumero(num);
            s.persist(d);
        });

        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void eliminar() {
        if(idSeleccionado == null) return;
        dentroDeTransaccion(s -> {
            Domicilio d = s.get(Domicilio.class, idSeleccionado);
            if(d!=null) s.remove(d);
        });
        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void limpiarCampos() {
        ciudadTxt.setText("");
        calleTxt.setText("");
        coloniaTxt.setText("");
        numeroTxt.setText("");
        idSeleccionado = null;
        cargarPersonas();
    }

    /**
     * Construye el formulario visual de captura de domicilios.
     * Incluye etiquetas, campos de texto y una botonera de acciones (guardar, eliminar, limpiar).
     * @return el panel completamente construido
     */
    private JPanel construirFormulario(){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        int y=0;

        // Campo: Persona asociada
        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Persona:"),gc);
        gc.gridx=1; p.add(personaCmb,gc); y++;

        // Campo: Ciudad
        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Ciudad:"),gc);
        gc.gridx=1; p.add(ciudadTxt,gc); y++;

        // Campo: Calle
        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Calle:"),gc);
        gc.gridx=1; p.add(calleTxt,gc); y++;

        // Campo: Colonia
        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Colonia:"),gc);
        gc.gridx=1; p.add(coloniaTxt,gc); y++;

        // Campo: Número
        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Número:"),gc);
        gc.gridx=1; p.add(numeroTxt,gc); y++;

        // Botonera inferior con acciones principales
        JPanel btns = new JPanel(); // Panel horizontal para agrupar botones

        JButton g = new JButton("Guardar");   // Guarda los datos ingresados
        JButton e = new JButton("Eliminar");  // Elimina el domicilio seleccionado
        JButton l = new JButton("Limpiar");   // Limpia los campos del formulario

        g.addActionListener(ev -> guardar());         // Acción guardar ejecuta método guardar()
        e.addActionListener(ev -> eliminar());        // Acción eliminar ejecuta método eliminar()
        l.addActionListener(ev -> limpiarCampos());   // Acción limpiar limpia todos los campos

        btns.add(g);
        btns.add(e);
        btns.add(l);

        // Se añade la botonera al panel de formulario
        gc.gridx=0; gc.gridy=y; gc.gridwidth=2; gc.anchor=GridBagConstraints.CENTER;
        p.add(btns,gc);

        return p;
    }
}
