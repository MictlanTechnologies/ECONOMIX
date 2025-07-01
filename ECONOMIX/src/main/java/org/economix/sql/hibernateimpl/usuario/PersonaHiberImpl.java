// Paquete encargado del manejo de la persistencia de la entidad Persona usando Hibernate.
package org.economix.sql.hibernateimpl.usuario;

// Importaciones para operaciones ORM con Hibernate y clases del modelo.
import org.economix.model.usuario.Usuario;
import org.economix.model.usuario.Persona;
import org.economix.sql.GenericSql;
import org.economix.util.HibernateUtil;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

/**
 * Clase DAO (Data Access Object) encargada de la persistencia de la entidad { Persona}
 * utilizando Hibernate. Implementa la interfaz { GenericSql} con métodos CRUD genéricos
 * e { Ejecutable} para habilitar su uso desde interfaces de consola u otros módulos.
 * <p>
 * Se implementa como un Singleton para evitar múltiples instancias del manejador.
 */
public class PersonaHiberImpl implements GenericSql<Persona>, Ejecutable {

    // Instancia única (Singleton)
    private static PersonaHiberImpl personaHiber;

    // Constructor privado
    private PersonaHiberImpl() {}

    /**
     * Devuelve la instancia Singleton del DAO de Persona.
     * regresa instancia única de {@code PersonaHiberImpl}
     */
    public static PersonaHiberImpl getInstance() {
        if (personaHiber == null) {
            personaHiber = new PersonaHiberImpl();
        }
        return personaHiber;
    }

    /**
     * Consulta todos los registros de persona, incluyendo su relación con el usuario.
     * regresa Lista de personas en la base de datos.
     */
    @Override
    public List<Persona> findAll() {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                    "select g from Persona g join fetch g.usuario",
                    Persona.class).getResultList();
        }
    }

    /**
     * Guarda una nueva persona vinculada a un usuario específico.
     * @param persona Objeto persona a persistir.
     * @param idUsuario ID del usuario al cual se asocia.
     * regresa true si la operación se realizó correctamente.
     */
    public boolean save(Persona persona, Long idUsuario) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            Usuario usuario = session.getReference(Usuario.class, idUsuario);
            persona.setUsuario(usuario);
            session.persist(persona);
            session.getTransaction().commit();
            return true;
        }
    }

    /**
     * Guarda una nueva persona sin asociación externa explícita.
     * @param persona Objeto persona a guardar.
     * regresa true si la operación fue exitosa.
     */
    @Override
    public boolean save(Persona persona) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.persist(persona);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Actualiza los datos de una persona existente.
     * @param persona Objeto con los datos actualizados.
     * regresa true si la modificación fue exitosa.
     */
    @Override
    public boolean update(Persona persona) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(persona);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Elimina una persona específica por ID.
     * @param persona Persona a eliminar de la base de datos.
     * regresa true si fue eliminada correctamente, false si no existe.
     */
    @Override
    public boolean delete(Persona persona) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        Persona managed = session.get(Persona.class, persona.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> La persona ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Busca una persona por su ID primario.
     * @param id Identificador de la persona.
     * regresa Objeto {@code Persona} si existe, null en caso contrario.
     */
    @Override
    public Persona findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Persona persona = session.get(Persona.class, id);
        session.close();
        return persona;
    }

    /**
     * Método sobrescrito de la interfaz Ejecutable.
     * Implementación pendiente o no requerida para ejecución directa.
     */
    @Override
    public void run() {
        // Lógica ejecutable opcional
    }

    /**
     * Método auxiliar para ejecución condicional.
     * @param flag valor de control.
     */
    @Override
    public void setFlag(boolean flag) {
        // Control de bandera condicional
    }
}
