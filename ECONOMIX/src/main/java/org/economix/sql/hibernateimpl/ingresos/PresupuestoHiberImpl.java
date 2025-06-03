package org.economix.sql.hibernateimpl.ingresos;

import org.economix.Ingresos.Ingresos;
import org.economix.hibernate.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;
import org.economix.Ingresos.Presupuesto;

import java.util.List;

public class PresupuestoHiberImpl implements GenericSql<Presupuesto>, Ejecutable {
    private static PresupuestoHiberImpl presupuestoHiber;

    private PresupuestoHiberImpl() {
    }

    public static PresupuestoHiberImpl getInstance() {
        if (presupuestoHiber == null) {
            presupuestoHiber = new PresupuestoHiberImpl();
        }
        return presupuestoHiber;
    }


    @Override
    public List<Presupuesto> findAll() {           // ← cambia Entidad por el tipo correcto
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select g from presupuesto g join fetch g.ingresos",  // 👈
                            Presupuesto.class)
                    .getResultList();
        }
    }

    public boolean save(Presupuesto presupuesto, Long idIngresos) {

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            // 1) Traer o referenciar el usuario
            Ingresos ingresos = session.getReference(Ingresos.class, idIngresos);
            //    (getReference evita un SELECT; usa get() si necesitas validar existencia)
            // 2) Vincular
            presupuesto.setIngresos(ingresos);
            // 3) Persistir
            session.persist(presupuesto);
            session.getTransaction().commit();
            return true;
        }
    }

    @Override
    public boolean save(Presupuesto presupuesto) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction(); //Crea un conjunto de instrucciones
        session.persist(presupuesto);
        session.getTransaction().commit(); //Crea un commit de todo el conjunto de instrucciones
        session.close();
        return true;
    }

    @Override
    public boolean update(Presupuesto presupuesto) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(presupuesto);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Presupuesto presupuesto) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesión
        Presupuesto managed = session.get(Presupuesto.class, presupuesto.getId());
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
    public Presupuesto findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Presupuesto presupuesto = session.get(Presupuesto.class, id);
        session.close();
        return presupuesto;
    }

    @Override
    public void run() {

    }

    @Override
    public void setFlag(boolean flag) {

    }
}

