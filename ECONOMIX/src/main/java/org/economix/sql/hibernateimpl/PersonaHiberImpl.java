package org.economix.sql.hibernateimpl;

import org.economix.hibernate.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.usuario.Persona;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

public class PersonaHiberImpl implements GenericSql<Persona>, Ejecutable {
    private static PersonaHiberImpl personaHiber ;

    private PersonaHiberImpl() {
    }

    public static PersonaHiberImpl getInstance() {
        if ( personaHiber == null) {
            personaHiber = new PersonaHiberImpl();
        }
        return personaHiber;
    }


    @Override
    public List<Persona> findAll() {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return null;
        }
        List<Persona> list = session
                .createQuery("FROM nombrePersona", Persona.class)
                .getResultList();
        session.close();
        return list;
    }

    @Override
    public boolean save(Persona persona) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction(); //Crea un conjunto de instrucciones
        session.persist(persona);
        session.getTransaction().commit(); //Crea un commit de todo el conjunto de instrucciones
        session.close();
        return true;
    }

    @Override
    public boolean update(Persona persona) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(persona);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Persona persona) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.remove(persona);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public Persona findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Persona persona = session.get(Persona.class, id);
        session.close();
        return persona;
    }

    @Override
    public void run() {

    }

    @Override
    public void setFlag(boolean flag) {

    }
}

