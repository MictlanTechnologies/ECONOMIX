package org.economix.ventana.vista;

/**
 * Contrato genérico para paneles CRUD Swing.
 * @param <T> Tipo de la entidad que administra el panel.
 */
public interface GenericSwing<T> {

    /** Inserta o actualiza la entidad según el estado del formulario. */
    void guardar();

    /** Elimina la entidad actualmente seleccionada. */
    void eliminar();

    /** Limpia los campos del formulario y reinicia estado interno. */
    void limpiarCampos();

    /** Carga en el formulario los datos del registro seleccionado. */
    void cargarSeleccion(T entity);

    /** Consulta la base de datos y refresca la JTable. */
    void cargarTabla();
}