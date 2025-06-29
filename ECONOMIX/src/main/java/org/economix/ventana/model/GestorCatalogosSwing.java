package org.economix.ventana.model;

import org.economix.model.usuario.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Plantilla reutilizable para construir catálogos CRUD con Hibernate + Swing.
 * Usa el patrón *Template Method*: la lógica repetitiva está aquí y los detalles
 * específicos se delegan a los métodos abstractos que sobrecarga cada sub‑clase.
 *
 * @param <T> Tipo de la entidad.
 */
public abstract class GestorCatalogosSwing<T> extends JPanel implements GenericSwing<T> {

    /* =====================================================
     *                   Atributos base
     * ===================================================== */
    protected final SessionFactory sf;
    protected final Usuario        usuario;
    protected final DefaultTableModel modelo;
    protected final JTable         tabla;

    /** ID del registro seleccionado (puede ser null). */
    protected Number idSeleccionado = null;

    protected GestorCatalogosSwing(SessionFactory sf,
                                   Usuario usuario,
                                   String[] columnNames) {
        this.sf      = sf;
        this.usuario = usuario;
        // Inserta la columna ID al inicio para uso interno y la oculta de la vista
        String[] cols = new String[columnNames.length + 1];
        cols[0] = "ID";
        System.arraycopy(columnNames, 0, cols, 1, columnNames.length);

        this.modelo  = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        this.tabla   = new JTable(modelo);
        // Ocultar la columna de la clave primaria
        tabla.getColumnModel().removeColumn(tabla.getColumnModel().getColumn(0));
        setLayout(new BorderLayout(5,5));
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        /* Listener para doble‑clic y selección */
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2) onRowDoubleClick();
            }
        });
    }

    /* =====================================================
     *            Implementación de operaciones comunes
     * ===================================================== */

    /** Acción por defecto al hacer doble‑clic en una fila. */
    private void onRowDoubleClick() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        Object idCell = modelo.getValueAt(fila, 0);
        if (!(idCell instanceof Number)) return;
        idSeleccionado = (Number) idCell;
        T entity = obtenerEntidadPorId(idSeleccionado);
        if(entity != null) cargarSeleccion(entity);
    }

    /** Recupera la entidad dada su clave primaria. */
    protected T obtenerEntidadPorId(Number id) {
        try(Session s = sf.openSession()) {
            // cast seguro – subclase indica su Class<T>
            return s.get(getEntityClass(), id);
        }
    }

    /** Proporciona la clase concreta de la entidad – lo define la sub‑clase. */
    protected abstract Class<T> getEntityClass();

    /* ====== Métodos utilitarios que pueden usar las sub‑clases ====== */

    /** Refresca la JTable con la lista provista. */
    protected void refrescarTabla(List<T> datos) {
        modelo.setRowCount(0);
        datos.forEach(this::agregarFilaATabla);
    }

    /** Inserta en el modelo la fila correspondiente a una entidad. */
    protected abstract void agregarFilaATabla(T entity);

    /** Delegado para abrir sesión y manejar transacciones de forma simple. */
    protected void dentroDeTransaccion(java.util.function.Consumer<Session> work){
        try(Session s = sf.openSession()){
            var tx = s.beginTransaction();
            work.accept(s);
            tx.commit();
        }
    }

    /* =====================================================
     *           Métodos de GenericSwing por omisión
     * ===================================================== */
    // Estos quedan abstract o se provee default vacía; la subclase decide.
    @Override public abstract void guardar();
    @Override public abstract void eliminar();
    @Override public abstract void limpiarCampos();
    @Override public abstract void cargarSeleccion(T entity);
    @Override public abstract void cargarTabla();
}
