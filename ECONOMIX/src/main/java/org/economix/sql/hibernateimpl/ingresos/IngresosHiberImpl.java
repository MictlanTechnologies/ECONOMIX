package org.economix.sql.hibernateimpl.ingresos;

import org.economix.model.ingresos.Ingresos;
import org.economix.util.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.model.usuario.Usuario;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

public class IngresosHiberImpl implements GenericSql<Ingresos>, Ejecutable {
    private static IngresosHiberImpl ingresosHiber;

    private IngresosHiberImpl() {
    }

    public static IngresosHiberImpl getInstance() {
        if (ingresosHiber == null) {
            ingresosHiber = new IngresosHiberImpl();
        }
        return ingresosHiber;
    }


    @Override
    public List<Ingresos> findAll() {           // ← cambia Entidad por el tipo correcto
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select g from Ingresos g join fetch g.usuario",  // 👈
                            Ingresos.class)
                    .getResultList();
        }
    }

    public boolean save(Ingresos ingresos, Long idUsuario) {

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            // 1) Traer o referenciar el usuario
            Usuario usuario = session.getReference(Usuario.class, idUsuario);
            //    (getReference evita un SELECT; usa get() si necesitas validar existencia)
            // 2) Vincular
            ingresos.setUsuario(usuario);
            // 3) Persistir
            session.persist(ingresos);
            session.getTransaction().commit();
            return true;
        }
    }

    @Override
    public boolean save(Ingresos ingresos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction(); //Crea un conjunto de instrucciones
        session.persist(ingresos);
        session.getTransaction().commit(); //Crea un commit de todo el conjunto de instrucciones
        session.close();
        return true;
    }

    @Override
    public boolean update(Ingresos ingresos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(ingresos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Ingresos ingresos) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesión
        Ingresos managed = session.get(Ingresos.class, ingresos.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El ingreso ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }


    @Override
    public Ingresos findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Ingresos ingresos = session.get(Ingresos.class, id);
        session.close();
        return ingresos;
    }

    @Override
    public void run() {

    }

    @Override
    public void setFlag(boolean flag) {

    }
}

