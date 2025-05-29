package org.economix.sql.hibernateimpl;

import org.economix.hibernate.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;


public class UsuarioHiberImpl implements GenericSql<Usuario>, Ejecutable {
    private static UsuarioHiberImpl usuarioHiber ;

    private UsuarioHiberImpl() {
    }

    public static UsuarioHiberImpl getInstance() {
        if ( usuarioHiber == null) {
            usuarioHiber = new UsuarioHiberImpl();
        }
        return usuarioHiber;
    }


    @Override
    public List<Usuario> findAll() {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return new ArrayList<>();
        }
        List<Usuario> list = session
                .createQuery("FROM Usuario", Usuario.class)   // ← usa el nombre de la CLASE, no de la tabla
                .getResultList();
        session.close();
        return list;
    }

    @Override
    public boolean save(Usuario usuario) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction(); //Crea un conjunto de instrucciones
        session.persist(usuario);
        session.getTransaction().commit(); //Crea un commit de todo el conjunto de instrucciones
        session.close();
        return true;
    }

    @Override
    public boolean update(Usuario usuario) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Usuario managed = (Usuario) s.merge(usuario);   // ← opcional, si la necesitas
            // puedes usar 'managed' aquí dentro
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();  // revierte cambios
            e.printStackTrace();            // o loguéalo con tu logger
            return false;
        }
    }

    @Override
    public boolean delete(Usuario usuario) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesiUón
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

    @Override
    public Usuario findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Usuario usuario = session.get(Usuario.class, id);
        session.close();
        return usuario;
    }

    @Override
    public void run() {

    }

    @Override
    public void setFlag(boolean flag) {

    }
}

