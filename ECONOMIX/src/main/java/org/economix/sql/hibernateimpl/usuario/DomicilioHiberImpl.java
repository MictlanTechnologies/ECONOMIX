package org.economix.sql.hibernateimpl.usuario;

import org.economix.util.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.model.usuario.Domicilio;
import org.economix.model.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

public class DomicilioHiberImpl implements GenericSql<Domicilio>, Ejecutable {
    private static DomicilioHiberImpl domicilioHiber;

    private DomicilioHiberImpl() {
    }

    public static DomicilioHiberImpl getInstance() {
        if (domicilioHiber == null) {
            domicilioHiber = new DomicilioHiberImpl();
        }
        return domicilioHiber;
    }


    @Override
    public List<Domicilio> findAll() {           // ← cambia Entidad por el tipo correcto
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select g from Domicilio g join fetch g.usuario",  // 👈
                            Domicilio.class)
                    .getResultList();
        }
    }

    public boolean save(Domicilio domicilio, Long idUsuario) {

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            // 1) Traer o referenciar el usuario
            Usuario usuario = session.getReference(Usuario.class, idUsuario);
            //    (getReference evita un SELECT; usa get() si necesitas validar existencia)
            // 2) Vincular
            domicilio.setUsuario(usuario);
            // 3) Persistir
            session.persist(domicilio);
            session.getTransaction().commit();
            return true;
        }
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
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesión
        Domicilio managed = session.get(Domicilio.class, domicilio.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El domicilio ya no existe en BD");
        }
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
