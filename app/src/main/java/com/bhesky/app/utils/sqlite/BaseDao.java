package com.bhesky.app.utils.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.bhesky.app.utils.sqlite.annotation.Entity;
import com.bhesky.app.utils.sqlite.annotation.Property;
import com.puhui.lib.utils.DMLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseDao<T> implements IBaseDao<T> {
    private SQLiteDatabase mSQLiteDatabase;
    private String tableName;
    private Class<T> entityClass;
    private Map<String, Field> cacheMap; //缓存字段名、成员变量
    private boolean isInit = false;

    protected void init(SQLiteDatabase sqLiteDatabase, Class<T> entityType) {
        this.mSQLiteDatabase = sqLiteDatabase;
        this.entityClass = entityType;

        if (!isInit) {
            isInit = true;
            Entity annotation = entityType.getAnnotation(Entity.class);
            if (null != annotation) {
                tableName = annotation.value();
                if (TextUtils.isEmpty(tableName)) {
                    tableName = entityType.getSimpleName();
                }
            } else {
                tableName = entityType.getSimpleName();
            }

            initCacheMap();
            //创建数据库表
            sqLiteDatabase.execSQL(getCreateTableSql());
        }
    }

    private void initCacheMap() {
        cacheMap = new HashMap<>();
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            String fieldName = getFiledName(field);
            //忽略默认字段: $change serialVersionUID
            if (fieldName.equals("$change") || fieldName.equals("serialVersionUID")) {
                continue;
            }
            cacheMap.put(fieldName, field);
        }
    }

    private String getFiledName(Field field) {
        String fieldName;
        Property annotation = field.getAnnotation(Property.class);
        if (null != annotation) {
            fieldName = annotation.columnName();
            if (TextUtils.isEmpty(fieldName)) {
                fieldName = field.getName();
            }
        } else {
            fieldName = field.getName();
        }
        return fieldName;
    }

    /**
     * @return 创建表的sql语句
     */
    private String getCreateTableSql() {
        //create table if not exists tableName (_id integer, name varchar(20), pwd varchar(32))
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName);
        stringBuffer.append(" (");
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            Class<?> type = field.getType();
            //获取到成员变量的注解的值(表字段名)  忽略默认字段: $change serialVersionUID
            String fieldName = getFiledName(field);
            if (fieldName.equals("$change") || fieldName.equals("serialVersionUID")) {
                continue;
            }

            if (type == String.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" varchar(20),");
            } else if (type == Integer.class || type == int.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" integer,");  //是否需要大写？？？
            } else if (type == Double.class || type == double.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" double,");
            } else if (type == Float.class || type == float.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" float,");
            } else if (type == Long.class || type == long.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" long,");
            } else if (type == byte[].class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" blob,");
            }
        }
        if (stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }

        stringBuffer.append(")");
        DMLog.e(this.getClass().getCanonicalName(), stringBuffer.toString());
        return stringBuffer.toString();
    }


    @Override
    public int insert(T entity) {
        //创建一个map
        Map<String, String> map = getValue(entity);
        ContentValues contentValues = getContentValues(map);
        return (int) mSQLiteDatabase.insert(tableName, null, contentValues);
    }

    @Override
    public T findFirst() {
        Cursor query = mSQLiteDatabase.query(tableName, null, null, null, null, null, null);
        if (query.moveToNext()) {
            String[] columnNames = query.getColumnNames();
            Set<String> strings = cacheMap.keySet();
            try {
                T entity = entityClass.newInstance();

                for (String key : strings) {
                    Field field = cacheMap.get(key);
                    String fieldName = getFiledName(field);
                    for (String columnName : columnNames) {
                        if (columnName.equals(fieldName)) {
                            DMLog.e(this.getClass().getCanonicalName(), "fieldName = " + fieldName);
                            DMLog.e(this.getClass().getCanonicalName(), "columnNames.contains(fieldName) = " + true);

                            field.setAccessible(true);
                            field.set(entity, getFiledValue(query, columnName, field));
                            break;
                        }
                    }
                }
                return entity;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Object getFiledValue(Cursor query, String columnName, Field field) {
        Class<?> type = field.getType();
        if (type == String.class) {
            return query.getString(query.getColumnIndex(columnName));
        } else if (type == Integer.class || type == int.class) {
            return query.getInt(query.getColumnIndex(columnName));
        } else if (type == Double.class || type == double.class) {
            return query.getDouble(query.getColumnIndex(columnName));
        } else if (type == Float.class || type == float.class) {
            return query.getFloat(query.getColumnIndex(columnName));
        } else if (type == Long.class || type == long.class) {
            return query.getLong(query.getColumnIndex(columnName));
        } else if (type == byte[].class) {
            return query.getBlob(query.getColumnIndex(columnName));
        }
        return new Object();
    }

    @Override
    public List<T> findBy(T entity) {
        return null;
    }

    @Override
    public List<T> findAll() {
        List<T> list = new ArrayList<>(5);
        Cursor query = mSQLiteDatabase.query(tableName, null, null, null, null, null, null);
        while (query.moveToNext()) {
            String[] columnNames = query.getColumnNames();
            Set<String> strings = cacheMap.keySet();
            try {
                Constructor<?>[] constructors = entityClass.getConstructors();
                DMLog.e(this.getClass().getCanonicalName(), "构造方法数量是：" + constructors.length);

                T entity = entityClass.newInstance();

                for (String key : strings) {
                    Field field = cacheMap.get(key);
                    String fieldName = getFiledName(field);
                    for (String columnName : columnNames) {
                        if (columnName.equals(fieldName)) {
                            field.setAccessible(true);
                            field.set(entity, getFiledValue(query, columnName, field));
                            break;
                        }
                    }
                }
                list.add(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 把所有要操作的数据从map中转移到contentValues
     */
    private ContentValues getContentValues(Map<String, String> map) {
        //创建一个ContentValues
        ContentValues contentValues = new ContentValues();
        //拿到map的keySet
        Set<String> strings = map.keySet();
        for (String key : strings) {
            String value = map.get(key);
            if (null != value) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    /**
     * 获取到对象中的属性值，并且按contentValues的格式存储起来
     */
    private Map<String, String> getValue(T entity) {
        Map<String, String> map = new HashMap<>();
        //从缓存map中获取到成员变量
        Iterator<Field> iterator = cacheMap.values().iterator();
        while (iterator.hasNext()) {
            Field next = iterator.next();
            next.setAccessible(true);
            try {
                //获取属性值
                Object obj = next.get(entity);
                if (null != obj) {
                    String value = obj.toString();

                    String fieldName = getFiledName(next);
                    map.put(fieldName, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
