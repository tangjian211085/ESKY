package com.bhesky.app.utils.sqlite;

import java.util.List;

public interface IBaseDao<T> {
    int insert(T entity);

    int insertAll(List<T> entityList);

    T findFirst();

    List<T> findBy(T entity);

    int queryTotalCount();

    List<T> findPage(int limit);
}
