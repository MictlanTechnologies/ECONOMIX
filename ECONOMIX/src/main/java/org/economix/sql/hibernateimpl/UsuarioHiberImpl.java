package org.economix.sql.hibernateimpl;

import org.economix.hibernate.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.usuario.Persona;
import org.economix.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;
//DEJO ESTA CLASE COMO EJEMPLO PARA CREAR LAS DEMÁS
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
            return null;
        }
        List<Usuario> list = session
                .createQuery("FROM USUARIO", Usuario.class)
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
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(usuario);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Usuario usuario) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.remove(usuario);
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

