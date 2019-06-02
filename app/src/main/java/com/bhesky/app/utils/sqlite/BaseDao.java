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

    /**
     * 两种方式耗时对比：
     * <p>
     * insertAllBySqlExpr耗时记录：（该方式一次最多插入500条数据，插入501条数据就报异常：SQLiteException  too many terms in compound SELECT
     * *    100条数据：28ms、27ms    500条数据：60ms  55ms  72ms
     * insertAllByContentValues耗时记录  可以插入任意多条数据 开启事务后速度很快
     * *    100条数据：90ms、84、83、81、82、82、87、73
     * *    500条数据：411ms 323ms 288ms 281ms 262ms 261ms 255ms 250ms 255ms 252ms 259ms 250ms
     */
    @Override
    public synchronized int insertAll(List<T> entityList) {
        if (null == entityList || entityList.size() == 0) {
            return -1;
        }

//        return insertAllByContentValues(entityList);
//        return insertAllBySqlExpr(entityList);
        return entityList.size() > 500
                ? insertAllByContentValues(entityList) : insertAllBySqlExpr(entityList);
    }

    /**
     * 通过sql语句插入数据
     * *    "insert into radiomap (location,ap1,ap2) select 'x=1,y=1',-80,-73 " +
     * *    "union all select 'x=2,y=3',80,40 union all select 'x=3,y=5',30,20 " +
     * *    "union all select 'x=4,y=5',3,2 union all select 'x=30,y=50',30,20 union all select 'x=3,y=5',40,20"
     * *
     * 该方法插入数据耗时记录：（该方式一次最多插入500条数据，插入501条数据就报异常：SQLiteException  too many terms in compound SELECT）
     * *    谷歌模拟器:  100条数据：28ms、27ms、31ms       500条数据：60ms  55ms  72ms
     * *    华为P10:    100条数据：12ms、14、15、14、15    500条数据: 41ms、42、42、44、42、48
     * insertAllByContentValues方法插入数据耗时记录
     * *    100条数据(谷歌模拟器)：90ms、84、83、81、82、82、87、73
     * *    500条数据(谷歌模拟器)：411ms 323ms 288ms 281ms 262ms 261ms 255ms 250ms 255ms 252ms 259ms 250ms
     */
    private int insertAllBySqlExpr(List<T> entityList) {
        int result = -1;
        try {
            if (null != entityList && entityList.size() > 0) {
                StringBuilder expSb = new StringBuilder();
                expSb.append(Utils.createInsertAllBySqlExpr(tableName, cacheMap, entityList));
                long startTime = System.currentTimeMillis();
                openWritableDataBase();
                mWritableDatabase.execSQL(expSb.toString());
                closeDataBase();
                DMLog.e(this.getClass().getCanonicalName(), "插入数据库耗时：" + (System.currentTimeMillis() - startTime));
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
     * *   开启事务10万条数据耗时(逍遥安卓模拟器): 4s、7.5s
     * *   四个字段不开启事务10万条数据耗时(逍遥安卓模拟器): 3分10秒
     * *
     * 将User结构增加到20个字段后，开启事务插入10万数据耗时:
     * * 逍遥安卓模拟器： 9488ms、9418ms、9430ms、10113ms
     * * 华为P10： 6580ms、6278ms、6322ms、6299ms、6323ms、6291ms
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
