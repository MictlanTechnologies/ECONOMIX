package org.economix.ventana.model;

import org.economix.model.presupuesto.Presupuesto;
import org.economix.model.usuario.Usuario;
import org.economix.sql.hibernateimpl.presupuesto.PresupuestoHiberImpl;
import org.economix.model.gastos.Gastos;
import org.economix.model.ingresos.Ingresos;
import org.economix.ventana.vista.GestorCatalogosSwing;
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
    private final JComboBox<Gastos>   gastoCmb   = new JComboBox<>();
    private final JComboBox<Ingresos> ingresoCmb = new JComboBox<>();
    private final JSlider limiteSld = new JSlider();
    private final JLabel limiteValLbl = new JLabel("0");
    private final JProgressBar barra      = new JProgressBar(0, 100);
    private final JProgressBar barraTotal = new JProgressBar(0, 100);

    public PresupuestoPanel(SessionFactory sf, Usuario u) {
        super(sf, u, new String[]{"Categoría", "Límite", "Gastado", "%"});
        barra.setStringPainted(true);
        barraTotal.setStringPainted(true);
        cargarCombos();
        add(construirFormulario(), BorderLayout.EAST);
        JPanel barras = new JPanel(new GridLayout(2,1));
        barras.add(barra);
        barras.add(barraTotal);
        add(barras, BorderLayout.SOUTH);
        cargarTabla();
        actualizarBarraTotal();
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
        actualizarBarraTotal();
    }

    @Override
    public void cargarSeleccion(Presupuesto p) {
        idSeleccionado = p.getIdPresupuesto();
        categoriaTxt.setText(p.getCategoria());
        limiteSld.setValue(p.getMontoMaximo().intValue());
        actualizarBarra(p);
    }

    @Override
    public void guardar() {
        String cat = categoriaTxt.getText().trim();
            Gastos gastoSel = (Gastos) gastoCmb.getSelectedItem();
            if (cat.isEmpty() || gastoSel == null) {
                JOptionPane.showMessageDialog(this, "Categoría y gasto requeridos", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BigDecimal max = BigDecimal.valueOf(limiteSld.getValue());
            YearMonth hoy = YearMonth.now();
            final Presupuesto[] saved = new Presupuesto[1];
            dentroDeTransaccion(s -> {
                Presupuesto p;
                if (idSeleccionado == null) {
                    p = new Presupuesto();
                    p.setUsuario(usuario);
                    p.setMontoGastado(gastoSel.getMontoGastos());
                    p.setMes(hoy.getMonthValue());
                    p.setAnio(hoy.getYear());
                } else {
                    p = s.get(Presupuesto.class, idSeleccionado);
                    p.setMontoGastado(gastoSel.getMontoGastos());
                }
                p.setCategoria(cat);
                p.setMontoMaximo(max);
                s.persist(p);
                saved[0] = p;
            });
            if (saved[0] != null) actualizarBarra(saved[0]);
            actualizarBarraTotal();
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
            limiteSld.setValue(0);
            limiteValLbl.setText("0   ");
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

            gc.gridx=0; gc.gridy=y; p.add(new JLabel("Ingreso:"), gc);
            gc.gridx=1; p.add(ingresoCmb, gc); y++;

            gc.gridx=0; gc.gridy=y; p.add(new JLabel("Gasto:"), gc);
            gc.gridx=1; p.add(gastoCmb, gc); y++;

            gc.gridx=0; gc.gridy=y; p.add(new JLabel("Límite:"), gc);
            gc.gridx=1; p.add(limiteSld, gc); y++;
            gc.gridx=1; p.add(limiteValLbl, gc); y++;

            JPanel botones = new JPanel();
            JButton guardarBtn = new JButton("Guardar");
            JButton eliminarBtn = new JButton("Eliminar");
            JButton limpiarBtn  = new JButton("Limpiar");
            JButton ayudaBtn    = new JButton("Ayuda");
            guardarBtn.addActionListener(e -> guardar());
            eliminarBtn.addActionListener(e -> eliminar());
            limpiarBtn.addActionListener(e -> limpiarCampos());
            botones.add(guardarBtn); botones.add(eliminarBtn); botones.add(limpiarBtn); botones.add(ayudaBtn);

            gc.gridx=0; gc.gridy=y; gc.gridwidth=2; gc.anchor=GridBagConstraints.CENTER;
            p.add(botones, gc);

            ayudaBtn.addActionListener(e -> mostrarInfo());
            return p;
        }

        /** Explica cómo fijar límites de gasto y consultar el progreso. */
        private void mostrarInfo() {
            JOptionPane.showMessageDialog(this,
                    "Seleccione la categoría y vincule un gasto registrado con un " +
                            "ingreso.\nEl deslizador representa el monto disponible del " +
                            "ingreso y se descontará con el gasto elegido.\n" +
                            "La primera barra muestra el porcentaje restante de ese " +
                            "ingreso y la segunda refleja el total ingresos vs gastos.",
                    "Ayuda",
                    JOptionPane.INFORMATION_MESSAGE);
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

        private void cargarCombos() {
            gastoCmb.removeAllItems();
            ingresoCmb.removeAllItems();
            try (Session s = sf.openSession()) {
                List<Gastos> gs = s.createQuery(
                                "from Gastos where usuario.id = :uid", Gastos.class)
                        .setParameter("uid", usuario.getId())
                        .list();
                for (Gastos g : gs) gastoCmb.addItem(g);

                List<Ingresos> ins = s.createQuery(
                                "from Ingresos where usuario.id = :uid", Ingresos.class)
                        .setParameter("uid", usuario.getId())
                        .list();
                for (Ingresos i : ins) ingresoCmb.addItem(i);
            }

            ingresoCmb.addActionListener(e -> {
                Ingresos ing = (Ingresos) ingresoCmb.getSelectedItem();
                if (ing != null) {
                    int max = ing.getMontoIngreso().intValue();
                    limiteSld.setMaximum(max);
                    limiteSld.setValue(max);
                }
            });

            gastoCmb.addActionListener(e -> actualizarBarraManual());
            limiteSld.addChangeListener(e -> actualizarBarraManual());
        }
        /** Vuelve a cargar datos de combos y tabla. */
        public void actualizar() {
            cargarCombos();
            cargarTabla();
        }
        private void actualizarBarraManual() {
            Ingresos ing = (Ingresos) ingresoCmb.getSelectedItem();
            Gastos g = (Gastos) gastoCmb.getSelectedItem();
            limiteValLbl.setText(String.valueOf(limiteSld.getValue()));
            if (ing == null || g == null) {
                barra.setValue(0);
                barra.setString("0% usado");
                return;
            }
            BigDecimal max = BigDecimal.valueOf(limiteSld.getValue());
            BigDecimal usado = g.getMontoGastos();
            int pct = usado.divide(max, 2, RoundingMode.HALF_UP)
                    .movePointRight(2).intValue();
            barra.setValue(pct);
            barra.setString(pct + "% usado");
            barra.setForeground(
                    pct < 70 ? Color.GREEN :
                            pct < 90 ? Color.ORANGE : Color.RED);
        }

        private void actualizarBarraTotal() {
            BigDecimal ingTotal;
            BigDecimal gasTotal;
            try (Session s = sf.openSession()) {
                ingTotal = s.createQuery(
                                "select coalesce(sum(i.montoIngreso),0) from Ingresos i " +
                                        "where i.usuario.id = :uid", BigDecimal.class)
                        .setParameter("uid", usuario.getId())
                        .uniqueResult();
                gasTotal = s.createQuery(
                                "select coalesce(sum(g.montoGastos),0) from Gastos g " +
                                        "where g.usuario.id = :uid", BigDecimal.class)
                        .setParameter("uid", usuario.getId())
                        .uniqueResult();
            }

            if (ingTotal.compareTo(BigDecimal.ZERO) == 0) {
                barraTotal.setValue(0);
                barraTotal.setString("Sin ingresos");
                return;
            }

            int pct = gasTotal.divide(ingTotal, 2, RoundingMode.HALF_UP)
                    .movePointRight(2).intValue();
            barraTotal.setValue(pct);
            barraTotal.setString(pct + "% gastado");
            barraTotal.setForeground(
                    pct < 70 ? Color.GREEN :
                            pct < 90 ? Color.ORANGE : Color.RED);
        }

    }