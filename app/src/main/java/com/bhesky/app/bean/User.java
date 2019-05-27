package com.bhesky.app.bean;

import com.bhesky.app.utils.sqlite.annotation.Entity;
import com.bhesky.app.utils.sqlite.annotation.Property;

@Entity("user")
public class User {
    private String name;
    private int age;
    private String idCardNum;
    @Property(columnName = "pwd")
    private String password;

    public User(String name, int age, String idCardNum, String password) {
        this.name = name;
        this.age = age;
        this.idCardNum = idCardNum;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
