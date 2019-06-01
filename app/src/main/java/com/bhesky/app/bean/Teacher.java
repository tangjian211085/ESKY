package com.bhesky.app.bean;

import com.bhesky.app.utils.sqlite.annotation.Entity;
import com.bhesky.app.utils.sqlite.annotation.Property;

@Entity(value = "table_teacher")
public class Teacher {
    private String name;

    @Property(columnName = "courseName")
    private String kecheng;

    public Teacher(String name, String kecheng) {
        this.name = name;
        this.kecheng = kecheng;
    }

    public String getKecheng() {
        return kecheng;
    }

    public void setKecheng(String kecheng) {
        this.kecheng = kecheng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    @Property(columnName = "course", oldColumnName = "courseName")
//    private String course;  //将原来的kecheng(courseName)字段改成现在的course
//
//    public Teacher(String name, String course) {
//        this.name = name;
//        this.course = course;
//    }
//
//    public String getCourse() {
//        return course;
//    }
//
//    public void setCourse(String course) {
//        this.course = course;
//    }
}
