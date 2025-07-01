// Paquete que contiene las implementaciones Hibernate para entidades de gastos
package org.economix.sql.hibernateimpl.gastos;

import org.economix.util.HibernateUtil;
import org.economix.model.gastos.Gastos;
import org.economix.sql.GenericSql;
import org.economix.model.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

/**
 * Implementación de {GenericSql} y {Ejecutable} para la entidad {Gastos}.
 * Esta clase proporciona las operaciones básicas de persistencia (CRUD) utilizando Hibernate.
 *
 * Sigue el patrón Singleton para mantener una única instancia reutilizable.
 */
public class GastosHiberImpl implements GenericSql<Gastos>, Ejecutable {

    /** Instancia única para Singleton */
    private static GastosHiberImpl gastosHiber;

    /** Constructor privado para evitar instanciación externa */
    private GastosHiberImpl() {}

    /**
     * Devuelve la instancia única de esta clase.
     * devuelve instancia Singleton de {@code GastosHiberImpl}
     */
    public static GastosHiberImpl getInstance() {
        if (gastosHiber == null) {
            gastosHiber = new GastosHiberImpl();
        }
        return gastosHiber;
    }

    /**
     * Recupera todos los registros de gastos con sus usuarios asociados mediante `join fetch`.
     * devuelve lista completa de objetos {@link Gastos}
     */
    @Override
    public List<Gastos> findAll() {
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select g from Gastos g join fetch g.usuario",
                            Gastos.class)
                    .getResultList();
        }
    }

    /**
     * Guarda un nuevo gasto en la base de datos, asociándolo a un usuario a través de su ID.
     * @param gastos entidad de tipo {@link Gastos} a guardar
     * @param idUsuario ID del usuario que registra el gasto
     * devuelve true si la operación fue exitosa
     */
    public boolean save(Gastos gastos, Long idUsuario) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            // Referencia al usuario sin hacer SELECT (lazy proxy)
            Usuario usuario = session.getReference(Usuario.class, idUsuario);
            gastos.setUsuario(usuario);

            session.persist(gastos);
            session.getTransaction().commit();
            return true;
        }
    }

    /**
     * Guarda un nuevo gasto directamente en la base de datos.
     * @param gastos entidad a guardar
     * devuelve true si se guardó correctamente
     */
    @Override
    public boolean save(Gastos gastos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.persist(gastos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Actualiza un gasto existente en la base de datos.
     * @param gastos entidad modificada
     * devuelve true si la operación fue exitosa
     */
    @Override
    public boolean update(Gastos gastos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(gastos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Elimina un gasto de la base de datos.
     * Primero asegura que el objeto esté gestionado por el contexto de persistencia.
     * @param gastos entidad a eliminar
     * devuelve true si se eliminó correctamente
     */
    @Override
    public boolean delete(Gastos gastos) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXIÓN");
            return false;
        }

        session.beginTransaction();
        Gastos managed = session.get(Gastos.class, gastos.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El gasto ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Busca un gasto específico por su ID.
     * @param id identificador único del gasto
     * devuelve entidad encontrada o null si no existe
     */
    @Override
    public Gastos findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Gastos gastos = session.get(Gastos.class, id);
        session.close();
        return gastos;
    }

    /**
     * Implementación vacía del método run() del contrato Ejecutable.
     * Este método puede sobreescribirse si se desea ejecutar lógica específica.
     */
    @Override
    public void run() {
    }

    /**
     * Implementación vacía del método setFlag() del contrato Ejecutable.
     * @param flag bandera para alguna acción condicional
     */
    @Override
    public void setFlag(boolean flag) {
    }
}
