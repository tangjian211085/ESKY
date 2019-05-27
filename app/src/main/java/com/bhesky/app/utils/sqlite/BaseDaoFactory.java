package com.bhesky.app.utils.sqlite;

import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class BaseDaoFactory {
    private static final BaseDaoFactory instance = new BaseDaoFactory();

    private SQLiteDatabase mSQLiteDatabase;
    private String dataBasePath;  //数据库保存位置

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    private BaseDaoFactory() {
        dataBasePath = "/data/data/com.bhesky.app/databases/data.db";
        File file = new File(dataBasePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }

        //创建数据库 同时返回该数据库的操作对象
        mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dataBasePath, null);
    }

    public <T> BaseDao<T> getBaseDao(Class<T> entityClazz) {
        BaseDao<T> baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(mSQLiteDatabase, entityClazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseDao;
    }
}
