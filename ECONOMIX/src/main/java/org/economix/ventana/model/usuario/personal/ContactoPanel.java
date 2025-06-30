package org.economix.ventana.model.usuario.personal;

import org.economix.model.usuario.Contacto;
import org.economix.model.usuario.Persona;
import org.economix.model.usuario.Usuario;
import org.economix.ventana.vista.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ContactoPanel extends GestorCatalogosSwing<Contacto> {
    private final JComboBox<Persona> personaCmb = new JComboBox<>();
    private final JTextField celularTxt = new JTextField(15);
    private final JTextField correoTxt  = new JTextField(20);

    public ContactoPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario, new String[]{"Persona", "Celular", "Correo"});
        add(construirFormulario(), BorderLayout.EAST);
        cargarPersonas();
        cargarTabla();
    }

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
    protected Class<Contacto> getEntityClass() { return Contacto.class; }

    @Override
    protected void agregarFilaATabla(Contacto c) {
        String persona = c.getPersona()!=null ? c.getPersona().getNombreP() : "";
        modelo.addRow(new Object[]{c.getId(), persona, c.getNumCelular(), c.getCorreo()});
    }

    @Override
    public void cargarTabla() {
        try(Session s = sf.openSession()){
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
        if(per==null || cel.isEmpty() || cor.isEmpty()){
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        dentroDeTransaccion(s -> {
            Contacto c;
            if(idSeleccionado == null){
                c = new Contacto();
                c.setPersona(per);
            }else{
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
        if(idSeleccionado == null) return;
        dentroDeTransaccion(s -> {
            Contacto c = s.get(Contacto.class, idSeleccionado);
            if(c!=null) s.remove(c);
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

    private JPanel construirFormulario(){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets=new Insets(4,4,4,4);
        gc.anchor=GridBagConstraints.WEST;
        gc.fill=GridBagConstraints.HORIZONTAL;
        gc.weightx=1;
        int y=0;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Persona:"),gc);
        gc.gridx=1; p.add(personaCmb,gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Celular:"),gc);
        gc.gridx=1; p.add(celularTxt,gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Correo:"),gc);
        gc.gridx=1; p.add(correoTxt,gc); y++;

        JPanel btns=new JPanel();
        JButton g=new JButton("Guardar");
        JButton e=new JButton("Eliminar");
        JButton l=new JButton("Limpiar");
        g.addActionListener(ev->guardar());
        e.addActionListener(ev->eliminar());
        l.addActionListener(ev->limpiarCampos());
        btns.add(g); btns.add(e); btns.add(l);

        gc.gridx=0; gc.gridy=y; gc.gridwidth=2; gc.anchor=GridBagConstraints.CENTER;
        p.add(btns,gc);
        return p;
    }
}