package org.economix.sql.hibernateimpl;

import org.economix.hibernate.HibernateUtil;
import org.economix.gastos.Gastos;
import org.economix.sql.GenericSql;
import org.economix.gastos.conceptoGastos;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

public class ConceptoGastosHiberImpl implements GenericSql<conceptoGastos>, Ejecutable {
    private static ConceptoGastosHiberImpl conceptoGastosHiber;

    private ConceptoGastosHiberImpl() {
    }

    public static ConceptoGastosHiberImpl getInstance() {
        if (conceptoGastosHiber == null) {
            conceptoGastosHiber = new ConceptoGastosHiberImpl();
        }
        return conceptoGastosHiber;
    }


    @Override
    public List<conceptoGastos> findAll() {           // ← cambia Entidad por el tipo correcto
        try (Session session = HibernateUtil.getSession()) {
            return session
                    .createQuery(
                            "select g from conceptoGastos g join fetch g.gastos",  // 👈
                            conceptoGastos.class)
                    .getResultList();
        }
    }

    public boolean save(conceptoGastos conceptoGastos, Long idUsuario) {

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            // 1) Traer o referenciar el usuario
            Gastos gastos = session.getReference(Gastos.class, idUsuario);
            //    (getReference evita un SELECT; usa get() si necesitas validar existencia)
            // 2) Vincular
            conceptoGastos.setGastos(gastos);
            // 3) Persistir
            session.persist(conceptoGastos);
            session.getTransaction().commit();
            return true;
        }
    }

    @Override
    public boolean save(conceptoGastos conceptoGastos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction(); //Crea un conjunto de instrucciones
        session.persist(conceptoGastos);
        session.getTransaction().commit(); //Crea un commit de todo el conjunto de instrucciones
        session.close();
        return true;
    }

    @Override
    public boolean update(conceptoGastos conceptoGastos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(conceptoGastos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(conceptoGastos conceptoGastos) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesión
        conceptoGastos managed = session.get(conceptoGastos.class, conceptoGastos.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El concepto de gasto ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }


    @Override
    public conceptoGastos findById(Integer id) {
        Session session = HibernateUtil.getSession();
        conceptoGastos conceptoGastos = session.get(conceptoGastos.class, id);
        session.close();
        return conceptoGastos;
    }

    @Override
    public void run() {

    }

    @Override
    public void setFlag(boolean flag) {

    }
}
