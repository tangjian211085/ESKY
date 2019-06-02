package com.bhesky.app.utils.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;

import com.bhesky.app.utils.sqlite.annotation.Property;
import com.puhui.lib.utils.DMLog;

import java.lang.reflect.Field;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String dbName = "sqlite_test.db";
    private Class[] updateClazzs;

    public MySQLiteOpenHelper(Context context, int version, Class... clazz) {
        // "/mnt/sdcard/database/" +
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/database/" + dbName, null, version);
        updateClazzs = clazz;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * 经测试，升级100000条数据，耗时207ms
     * 将User表增加到20个字段，并插入30万条数据，然后修改字段升级，耗时1082ms、475ms
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        long startTime = System.currentTimeMillis();
        if (null != updateClazzs && updateClazzs.length > 0) {
            String tableName;
            StringBuilder updateSql = new StringBuilder();
            for (Class clazz : updateClazzs) {
                //第一次安装不会执行onUpdate方法
                //判断是否有这张表  用户跨版本升级的话就可能是没有的  没有的话就不用执行了，在调用api时会去建表并插入数据
                tableName = Utils.getTableName(clazz);
                if (haveTable(tableName, db)) {
//                        db.beginTransaction();
                    try {
                        if (updateSql.length() > 0)
                            updateSql = updateSql.delete(0, updateSql.length());  //清空renameSql
                        String tempTableName = tableName.concat("_temp");
//                            // 1、将原有表重命名
                        updateSql.append("ALTER TABLE ").append(tableName).append(" RENAME TO ").append(tempTableName);
                        db.execSQL(updateSql.toString());

                        // 2、创建用户表(各个字段)
                        updateSql = updateSql.delete(0, updateSql.length());
                        updateSql.append(Utils.getCreateTableSql(tableName, clazz));
                        db.execSQL(updateSql.toString());

                        // 3、将旧表中的数据导入到新表
                        updateSql = updateSql.delete(0, updateSql.length());
                        String[] fieldNames = getSameFieldOrColumnName(clazz, tempTableName, db);
                        updateSql.append("INSERT INTO ").append(tableName).append(" (").append(fieldNames[0]).append(")");
                        updateSql.append(" SELECT ").append(fieldNames[1]).append(" FROM ").append(tempTableName);
                        DMLog.e(this.getClass().getCanonicalName(), "将旧表中的数据导入到新表 updateSql = " + updateSql.toString());
                        db.execSQL(updateSql.toString());

                        // 4、删除临时表
                        updateSql = updateSql.delete(0, updateSql.length());
                        updateSql.append("DROP TABLE IF EXISTS ").append(tempTableName);
                        db.execSQL(updateSql.toString());

                        DMLog.e(this.getClass().getCanonicalName(), "是否存在了 " + haveTable(tableName, db));

//                            db.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
//                            db.endTransaction();
                    }
                } else {
                    DMLog.e(this.getClass().getCanonicalName(), "没有这个表");
                }
            }
        }
        DMLog.e(this.getClass().getCanonicalName(), "升级耗时：" + (System.currentTimeMillis() - startTime));
    }

    /**
     * 检测是否可以忽略更新，防止使用者错误的把实际上不需要更新的表也传了进来
     *
     * @param clazz         将要更新的类型
     * @param tempTableName 将要更新的表
     * @return 是否可以忽略更新
     */
    private boolean checkIgnoreUpdate(Class clazz, String tempTableName, SQLiteDatabase db) {
        Cursor cursor = db.query(tempTableName, null,
                null, null, null, null, null, "1");
        boolean isIgnoreUpdate = false;
        if (null != cursor) {
            String fieldName;
            String[] columnNames = cursor.getColumnNames();  //获取表中所有的字段名数组
            Field[] fields = clazz.getDeclaredFields();
            //先决条件，表字段的数量和对象字段的个数必须相等
            if (fields.length == columnNames.length) {
                for (Field field : fields) {
                    isIgnoreUpdate = false;
                    //通过Property注解的columnName属性获取应该保存在数据库中的字段名
                    fieldName = Utils.getFiledName(field);
                    if (fieldName.equals("$change") || fieldName.equals("serialVersionUID")) {
                        continue;
                    }
                    for (String columnName : columnNames) {
                        if (fieldName.equals(columnName)) {
                            isIgnoreUpdate = true;
                            break;
                        }
                    }
                }
                //for执行完了，isIgnoreUpdate为true，则表示clazz的字段个数 与 tempTableName表的字段个数相同，并且对应的名字也是一样的。
                //当然有一种情况：就是使用者故意将clazz对象的不同类型的字段命名为何数据库中一样，这样就会有问题
            }
            cursor.close();
        }
        return isIgnoreUpdate;
    }

    /**
     * 获取当前对象的字段与数据库表中对应字段的字段列表，如："name, age, pwd, idCardNum"
     * *  如: User(name, age, password, idCardNum), table(name, age, password) 那么结果就是name, age, pwd(或password)
     * *
     * *  如：User(name, age, password), table(name, age, password, idCardNum) 那么结果还是name, age, pwd(或password)
     */
    private String[] getSameFieldOrColumnName(Class clazz, String tempTableName, SQLiteDatabase db) throws SQLException {
        Cursor cursor = db.query(tempTableName, null,
                null, null, null, null, null, "1");

        StringBuilder currentSb = new StringBuilder(); //保存当前对象中对应的数据库字段名
        StringBuilder oldSb = new StringBuilder(); //保存当前数据库中对应的数据库字段名
        if (null != cursor) {
            String fieldName;
            String oldFieldName;
            String[] columnNames = cursor.getColumnNames();  //获取表中所有的字段名数组
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //通过Property注解的columnName属性获取应该保存在数据库中的字段名
                fieldName = Utils.getFiledName(field);
                //通过Property注解的oldColumnName属性获取已经保存在数据库中的字段名
                oldFieldName = getFiledOldName(field);
                if (fieldName.equals("$change") || fieldName.equals("serialVersionUID")) {
                    continue;
                }
                for (String columnName : columnNames) {
                    if (oldFieldName.equals(columnName)) {
                        currentSb.append(fieldName).append(",");
                        oldSb.append(columnName).append(",");
                        break;
                    }
                }
            }
            cursor.close();
        }
        if (oldSb.toString().endsWith(",")) {
            oldSb = oldSb.deleteCharAt(oldSb.length() - 1);
        }
        if (currentSb.toString().endsWith(",")) {
            currentSb = currentSb.deleteCharAt(currentSb.length() - 1);
        }
        DMLog.e(this.getClass().getCanonicalName(), "currentSb = " + currentSb.toString());
        DMLog.e(this.getClass().getCanonicalName(), "oldSb = " + oldSb.toString());
        return new String[]{currentSb.toString(), oldSb.toString()};
    }

    /**
     * 获取保存在数据库中的原有列名。
     * 如果发生了表字段改名字，那么该方法就会起作用了。通过Property的oldColumnName()属性设置
     * 如果没有设置的话，那么就采用默认的规则
     */
    private String getFiledOldName(Field field) {
        String fieldName;
        Property annotation = field.getAnnotation(Property.class);
        if (null != annotation) {
            fieldName = annotation.oldColumnName();
            if (TextUtils.isEmpty(fieldName)) {
                fieldName = annotation.columnName();
                if (TextUtils.isEmpty(fieldName)) {
                    fieldName = field.getName();
                }
            }
        } else {
            fieldName = field.getName();
        }
        return fieldName;
    }

    /**
     * 判断数据库表是否存在
     * SELECT name FROM sqlite_master WHERE type=‘table’ AND name=‘table_name’;
     */
    private boolean haveTable(String tableName, SQLiteDatabase sqLiteDatabase) {
        StringBuilder stringBuilder = new StringBuilder();
        //两条语句得到的cursor都不为null   cursor.moveToNext = false
        stringBuilder.append("SELECT name FROM sqlite_master WHERE type='table' AND name='").append(tableName).append("'");
        //  cursor.moveToNext = true 用第一条语句更准   见方法：tableIsExist(tableName, sqliteDatabase)
//        stringBuilder.append("select count(*) from sqlite_master where type ='table' and name = '").append(tableName).append("'");
        Cursor cursor = sqLiteDatabase.rawQuery(stringBuilder.toString(), null);
        boolean haveTable = false;
        if (cursor != null) {
            haveTable = cursor.moveToNext();
            DMLog.e(this.getClass().getCanonicalName(), "cursor.moveToNext() = " + haveTable);
            cursor.close();
        }
        return haveTable;
    }

    /**
     * 判断数据库表是否存在
     * select count(*) from sqlite_master where type ='table' and name = 'tableName'
     */
    @SuppressWarnings("unused")
    private boolean tabIsExist(String tabName, SQLiteDatabase sqLiteDatabase) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        String sql = "select count(*) from sqlite_master where type ='table' and name =?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{tabName.trim()});
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                result = true;
            }
        }
        cursor.close();
        return result;
    }
}
