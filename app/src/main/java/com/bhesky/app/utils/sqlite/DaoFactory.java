package com.bhesky.app.utils.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;

import com.puhui.lib.utils.DMLog;

/**
 * getWritableDatabase  getReadableDatabase
 * 1、两个方法都是返回读写数据库的对象，但是当磁盘已经满了时，getWritableDatabase会抛异常，
 * 而getReadableDatabase不会报错，它此时不会返回读写数据库的对象，而是仅仅返回一个读数据库的对象。
 * 2、getReadableDatabase会在问题修复后继续返回一个读写的数据库对象。
 * 3、两者都是数据库操作，可能存在延迟等待，所以尽量不要在主线程中调用。
 */
public class DaoFactory {
    private static DaoFactory instance = null;

    private SQLiteDatabase mWritableDataBase;
    private SQLiteDatabase mReadableDataBase;

    //    private String dataBasePath;  //数据库保存位置
    private ArrayMap<Class, Object> mDaoMap = new ArrayMap<>();
    private MySQLiteOpenHelper mOpenHelper;


    public static DaoFactory getInstance() {
        if (null == instance) {
            synchronized (DaoFactory.class) {
                if (null == instance) {
                    instance = new DaoFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 主要用来初始化，并检查是否数据库升级
     *
     * @param sqliteVersion 数据库升级后的版本
     * @param clazz         本次升级数据库需要更新的表
     */
    public void init(Context context, int sqliteVersion, Class... clazz) {
        //根据BuildConfig.VERSION_CODE来确定数据库版本  最好通过自定义build.gradle的属性来配置数据库版本号
        mOpenHelper = new MySQLiteOpenHelper(context, sqliteVersion, clazz);
        mWritableDataBase = mOpenHelper.getWritableDatabase();
        mReadableDataBase = mOpenHelper.getReadableDatabase();
    }

    /**
     * 创建数据库 同时返回该数据库的操作对象  QLiteDatabase.openOrCreateDatabase(dataBasePath, null);
     */
    private DaoFactory() {
    }

    public <T, D extends BaseDao<T>> D getDao(Class<D> daoClazz, Class<T> entityClazz) {
        if (null == mOpenHelper) {
            throw new RuntimeException("method init(Context context...) must be called first");
        }

        Object object = mDaoMap.get(entityClazz);
        if (null != object && object.getClass() == daoClazz) {
            DMLog.e(this.getClass().getCanonicalName(), "reuse object: " + object.toString());
            return (D) object;
        } else {
            try {
                D dao = daoClazz.newInstance();
                //由于init方法是default的，所以别人只能通过getDao方法得到Dao对象   当然通过反射也可以做到
                dao.init(mWritableDataBase, entityClazz);
                mDaoMap.put(entityClazz, dao);
                DMLog.e(this.getClass().getCanonicalName(), dao.toString());
                return dao;
            } catch (InstantiationException | IllegalAccessException e) {
                if (e instanceof InstantiationException) {
                    DMLog.e(getClass().getCanonicalName(), "the dao need a default constructor method without parameters");
                }
                e.printStackTrace();
            }
            return null;
        }
    }

    SQLiteDatabase openWritableDataBase() {
        if (null != mWritableDataBase && mWritableDataBase.isOpen()) {
            return mWritableDataBase;
        }
        mWritableDataBase = mOpenHelper.getWritableDatabase();
        return mWritableDataBase;
    }

    SQLiteDatabase openReadableDataBase() {
        if (null != mReadableDataBase && mReadableDataBase.isOpen()) {
            return mReadableDataBase;
        }
        mReadableDataBase = mOpenHelper.getReadableDatabase();
        return mReadableDataBase;
    }
}
