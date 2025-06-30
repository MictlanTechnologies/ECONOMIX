package org.economix.ventana.model.gastos;

import org.economix.model.gastos.Gastos;
import org.economix.model.usuario.Usuario;
import org.economix.ventana.model.GestorCatalogosSwing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.economix.sql.hibernateimpl.presupuesto.PresupuestoHiberImpl;
import org.economix.model.presupuesto.Presupuesto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel CRUD de Gastos reutilizando GestorCatalogosSwing.
 */
public class GastosPanel extends GestorCatalogosSwing<Gastos> {

    // Campos del formulario
    private final JTextField articuloTxt = new JTextField(15);
    private final JTextField descripcionTxt = new JTextField(15);
    private final JTextField montoTxt = new JTextField(10);
    private final JTextField fechaTxt = new JTextField(10); // yyyy-MM-dd
    private final JTextField periodoTxt = new JTextField(12);

    private final JCheckBox recurrenteChk = new JCheckBox("Gasto recurrente");
    private final DefaultListModel<GastoRec> recurrentesModelo = new DefaultListModel<>();
    private final JList<GastoRec> recurrentesLista = new JList<>(recurrentesModelo);

    private record GastoRec(String articulo, String descripcion,
                            BigDecimal monto, String periodo) {
        @Override public String toString() {
            return articulo + " (" + monto + ")";
        }
    }

    public GastosPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario,
                new String[]{"Artículo", "Descripción", "Monto", "Fecha", "Periodo"});

        recurrentesLista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recurrentesLista.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var info = recurrentesLista.getSelectedValue();
                    if (info != null) {
                        articuloTxt.setText(info.articulo());
                        descripcionTxt.setText(info.descripcion());
                        montoTxt.setText(info.monto().toPlainString());
                        periodoTxt.setText(info.periodo());
                        fechaTxt.requestFocus();
                    }
                }
            }
        });
        add(construirFormulario(), BorderLayout.EAST);
        cargarTabla();
        cargarConceptosRecurrentes();
}
    /* =====================================================
     *          Implementación de métodos abstractos
     * ===================================================== */

    @Override
    protected Class<Gastos> getEntityClass() {
        return Gastos.class;
    }

    @Override
    protected void agregarFilaATabla(Gastos g) {
        modelo.addRow(new Object[]{
                g.getId(),
                g.getArticuloGasto(),
                g.getDescripcionGastos(),
                g.getMontoGastos(),
                g.getFechaGastos(),
                g.getPeriodoGastos()
        });
    }

    @Override
    public void cargarTabla() {
        try (Session s = sf.openSession()) {
            List<Gastos> lista = s.createQuery(
                            "from Gastos where usuario.id = :uid", Gastos.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            refrescarTabla(lista);
        }
    }

    @Override
    public void cargarSeleccion(Gastos g) {
        if (g == null) return;
        idSeleccionado = g.getId();
        articuloTxt.setText   (g.getArticuloGasto());
        descripcionTxt.setText(g.getDescripcionGastos());
        montoTxt.setText      (g.getMontoGastos().toPlainString());
        fechaTxt.setText      (g.getFechaGastos().toString());
        periodoTxt.setText    (g.getPeriodoGastos());
    }

    @Override
    public void guardar() {
        String art = articuloTxt.getText().trim();
        String desc = descripcionTxt.getText().trim();
        String mon = montoTxt.getText().trim();
        String fec = fechaTxt.getText().trim();
        String per = periodoTxt.getText().trim();

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

        final Gastos[] saved = new Gastos[1];
        dentroDeTransaccion(s -> {
            Gastos g;
            if (idSeleccionado == null) {
                g = new Gastos();
                g.setUsuario(usuario);
            } else {
                g = s.get(Gastos.class, idSeleccionado);
            }
            g.setArticuloGasto(art);
            g.setDescripcionGastos(desc);
            g.setMontoGastos(monto);
            g.setFechaGastos(fecha);
            g.setPeriodoGastos(per);
            s.persist(g);
            saved[0] = g;

            if (recurrenteChk.isSelected()) {
                var cg = new org.economix.model.gastos.conceptoGastos();
                cg.setNombreConcepto(art);
                cg.setDescripcionConcepto(desc);
                cg.setPrecioConcepto(monto);
                cg.setGastos(g);
                s.persist(cg);
            }
        });

        if (saved[0] != null) {
            registrarGasto(saved[0]);
        }

        if (recurrenteChk.isSelected()) {
            cargarConceptosRecurrentes();
        }

        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void eliminar() {
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

        dentroDeTransaccion(s -> {
            Gastos g = s.get(Gastos.class, idSeleccionado);
            if (g != null) s.remove(g);
        });

        limpiarCampos();
        cargarTabla();
    }

    @Override
    public void limpiarCampos() {
        articuloTxt.setText("");
        descripcionTxt.setText("");
        montoTxt.setText("");
        fechaTxt.setText("");
        periodoTxt.setText("");
        recurrenteChk.setSelected(false);
        idSeleccionado = null;
    }

    /**
     * Carga de la base de datos los gastos marcados como recurrentes
     * por el usuario y los muestra en la lista lateral.
     */
    private void cargarConceptosRecurrentes(){
        recurrentesModelo.clear();
        try(Session s = sf.openSession()){
            List<org.economix.model.gastos.conceptoGastos> lista = s.createQuery(
                            "select c from conceptoGastos c join c.gastos g " +
                                    "where g.usuario.id = :uid",
                            org.economix.model.gastos.conceptoGastos.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            for(var c : lista){
                recurrentesModelo.addElement(new GastoRec(
                        c.getNombreConcepto(),
                        c.getDescripcionConcepto(),
                        c.getPrecioConcepto(),
                        c.getGastos().getPeriodoGastos()));
            }
        }
    }

    /**
     * Actualiza el presupuesto asociado al gasto y muestra alerta si supera el 80%.
     */
    private void registrarGasto(Gastos g) {
        var ph  = PresupuestoHiberImpl.get();
        var hoy = g.getFechaGastos().toLocalDate();
        Presupuesto pres = ph.buscarActivo(
                g.getArticuloGasto(),
                usuario,
                hoy.getMonthValue(),
                hoy.getYear());

        if (pres != null) {
            ph.agregarGasto(pres, g.getMontoGastos());

            BigDecimal pct = pres.getMontoGastado()
                    .divide(pres.getMontoMaximo(), 2, RoundingMode.HALF_UP);

            if (pct.compareTo(new BigDecimal("0.80")) >= 0) {
                JOptionPane.showMessageDialog(this,
                        "¡Ojo! Has usado el " +
                                pct.movePointRight(2).intValue() + "% de tu presupuesto de " +
                                pres.getCategoria(),
                        "Alerta", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    /* =====================================================
     *                    UI auxiliar
     * ===================================================== */

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(4,4,4,4);
        gc.anchor  = GridBagConstraints.WEST;
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
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

        gc.gridx = 0; gc.gridy = y; p.add(recurrenteChk, gc); gc.gridwidth = 2; y++;
        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Gastos recurrentes:"), gc); y++;
        gc.gridx = 0; gc.gridy = y; p.add(new JScrollPane(recurrentesLista), gc); y++;
        gc.gridwidth = 1;

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton guardarBtn = new JButton("Guardar");
        JButton eliminarBtn = new JButton("Eliminar");
        JButton limpiarBtn  = new JButton("Limpiar");

        guardarBtn.addActionListener(e -> guardar());
        eliminarBtn.addActionListener(e -> eliminar());
        limpiarBtn.addActionListener(e -> limpiarCampos());

        botones.add(guardarBtn);
        botones.add(eliminarBtn);
        botones.add(limpiarBtn);

        gc.gridx = 0; gc.gridy = y; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        p.add(botones, gc);

        return p;
    }
}
