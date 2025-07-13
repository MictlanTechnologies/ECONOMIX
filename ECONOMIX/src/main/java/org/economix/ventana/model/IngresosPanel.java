package org.economix.ventana.model;

// Importaciones necesarias para conexión a Hibernate, interfaz gráfica y tipos de datos.
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.CalendarBorderProperties;
import org.economix.model.ingresos.Ingresos;
import org.economix.model.usuario.Usuario;
import org.economix.ventana.vista.GestorCatalogosSwing;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de interfaz gráfica para el registro y gestión de ingresos económicos.
 * Hereda de {@link GestorCatalogosSwing} para aprovechar la lógica genérica de CRUD.
 * Permite registrar ingresos únicos o recurrentes, verlos en una tabla y eliminarlos.
 */
public class IngresosPanel extends GestorCatalogosSwing<Ingresos> {

    /* ======= Campos del formulario de ingreso ======= */
    private final JTextField descripcionTxt = new JTextField(15);
    private final JTextField montoTxt = new JTextField(10);
    private final DatePicker fechaPicker = createDarkDatePicker();
    private final JTextField periodoTxt = new JTextField(12);
    private final JCheckBox recurrenteChk = new JCheckBox("Ingreso recurrente");
    private final DefaultListModel<IngRec> recurrentesModelo = new DefaultListModel<>();
    private final JList<IngRec> recurrentesLista = new JList<>(recurrentesModelo);
    private Runnable cambioListener;

    /** Establece una función que se ejecutará cada vez que se guarde o elimine un ingreso. */
    public void setCambioListener(Runnable r) { this.cambioListener = r; }

    /**
     * Estructura que representa brevemente un ingreso recurrente en la lista lateral.
     */
    private record IngRec(String descripcion, BigDecimal monto, String periodo) {
        @Override public String toString() {
            return descripcion + " (" + monto + ")";
        }
    }

    /**
     * Constructor del panel de ingresos.
     * Crea la UI, carga la tabla desde la base de datos y muestra ingresos recurrentes.
     */
    public IngresosPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario, new String[]{"Descripción", "Monto", "Fecha", "Periodo"});

        // Configura la lista lateral de ingresos recurrentes para que cargue datos en el formulario al hacer doble clic.
        recurrentesLista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recurrentesLista.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var rec = recurrentesLista.getSelectedValue();
                    if (rec != null) {
                        descripcionTxt.setText(rec.descripcion());
                        montoTxt.setText(rec.monto().toPlainString());
                        periodoTxt.setText(rec.periodo());
                        fechaPicker.requestFocus();
                    }
                }
            }
        });

        // Agrega el formulario al panel principal.
        add(construirFormulario(), BorderLayout.EAST);

        // Carga registros de ingresos desde la base de datos y conceptos recurrentes.
        cargarTabla();
        cargarConceptosRecurrentes();
    }

    /* ===============================
     *  Implementación de métodos abstractos
     * =============================== */

    @Override
    protected Class<Ingresos> getEntityClass() {
        return Ingresos.class;
    }

    /** Añade una fila a la tabla visual con los datos del ingreso. */
    @Override
    protected void agregarFilaATabla(Ingresos i) {
        modelo.addRow(new Object[]{
                i.getId(),
                i.getDescripcionIngreso(),
                i.getMontoIngreso(),
                i.getFechaIngresos(),
                i.getPeriodicidadIngreso()
        });
    }

    /** Carga todos los ingresos registrados por el usuario desde la base de datos. */
    @Override
    public void cargarTabla() {
        try (Session s = sf.openSession()) {
            List<Ingresos> lista = s.createQuery(
                            "from Ingresos where usuario.id = :uid", Ingresos.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            refrescarTabla(lista);
        }
    }

    /** Carga los datos de un ingreso seleccionado en el formulario para su edición. */
    @Override
    public void cargarSeleccion(Ingresos i) {
        idSeleccionado = i.getId();
        descripcionTxt.setText(i.getDescripcionIngreso());
        montoTxt.setText(i.getMontoIngreso().toPlainString());
        fechaPicker.setDate(i.getFechaIngresos().toLocalDate());
        periodoTxt.setText(i.getPeriodicidadIngreso());
    }

    /** Guarda o actualiza un ingreso, y si es recurrente, lo guarda como concepto. */
    @Override
    public void guardar() {
        String desc = descripcionTxt.getText().trim();
        String mon  = montoTxt.getText().trim();
        LocalDate fec = fechaPicker.getDate();
        String per  = periodoTxt.getText().trim();

        // Validación mínima
        if (mon.isEmpty() || fec == null) {
            JOptionPane.showMessageDialog(this,
                    "Monto y fecha son obligatorios",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Conversión segura de datos numéricos y fechas
        BigDecimal monto;
        Date fecha;
        try {
            monto = new BigDecimal(mon);
            fecha = Date.valueOf(fec);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Monto o fecha con formato inválido",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registro o actualización en la base de datos
        dentroDeTransaccion(s -> {
            Ingresos ing;
            if (idSeleccionado == null) {
                ing = new Ingresos();
                ing.setUsuario(usuario);
            } else {
                ing = s.get(Ingresos.class, idSeleccionado);
            }

            ing.setDescripcionIngreso(desc);
            ing.setMontoIngreso(monto);
            ing.setFechaIngresos(fecha);
            ing.setPeriodicidadIngreso(per);
            s.persist(ing);

            // Si es recurrente, se guarda como concepto para reutilizar.
            if (recurrenteChk.isSelected()) {
                var ci = new org.economix.model.ingresos.conceptoIngresos();
                ci.setNombreConcepto(desc);
                ci.setDescripcionConcepto(desc);
                ci.setPrecioConcepto(monto);
                ci.setIngresos(ing);
                s.persist(ci);
            }
        });

        if (recurrenteChk.isSelected()) {
            cargarConceptosRecurrentes();
        }

        limpiarCampos();
        cargarTabla();
        if (cambioListener != null) cambioListener.run();
    }

    /** Elimina un ingreso seleccionado, previa confirmación del usuario. */
    @Override
    public void eliminar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un registro para eliminar",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el ingreso seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        dentroDeTransaccion(s -> {
            Ingresos ing = s.get(Ingresos.class, idSeleccionado);
            if (ing != null) s.remove(ing);
        });

        limpiarCampos();
        cargarTabla();
        if (cambioListener != null) cambioListener.run();
    }

    /** Limpia los campos del formulario para preparar un nuevo ingreso. */
    @Override
    public void limpiarCampos() {
        descripcionTxt.setText("");
        montoTxt.setText("");
        fechaPicker.clear();
        periodoTxt.setText("");
        recurrenteChk.setSelected(false);
        idSeleccionado = null;
    }

    /**
     * Carga desde la base los ingresos recurrentes guardados por el usuario
     * y los muestra en la lista lateral para facilitar su reutilización.
     */
    private void cargarConceptosRecurrentes() {
        recurrentesModelo.clear();
        try (Session s = sf.openSession()) {
            List<org.economix.model.ingresos.conceptoIngresos> lista = s.createQuery(
                            "select c from conceptoIngresos c join c.ingresos i where i.usuario.id = :uid",
                            org.economix.model.ingresos.conceptoIngresos.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            for (var c : lista) {
                recurrentesModelo.addElement(new IngRec(
                        c.getNombreConcepto(),
                        c.getPrecioConcepto(),
                        c.getIngresos().getPeriodicidadIngreso()));
            }
        }
    }

    /* ==========================
     *  Construcción del formulario UI
     * ========================== */
    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        int y = 0;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Descripción:"), gc);
        gc.gridx = 1; p.add(descripcionTxt, gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Monto:"), gc);
        gc.gridx = 1; p.add(montoTxt, gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Fecha:"), gc);
        gc.gridx = 1; p.add(fechaPicker, gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Periodo:"), gc);
        gc.gridx = 1; p.add(periodoTxt, gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(recurrenteChk, gc); gc.gridwidth = 2; y++;
        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Ingresos recurrentes:"), gc); y++;
        gc.gridx = 0; gc.gridy = y; p.add(new JScrollPane(recurrentesLista), gc); y++;
        gc.gridwidth = 1;

        JPanel botones = new JPanel();
        JButton guardar = new JButton("Guardar");
        JButton eliminar = new JButton("Eliminar");
        JButton limpiar = new JButton("Limpiar");
        JButton ayudaBtn = new JButton("Ayuda");

        guardar.addActionListener(e -> guardar());
        eliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiarCampos());
        ayudaBtn.addActionListener(e -> mostrarInfo());

        botones.add(guardar);
        botones.add(eliminar);
        botones.add(limpiar);
        botones.add(ayudaBtn);

        gc.gridx = 0; gc.gridy = y; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        p.add(botones, gc);

        return p;
    }

    /** Crea un selector de fecha en modo oscuro con formato yyyy-MM-dd. */
    private static DatePicker createDarkDatePicker() {
        /* ===== 1. Paleta (mismos tonos que tu GUI) ====================== */
        Color bgBase   = new Color(0x2E2E2C);   // fondo principal (casi negro)
        Color bgDark   = bgBase.darker();       // un poco más oscuro
        Color accent   = new Color(0xB6AC94);   // resaltados y bordes
        Color txtLight = new Color(0xF3F1E4);   // texto muy claro
        Color txtMid   = new Color(0xD5D0C3);   // texto secundario

        /* ===== 2. Configuración general del DatePicker ================== */
        DatePickerSettings s = new DatePickerSettings();
        s.setAllowKeyboardEditing(false);
        s.setAllowEmptyDates(true);             // importante para que «Borrar» pueda activarse
        s.setFormatForDatesCommonEra("yyyy-MM-dd");
        s.setFormatForDatesBeforeCommonEra("yyyy-MM-dd");

        /* ===== 3. Colores de FONDO ====================================== */
        s.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel,      Color.BLACK);
        s.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels,    Color.BLACK);
        s.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearNavigationButtons, Color.BLACK);
        s.setColor(DatePickerSettings.DateArea.BackgroundTodayLabel,                Color.BLACK);
        s.setColor(DatePickerSettings.DateArea.BackgroundClearLabel,                Color.BLACK);          // botón Borrar
        s.setColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates,       Color.BLACK);
        s.setColor(DatePickerSettings.DateArea.CalendarBackgroundVetoedDates,       Color.BLACK);
        s.setColor(DatePickerSettings.DateArea.CalendarBackgroundSelectedDate,      accent.darker());

        /* ===== 4. Colores de TEXTO ====================================== */
        s.setColor(DatePickerSettings.DateArea.CalendarTextNormalDates,             txtLight);        // números
        s.setColor(DatePickerSettings.DateArea.CalendarTextWeekdays,                txtLight);        // dom-lun-…
        s.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels,          txtLight);
        s.setColor(DatePickerSettings.DateArea.TextMonthAndYearNavigationButtons,   txtLight);
        s.setColor(DatePickerSettings.DateArea.TextTodayLabel,                      accent);
        s.setColor(DatePickerSettings.DateArea.TextClearLabel,                      accent);          // «Borrar»
        s.setColor(DatePickerSettings.DateArea.CalendarBorderSelectedDate,          accent);
        // -- 1. Fila de cabeceras: mismo fondo oscuro -----------------------
        s.setColor(DatePickerSettings.DateArea.BackgroundTopLeftLabelAboveWeekNumbers, Color.BLACK);

        // -- 2. Color cuando pasas el ratón por encima de cualquier etiqueta
        s.setColor(DatePickerSettings.DateArea.BackgroundCalendarPanelLabelsOnHover, Color.black);

        // -- 3. Desactivar o recolorear TODAS las líneas/bordes azules -------
        s.setBorderPropertiesList(new ArrayList<>());        // sin rejilla interna
        s.setBorderCalendarPopup(BorderFactory.createLineBorder(Color.BLACK));
        s.setBorderPropertiesList(new ArrayList<CalendarBorderProperties>());

        s.setBorderCalendarPopup(BorderFactory.createLineBorder(Color.BLACK)); // marco exterior oscuro
        /* ===== 6. Crear el picker y retocar sus sub-componentes ========= */
        DatePicker picker = new DatePicker(s);

        // Caja de texto
        JTextField tf = picker.getComponentDateTextField();
        tf.setOpaque(true);
        tf.setBackground(bgBase);
        tf.setForeground(txtLight);
        tf.setCaretColor(txtLight);
        tf.setBorder(BorderFactory.createLineBorder(accent));

        // Botón con los tres puntos (toggle)
        JButton toggle = picker.getComponentToggleCalendarButton();
        toggle.setBackground(bgBase);
        toggle.setForeground(txtLight);
        toggle.setBorder(null);

        /* ===== 7. Tipografías coherentes con tu guía de estilo ========== */
        Font title = new Font("Aharoni", Font.BOLD, 18);
        Font body  = new Font("Aptos Mono", Font.PLAIN, 14);

        s.setFontCalendarWeekdayLabels(title);
        s.setFontMonthAndYearMenuLabels(title);
        s.setFontMonthAndYearNavigationButtons(title);
        s.setFontCalendarDateLabels(body);
        tf.setFont(body);

        return picker;
    }
    /** Muestra un mensaje explicativo al usuario sobre cómo usar el formulario de ingresos. */
    private void mostrarInfo() {
        JOptionPane.showMessageDialog(this,
                "Registra aquí tus ingresos.\n" +
                        "Puedes guardar ingresos frecuentes y reutilizarlos desde la lista.",
                "Ayuda",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
