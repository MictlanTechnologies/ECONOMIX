// Paquete que contiene las implementaciones Hibernate para entidades de gastos
package org.economix.sql.hibernateimpl.gastos;

import org.economix.util.HibernateUtil;
import org.economix.model.gastos.Gastos;
import org.economix.sql.GenericSql;
import org.economix.model.gastos.conceptoGastos;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

/**
 * Implementación específica de {GenericSql} y {Ejecutable} para la entidad {conceptoGastos}.
 * Esta clase encapsula las operaciones CRUD utilizando Hibernate para la entidad Concepto de Gastos.
 *
 * Sigue el patrón Singleton para garantizar una única instancia reutilizable.
 */
public class ConceptoGastosHiberImpl implements GenericSql<conceptoGastos>, Ejecutable {

    /** Instancia única (Singleton) */
    private static ConceptoGastosHiberImpl conceptoGastosHiber;

    /** Constructor privado para el patrón Singleton */
    private ConceptoGastosHiberImpl() {}

    /**
     * Devuelve la instancia única de esta implementación.
     * devuelve instancia única de ConceptoGastosHiberImpl
     */
    public static ConceptoGastosHiberImpl getInstance() {
        if (conceptoGastosHiber == null) {
            conceptoGastosHiber = new ConceptoGastosHiberImpl();
        }
        return conceptoGastosHiber;
    }

    /**
     * Recupera todos los conceptos de gasto de la base de datos,
     * incluyendo los gastos asociados (uso de `join fetch` para evitar `LazyInitializationException`).
     *
     * devuelve lista de todos los {@link conceptoGastos} registrados.
     */
    @Override
    public List<conceptoGastos> findAll() {
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select g from conceptoGastos g join fetch g.gastos",
                            conceptoGastos.class)
                    .getResultList();
        }
    }

    /**
     * Guarda un nuevo concepto de gasto vinculándolo a un gasto existente, usando su ID.
     * @param conceptoGastos el concepto a guardar
     * @param idGastos identificador del gasto al que se asociará el concepto
     * devuelve true si la operación fue exitosa
     */
    public boolean save(conceptoGastos conceptoGastos, Long idGastos) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            // Referencia al gasto sin hacer SELECT explícito
            Gastos gastos = session.getReference(Gastos.class, idGastos);

            // Vinculación del gasto
            conceptoGastos.setGastos(gastos);

            // Persistencia
            session.persist(conceptoGastos);
            session.getTransaction().commit();
            return true;
        }
    }

    /**
     * Guarda un nuevo concepto de gasto en la base de datos.
     * @param conceptoGastos entidad a guardar
     * devuelve true si la operación fue exitosa
     */
    @Override
    public boolean save(conceptoGastos conceptoGastos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.persist(conceptoGastos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Actualiza un concepto de gasto existente.
     * @param conceptoGastos entidad con datos modificados
     * devuelve true si la operación fue exitosa
     */
    @Override
    public boolean update(conceptoGastos conceptoGastos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(conceptoGastos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Elimina un concepto de gasto de la base de datos.
     * Se asegura de que la entidad esté gestionada antes de eliminar.
     * @param conceptoGastos entidad a eliminar
     * devuelve true si la operación fue exitosa
     */
    @Override
    public boolean delete(conceptoGastos conceptoGastos) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXIÓN");
            return false;
        }

        session.beginTransaction();
        // Se asegura de que la entidad esté en contexto
        conceptoGastos managed = session.get(conceptoGastos.class, conceptoGastos.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El concepto de gasto ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Busca un concepto de gasto por su ID.
     * @param id identificador del concepto
     * devuelve la entidad encontrada o null si no existe
     */
    @Override
    public conceptoGastos findById(Integer id) {
        Session session = HibernateUtil.getSession();
        conceptoGastos conceptoGastos = session.get(conceptoGastos.class, id);
        session.close();
        return conceptoGastos;
    }

    // Métodos del contrato Ejecutable (aún no implementados)

    @Override
    public void run() {
        // Método aún no implementado
    }

    @Override
    public void setFlag(boolean flag) {
        // Método aún no implementado
    }
}
