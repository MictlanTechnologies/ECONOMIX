// Implementación de acceso a datos para Presupuesto, utilizando Hibernate
package org.economix.sql.hibernateimpl.presupuesto;

import org.economix.model.presupuesto.Presupuesto;
import org.economix.model.usuario.Usuario;
import org.economix.sql.GenericSql;
import org.economix.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación de la interfaz {@link GenericSql} para la entidad {@link Presupuesto}.
 * Esta clase proporciona métodos para realizar operaciones CRUD, así como funcionalidades
 * adicionales específicas del negocio, como buscar presupuestos activos, clonar meses anteriores,
 * y acumular gastos.
 *
 * Utiliza el patrón Singleton para evitar múltiples instancias.
 */
public class PresupuestoHiberImpl implements GenericSql<Presupuesto> {

    /** Instancia única de la clase (Singleton). */
    private static PresupuestoHiberImpl instancia;

    /** Constructor privado para restringir la creación externa. */
    private PresupuestoHiberImpl() {}

    /**
     * Método de acceso al singleton.
     * @return instancia única de {@code PresupuestoHiberImpl}
     */
    public static PresupuestoHiberImpl get() {
        return instancia == null ? instancia = new PresupuestoHiberImpl() : instancia;
    }

    /**
     * Recupera todos los registros de presupuesto de la base de datos.
     * @return lista de presupuestos existentes
     */
    @Override
    public List<Presupuesto> findAll() {
        try (Session s = HibernateUtil.getSession()) {
            return s.createQuery("from Presupuesto", Presupuesto.class).list();
        }
    }

    /**
     * Guarda un nuevo registro de presupuesto.
     * @param p el objeto {@link Presupuesto} a guardar
     * @return true si fue exitoso
     */
    @Override
    public boolean save(Presupuesto p) {
        try (Session s = HibernateUtil.getSession()) {
            Transaction tx = s.beginTransaction();
            s.persist(p);
            tx.commit();
            return true;
        }
    }

    /**
     * Actualiza un presupuesto existente.
     * @param p presupuesto actualizado
     * @return true si la actualización fue exitosa
     */
    @Override
    public boolean update(Presupuesto p) {
        try (Session s = HibernateUtil.getSession()) {
            Transaction tx = s.beginTransaction();
            s.merge(p);
            tx.commit();
            return true;
        }
    }

    /**
     * Elimina un presupuesto de la base de datos.
     * @param p presupuesto a eliminar
     * @return true si fue eliminado correctamente o no existía
     */
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

    /**
     * Busca un presupuesto por su ID.
     * @param id identificador único
     * @return objeto {@link Presupuesto} si existe, o null en caso contrario
     */
    @Override
    public Presupuesto findById(Integer id) {
        try (Session s = HibernateUtil.getSession()) {
            return s.get(Presupuesto.class, id);
        }
    }

    /**
     * Busca un presupuesto activo por categoría, usuario, mes y año.
     * @param categoria categoría del presupuesto
     * @param u usuario asociado
     * @param mes mes correspondiente
     * @param anio año correspondiente
     * @return presupuesto activo encontrado o null si no existe
     */
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

    /**
     * Suma un nuevo gasto al monto gastado en el presupuesto y guarda el cambio.
     * @param p presupuesto al que se le acumula el gasto
     * @param importe cantidad a sumar
     */
    public void agregarGasto(Presupuesto p, BigDecimal importe) {
        try (Session s = HibernateUtil.getSession()) {
            Transaction tx = s.beginTransaction();
            p.setMontoGastado(p.getMontoGastado().add(importe));
            s.merge(p);
            tx.commit();
        }
    }

    /**
     * Clona los presupuestos del mes anterior al mes actual (nuevo).
     * Útil para establecer presupuestos mensuales recurrentes.
     *
     * @param u usuario propietario del presupuesto
     * @param mesAnterior mes de origen
     * @param anioAnterior año de origen
     * @param mesNuevo nuevo mes
     * @param anioNuevo nuevo año
     */
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
