package com.esky.proxy_tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File temp : files) {
                deleteFile(temp);
            }
        } else {
            file.delete();
        }
    }

    /**
     * 解压zip文件至desDir目录
     *
     * @param zipSource 需要被解压的文件
     * @param desDir    解压目录
     */
    public static void unZip(File zipSource, File desDir) {
        deleteFile(desDir);
        try {
            ZipFile zipFile = new ZipFile(zipSource);
            //得到zip文件中的每一个条目
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            //遍历
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                //zip中文件、目录名
                String name = zipEntry.getName();
                //原来的签名文件不需要了
                if (name.equals("META-INF/CERT.RSA") || name.equals("META-INF/CERT.SF") || name.equals("META-INF/MANIFEST.MF")) {
                    System.out.println("DMLog*******  name = " + name);
                    continue;
                }
                //空目录不管
                if (!zipEntry.isDirectory()) {
                    File file = new File(desDir, name);
                    //创建目录
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    //写文件
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = zipFile.getInputStream(zipEntry);
                    byte[] bytes = new byte[2048];
                    int len;
                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                    }
                    fos.close();
                    is.close();
                }
            }
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩文件或目录为zip文件
     *
     * @param srcFile 待压缩的文件
     * @param desDir  压缩后保存的文件目录
     */
    public static void zipFile(File srcFile, File desDir) {
        desDir.delete();
        //对输出文件做CRC32校验
        try {
            CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(desDir), new CRC32());
            ZipOutputStream zos = new ZipOutputStream(cos);
            //压缩
            compress(srcFile, zos, "");
            zos.flush();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DMLog*******  zipFile error");
        }
    }

    /**
     * 递归压缩文件
     *
     * @param srcFile  需要添加的目录/文件
     * @param zos      zip输出流
     * @param savePath 递归子目录时的完整目录 如 lib/x86
     */
    private static void compress(File srcFile, ZipOutputStream zos, String savePath) {
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            for (File file : files) {
                compress(file, zos, savePath + srcFile.getName() + "/");
            }
        } else {
            compressFile(srcFile, zos, savePath);
        }
    }

    private static void compressFile(File srcFile, ZipOutputStream zos, String dir) {
        FileInputStream is = null;
        String fullName = dir + srcFile.getName();
        //需要去掉temp
        String[] fileNames = fullName.split("/");
        StringBuilder sb = new StringBuilder();
        if (fileNames.length > 1) {
            for (int i = 1; i < fileNames.length; ++i) {
                sb.append("/");
                sb.append(fileNames[i]);
            }
        } else {
            sb.append("/");
        }
        //添加一个zip条目
        ZipEntry zipEntry = new ZipEntry(sb.substring(1));
        System.out.println("DMLog*******  " + sb.substring(1));
        try {
            zos.putNextEntry(zipEntry);
            //读取条目输出到zip中
            is = new FileInputStream(srcFile);
            int len;
            byte[] buffer = new byte[2048];
            while ((len = is.read(buffer)) != -1) {
                zos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                zos.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
