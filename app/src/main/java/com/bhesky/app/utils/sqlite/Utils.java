package com.bhesky.app.utils.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.bhesky.app.utils.sqlite.annotation.Entity;
import com.bhesky.app.utils.sqlite.annotation.Property;
import com.puhui.lib.utils.DMLog;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Utils {

    /**
     * 拼接创建表的Sql语句
     *
     * @param tableName   表名
     * @param entityClass 实体类的类型
     * @return 建表sql
     */
    static String getCreateTableSql(String tableName, Class<?> entityClass) {
        //create table if not exists tableName (_id integer, name varchar(20), pwd varchar(32))
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName);
        stringBuffer.append(" (");
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            Class<?> type = field.getType();
            //获取到成员变量的注解的值(表字段名)  忽略默认字段: $change serialVersionUID
            String fieldName = Utils.getFiledName(field);
            if (fieldName.equals("$change") || fieldName.equals("serialVersionUID")) {
                continue;
            }

            if (type == String.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" text, ");
            } else if (type == Integer.class || type == int.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" integer, ");  //是否需要大写？？？
            } else if (type == Double.class || type == double.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" double, ");
            } else if (type == Float.class || type == float.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" float, ");
            } else if (type == Long.class || type == long.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" long, ");
            } else if (type == Boolean.class || type == boolean.class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" varchar(5), ");  //"true", "false"  cursor没有getBoolean()方法
            } else if (type == byte[].class) {
                stringBuffer.append(fieldName);
                stringBuffer.append(" blob, ");
            }
        }
        if (stringBuffer.toString().endsWith(", ")) {
            stringBuffer.delete(stringBuffer.length() - 2, stringBuffer.length());
        }

        stringBuffer.append(")");
        DMLog.e(stringBuffer.toString());
        return stringBuffer.toString();
    }

    static String getFiledName(Field field) {
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
     * 把所有要操作的数据从map中转移到contentValues
     *
     * @param map 表字段名 : 表字段值
     */
    static ContentValues getContentValues(Map<String, Object> map) {
        //创建一个ContentValues
        ContentValues contentValues = new ContentValues();
        //拿到map的keySet
        Set<String> strings = map.keySet();
        Object value; //字段值
        Class clazz;  //字段类型
        for (String key : strings) {
            value = map.get(key);
            if (null != value) {
                clazz = value.getClass();
                if (clazz == Double.class || clazz == double.class) {
                    contentValues.put(key, (Double) value);
                } else if (clazz == Integer.class || clazz == int.class) {
                    contentValues.put(key, (Integer) value);
                } else if (clazz == Long.class || clazz == long.class) {
                    contentValues.put(key, (Long) value);
                } else if (clazz == Float.class || clazz == float.class) {
                    contentValues.put(key, (Float) value);
                } else if (clazz == Byte.class || clazz == byte.class) {
                    contentValues.put(key, (Byte) value);
                } else if (clazz == Byte[].class || clazz == byte[].class) {
                    contentValues.put(key, (byte[]) value);
                } else if (clazz == Short.class || clazz == short.class) {
                    contentValues.put(key, (Short) value);
                } else if (clazz == Boolean.class || clazz == boolean.class) {
                    //Cursor没有获取boolean类型数据的方法 在Sqlite中布尔类型的数据可以用0 1表示
                    //与该语句对应："true".equalsIgnoreCase(query.getString(query.getColumnIndex(columnName)));
                    contentValues.put(key, String.valueOf(value));
                } else if (clazz == String.class) {
                    contentValues.put(key, (String) value);
                }
            }
        }
        return contentValues;
    }

    /**
     * 解析Cursor, 将数据放入集合
     */
    static <T> void parseCursor(Cursor cursor, List<T> entityList, Map<String, Field> cacheMap, Class<T> entityClass) {
        String[] columnNames = cursor.getColumnNames();
        Set<String> strings = cacheMap.keySet();
        Field field;
        String fieldName;
        T entity;
        while (cursor.moveToNext()) {
            try {
                entity = entityClass.newInstance();
                for (String key : strings) {
                    field = cacheMap.get(key);
                    fieldName = getFiledName(field);
                    for (String columnName : columnNames) {
                        if (columnName.equals(fieldName)) {
                            field.setAccessible(true);
                            field.set(entity, getFiledValue(cursor, columnName, field.getType()));
                            break;
                        }
                    }
                }
                entityList.add(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
    }

    static Object getFiledValue(Cursor query, String columnName, Class<?> type) {
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
        } else if (type == Boolean.class || type == boolean.class) {
            //Cursor没有获取boolean类型数据的方法
            return "true".equalsIgnoreCase(query.getString(query.getColumnIndex(columnName)));
        } else if (type == byte[].class || type == Byte[].class) {
            return query.getBlob(query.getColumnIndex(columnName));
        } else if (type == Byte.class || type == byte.class) {
            return query.getInt(query.getColumnIndex(columnName));   //todo ?????
        } else if (type == Short.class || type == short.class) {
            return query.getShort(query.getColumnIndex(columnName));
        }   //还有Date类型
        return new Object();
    }

    /**
     * 获取到对象中的属性值，并且按contentValues的格式存储起来
     */
    static <T> Map<String, Object> getKeyAndValue(Map<String, Field> cacheMap, T entity) {
        Map<String, Object> kayAndValue = new ArrayMap<>();  //表字段名 : 表字段值
        //从缓存map中获取到成员变量
        Set<String> fieldNames = cacheMap.keySet();
        Field next;
        for (String fieldName : fieldNames) {
            next = cacheMap.get(fieldName);
            next.setAccessible(true);
            try {
                //获取属性值
                Object value = next.get(entity);
                kayAndValue.put(fieldName, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return kayAndValue;
    }

    /**
     * 根据对象类型通过反射获取对应的数据库表名
     */
    static <T> String getTableName(Class<T> entityType) {
        String tableName;
        Entity annotation = entityType.getAnnotation(Entity.class);
        if (null != annotation) {
            tableName = annotation.value();
            if (TextUtils.isEmpty(tableName)) {
                tableName = entityType.getSimpleName();
            }
        } else {
            tableName = entityType.getSimpleName();
        }
        return tableName;
    }
}
