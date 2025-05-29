package org.economix.sql.hibernateimpl;

import org.economix.hibernate.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.usuario.Persona;
import org.economix.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
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
            return new ArrayList<>();
        }
        List<Persona> list = session
                .createQuery("FROM Persona", Persona.class)   // ← usa el nombre de la CLASE, no de la tabla
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
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Persona managed = (Persona) s.merge(persona);   // ← opcional, si la necesitas
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
    public boolean delete(Persona persona) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesiUón
        Usuario managed = session.get(Usuario.class, persona.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> La Persona ya no existe en BD");
        }
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

