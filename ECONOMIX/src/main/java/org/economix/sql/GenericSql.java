// Paquete que define operaciones básicas para acceder a la base de datos
package org.economix.sql;

import java.util.List;

/**
 * Interfaz genérica para realizar operaciones básicas (CRUD) en cualquier entidad.
 *
 * @param <T> Tipo de entidad que se va a manejar.
 */
public interface GenericSql<T> {

    /**
     * Obtiene todos los registros de la entidad.
     *
     * devolver Lista con todos los registros.
     */
    List<T> findAll();

    /**
     * Guarda un nuevo registro en la base de datos.
     *
     * @param t Objeto a guardar.
     * devolver true si se guardó correctamente.
     */
    boolean save(T t);

    /**
     * Actualiza un registro existente.
     *
     * @param t Objeto con los nuevos datos.
     * devolver true si se actualizó correctamente.
     */
    boolean update(T t);

    /**
     * Elimina un registro de la base de datos.
     *
     * @param t Objeto a eliminar.
     * devolver true si se eliminó correctamente.
     */
    boolean delete(T t);

    /**
     * Busca un registro por su ID.
     *
     * @param id Identificador del registro.
     * devolver Objeto encontrado o null si no existe.
     */
    T findById(Integer id);
}
