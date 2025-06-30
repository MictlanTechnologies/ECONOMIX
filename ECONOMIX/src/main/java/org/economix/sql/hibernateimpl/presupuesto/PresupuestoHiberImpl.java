package org.economix.sql.hibernateimpl.presupuesto;

import org.economix.model.presupuesto.Presupuesto;
import org.economix.model.usuario.Usuario;
import org.economix.sql.GenericSql;
import org.economix.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class PresupuestoHiberImpl implements GenericSql<Presupuesto> {

    private static PresupuestoHiberImpl instancia;
    private PresupuestoHiberImpl() {}

    public static PresupuestoHiberImpl get() {
        return instancia == null ? instancia = new PresupuestoHiberImpl() : instancia;
    }

    @Override
    public List<Presupuesto> findAll() {
        try (Session s = HibernateUtil.getSession()) {
            return s.createQuery("from Presupuesto", Presupuesto.class).list();
        }
    }

    @Override
    public boolean save(Presupuesto p) {
        try (Session s = HibernateUtil.getSession()) {
            Transaction tx = s.beginTransaction();
            s.persist(p);
            tx.commit();
            return true;
        }
    }

    @Override
    public boolean update(Presupuesto p) {
        try (Session s = HibernateUtil.getSession()) {
            Transaction tx = s.beginTransaction();
            s.merge(p);
            tx.commit();
            return true;
        }
    }

    @Override
    public boolean delete(Presupuesto p) {
        try (Session s = HibernateUtil.getSession()) {
            Transaction tx = s.beginTransaction();
            Presupuesto managed = s.get(Presupuesto.class, p.getIdPresupuesto());
            if (managed != null) s.remove(managed);
            tx.commit();
            return true;
        }
    }

    @Override
    public Presupuesto findById(Integer id) {
        try (Session s = HibernateUtil.getSession()) {
            return s.get(Presupuesto.class, id);
        }
    }

    /** Presupuesto activo para categoría + mes + usuario */
    public Presupuesto buscarActivo(String categoria, Usuario u, int mes, int anio) {
        try (Session s = HibernateUtil.getSession()) {
            return s.createQuery("""
                    FROM Presupuesto p
                    WHERE p.usuario = :u AND p.categoria = :c
                          AND p.mes = :m AND p.anio = :a
                    """, Presupuesto.class)
                    .setParameter("u", u)
                    .setParameter("c", categoria)
                    .setParameter("m", mes)
                    .setParameter("a", anio)
                    .uniqueResult();
        }
    }

    /** Suma un nuevo gasto y guarda el presupuesto */
    public void agregarGasto(Presupuesto p, BigDecimal importe) {
        try (Session s = HibernateUtil.getSession()) {
            Transaction tx = s.beginTransaction();
            p.setMontoGastado(p.getMontoGastado().add(importe));
            s.merge(p);
            tx.commit();
        }
    }

    /** Copiar límites del mes anterior (opcional) */
    public void clonarMesAnterior(Usuario u, int mesAnterior, int anioAnterior,
                                  int mesNuevo, int anioNuevo) {
        try (Session s = HibernateUtil.getSession()) {
            Transaction tx = s.beginTransaction();
            List<Presupuesto> anteriores = s.createQuery("""
                    FROM Presupuesto p
                    WHERE p.usuario = :u AND p.mes = :m AND p.anio = :a
                    """, Presupuesto.class)
                    .setParameter("u", u)
                    .setParameter("m", mesAnterior)
                    .setParameter("a", anioAnterior)
                    .getResultList();

            for (Presupuesto old : anteriores) {
                Presupuesto nuevo = Presupuesto.builder()
                        .usuario(u)
                        .categoria(old.getCategoria())
                        .montoMaximo(old.getMontoMaximo())
                        .montoGastado(BigDecimal.ZERO)
                        .mes(mesNuevo)
                        .anio(anioNuevo)
                        .build();
                s.persist(nuevo);
            }
            tx.commit();
        }
    }
}