package org.economix.sql.hibernateimpl.ingresos;

import org.economix.model.ingresos.Ingresos;
import org.economix.util.HibernateUtil;
import org.economix.sql.GenericSql;
import org.economix.model.ingresos.conceptoIngresos;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

public class ConceptoIngresosHiberImpl implements GenericSql<conceptoIngresos>, Ejecutable {
    private static ConceptoIngresosHiberImpl conceptoIngresosHiber;

    private ConceptoIngresosHiberImpl() {
    }

    public static ConceptoIngresosHiberImpl getInstance() {
        if (conceptoIngresosHiber == null) {
            conceptoIngresosHiber = new ConceptoIngresosHiberImpl();
        }
        return conceptoIngresosHiber;
    }


    @Override
    public List<conceptoIngresos> findAll() {           // ← cambia Entidad por el tipo correcto
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select c from ConceptoIngresos c join fetch c.ingresos",  // 👈
                            conceptoIngresos.class)
                    .getResultList();
        }
    }

    public boolean save(conceptoIngresos conceptoIngresos, Long idIngresos) {

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            // 1) Traer o referenciar el usuario
            Ingresos ingresos = session.getReference(Ingresos.class, idIngresos);
            //    (getReference evita un SELECT; usa get() si necesitas validar existencia)
            // 2) Vincular
            conceptoIngresos.setIngresos(ingresos);
            // 3) Persistir
            session.persist(conceptoIngresos);
            session.getTransaction().commit();
            return true;
        }
    }

    @Override
    public boolean save(conceptoIngresos conceptoIngresos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction(); //Crea un conjunto de instrucciones
        session.persist(conceptoIngresos);
        session.getTransaction().commit(); //Crea un commit de todo el conjunto de instrucciones
        session.close();
        return true;
    }

    @Override
    public boolean update(conceptoIngresos conceptoIngresos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(conceptoIngresos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(conceptoIngresos conceptoIngresos) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesión
        conceptoIngresos managed = session.get(conceptoIngresos.class, conceptoIngresos.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El concepto de ingreso ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }


    @Override
    public conceptoIngresos findById(Integer id) {
        Session session = HibernateUtil.getSession();
        conceptoIngresos conceptoIngresos = session.get(conceptoIngresos.class, id);
        session.close();
        return conceptoIngresos;
    }

    @Override
    public void run() {

    }

    @Override
    public void setFlag(boolean flag) {

    }
}
