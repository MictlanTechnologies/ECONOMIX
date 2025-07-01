// Paquete donde se gestiona la persistencia de usuarios en la base de datos con Hibernate.
package org.economix.sql.hibernateimpl.usuario;

// Importaciones necesarias para la lógica de persistencia
import org.economix.util.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.model.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta del acceso a datos para la entidad {@link Usuario}.
 * Utiliza Hibernate como ORM y aplica el patrón Singleton.
 * Implementa la interfaz {@link GenericSql} para operaciones CRUD genéricas
 * e {@link Ejecutable} para integrar con interfaces dinámicas (como consola).
 */
public class UsuarioHiberImpl implements GenericSql<Usuario>, Ejecutable {

    /** Instancia estática única (Singleton). */
    private static UsuarioHiberImpl usuarioHiber;

    /** Constructor privado para aplicar Singleton. */
    private UsuarioHiberImpl() {}

    /**
     * Devuelve la única instancia disponible de {@code UsuarioHiberImpl}.
     * @return Instancia Singleton.
     */
    public static UsuarioHiberImpl getInstance() {
        if (usuarioHiber == null) {
            usuarioHiber = new UsuarioHiberImpl();
        }
        return usuarioHiber;
    }

    /**
     * Recupera todos los usuarios registrados en la base de datos.
     * @return Lista completa de usuarios; lista vacía si hay error de conexión.
     */
    @Override
    public List<Usuario> findAll() {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return new ArrayList<>();
        }
        List<Usuario> list = session
                .createQuery("FROM Usuario", Usuario.class)
                .getResultList();
        session.close();
        return list;
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     * @param usuario Entidad {@code Usuario} a persistir.
     * @return true si la operación fue exitosa.
     */
    @Override
    public boolean save(Usuario usuario) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.persist(usuario);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Actualiza un usuario existente con nuevos datos.
     * Utiliza manejo de excepciones y rollback si ocurre error.
     * @param usuario Objeto actualizado.
     * @return true si la actualización fue exitosa; false si ocurrió algún error.
     */
    @Override
    public boolean update(Usuario usuario) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Usuario managed = (Usuario) s.merge(usuario); // opcionalmente usable
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace(); // o usar un logger
            return false;
        }
    }

    /**
     * Elimina un usuario de la base de datos.
     * @param usuario Usuario a eliminar.
     * @return true si la operación fue exitosa; false si no se encontró.
     */
    @Override
    public boolean delete(Usuario usuario) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        Usuario managed = session.get(Usuario.class, usuario.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El usuario ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }

    /**
     * Busca un usuario por su ID único.
     * @param id Identificador primario del usuario.
     * @return Instancia {@code Usuario} si existe; null en caso contrario.
     */
    @Override
    public Usuario findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Usuario usuario = session.get(Usuario.class, id);
        session.close();
        return usuario;
    }

    /**
     * Método sobrescrito para ejecución autónoma desde UI o CLI.
     */
    @Override
    public void run() {
        // Implementación opcional para ejecución directa
    }

    /**
     * Bandera de configuración, utilizada opcionalmente desde interfaces.
     * @param flag valor booleano para ajustar ejecución condicional.
     */
    @Override
    public void setFlag(boolean flag) {
        // Implementación opcional
    }
}
