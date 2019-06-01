package com.esky.proxy_core;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyApplication extends Application {
    //定义好解密后的文件存放路径
    private String app_name;
    private String app_version;
    private boolean isUseEncrypt = false;

    /**
     * ActivityThread在创建Application之后调用的第一个方法，
     * 可以在这个方法中进行解密，同时把dex交给android去加载
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (!isUseEncrypt) {
            return;
        }

        //获取用户填入的metaData;
        getMetaData();

        //得到当前加密了的apk文件
        File apkFile = new File(getApplicationInfo().sourceDir);

        //把apk解压  对用户来说，app_name + "_" + app_version目录中的内容需要root权限才能使用
        File versionDir = getDir(app_name + "_" + app_version, MODE_PRIVATE);
        System.out.println("DMLog*******  versionDir = " + versionDir.getAbsolutePath());
        File appDir = new File(versionDir, "app");
        File dexDir = new File(appDir, "dexDir");

        //得到我们需要加载的dex文件
        List<File> dexFiles = new ArrayList<>();
        unZipFile(dexFiles, apkFile, appDir, dexDir);

        //把解密后的文件加载到系统
        loadDex(dexFiles, versionDir);
    }

    private void unZipFile(List<File> dexFiles, File apkFile, File appDir, File dexDir) {
        //进行解密(最好做MD5文件校验)    这里简单化只判断是否存在或是否目录下为空
        if (!dexDir.exists() || dexDir.list() == null || dexDir.list().length == 0) {
            System.out.println("DMLog*******  dexDir.exists() = " + dexDir.exists());
            System.out.println("DMLog*******  dexDir.list() == null is " + (dexDir.list() == null));
            if (dexDir.list() != null) {
                System.out.println("DMLog*******  dexDir.list().length == 0 is " + (dexDir.list().length == 0));
            }

            //把apk解压到appDir
            ZipUtils.unZip(apkFile, appDir);
            //获取目录下所有的文件
            File[] files = appDir.listFiles();
            for (File file : files) {
                String name = file.getName();
                //classes.dex 主包
                if (name.endsWith(".dex") && !TextUtils.equals(name, "classes.dex")) {
                    try {
                        AESUtils.init(AESUtils.DEFAULT_PWD);
                        byte[] bytes = ReflectUtils.getBytes(file);
                        byte[] result = AESUtils.decrypt(bytes);
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(result);
                        fos.flush();
                        fos.close();
                        dexFiles.add(file);

                        //保存文件到dexDir
                        FileUtils.saveFile(dexDir, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("DMLog*******  " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("DMLog*******  dexDir exists");
            Collections.addAll(dexFiles, dexDir.listFiles());
//            for (File file : dexDir.listFiles()) {
//                dexFiles.add(file);
//            }
        }
    }

    private void loadDex(List<File> dexFiles, File versionDir) {
        try {
            //1、获取 pathList
            Field pathListField = ReflectUtils.getField(getClassLoader(), "pathList");
            Object pathList = pathListField.get(getClassLoader());

            //2、获取数组dexElements
            Field dexElementsField = ReflectUtils.getField(pathList, "dexElements");
            Object[] dexElements = (Object[]) dexElementsField.get(pathList);

            //3、反射得到初始化dexElements的方法
            Method makeDexElements = ReflectUtils.getMethod(pathList, "makePathElements", List.class, File.class, List.class);
            ArrayList<IOException> suppressedException = new ArrayList<>();  //照抄源码里面的
            Object[] addElements = (Object[]) makeDexElements.invoke(pathList, dexFiles, versionDir, suppressedException);
            System.out.println("DMLog*******  makeDexElements has got");

            //4、合并数组
            Object[] newElements = (Object[]) Array.newInstance(dexElements.getClass().getComponentType(),
                    dexElements.length + addElements.length);
            System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
            System.arraycopy(addElements, 0, newElements, dexElements.length, addElements.length);
            System.out.println("DMLog*******  newElements has got");

            //5、替换classLoader中的element数组
            dexElementsField.set(pathList, newElements);
            System.out.println("DMLog*******  5、替换classLoader中的element数组");
        } catch (IllegalAccessException | NullPointerException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("DMLog*******  loadDex error");
        }
    }

    private void getMetaData() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appInfo.metaData;
            if (null != metaData) {
                if (metaData.containsKey("app_name")) {
                    app_name = metaData.getString("app_name");
                    System.out.println("DMLog*******  app_name = " + app_name);
                }
                if (metaData.containsKey("app_version")) {
                    app_version = metaData.getString("app_version");
                    System.out.println("DMLog*******  app_version = " + app_version);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DMLog*******  getMetaData() error ");
        }
    }
}
