package org.economix.vista.ventana;

import org.economix.model.gastos.Gastos;
import org.economix.model.usuario.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel CRUD de Gastos filtrado por el usuario autenticado.
 */
class GastosPanel extends JPanel {

    /* ----------  Atributos ---------- */
    private final SessionFactory sf;
    private final Usuario usuario;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Artículo", "Descripción", "Monto", "Fecha", "Periodo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JTextField articuloTxt    = new JTextField(15);
    private final JTextField descripcionTxt = new JTextField(15);
    private final JTextField montoTxt       = new JTextField(10);
    private final JTextField fechaTxt       = new JTextField(10); // yyyy-MM-dd
    private final JTextField periodoTxt     = new JTextField(12);

    private Long idSeleccionado = null;     // null = insertar; distinto de null = editar

    /* ----------  Constructor ---------- */
    GastosPanel(SessionFactory sf, Usuario usuario) {
        this.sf      = sf;
        this.usuario = usuario;

        setLayout(new BorderLayout(5, 5));

        /* ---- Tabla ---- */
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) cargarSeleccion();
            }
        });

        /* ---- Formulario ---- */
        add(construirFormulario(), BorderLayout.SOUTH);

        /* ---- Datos iniciales ---- */
        cargarTabla();
    }

    /* ======================================================
     *             Métodos de Carga / Selección
     * ====================================================== */

    /** Llena la JTable con los gastos del usuario. */
    private void cargarTabla() {
        modelo.setRowCount(0);
        try (Session s = sf.openSession()) {
            List<Gastos> lista = s.createQuery(
                            "from Gastos where usuario.id = :uid", Gastos.class)
                    .setParameter("uid", usuario.getId())
                    .list();

            lista.forEach(g -> modelo.addRow(new Object[]{
                    g.getId(),
                    g.getArticuloGasto(),
                    g.getDescripcionGastos(),
                    g.getMontoGastos(),
                    g.getFechaGastos(),
                    g.getPeriodoGastos()
            }));
        }
    }

    /** Carga en el formulario los datos de la fila seleccionada (doble-clic). */
    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        idSeleccionado = (Long) modelo.getValueAt(fila, 0);
        articuloTxt.setText   (modelo.getValueAt(fila, 1).toString());
        descripcionTxt.setText(modelo.getValueAt(fila, 2).toString());
        montoTxt.setText      (modelo.getValueAt(fila, 3).toString());
        fechaTxt.setText      (modelo.getValueAt(fila, 4).toString());
        periodoTxt.setText    (modelo.getValueAt(fila, 5).toString());
    }

    /* ======================================================
     *             Métodos CRUD (Guardar / Eliminar)
     * ====================================================== */

    /** Inserta o actualiza el gasto según `idSeleccionado`. */
    private void guardarGasto() {
        String art  = articuloTxt.getText().trim();
        String desc = descripcionTxt.getText().trim();
        String mon  = montoTxt.getText().trim();
        String fec  = fechaTxt.getText().trim();
        String per  = periodoTxt.getText().trim();

        // Validaciones básicas
        if (art.isEmpty() || mon.isEmpty() || fec.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Artículo, monto y fecha son obligatorios",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal monto;
        Date fecha;
        try {
            monto = new BigDecimal(mon);
            fecha = Date.valueOf(LocalDate.parse(fec));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Monto o fecha con formato inválido",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();

            Gastos g;
            if (idSeleccionado == null) {
                g = new Gastos();
                g.setUsuario(usuario);
            } else {
                g = s.get(Gastos.class, idSeleccionado);
            }

            g.setArticuloGasto    (art);
            g.setDescripcionGastos (desc);
            g.setMontoGastos     (monto);
            g.setFechaGastos      (fecha);
            g.setPeriodoGastos    (per);

            s.persist(g);
            tx.commit();
        }

        limpiarCampos();
        cargarTabla();
    }

    /** Elimina el gasto seleccionado en la tabla. */
    private void eliminarGasto() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un registro para eliminar",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el gasto seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            Gastos g = s.get(Gastos.class, idSeleccionado);
            if (g != null) s.remove(g);
            tx.commit();
        }

        limpiarCampos();
        cargarTabla();
    }

    /* ======================================================
     *             Métodos de Utilidad UI
     * ====================================================== */

    /** Limpia campos y reinicia `idSeleccionado`. */
    private void limpiarCampos() {
        articuloTxt.setText("");
        descripcionTxt.setText("");
        montoTxt.setText("");
        fechaTxt.setText("");
        periodoTxt.setText("");
        idSeleccionado = null;
    }

    /** Construye el formulario con GridBagLayout y agrega listeners. */
    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets     = new Insets(2, 2, 2, 2);
        gc.anchor     = GridBagConstraints.WEST;
        gc.fill       = GridBagConstraints.HORIZONTAL;
        gc.weightx    = 1;

        int y = 0;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Artículo:"), gc);
        gc.gridx = 1; p.add(articuloTxt, gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Descripción:"), gc);
        gc.gridx = 1; p.add(descripcionTxt, gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Monto:"), gc);
        gc.gridx = 1; p.add(montoTxt, gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Fecha (yyyy-MM-dd):"), gc);
        gc.gridx = 1; p.add(fechaTxt, gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Periodo:"), gc);
        gc.gridx = 1; p.add(periodoTxt, gc); y++;

        /* ----- Botones ----- */
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton guardarBtn = new JButton("Guardar");
        JButton eliminarBtn = new JButton("Eliminar");
        JButton limpiarBtn  = new JButton("Limpiar");

        guardarBtn.addActionListener(e -> guardarGasto());
        eliminarBtn.addActionListener(e -> eliminarGasto());
        limpiarBtn .addActionListener(e -> limpiarCampos());

        botones.add(guardarBtn);
        botones.add(eliminarBtn);
        botones.add(limpiarBtn);

        gc.gridx = 0; gc.gridy = y; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        p.add(botones, gc);

        return p;
    }
}

