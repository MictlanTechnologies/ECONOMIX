package org.economix.sql.hibernateimpl.ahorro;

import org.economix.model.ahorro.Ahorro;
import org.economix.model.usuario.Usuario;
import org.economix.sql.GenericSql;
import org.economix.util.HibernateUtil;
import org.economix.vista.acciones.Ejecutable;
import org.hibernate.Session;

import java.util.List;

/**
 * Implementación de acceso a datos para la entidad {@link Ahorro}.
 * Utiliza Hibernate y aplica el patrón Singleton.
 */
public class AhorroHiberImpl implements GenericSql<Ahorro>, Ejecutable {

    private static AhorroHiberImpl instancia;

    private AhorroHiberImpl() {}

    public static AhorroHiberImpl getInstance() {
        if (instancia == null) instancia = new AhorroHiberImpl();
        return instancia;
    }

    @Override
    public List<Ahorro> findAll() {
        try (Session s = HibernateUtil.getSession()) {
            return s.createQuery("select a from Ahorro a join fetch a.usuario", Ahorro.class).list();
        }
    }

    public boolean save(Ahorro a, Long idUsuario) {
        try (Session s = HibernateUtil.getSession()) {
            s.beginTransaction();
            Usuario u = s.getReference(Usuario.class, idUsuario);
            a.setUsuario(u);
            s.persist(a);
            s.getTransaction().commit();
            return true;
        }
    }

    @Override
    public boolean save(Ahorro ahorro) {
        Session s = HibernateUtil.getSession();
        s.beginTransaction();
        s.persist(ahorro);
        s.getTransaction().commit();
        s.close();
        return true;
    }

    @Override
    public boolean update(Ahorro ahorro) {
        Session s = HibernateUtil.getSession();
        s.beginTransaction();
        s.merge(ahorro);
        s.getTransaction().commit();
        s.close();
        return true;
    }

    @Override
    public boolean delete(Ahorro ahorro) {
        Session s = HibernateUtil.getSession();
        if (s == null) return false;
        s.beginTransaction();
        Ahorro managed = s.get(Ahorro.class, ahorro.getId());
        if (managed != null) {
            s.remove(managed);
        }
        s.getTransaction().commit();
        s.close();
        return true;
    }

    @Override
    public Ahorro findById(Integer id) {
        Session s = HibernateUtil.getSession();
        Ahorro a = s.get(Ahorro.class, id);
        s.close();
        return a;
    }

    @Override public void run() {}
    @Override public void setFlag(boolean flag) {}
}