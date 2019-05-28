package com.esky.proxy_core;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {
    public static byte[] getBytes(File file) {
        try {
            RandomAccessFile accessFile = new RandomAccessFile(file, "r");
            byte[] buffer = new byte[(int) accessFile.length()];
            accessFile.readFully(buffer);
            accessFile.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射获得 指定对象(当前-》父类-》父类...)中的 成员属性
     */
    public static Field getField(Object instance, String name) {
        Class clazz = instance.getClass();
        while (null != clazz) {
            try {
                Field declaredField = clazz.getDeclaredField(name);
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                return declaredField;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 反射获得 指定对象(当前-》父类-》父类...)中的 函数
     */
    public static Method getMethod(Object instance, String name, Class... parameterTypes) {
        Class clazz = instance.getClass();
        while (clazz != null) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
