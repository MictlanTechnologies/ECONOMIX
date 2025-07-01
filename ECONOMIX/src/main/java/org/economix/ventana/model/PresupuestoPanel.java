package org.economix.ventana.model;

// Importaciones de clases de modelo y utilidades necesarias
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
 * Panel gráfico para gestionar presupuestos mensuales de un usuario.
 * Permite crear, editar, eliminar presupuestos, así como visualizar el consumo
 * con barras de progreso e interacciones visuales.
 */
public class PresupuestoPanel extends GestorCatalogosSwing<Presupuesto> {

    // Campo de texto para ingresar el nombre de la categoría del presupuesto
    private final JTextField categoriaTxt = new JTextField(15);

    // Combos desplegables para seleccionar un gasto y un ingreso existente
    private final JComboBox<Gastos> gastoCmb   = new JComboBox<>();
    private final JComboBox<Ingresos> ingresoCmb = new JComboBox<>();

    // Deslizador para establecer el límite de presupuesto
    private final JSlider limiteSld = new JSlider();

    // Etiqueta que muestra el valor numérico del deslizador
    private final JLabel limiteValLbl = new JLabel("0");

    // Barras de progreso para visualizar el uso del presupuesto y el total de ingresos vs gastos
    private final JProgressBar barra      = new JProgressBar(0, 100);
    private final JProgressBar barraTotal = new JProgressBar(0, 100);

    // Variables para evitar mostrar advertencias múltiples al repetir porcentajes
    private int ultimoPctCategoria = -1;
    private int ultimoPctTotal     = -1;

    // Permite notificar a otros paneles después de guardar o eliminar
    private Runnable cambioListener;

    /** Registra un callback para ejecutar tras modificaciones */
    public void setCambioListener(Runnable r) { this.cambioListener = r; }

    /**
     * Constructor principal que inicializa el panel de presupuesto.
     * Se configura el layout, se cargan datos y se construye la UI.
     */
    public PresupuestoPanel(SessionFactory sf, Usuario u) {
        super(sf, u, new String[]{"Categoría", "Límite", "Gastado", "%"});
        barra.setStringPainted(true);
        barraTotal.setStringPainted(true);
        cargarCombos(); // Cargar datos en los combos desplegables
        add(construirFormulario(), BorderLayout.EAST); // Lado derecho: formulario
        JPanel barras = new JPanel(new GridLayout(2,1));
        barras.add(barra);       // Barra para categoría actual
        barras.add(barraTotal);  // Barra para resumen total
        add(barras, BorderLayout.SOUTH); // Lado inferior
        cargarTabla();           // Llenar la tabla con presupuestos del mes actual
        actualizarBarraTotal();  // Mostrar el uso total de ingresos
    }

    /**
     * Especifica la clase de entidad que maneja este panel.
     */
    @Override
    protected Class<Presupuesto> getEntityClass() {
        return Presupuesto.class;
    }

    /**
     * Agrega una fila a la tabla visual con los datos del presupuesto dado.
     */
    @Override
    protected void agregarFilaATabla(Presupuesto p) {
        var pct = p.getMontoGastado()
                .divide(p.getMontoMaximo(), 2, RoundingMode.HALF_UP)
                .movePointRight(2).intValue(); // % = (gastado/max) * 100
        modelo.addRow(new Object[]{
                p.getIdPresupuesto(),
                p.getCategoria(),
                p.getMontoMaximo(),
                p.getMontoGastado(),
                pct + "%"
        });
    }

    /**
     * Carga la tabla con los presupuestos del mes actual.
     */
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
            refrescarTabla(lista); // Llenar tabla Swing
        }
        actualizarBarraTotal(); // Recalcular barra total
    }

    /**
     * Carga en los campos del formulario los datos del presupuesto seleccionado.
     */
    @Override
    public void cargarSeleccion(Presupuesto p) {
        idSeleccionado = p.getIdPresupuesto();
        categoriaTxt.setText(p.getCategoria());
        limiteSld.setValue(p.getMontoMaximo().intValue());
        actualizarBarra(p);
    }

    /**
     * Guarda (o actualiza) un presupuesto con base en los datos ingresados por el usuario.
     */
    @Override
    public void guardar() {
        String cat = categoriaTxt.getText().trim();
        Gastos gastoSel = (Gastos) gastoCmb.getSelectedItem();
        if (cat.isEmpty() || gastoSel == null) {
            JOptionPane.showMessageDialog(this, "Categoría y gasto requeridos", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal max = BigDecimal.valueOf(limiteSld.getValue());
        BigDecimal usado = gastoSel.getMontoGastos();

        // Validación: el gasto no puede superar el límite
        if (usado.compareTo(max) > 0) {
            JOptionPane.showMessageDialog(this,
                    "El gasto seleccionado excede el límite elegido",
                    "Límite superado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        YearMonth hoy = YearMonth.now();
        final Presupuesto[] saved = new Presupuesto[1];
        dentroDeTransaccion(s -> {
            Presupuesto p;
            if (idSeleccionado == null) {
                p = new Presupuesto(); // Nuevo
                p.setUsuario(usuario);
                p.setMontoGastado(usado);
                p.setMes(hoy.getMonthValue());
                p.setAnio(hoy.getYear());
            } else {
                p = s.get(Presupuesto.class, idSeleccionado);
                p.setMontoGastado(usado);
            }
            p.setCategoria(cat);
            p.setMontoMaximo(max);
            s.persist(p); // Guardar
            saved[0] = p;
        });

        // Actualizar interfaz
        if (saved[0] != null) actualizarBarra(saved[0]);
        actualizarBarraTotal();
        limpiarCampos();
        cargarTabla();
        if (cambioListener != null) cambioListener.run();
    }

    /**
     * Elimina el presupuesto seleccionado.
     */
    @Override
    public void eliminar() {
        if (idSeleccionado == null) return;
        dentroDeTransaccion(s -> {
            Presupuesto p = s.get(Presupuesto.class, idSeleccionado);
            if (p != null) s.remove(p);
        });
        limpiarCampos();
        cargarTabla();
        if (cambioListener != null) cambioListener.run();
    }

    /**
     * Limpia los campos del formulario.
     */
    @Override
    public void limpiarCampos() {
        categoriaTxt.setText("");
        limiteSld.setValue(0);
        limiteValLbl.setText("0   ");
        idSeleccionado = null;
        barra.setValue(0);
        barra.setString("0% usado");
    }

    /**
     * Construye el panel de formulario con todos los campos y botones.
     */
    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        int y = 0;

        // Campos de entrada
        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Categoría:"), gc);
        gc.gridx=1; p.add(categoriaTxt, gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Ingreso:"), gc);
        gc.gridx=1; p.add(ingresoCmb, gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Gasto:"), gc);
        gc.gridx=1; p.add(gastoCmb, gc); y++;

        gc.gridx=0; gc.gridy=y; p.add(new JLabel("Límite:"), gc);
        gc.gridx=1; p.add(limiteSld, gc); y++;
        gc.gridx=1; p.add(limiteValLbl, gc); y++;

        // Botones
        JPanel botones = new JPanel();
        JButton guardarBtn = new JButton("Guardar");
        JButton eliminarBtn = new JButton("Eliminar");
        JButton limpiarBtn  = new JButton("Limpiar");
        JButton ayudaBtn    = new JButton("Ayuda");

        guardarBtn.addActionListener(e -> guardar());
        eliminarBtn.addActionListener(e -> eliminar());
        limpiarBtn.addActionListener(e -> limpiarCampos());
        ayudaBtn.addActionListener(e -> mostrarInfo());

        botones.add(guardarBtn);
        botones.add(eliminarBtn);
        botones.add(limpiarBtn);
        botones.add(ayudaBtn);

        gc.gridx=0; gc.gridy=y; gc.gridwidth=2; gc.anchor=GridBagConstraints.CENTER;
        p.add(botones, gc);

        return p;
    }

    /**
     * Muestra una ventana de ayuda al usuario explicando cómo funciona este panel.
     */
    private void mostrarInfo() {
        JOptionPane.showMessageDialog(this,
                "Seleccione la categoría y vincule un gasto registrado con un ingreso.\n" +
                        "El deslizador indica cuánto dinero hay disponible y no permite exceder esa cantidad.\n" +
                        "La barra superior muestra qué porcentaje has usado y cambia de color si te acercas al límite.\n" +
                        "La segunda barra muestra el porcentaje total de ingresos usados.",
                "Ayuda",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Actualiza la barra visual de un presupuesto específico.
     */
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

    // Evita registrar múltiples listeners en cada recarga
    private boolean listenersInit = false;

    /**
     * Carga los gastos e ingresos del usuario en los combos desplegables.
     * También configura listeners para actualizar el límite según el ingreso.
     */
    private void cargarCombos() {
        gastoCmb.removeAllItems();
        ingresoCmb.removeAllItems();
        try (Session s = sf.openSession()) {
            List<Gastos> gs = s.createQuery("from Gastos where usuario.id = :uid", Gastos.class)
                    .setParameter("uid", usuario.getId()).list();
            for (Gastos g : gs) gastoCmb.addItem(g);

            List<Ingresos> ins = s.createQuery("from Ingresos where usuario.id = :uid", Ingresos.class)
                    .setParameter("uid", usuario.getId()).list();
            for (Ingresos i : ins) ingresoCmb.addItem(i);
        }

        if (!listenersInit) {
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
            listenersInit = true;
        }
    }

    /**
     * Refresca los combos y la tabla, usado al volver al panel.
     */
    public void actualizar() {
        cargarCombos();
        cargarTabla();
    }

    /**
     * Actualiza la barra de progreso manualmente según los valores seleccionados.
     */
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

    /**
     * Calcula el porcentaje de ingresos gastados en total y actualiza la barra inferior.
     */
    private void actualizarBarraTotal() {
        BigDecimal ingTotal;
        BigDecimal gasTotal;
        try (Session s = sf.openSession()) {
            ingTotal = s.createQuery(
                            "select coalesce(sum(i.montoIngreso),0) from Ingresos i where i.usuario.id = :uid", BigDecimal.class)
                    .setParameter("uid", usuario.getId()).uniqueResult();
            gasTotal = s.createQuery(
                            "select coalesce(sum(g.montoGastos),0) from Gastos g where g.usuario.id = :uid", BigDecimal.class)
                    .setParameter("uid", usuario.getId()).uniqueResult();
        }

        if (ingTotal.compareTo(BigDecimal.ZERO) == 0) {
            barraTotal.setValue(0);
            barraTotal.setString("Sin ingresos");
            return;
        }

        int pct = gasTotal.divide(ingTotal, 2, RoundingMode.HALF_UP).movePointRight(2).intValue();
        barraTotal.setValue(pct);
        barraTotal.setString(pct + "% gastado");
        barraTotal.setForeground(
                pct < 70 ? Color.GREEN :
                        pct < 90 ? Color.ORANGE : Color.RED);
    }
}
