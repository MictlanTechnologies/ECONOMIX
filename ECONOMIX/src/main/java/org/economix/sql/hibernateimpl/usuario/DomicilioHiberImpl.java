// Paquete que define la lógica de acceso a datos para la entidad Domicilio mediante Hibernate.
package org.economix.sql.hibernateimpl.usuario;

// Importaciones necesarias para acceso a BD, modelo, y funcionalidad de la interfaz ejecutable.
import org.economix.model.usuario.Persona;
import org.economix.model.usuario.Domicilio;
import org.economix.util.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

/**
 * Implementación de la lógica de acceso a datos (DAO) para la entidad { Domicilio}.
 * Utiliza Hibernate para la persistencia y recuperación de datos.
 * Aplica el patrón Singleton para asegurar una sola instancia.
 * Implementa { GenericSql} para operaciones CRUD genéricas
 * e { Ejecutable} para interacción desde consola u otras interfaces.
 */
public class DomicilioHiberImpl implements GenericSql<Domicilio>, Ejecutable {

    // Instancia Singleton
    private static DomicilioHiberImpl domicilioHiber;

    // Constructor privado para evitar instanciación directa
    private DomicilioHiberImpl() {
    }

    /**
     * Devuelve una instancia única del DAO (Singleton).
     */
    public static DomicilioHiberImpl getInstance() {
        if (domicilioHiber == null) {
            domicilioHiber = new DomicilioHiberImpl();
        }
        return domicilioHiber;
    }

    /**
     * Recupera todos los registros de domicilio, incluyendo la relación con Persona.
     * regresa Lista de domicilios registrados.
     */
    @Override
    public List<Domicilio> findAll() {
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery("select g from Domicilio g join fetch g.persona", Domicilio.class)
                    .getResultList();
        }
    }

    /**
     * Guarda un domicilio y lo vincula con una persona específica.
     * @param domicilio Objeto domicilio a guardar.
     * @param idPersona ID de la persona a asociar.
     * regresa true si la operación fue exitosa.
     */
    public boolean save(Domicilio domicilio, Long idPersona) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            Persona persona = session.getReference(Persona.class, idPersona);
            domicilio.setPersona(persona);
            session.persist(domicilio);
            session.getTransaction().commit();
            return true;
        }
    }

    /**
     * Guarda un nuevo domicilio en la base de datos.
     * @param domicilio Domicilio a persistir.
     * regresa true si la operación fue exitosa.
     */
    @Override
    public boolean save(Domicilio domicilio) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.persist(domicilio);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Actualiza un domicilio existente.
     * @param domicilio Objeto con los nuevos datos.
     * regresa true si la operación fue exitosa.
     */
    @Override
    public boolean update(Domicilio domicilio) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(domicilio);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Elimina un domicilio por su ID, si existe.
     * @param domicilio Domicilio a eliminar.
     * regresa true si fue eliminado correctamente.
     */
    @Override
    public boolean delete(Domicilio domicilio) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        Domicilio managed = session.get(Domicilio.class, domicilio.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El domicilio ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Busca un domicilio por su ID.
     * @param id Identificador del domicilio.
     * regresa El objeto Domicilio encontrado o null si no existe.
     */
    @Override
    public Domicilio findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Domicilio domicilio = session.get(Domicilio.class, id);
        session.close();
        return domicilio;
    }

    /**
     * Método sobrescrito de la interfaz Ejecutable.
     */
    @Override
    public void run() {
    }

    /**
     * Método de bandera sobrescrito. Reservado para ejecución condicional.
     * @param flag Valor booleano del estado.
     */
    @Override
    public void setFlag(boolean flag) {
    }
}
