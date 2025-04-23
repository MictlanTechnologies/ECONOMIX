package org.economix.sql.hibernateimpl;

import org.economix.hibernate.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.usuario.Domicilio;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

public class DomicilioHiberImpl implements GenericSql<Domicilio>, Ejecutable {
    private static DomicilioHiberImpl domicilioHiber ;

    private DomicilioHiberImpl() {
    }

    public static DomicilioHiberImpl getInstance() {
        if ( domicilioHiber == null) {
            domicilioHiber = new DomicilioHiberImpl();
        }
        return domicilioHiber;
    }


    @Override
    public List<Domicilio> findAll() {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return null;
        }
        List<Domicilio> list = session
                .createQuery("FROM DOMICILIO", Domicilio.class)
                .getResultList();
        session.close();
        return list;
    }

    @Override
    public boolean save(Domicilio domicilio) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction(); //Crea un conjunto de instrucciones
        session.persist(domicilio);
        session.getTransaction().commit(); //Crea un commit de todo el conjunto de instrucciones
        session.close();
        return true;
    }

    @Override
    public boolean update(Domicilio domicilio) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(domicilio);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Domicilio domicilio) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.remove(domicilio);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public Domicilio findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Domicilio domicilio = session.get(Domicilio.class, id);
        session.close();
        return domicilio;
    }

    @Override
    public void run() {

    }

    @Override
    public void setFlag(boolean flag) {

    }
}

