package org.economix.ventana.model.presupuesto;

import org.economix.model.presupuesto.Presupuesto;
import org.economix.model.usuario.Usuario;
import org.economix.sql.hibernateimpl.presupuesto.PresupuestoHiberImpl;
import org.economix.ventana.model.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

/**
 * Panel para gestionar presupuestos mensuales.
 */
public class PresupuestoPanel extends GestorCatalogosSwing<Presupuesto> {

    private final JTextField categoriaTxt = new JTextField(15);
    private final JTextField limiteTxt    = new JTextField(10);
    private final JProgressBar barra      = new JProgressBar(0, 100);

    private final PresupuestoHiberImpl dao = PresupuestoHiberImpl.get();

    public PresupuestoPanel(SessionFactory sf, Usuario u) {
        super(sf, u, new String[]{"Categoría", "Límite", "Gastado", "%"});
        barra.setStringPainted(true);
        add(construirFormulario(), BorderLayout.EAST);
        add(barra, BorderLayout.SOUTH);
        cargarTabla();
    }

    @Override
    protected Class<Presupuesto> getEntityClass() { return Presupuesto.class; }

    @Override
    protected void agregarFilaATabla(Presupuesto p) {
        var pct = p.getMontoGastado()
                .divide(p.getMontoMaximo(), 2, RoundingMode.HALF_UP)
                .movePointRight(2).intValue();
        modelo.addRow(new Object[]{
                p.getIdPresupuesto(),
                p.getCategoria(),
                p.getMontoMaximo(),
                p.getMontoGastado(),
                pct + "%"
        });
    }

    @Override
    public void cargarTabla() {
        YearMonth hoy = YearMonth.now();
        try (Session s = sf.openSession()) {
            List<Presupuesto> lista = s.createQuery(
                            "from Presupuesto p where p.usuario.id = :uid" +
                                    " and p.mes = :m and p.anio = :a", Presupuesto.class)
                    .setParameter("uid", usuario.getId())
                    .setParameter("m", hoy.getMonthValue())
                    .setParameter("a", hoy.getYear())
                    .list();
            refrescarTabla(lista);
        }
    }

    @Override
    public void cargarSeleccion(Presupuesto p) {
        idSeleccionado = p.getIdPresupuesto();
        categoriaTxt.setText(p.getCategoria());
        limiteTxt.setText(p.getMontoMaximo().toPlainString());
        actualizarBarra(p);
    }

    @Override
    public void guardar() {
        String cat = categoriaTxt.getText().trim();
        String lim = limiteTxt.getText().trim();
        if (cat.isEmpty() || lim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Categoría y límite obligatorios", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal max;
        try { max = new BigDecimal(lim); } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Límite inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        YearMonth hoy = YearMonth.now();
        final Presupuesto[] saved = new Presupuesto[1];
        dentroDeTransaccion(s -> {
            Presupuesto p;
            if (idSeleccionado == null) {
                p = new Presupuesto();
                p.setUsuario(usuario);
                p.setMontoGastado(BigDecimal.ZERO);
                p.setMes(hoy.getMonthValue());
                p.setAnio(hoy.getYear());
            } else {
                p = s.get(Presupuesto.class, idSeleccionado);
            }
            p.setCategoria(cat);
            p.setMontoMaximo(max);
            s.persist(p);
            saved[0] = p;
        });
        if (saved[0] != null) actualizarBarra(saved[0]);
        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void eliminar() {
        if (idSeleccionado == null) return;
        dentroDeTransaccion(s -> {
            Presupuesto p = s.get(Presupuesto.class, idSeleccionado);
            if (p != null) s.remove(p);
        });
        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void limpiarCampos() {
        categoriaTxt.setText("");
        limiteTxt.setText("");
        idSeleccionado = null;
        barra.setValue(0);
        barra.setString("0% usado");
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        int y=0;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Categoría:"), gc);
        gc.gridx=1; p.add(categoriaTxt, gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Límite:"), gc);
        gc.gridx=1; p.add(limiteTxt, gc); y++;

        JPanel botones = new JPanel();
        JButton guardarBtn = new JButton("Guardar");
        JButton eliminarBtn = new JButton("Eliminar");
        JButton limpiarBtn  = new JButton("Limpiar");
        guardarBtn.addActionListener(e -> guardar());
        eliminarBtn.addActionListener(e -> eliminar());
        limpiarBtn.addActionListener(e -> limpiarCampos());
        botones.add(guardarBtn); botones.add(eliminarBtn); botones.add(limpiarBtn);

        gc.gridx=0; gc.gridy=y; gc.gridwidth=2; gc.anchor=GridBagConstraints.CENTER;
        p.add(botones, gc);
        return p;
    }

    private void actualizarBarra(Presupuesto p) {
        int pct = p.getMontoGastado()
                .divide(p.getMontoMaximo(), 2, RoundingMode.HALF_UP)
                .movePointRight(2).intValue();
        barra.setValue(pct);
        barra.setString(pct + "% usado");
        barra.setForeground(
                pct < 70 ? Color.GREEN :
                        pct < 90 ? Color.ORANGE : Color.RED);
    }
}