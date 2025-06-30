package org.economix.ventana.model.usuario.personal;

import org.economix.model.usuario.Persona;
import org.economix.model.usuario.Usuario;
import org.economix.ventana.vista.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PersonaPanel extends GestorCatalogosSwing<Persona> {
    private final JTextField nombreTxt   = new JTextField(15);
    private final JTextField paternoTxt  = new JTextField(15);
    private final JTextField maternoTxt  = new JTextField(15);
    private Runnable cambioListener;

    public PersonaPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario, new String[]{"Nombre", "Apellido P", "Apellido M"});
        add(construirFormulario(), BorderLayout.EAST);
        cargarTabla();
    }

    public void setCambioListener(Runnable r) { this.cambioListener = r; }

    @Override
    protected Class<Persona> getEntityClass() { return Persona.class; }

    @Override
    protected void agregarFilaATabla(Persona p) {
        modelo.addRow(new Object[]{p.getId(), p.getNombreP(), p.getApellidoP(), p.getApellidoM()});
    }

    @Override
    public void cargarTabla() {
        try(Session s = sf.openSession()){
            List<Persona> lista = s.createQuery("from Persona where usuario.id = :uid", Persona.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            refrescarTabla(lista);
        }
    }

    @Override
    public void cargarSeleccion(Persona p) {
        idSeleccionado = p.getId();
        nombreTxt.setText(p.getNombreP());
        paternoTxt.setText(p.getApellidoP());
        maternoTxt.setText(p.getApellidoM());
    }

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

    @Override
    public void limpiarCampos() {
        nombreTxt.setText("");
        paternoTxt.setText("");
        maternoTxt.setText("");
        idSeleccionado = null;
    }

    private JPanel construirFormulario(){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        int y = 0;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Nombre:"),gc);
        gc.gridx=1; p.add(nombreTxt,gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Apellido P:"),gc);
        gc.gridx=1; p.add(paternoTxt,gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Apellido M:"),gc);
        gc.gridx=1; p.add(maternoTxt,gc); y++;

        JPanel btns = new JPanel();
        JButton g = new JButton("Guardar");
        JButton e = new JButton("Eliminar");
        JButton l = new JButton("Limpiar");
        g.addActionListener(ev->guardar());
        e.addActionListener(ev->eliminar());
        l.addActionListener(ev->limpiarCampos());
        btns.add(g); btns.add(e); btns.add(l);

        gc.gridx=0; gc.gridy=y; gc.gridwidth=2; gc.anchor=GridBagConstraints.CENTER;
        p.add(btns,gc);

        return p;
    }
}