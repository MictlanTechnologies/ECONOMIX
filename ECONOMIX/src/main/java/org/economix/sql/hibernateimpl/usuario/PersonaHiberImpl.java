package org.economix.sql.hibernateimpl.usuario;

import org.economix.usuario.Usuario;
import org.economix.hibernate.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.usuario.Persona;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;


public class PersonaHiberImpl implements GenericSql<Persona>, Ejecutable {
    private static PersonaHiberImpl personaHiber;

    private PersonaHiberImpl() {
    }

    public static PersonaHiberImpl getInstance() {
        if (personaHiber == null) {
            personaHiber = new PersonaHiberImpl();
        }
        return personaHiber;
    }


    @Override
    public List<Persona> findAll() {           // ← cambia Entidad por el tipo correcto
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select g from Persona g join fetch g.usuario",  // 👈
                            Persona.class)
                    .getResultList();
        }
    }

    public boolean save(Persona persona, Long idUsuario) {

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            // 1) Traer o referenciar el usuario
            Usuario usuario= session.getReference(Usuario.class, idUsuario);
            //    (getReference evita un SELECT; usa get() si necesitas validar existencia)
            // 2) Vincular
            persona.setUsuario(usuario);
            // 3) Persistir
            session.persist(persona);
            session.getTransaction().commit();
            return true;
        }
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
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesión
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

