package org.economix.ventana.model;

import org.economix.model.ahorro.Ahorro;
import org.economix.model.ingresos.Ingresos;
import org.economix.model.usuario.Usuario;
import org.economix.ventana.vista.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Panel para registrar objetivos de ahorro y su progreso.
 */
public class AhorroPanel extends GestorCatalogosSwing<Ahorro> {

    private final JTextField nombreTxt = new JTextField(15);
    private final JTextField descripcionTxt = new JTextField(15);
    private final JTextField metaTxt = new JTextField(10);
    private final JComboBox<Ingresos> ahorradoCmb = new JComboBox<>();
    private final JProgressBar barra = new JProgressBar(0, 100);

    // Permite notificar a otros paneles tras vincular un ingreso
    private Runnable cambioListener;

    /** Registra un callback a ejecutar cuando se modifiquen los ingresos */
    public void setCambioListener(Runnable r) { this.cambioListener = r; }

    public AhorroPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario, new String[]{"Objetivo", "Meta", "Ahorrado", "%"});
        barra.setStringPainted(true);
        cargarIngresos();
        add(construirFormulario(), BorderLayout.EAST);
        cargarTabla();
    }

    @Override
    protected Class<Ahorro> getEntityClass() { return Ahorro.class; }

    @Override
    protected void agregarFilaATabla(Ahorro a) {
        int pct = a.getMeta().compareTo(BigDecimal.ZERO) == 0 ? 0 :
                a.getMontoAhorrado().divide(a.getMeta(), 2, RoundingMode.HALF_UP)
                        .movePointRight(2).intValue();
        modelo.addRow(new Object[]{
                a.getId(), a.getNombreObjetivo(), a.getMeta(),
                a.getMontoAhorrado(), pct + "%"
        });
    }

    @Override
    public void cargarTabla() {
        try (Session s = sf.openSession()) {
            List<Ahorro> lista = s.createQuery(
                            "from Ahorro where usuario.id = :uid", Ahorro.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            refrescarTabla(lista);
        }
    }

    @Override
    public void cargarSeleccion(Ahorro a) {
        idSeleccionado = a.getId();
        nombreTxt.setText(a.getNombreObjetivo());
        descripcionTxt.setText(a.getDescripcionObjetivo());
        metaTxt.setText(a.getMeta().toPlainString());
        actualizarBarra(a);
    }

    @Override
    public void guardar() {
        String nom = nombreTxt.getText().trim();
        String des = descripcionTxt.getText().trim();
        String met = metaTxt.getText().trim();
        if (nom.isEmpty() || met.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre y meta son obligatorios",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal metaVal;
        try {
            metaVal = new BigDecimal(met);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato numérico inválido",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final Ahorro[] saved = new Ahorro[1];
        dentroDeTransaccion(s -> {
            Ahorro a;
            if (idSeleccionado == null) {
                a = new Ahorro();
                a.setUsuario(usuario);
                a.setMontoAhorrado(BigDecimal.ZERO);
            } else {
                a = s.get(Ahorro.class, idSeleccionado);
            }
            a.setNombreObjetivo(nom);
            a.setDescripcionObjetivo(des);
            a.setMeta(metaVal);
            s.persist(a);
            saved[0] = a;
        });
        if (saved[0] != null) {
            actualizarBarra(saved[0]);
            if (saved[0].getMontoAhorrado().compareTo(saved[0].getMeta()) >= 0) {
                JOptionPane.showMessageDialog(this,
                        "¡Felicidades cumpliste tu objetivo, ya puedes comprar " +
                                saved[0].getNombreObjetivo() + "!",
                        "Objetivo logrado", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void eliminar() {
        if (idSeleccionado == null) return;
        dentroDeTransaccion(s -> {
            Ahorro a = s.get(Ahorro.class, idSeleccionado);
            if (a != null) s.remove(a);
        });
        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void limpiarCampos() {
        nombreTxt.setText("");
        descripcionTxt.setText("");
        metaTxt.setText("");
        idSeleccionado = null;
        barra.setValue(0);
        barra.setString("0% ahorrado");
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        int y = 0;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Objetivo:"), gc);
        gc.gridx=1; p.add(nombreTxt, gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Descripción:"), gc);
        gc.gridx=1; p.add(descripcionTxt, gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Meta:"), gc);
        gc.gridx=1; p.add(metaTxt, gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Ahorrado:"), gc);
        gc.gridx=1; p.add(ahorradoCmb, gc); y++;

        gc.gridx=0; gc.gridy=y; gc.gridwidth=2; p.add(barra, gc); y++;
        gc.gridwidth=1;

        JPanel botones = new JPanel();
        JButton guardar = new JButton("Guardar");
        JButton eliminar = new JButton("Eliminar");
        JButton limpiar = new JButton("Limpiar");
        JButton vincular = new JButton("Vincular");
        JButton ayudaBtn = new JButton("Ayuda");

        guardar.addActionListener(e -> guardar());
        eliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiarCampos());
        vincular.addActionListener(e -> vincularIngreso());
        ayudaBtn.addActionListener(e -> mostrarInfo());

        botones.add(guardar);
        botones.add(eliminar);
        botones.add(limpiar);
        botones.add(vincular);
        botones.add(ayudaBtn);
        gc.gridx=0; gc.gridy=y; gc.gridwidth=2; gc.anchor=GridBagConstraints.CENTER;
        p.add(botones, gc);
        return p;
    }

    private void cargarIngresos() {
        ahorradoCmb.removeAllItems();
        try (Session s = sf.openSession()) {
            List<Ingresos> lista = s.createQuery(
                            "from Ingresos where usuario.id = :uid", Ingresos.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            for (Ingresos i : lista) ahorradoCmb.addItem(i);
        }
    }

    private void vincularIngreso() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un objetivo en la tabla",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Ingresos ingreso = (Ingresos) ahorradoCmb.getSelectedItem();
        if (ingreso == null) return;

        dentroDeTransaccion(s -> {
            Ahorro a = s.get(Ahorro.class, idSeleccionado);
            Ingresos ing = s.get(Ingresos.class, ingreso.getId());
            if (a != null && ing != null) {
                a.setMontoAhorrado(a.getMontoAhorrado().add(ing.getMontoIngreso()));
                ing.setMontoIngreso(BigDecimal.ZERO);
                s.persist(a);
                s.persist(ing);
                actualizarBarra(a);
            }
        });
        cargarIngresos();
        cargarTabla();
        if (cambioListener != null) cambioListener.run();
    }

    /** Refresca combo y tabla, usado al volver a la sección. */
    public void actualizar() {
        cargarIngresos();
        cargarTabla();
    }

    private void actualizarBarra(Ahorro a) {
        int pct = a.getMeta().compareTo(BigDecimal.ZERO)==0 ? 0 :
                a.getMontoAhorrado().divide(a.getMeta(),2, RoundingMode.HALF_UP)
                        .movePointRight(2).intValue();
        barra.setValue(pct);
        barra.setString(pct + "% ahorrado");
    }

    /** Muestra instrucciones básicas de uso del panel. */
    private void mostrarInfo() {
        JOptionPane.showMessageDialog(this,
                "Selecciona un objetivo, elige un ingreso y presiona Vincular " +
                        "para sumarlo al monto ahorrado.",
                "Ayuda",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
