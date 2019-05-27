package com.bhesky.app.utils.sqlite;

import java.util.List;

public interface IBaseDao<T> {
    int insert(T entity);

    T findFirst();

    List<T> findBy(T entity);

    List<T> findAll();
}
