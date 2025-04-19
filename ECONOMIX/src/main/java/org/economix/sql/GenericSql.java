package org.economix.sql;

import java.util.List;

public interface GenericSql<T>
{
    List<T> findAll();
    boolean save(T t);
    boolean update(T t);
    boolean delete(T t);
    T findById( Integer id );
}
