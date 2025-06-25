package org.economix.ventana.model.ingresos;

import org.economix.model.ingresos.Ingresos;
import org.economix.model.usuario.Usuario;
import org.economix.ventana.model.GestorCatalogosSwing;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import javax.swing.*;

public class IngresosPanel extends GestorCatalogosSwing<Ingresos> {


    /* ===== Componentes del formulario ===== */
    private final JTextField descripcionTxt = new JTextField(15);
    private final JTextField montoTxt       = new JTextField(10);
    private final JTextField fechaTxt       = new JTextField(10); // yyyy-MM-dd
    private final JTextField periodoTxt     = new JTextField(12);

    public IngresosPanel(SessionFactory sf, Usuario usuario) {
        super(sf, usuario,
                new String[]{"ID", "Descripción", "Monto", "Fecha", "Periodo"});

                add(construirFormulario(), BorderLayout.SOUTH);
        cargarTabla();
    }

        /* ======================================================
         *           Implementación de métodos abstractos
         * ====================================================== */

        @Override protected Class<Ingresos> getEntityClass() { return Ingresos.class; }

        @Override protected void agregarFilaATabla(Ingresos i) {
            modelo.addRow(new Object[]{
                    i.getId(),               // Ajusta getter
                    i.getDescripcionIngreso(),
                    i.getMontoIngreso(),
                    i.getFechaIngresos(),
                    i.getPeriodicidadIngreso()
            });
        }

        @Override public void cargarTabla() {
            try(Session s = sf.openSession()) {
                List<Ingresos> lista = s.createQuery(
                        "from Ingresos where usuario.id = :uid", Ingresos.class)
                                .setParameter("uid", usuario.getId())
                                        .list();
                refrescarTabla(lista);
            }
        }

        @Override public void cargarSeleccion(Ingresos i) {
            idSeleccionado = i.getId();
            descripcionTxt.setText (i.getDescripcionIngreso());
            montoTxt.setText       (i.getMontoIngreso().toPlainString());
            fechaTxt.setText       (i.getFechaIngresos().toString());
            periodoTxt.setText     (i.getPeriodicidadIngreso());
        }

        @Override public void guardar() {
            String desc = descripcionTxt.getText().trim();
            String mon  = montoTxt.getText().trim();
            String fec  = fechaTxt.getText().trim();
            String per  = periodoTxt.getText().trim();

            if(mon.isEmpty()||fec.isEmpty()){
                JOptionPane.showMessageDialog(this,
                        "Monto y fecha son obligatorios",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal monto; Date fecha;
            try{
                monto = new BigDecimal(mon);
                fecha = Date.valueOf(LocalDate.parse(fec));
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,
                        "Monto o fecha con formato inválido",
                        "Error de formato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dentroDeTransaccion(s -> {
                Ingresos ing;
                if(idSeleccionado==null){
                    ing = new Ingresos();
                    ing.setUsuario(usuario);
                }else{
                    ing = s.get(Ingresos.class, idSeleccionado);
                }

                ing.setDescripcionIngreso(desc);
                ing.setMontoIngreso      (monto);
                ing.setFechaIngresos      (fecha);
                ing.setPeriodicidadIngreso    (per);

                s.persist(ing);
            });

            limpiarCampos();
            cargarTabla();
        }

        @Override public void eliminar() {
            if(idSeleccionado==null){
                JOptionPane.showMessageDialog(this,
                        "Selecciona un registro para eliminar",
                        "Sin selección", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el ingreso seleccionado?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if(confirm!=JOptionPane.YES_OPTION) return;

            dentroDeTransaccion(s -> {
                Ingresos ing = s.get(Ingresos.class, idSeleccionado);
                if(ing!=null) s.remove(ing);
            });

            limpiarCampos();
            cargarTabla();
        }

        @Override public void limpiarCampos() {
                    descripcionTxt.setText("");
                            montoTxt.setText("");
                                    fechaTxt.setText("");
                                            periodoTxt.setText("");
                                                    idSeleccionado = null;
        }

        /* ======================================================
         *                   Formulario UI
         * ====================================================== */
        private JPanel construirFormulario(){
            JPanel p = new JPanel(new GridBagLayout());
            var gc = new GridBagConstraints();
            gc.insets  = new Insets(4,4,4,4);
            gc.anchor  = GridBagConstraints.WEST;
            gc.fill    = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1;
            int y=0;

            gc.gridx=0; gc.gridy=y; p.add(new JLabel("Descripción:"), gc);
                    gc.gridx=1; p.add(descripcionTxt, gc); y++;

            gc.gridx=0; gc.gridy=y; p.add(new JLabel("Monto:"), gc);
                    gc.gridx=1; p.add(montoTxt, gc); y++;

            gc.gridx=0; gc.gridy=y; p.add(new JLabel("Fecha (yyyy-MM-dd):"), gc);
                    gc.gridx=1; p.add(fechaTxt, gc); y++;

            gc.gridx=0; gc.gridy=y; p.add(new JLabel("Periodo:"), gc);
                    gc.gridx=1; p.add(periodoTxt, gc); y++;

            JPanel botones = new JPanel();
            JButton guardar = new JButton("Guardar");
                    JButton eliminar= new JButton("Eliminar");
                    JButton limpiar = new JButton("Limpiar");

                    guardar.addActionListener(e->guardar());
            eliminar.addActionListener(e->eliminar());
            limpiar .addActionListener(e->limpiarCampos());

            botones.add(guardar); botones.add(eliminar); botones.add(limpiar);

            gc.gridx=0; gc.gridy=y; gc.gridwidth=2; gc.anchor= GridBagConstraints.CENTER;
            p.add(botones, gc);

            return p;
        }
    }
