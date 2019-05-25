package com.puhui.lib.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/6/16.
 * Description:
 * Modified By:
 */

public class FileUtils {
    public static void deleteFilesInFolder(File folder) throws Throwable {
        if (folder != null && folder.exists()) {
            if (folder.isFile()) {
                folder.delete();
            } else {
                String[] names = folder.list();
                if (names != null && names.length > 0) {
                    String[] arr$ = names;
                    int len$ = names.length;

                    for (int i$ = 0; i$ < len$; ++i$) {
                        String name = arr$[i$];
                        File f = new File(folder, name);
                        if (f.isDirectory()) {
                            deleteFilesInFolder(f);
                        } else {
                            f.delete();
                        }
                    }
                }
            }
        }
    }

    public static void deleteFileAndFolder(File folder) throws Throwable {
        if (folder != null && folder.exists()) {
            if (folder.isFile()) {
                folder.delete();
            } else {
                String[] names = folder.list();
                if (names != null && names.length > 0) {
                    String[] arr$ = names;
                    int len$ = names.length;

                    for (int i$ = 0; i$ < len$; ++i$) {
                        String name = arr$[i$];
                        File f = new File(folder, name);
                        if (f.isDirectory()) {
                            deleteFileAndFolder(f);
                        } else {
                            f.delete();
                        }
                    }
                    folder.delete();
                } else {
                    folder.delete();
                }
            }
        }
    }

    public static long getFileSize(String path) throws Throwable {
        if (TextUtils.isEmpty(path)) {
            return 0L;
        } else {
            File file = new File(path);
            return getFileSize(file);
        }
    }

    public static long getFileSize(File file) throws Throwable {
        if (!file.exists()) {
            return 0L;
        } else if (!file.isDirectory()) {
            return file.length();
        } else {
            String[] names = file.list();
            int size = 0;

            for (int i = 0; i < names.length; ++i) {
                File f = new File(file, names[i]);
                size = (int) ((long) size + getFileSize(f));
            }

            return (long) size;
        }
    }

    public static boolean saveObjectToFile(String filePath, Object object) {
        if (!TextUtils.isEmpty(filePath)) {
            File cacheFile = null;

            try {
                cacheFile = new File(filePath);
                if (cacheFile.exists()) {
                    cacheFile.delete();
                }

                if (!cacheFile.getParentFile().exists()) {
                    cacheFile.getParentFile().mkdirs();
                }

                cacheFile.createNewFile();
            } catch (Throwable var6) {
                var6.printStackTrace();
                cacheFile = null;
            }

            if (cacheFile != null) {
                try {
                    FileOutputStream t = new FileOutputStream(cacheFile);
                    GZIPOutputStream gzos = new GZIPOutputStream(t);
                    ObjectOutputStream oos = new ObjectOutputStream(gzos);
                    oos.writeObject(object);
                    oos.flush();
                    oos.close();
                    return true;
                } catch (Throwable var7) {
                    var7.printStackTrace();
                }
            }
        }

        return false;
    }

    public static Object readObjectFromFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File cacheFile = null;

            try {
                cacheFile = new File(filePath);
                if (!cacheFile.exists()) {
                    cacheFile = null;
                }
            } catch (Throwable var6) {
                var6.printStackTrace();
                cacheFile = null;
            }

            if (cacheFile != null) {
                try {
                    FileInputStream t = new FileInputStream(cacheFile);
                    GZIPInputStream gzis = new GZIPInputStream(t);
                    ObjectInputStream ois = new ObjectInputStream(gzis);
                    Object object = ois.readObject();
                    ois.close();
                    return object;
                } catch (Throwable var7) {
                    var7.printStackTrace();
                }
            }
        }

        return null;
    }
}
