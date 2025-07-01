// Paquete de utilidades para gestionar sesiones con Hibernate
package org.economix.util;

import org.economix.model.usuario.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

/**
 * Clase de utilidad para configurar y obtener sesiones de Hibernate.
 * Administra la creación de la SessionFactory a partir del archivo hibernate.cfg.xml.
 */
public final class HibernateUtil {

    // Fábrica de sesiones (única en toda la app)
    private static SessionFactory sessionFactory;

    // Registro de configuración de Hibernate
    private static StandardServiceRegistry registry;

    /**
     * Carga el registro de Hibernate desde el archivo de configuración.
     *
     * regresa true si se cargó correctamente.
     */
    public static boolean loadRegistry() {
        try {
            System.out.println("HibernateUtil.init()");
            registry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml") // Carga configuración de hibernate
                    .build();
            System.out.println("HibernateUtil.registry");
            return registry != null;
        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return false;
    }

    /**
     * Crea la SessionFactory usando el registro cargado.
     *
     * regresa true si la SessionFactory se creó correctamente.
     */
    public static boolean loadSessionFactory() {
        try {
            if (registry == null) {
                if (!loadRegistry()) {
                    return false;
                }
            }
            System.out.println("HibernateUtil.init.sessionFactory");
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            System.out.println("HibernateUtil.sessionFactory");
            return sessionFactory != null;
        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return false;
    }

    /**
     * Devuelve el registro de configuración de Hibernate.
     *
     * devuelve StandardServiceRegistry o null si falla.
     */
    public static StandardServiceRegistry getRegistry() {
        if (registry == null) {
            if (!loadRegistry()) {
                return null;
            }
        }
        return registry;
    }

    /**
     * Devuelve la SessionFactory (única instancia global).
     *
     * devuelve SessionFactory o null si falla.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            if (!loadSessionFactory()) {
                return null;
            }
        }
        return sessionFactory;
    }

    /**
     * Abre una nueva sesión de Hibernate.
     *
     * devolver Sesión activa o null si falla.
     */
    public static Session getSession() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            if (!loadSessionFactory()) {
                return null;
            }
        }
        return sessionFactory.openSession();
    }

    /**
     * Clase interna de ejemplo para manejar objetos Usuario con Hibernate.
     */
    public static class UsuarioDAO {

        /**
         * Guarda un usuario en la base de datos.
         *
         * @param u Usuario a guardar.
         */
        public void save(Usuario u) {
            Session s = HibernateUtil.getSession();
            Transaction tx = null;
            try {
                tx = s.beginTransaction();
                s.persist(u);  // Guarda nuevo registro
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null) tx.rollback();
                throw e;
            } finally {
                s.close();
            }
        }

        /**
         * Consulta todos los usuarios registrados.
         *
         * devolver Lista de usuarios.
         */
        public List<Usuario> findAll() {
            try (Session s = HibernateUtil.getSession()) {
                return s.createQuery("from Usuario", Usuario.class).getResultList();
            }
        }
    }
}
