package com.bhesky.app.utils.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;

import com.puhui.lib.utils.DMLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在SQLite中查询一个表是否存在的方法：
 * *      SELECT name FROM sqlite_master WHERE type=‘table’ AND name=‘table_name’;
 * table_name就是传进去要查找的表的名字，当然也可以不要。这个语句会返回数据库中的表名字哦！
 */
public class BaseDao<T> implements IBaseDao<T> {
    protected SQLiteDatabase mWritableDatabase;
    protected SQLiteDatabase mReadableDatabase;

    protected String tableName;
    protected Class<T> entityClass;
    protected ArrayMap<String, Field> cacheMap; //缓存(表)字段名、成员变量

    void init(SQLiteDatabase writableDatabase, Class<T> entityType) {
        this.mWritableDatabase = writableDatabase;
        this.entityClass = entityType;
        this.tableName = Utils.getTableName(entityType);

        initCacheMap();
        //创建数据库表
        writableDatabase.execSQL(Utils.getCreateTableSql(tableName, entityClass));
    }

    private void initCacheMap() {
        cacheMap = new ArrayMap<>();
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            String fieldName = Utils.getFiledName(field);  //得到表字段名
            //忽略默认字段: $change serialVersionUID
            if (fieldName.equals("$change") || fieldName.equals("serialVersionUID")) {
                continue;
            }
            cacheMap.put(fieldName, field);
        }
    }

    @Override
    public synchronized int insert(T entity) {
        openWritableDataBase();
        //创建一个map
        Map<String, Object> map = Utils.getKeyAndValue(cacheMap, entity);
        ContentValues contentValues = Utils.getContentValues(map);
        int result = (int) mWritableDatabase.insert(tableName, null, contentValues);
        closeDataBase();
        return result;
    }

    @Override
    public synchronized int insertAll(List<T> entityList) {
//        int result = insertAllBySqlExpr(entityList);  //方法是失败的，sql错误 ??????
        return insertAllByContentValues(entityList);
    }

    /**
     * 通过sql语句插入数据
     * <p>
     * 批量插入数据的两种写法(第二种速度要快几十倍):
     * INSERT INTO 'tablename' ('column1', 'column2') VALUES
     * ('data1', 'data2'), ('data1', 'data2'), ('data1', 'data2'), ('data1', 'data2');
     * <p>
     * INSERT INTO 'tablename' SELECT 'data1' AS 'column1', 'data2' AS 'column2'
     * UNION SELECT 'data3', 'data4'  UNION SELECT 'data5', 'data6'  UNION SELECT 'data7', 'data8'
     */
    private int insertAllBySqlExpr(List<T> entityList) {
        int result = -1;
        try {
            if (null != entityList && entityList.size() > 0) {
                StringBuilder expSb = new StringBuilder();
                expSb.append("insert into ").append(tableName).append(" select ");
                int size = entityList.size();
                for (int i = 0; i < size; i++) {
                    //得到表字段名，对象字段的值
                    Map<String, Object> keyAndValue = Utils.getKeyAndValue(cacheMap, entityList.get(i));
                    Set<String> strings = keyAndValue.keySet();
                    if (i >= 1) {
                        if (i == 1 && expSb.toString().endsWith(",")) {
                            expSb = expSb.deleteCharAt(expSb.length() - 1);
                        }
                        expSb.append(" union select ");
                    }

                    for (String key : strings) {
                        Object value = keyAndValue.get(key);
//                        if (value.getClass() == Double.class || value.getClass() == double.class
//                                || value.getClass() == Integer.class || value.getClass() == int.class
//                                || value.getClass() == Long.class || value.getClass() == long.class
//                                || value.getClass() == Float.class || value.getClass() == float.class
//                                || value.getClass() == Byte.class || value.getClass() == byte.class
//                                || value.getClass() == Byte[].class || value.getClass() == byte[].class
//                                || value.getClass() == Short.class || value.getClass() == short.class
//                                || value.getClass() == String.class) {
                        if (i == 0) {
                            expSb.append(key).append(" as ").append(value).append(",");
                        } else {
                            expSb.append(" ").append(value).append(",");
                        }
                    }
                }
                if (expSb.toString().endsWith(",")) {
                    expSb = expSb.deleteCharAt(expSb.length() - 1);
                }
                DMLog.e(this.getClass().getCanonicalName(), expSb.toString());
                mWritableDatabase.execSQL(expSb.toString());
                result = entityList.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通过ContentValues插入数据
     * User结构: userName password idCardNum age
     * *   开启事务10万条数据耗时: 4s、7.5s
     * *        将User结构增加到20个字段后，插入10万数据耗时9488ms、9418ms、9430ms、10113ms
     * *   四个字段不开启事务10万条数据耗时: 3分10秒
     */
    private int insertAllByContentValues(List<T> entityList) {
        openWritableDataBase();
        int result = -1;
        if (null != entityList && entityList.size() > 0) {
            long startTime = System.currentTimeMillis();
            mWritableDatabase.beginTransaction();
            try {
                for (T entity : entityList) {
                    Map<String, Object> map = Utils.getKeyAndValue(cacheMap, entity);
                    ContentValues contentValues = Utils.getContentValues(map);
                    result = (int) mWritableDatabase.insert(tableName, null, contentValues);
                }
                mWritableDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mWritableDatabase.endTransaction();
                closeDataBase();
            }
            DMLog.e(this.getClass().getCanonicalName(),
                    "插入数据库耗时：" + (System.currentTimeMillis() - startTime) + "ms");
        }
        return result;
    }

    /**
     * 需要一个无参的构造方法
     */
    @Override
    public T findFirst() {
        openReadableDataBase();
        T entity = null;
        Cursor query = mReadableDatabase.query(tableName,
                null, null, null, null, null, null, "1");
        String[] columnNames = query.getColumnNames();
        DMLog.e(this.getClass().getCanonicalName(), columnNames.length + "");
        Set<String> strings = cacheMap.keySet();
        if (query.moveToNext()) {
            try {
                StringBuilder fieldSb = new StringBuilder("fieldName = ");
                entity = entityClass.newInstance();
                for (String key : strings) {
                    Field field = cacheMap.get(key);
                    String fieldName = Utils.getFiledName(field);
                    for (String columnName : columnNames) {
                        if (columnName.equals(fieldName)) {
                            fieldSb.append(fieldName).append(", ");
                            field.setAccessible(true);
                            field.set(entity, Utils.getFiledValue(query, columnName, field.getType()));
                            break;
                        }
                    }
                }
                if (fieldSb.toString().endsWith(", ")) {  //打印出字段名
                    DMLog.e(this.getClass().getCanonicalName(), fieldSb.deleteCharAt(fieldSb.length() - 2).toString());
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        closeDataBase();
        return entity;
    }

    @Override
    public List<T> findBy(T entity) {
        openReadableDataBase();
        closeDataBase();
        return null;
    }

    @Override
    public List<T> findPage(int limit) {
        openReadableDataBase();

        List<T> list = new ArrayList<>(5);
        Cursor query = mReadableDatabase.query(tableName,
                null, null, null, null, null, null, String.valueOf(limit));
        Utils.parseCursor(query, list, cacheMap, entityClass);
        closeDataBase();
        return list;
    }

    /**
     * User 20个字段
     * 查询30万条数据耗时：1ms, 但是在解析数据时特别特别的耗时parseCursor: 68289ms
     */
    @Override
    public int queryTotalCount() {
        openReadableDataBase();
        long startTime = System.currentTimeMillis();
        Cursor query = mReadableDatabase.query(tableName,
                null, null, null, null, null, null);
        DMLog.e(this.getClass().getCanonicalName(), "查询所有耗时：" + (System.currentTimeMillis() - startTime));
        int totalCount = query.getCount();
        query.close();
        closeDataBase();
        return totalCount;

//        List<T> list = new ArrayList<>(5);
//        Utils.parseCursor(query, list, cacheMap, entityClass);
//        DMLog.e(this.getClass().getCanonicalName(), "解析数据耗时：" + (System.currentTimeMillis() - startTime));
//        closeDataBase();
//        return list;
    }

    protected void openWritableDataBase() {
        mWritableDatabase = DaoFactory.getInstance().openWritableDataBase();
    }

    protected void openReadableDataBase() {
        mReadableDatabase = DaoFactory.getInstance().openReadableDataBase();
    }

    /**
     * 关闭数据库连接
     */
    protected void closeDataBase() {
        if (null != mWritableDatabase && mWritableDatabase.isOpen()) {
            mWritableDatabase.close();
        }

        if (null != mReadableDatabase && mReadableDatabase.isOpen()) {
            mReadableDatabase.close();
        }
    }

}
