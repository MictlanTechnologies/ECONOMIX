// Paquete donde se agrupan las implementaciones Hibernate para ingresos
package org.economix.sql.hibernateimpl.ingresos;

import org.economix.model.ingresos.Ingresos;
import org.economix.util.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.model.ingresos.conceptoIngresos;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

/**
 * Implementación del acceso a datos para la entidad {@link conceptoIngresos},
 * utilizando Hibernate como ORM. Esta clase implementa el contrato {@link GenericSql}
 * para operaciones CRUD y {@link Ejecutable} para futuras extensiones funcionales.
 *
 * Emplea el patrón Singleton para asegurar una única instancia reutilizable.
 */
public class ConceptoIngresosHiberImpl implements GenericSql<conceptoIngresos>, Ejecutable {

    /** Instancia Singleton de la clase */
    private static ConceptoIngresosHiberImpl conceptoIngresosHiber;

    /** Constructor privado para patrón Singleton */
    private ConceptoIngresosHiberImpl() {}

    /**
     * Obtiene la instancia única de esta implementación.
     * devuelve instancia de {@code ConceptoIngresosHiberImpl}
     */
    public static ConceptoIngresosHiberImpl getInstance() {
        if (conceptoIngresosHiber == null) {
            conceptoIngresosHiber = new ConceptoIngresosHiberImpl();
        }
        return conceptoIngresosHiber;
    }

    /**
     * Recupera todos los conceptos de ingresos con sus ingresos relacionados.
     * Utiliza `join fetch` para evitar consultas adicionales al acceder a ingresos.
     * @devuelve lista de {@link conceptoIngresos}
     */
    @Override
    public List<conceptoIngresos> findAll() {
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select c from conceptoIngresos c join fetch c.ingresos",
                            conceptoIngresos.class)
                    .getResultList();
        }
    }

    /**
     * Guarda un nuevo concepto de ingreso, vinculándolo con un ingreso existente.
     * @param conceptoIngresos objeto a guardar
     * @param idIngresos identificador del ingreso al que pertenece
     * devuelve true si la operación fue exitosa
     */
    public boolean save(conceptoIngresos conceptoIngresos, Long idIngresos) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            // Referencia rápida (lazy) al ingreso
            Ingresos ingresos = session.getReference(Ingresos.class, idIngresos);
            conceptoIngresos.setIngresos(ingresos);

            session.persist(conceptoIngresos);
            session.getTransaction().commit();
            return true;
        }
    }

    /**
     * Guarda directamente un concepto de ingreso sin vinculación explícita.
     * @param conceptoIngresos objeto a persistir
     * devuelve true si se completó la transacción correctamente
     */
    @Override
    public boolean save(conceptoIngresos conceptoIngresos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.persist(conceptoIngresos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Actualiza los datos de un concepto de ingreso existente.
     * @param conceptoIngresos entidad con los nuevos valores
     * devuelve true si la operación fue exitosa
     */
    @Override
    public boolean update(conceptoIngresos conceptoIngresos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(conceptoIngresos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Elimina un concepto de ingreso. Verifica primero que exista en la sesión.
     * @param conceptoIngresos entidad a eliminar
     * devuelve true si la eliminación fue correcta
     */
    @Override
    public boolean delete(conceptoIngresos conceptoIngresos) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();

        conceptoIngresos managed = session.get(conceptoIngresos.class, conceptoIngresos.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El concepto de ingreso ya no existe en BD");
        }

        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Recupera un concepto de ingreso por su ID único.
     * @param id identificador del concepto
     * devuelve objeto encontrado o null si no existe
     */
    @Override
    public conceptoIngresos findById(Integer id) {
        Session session = HibernateUtil.getSession();
        conceptoIngresos conceptoIngresos = session.get(conceptoIngresos.class, id);
        session.close();
        return conceptoIngresos;
    }

    /**
     * Método del contrato {Ejecutable}, sin lógica por el momento.
     */
    @Override
    public void run() {
        // No implementado
    }

    /**
     * Método del contrato {Ejecutable}, sin uso actual.
     * @param flag indicador para ejecución condicional
     */
    @Override
    public void setFlag(boolean flag) {
        // No implementado
    }
}
