package com.bhesky.app.bean;

import com.bhesky.app.utils.sqlite.BaseDao;

public class UserDao<T> extends BaseDao<T> {

    public User findUserByIdCard(String idCard) {
        openReadableDataBase();
        closeDataBase();
        return null;
    }
}
