// Paquete de implementación de acceso a datos para ingresos, usando Hibernate
package org.economix.sql.hibernateimpl.ingresos;

import org.economix.model.ingresos.Ingresos;
import org.economix.util.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.model.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

/**
 * Clase que implementa el acceso a datos de la entidad { Ingresos}
 * utilizando Hibernate como ORM. Implementa las operaciones CRUD definidas
 * por { GenericSql}, además de una sobrecarga para guardar con vínculo a { Usuario}.
 *
 * Aplica el patrón Singleton para garantizar una única instancia reutilizable
 * durante la ejecución de la aplicación.
 */
public class IngresosHiberImpl implements GenericSql<Ingresos>, Ejecutable {

    /** Instancia única (Singleton) de esta clase */
    private static IngresosHiberImpl ingresosHiber;

    /** Constructor privado para evitar instanciación externa */
    private IngresosHiberImpl() {}

    /**
     * Devuelve la instancia Singleton de {@code IngresosHiberImpl}.
     * devuelve instancia única
     */
    public static IngresosHiberImpl getInstance() {
        if (ingresosHiber == null) {
            ingresosHiber = new IngresosHiberImpl();
        }
        return ingresosHiber;
    }

    /**
     * Recupera todos los registros de ingresos, incluyendo la relación con el usuario.
     * Usa `join fetch` para prevenir problemas de LazyInitializationException.
     *
     * devuelve lista de ingresos existentes
     */
    @Override
    public List<Ingresos> findAll() {
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select g from Ingresos g join fetch g.usuario",
                            Ingresos.class)
                    .getResultList();
        }
    }

    /**
     * Guarda un nuevo ingreso y lo asocia a un usuario existente.
     *
     * @param ingresos objeto de ingreso a guardar
     * @param idUsuario ID del usuario al que pertenece el ingreso
     * devuelve true si la operación fue exitosa
     */
    public boolean save(Ingresos ingresos, Long idUsuario) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            // Recupera una referencia al usuario sin ejecutar SELECT
            Usuario usuario = session.getReference(Usuario.class, idUsuario);
            ingresos.setUsuario(usuario);

            session.persist(ingresos);
            session.getTransaction().commit();
            return true;
        }
    }

    /**
     * Guarda un ingreso sin necesidad de vincularlo manualmente.
     *
     * @param ingresos objeto a persistir
     * devuelve true si la operación se completó correctamente
     */
    @Override
    public boolean save(Ingresos ingresos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.persist(ingresos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Actualiza los datos de un ingreso ya existente en la base de datos.
     *
     * @param ingresos objeto con valores actualizados
     * devuelve true si se realizó el update correctamente
     */
    @Override
    public boolean update(Ingresos ingresos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(ingresos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Elimina un ingreso de la base de datos.
     * Primero verifica si la entidad existe dentro de la sesión activa.
     *
     * @param ingresos objeto a eliminar
     * devuelve true si se eliminó correctamente o ya no existía
     */
    @Override
    public boolean delete(Ingresos ingresos) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        Ingresos managed = session.get(Ingresos.class, ingresos.getId());

        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El ingreso ya no existe en BD");
        }

        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Busca un ingreso por su identificador único (ID).
     *
     * @param id identificador del ingreso
     * devuelve objeto {@link Ingresos} si existe, o null en caso contrario
     */
    @Override
    public Ingresos findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Ingresos ingresos = session.get(Ingresos.class, id);
        session.close();
        return ingresos;
    }

    /**
     * Método vacío implementado por el contrato {@link Ejecutable}.
     * Puede usarse para ejecutar operaciones específicas desde consola o UI.
     */
    @Override
    public void run() {
    }

    /**
     * Método no utilizado actualmente.
     * Puede activarse para controlar comportamientos condicionales de ejecución.
     * @param flag bandera booleana
     */
    @Override
    public void setFlag(boolean flag) {
    }
}
