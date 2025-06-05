package org.economix.sql.hibernateimpl.usuario;

import org.economix.util.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.model.usuario.Contacto;
import org.economix.model.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

public class ContactoHiberImpl implements GenericSql<Contacto>, Ejecutable {
    private static ContactoHiberImpl contactoHiber;

    private ContactoHiberImpl() {
    }

    public static ContactoHiberImpl getInstance() {
        if (contactoHiber == null) {
            contactoHiber = new ContactoHiberImpl();
        }
        return contactoHiber;
    }


    @Override
    public List<Contacto> findAll() {           // ← cambia Entidad por el tipo correcto
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select g from Contacto g join fetch g.usuario",  // 👈
                            Contacto.class)
                    .getResultList();
        }
    }

    public boolean save(Contacto contacto, Long idUsuario) {

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            // 1) Traer o referenciar el usuario
            Usuario usuario = session.getReference(Usuario.class, idUsuario);
            //    (getReference evita un SELECT; usa get() si necesitas validar existencia)
            // 2) Vincular
            contacto.setUsuario(usuario);
            // 3) Persistir
            session.persist(contacto);
            session.getTransaction().commit();
            return true;
        }
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
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesión
        Contacto managed = session.get(Contacto.class, contacto.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El contacto ya no existe en BD");
        }
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

