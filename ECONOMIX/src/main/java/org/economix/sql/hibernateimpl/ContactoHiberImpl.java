package org.economix.sql.hibernateimpl;

import org.economix.hibernate.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.usuario.Contacto;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

public class ContactoHiberImpl implements GenericSql<Contacto>, Ejecutable {
    private static ContactoHiberImpl contactoHiber ;

    private ContactoHiberImpl() {
    }

    public static ContactoHiberImpl getInstance() {
        if ( contactoHiber == null) {
            contactoHiber = new ContactoHiberImpl();
        }
        return contactoHiber;
    }


    @Override
    public List<Contacto> findAll() {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return null;
        }
        List<Contacto> list = session
                .createQuery("FROM CONTACTOS", Contacto.class)
                .getResultList();
        session.close();
        return list;
    }

    @Override
    public boolean save(Contacto contacto) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction(); //Crea un conjunto de instrucciones
        session.persist(contacto);
        session.getTransaction().commit(); //Crea un commit de todo el conjunto de instrucciones
        session.close();
        return true;
    }

    @Override
    public boolean update(Contacto contacto) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(contacto);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Contacto contacto) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.remove(contacto);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public Contacto findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Contacto contacto = session.get(Contacto.class, id);
        session.close();
        return contacto;
    }

    @Override
    public void run() {

    }

    @Override
    public void setFlag(boolean flag) {

    }
}

