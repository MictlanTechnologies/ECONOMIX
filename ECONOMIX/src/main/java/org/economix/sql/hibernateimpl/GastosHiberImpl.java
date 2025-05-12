package org.economix.sql.hibernateimpl;

import org.economix.hibernate.HibernateUtil;
import org.economix.gastos.Gastos;
import org.economix.sql.GenericSql;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class GastosHiberImpl implements GenericSql<Gastos>, Ejecutable {
    private static GastosHiberImpl gastosHiber;

    private GastosHiberImpl() {
    }

    public static GastosHiberImpl getInstance() {
        if (gastosHiber == null) {
            gastosHiber = new GastosHiberImpl();
        }
        return gastosHiber;
    }


    @Override
    public List<Gastos> findAll() {           // ← cambia Entidad por el tipo correcto
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return new ArrayList<>();
        }
        List<Gastos> list = session
                .createQuery("FROM Gastos", Gastos.class)   // ← usa el nombre de la CLASE, no de la tabla
                .getResultList();
        session.close();
        return list;
    }


    @Override
    public boolean save(Gastos gastos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction(); //Crea un conjunto de instrucciones
        session.persist(gastos);
        session.getTransaction().commit(); //Crea un commit de todo el conjunto de instrucciones
        session.close();
        return true;
    }

    @Override
    public boolean update(Gastos gastos) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.merge(gastos);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Gastos gastos) {
        Session session = HibernateUtil.getSession();
        if (session == null) {
            System.out.println("ERROR DE CONEXION");
            return false;
        }

        session.beginTransaction();
        // ① Carga el managed entity dentro de la misma sesión
        Gastos managed = session.get(Gastos.class, gastos.getId());
        if (managed != null) {
            session.remove(managed);
        } else {
            System.out.println("> El gasto ya no existe en BD");
        }
        session.getTransaction().commit();
        session.close();
        return true;
    }


    @Override
    public Gastos findById(Integer id) {
        Session session = HibernateUtil.getSession();
        Gastos gastos = session.get(Gastos.class, id);
        session.close();
        return gastos;
    }

    @Override
    public void run() {

    }

    @Override
    public void setFlag(boolean flag) {

    }
}

