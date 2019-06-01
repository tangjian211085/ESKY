package com.bhesky.app.utils.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    //列名, 即表字段名 建表时使用
    String columnName();

    //数据库升级时用到， 发生数据库表该字段时， 某个字段对应在数据库原来的名字
    String oldColumnName() default "";
}
