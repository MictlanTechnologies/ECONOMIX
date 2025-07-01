// Implementación de operaciones CRUD para Contacto usando Hibernate
package org.economix.sql.hibernateimpl.usuario;

import org.economix.model.usuario.Persona;
import org.economix.util.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.model.usuario.Contacto;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

/**
 * Clase que implementa las operaciones de persistencia (CRUD) para la entidad { Contacto}
 * utilizando Hibernate. También implementa { Ejecutable} para posible ejecución desde menú.
 *
 * Esta clase sigue el patrón Singleton y expone métodos para manejar la relación
 * entre contactos y personas.
 */
public class ContactoHiberImpl implements GenericSql<Contacto>, Ejecutable {

    /** Instancia única (Singleton). */
    private static ContactoHiberImpl contactoHiber;

    /** Constructor privado para evitar instanciación directa. */
    private ContactoHiberImpl() {}

    /**
     * Devuelve la instancia Singleton de la clase.
     * @return instancia única de {@code ContactoHiberImpl}
     */
    public static ContactoHiberImpl getInstance() {
        if (contactoHiber == null) {
            contactoHiber = new ContactoHiberImpl();
        }
        return contactoHiber;
    }

    /**
     * Obtiene todos los contactos, incluyendo su relación con persona (fetch).
     * devuelve lista de objetos { Contacto}
     */
    @Override
    public List<Contacto> findAll() {
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery("select g from Contacto g join fetch g.persona", Contacto.class)
                    .getResultList();
        }
    }

    /**
     * Guarda un nuevo contacto, asociándolo con una persona mediante su ID.
     * @param contacto objeto {@link Contacto} a guardar
     * @param idPersona ID de la persona asociada
     * devuelve true si se guardó correctamente
     */
    public boolean save(Contacto contacto, Long idPersona) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            // Asocia contacto con persona existente
            Persona persona = session.getReference(Persona.class, idPersona);
            contacto.setPersona(persona);
            session.persist(contacto);
            session.getTransaction().commit();
            return true;
        }
    }

    /**
     * Guarda un nuevo contacto de forma directa (sin asociación explícita).
     * @param contacto contacto a persistir
     * devuelve true si fue exitoso
     */
    @Override
    public boolean save(Contacto contacto) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.persist(contacto);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Actualiza un contacto existente en la base de datos.
     * @param contacto objeto actualizado
     * devuelve true si la operación fue exitosa
     */
    @Override
    public boolean update(Contacto contacto) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(contacto);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Elimina un contacto de la base de datos.
     * @param contacto objeto a eliminar
     * devuelve true si fue eliminado correctamente o no existía
     */
    @Override
    public boolean delete(Contacto contacto) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // Cargar entidad en estado managed
        Contacto managed = session.get(Contacto.class, contacto.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El contacto ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Busca un contacto por su identificador único.
     * @param id identificador del contacto
     * devuelve objeto { Contacto} si se encuentra; null en otro caso
     */
    @Override
    public Contacto findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Contacto contacto = session.get(Contacto.class, id);
        session.close();
        return contacto;
    }

    // Métodos del contrato Ejecutable
    @Override
    public void run() {
    }

    @Override
    public void setFlag(boolean flag) {

    }
}
