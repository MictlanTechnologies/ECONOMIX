package org.economix.util;

import org.economix.model.usuario.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public final class HibernateUtil
{
    private static SessionFactory sessionFactory;
    private static StandardServiceRegistry registry;

    public static boolean loadRegistry( )
    {
        try
        {
            System.out.println( "HibernateUtil.init()");
            registry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml") // se carga la configuracion hibernate
                    .build();
            System.out.println( "HibernateUtil.registry");
            return registry != null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy( registry );
        }
        return false;
    }

    public static boolean loadSessionFactory( )
    {
        try
        {
            if( registry == null )
            {
                if( !loadRegistry() )
                {
                    return false;
                }
            }
            System.out.println( "HibernateUtil.init.sessionFactory");
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
            System.out.println( "HibernateUtil.sessionFactory");
            return sessionFactory != null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy( registry );
        }
        return false;
    }

    public static StandardServiceRegistry getRegistry( )
    {
        if( registry == null )
        {
            if( !loadRegistry( ) )
            {
                return null;
            }
        }
        return registry;
    }

    public static SessionFactory getSessionFactory( )
    {
        if( sessionFactory == null )
        {
            if( !loadSessionFactory() )
            {
                return null;
            }
        }
        return sessionFactory;
    }

    public static Session getSession( )
    {
        if( sessionFactory == null || sessionFactory.isClosed() )
        {
            if( !loadSessionFactory() )
            {
                return null;
            }
        }
        return sessionFactory.openSession( );
    }

    public static class UsuarioDAO {

        public void save(Usuario u) {
            Session s = HibernateUtil.getSession();
            Transaction tx = null;
            try {
                tx = s.beginTransaction();
                s.persist(u);           // o merge/update según el caso
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null) tx.rollback();
                throw e;
            } finally {
                s.close();
            }
        }

        public List<Usuario> findAll() {
            try (Session s = HibernateUtil.getSession()) {
                return s.createQuery("from Usuario", Usuario.class).getResultList();
            }
        }
    }
}
