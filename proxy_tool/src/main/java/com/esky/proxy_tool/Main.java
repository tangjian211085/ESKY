package com.esky.proxy_tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

/**
 * 由于可能Runtime.getRuntime().exec()有问题，可将命令提取出来放在cmd窗口执行
 * 都是绝对路径
 * 一、cmd /c dx --dex --output *****\proxy_tool\temp\classes.dex *****\proxy_tool\temp\classes.jar
 * 二、cmd /c zipalign -v -p 4 *****\outputs\apk\debug\app-unsigned.apk *****\outputs\apk\debug\app-unsigned-aligned.apk
 * 三、cmd /c apksigner sign --ks *****\app\esky.jks --ks-key-alias esky --ks-pass pass:123456 --key-pass pass:123456 --out *****\outputs\apk\debug\app-signed-aligned.apk *****\outputs\apk\debug\app-unsigned-aligned.apk
 * <p>
 * 执行顺序：
 * 1、注释掉 exeSecondStep、exeThirdStep、exeFourthStep、exeFifthStep，执行exeFirstStep
 * 2、在cmd窗口执行一
 * 3、注释掉 exeFirstStep、exeFourthStep、exeFifthStep，执行exeSecondStep、exeThirdStep
 * 4、注释掉 exeFirstStep、exeSecondStep、exeThirdStep、exeFourthStep、exeFifthStep，在cmd窗口执行二、三
 * <p>
 * 如果Runtime.getRuntime().exec()没有问题，可以打开所有步骤注释，一次执行完毕
 * 似乎这样子可以一次性执行完成:  cmd /c D:\Develop\Android\AndroidSDK\build-tools\27.0.2\dx --dex --output
 */
public class Main {
    private static final String buildToolsPath = "D:\\Develop\\Android\\AndroidSDK\\build-tools\\27.0.2\\";

    public static void main(String[] args) throws Exception {
        exeFirstStep();
        exeSecondStep();
        exeThirdStep();
//        exeFourthStep();
//        exeFifthStep();
    }

    /**
     * 1. 制作只包含解密代码的dex文件    proxy_core-debug.aar需要重新build才能生成
     */
    private static void exeFirstStep() throws Exception {
        File aarFile = new File("proxy_core/build/outputs/aar/proxy_core-debug.aar");
        File aarTemp = new File("proxy_tool/temp");
        ZipUtils.unZip(aarFile, aarTemp);
        File classesJar = new File(aarTemp, "classes.jar");
        File classesDex = new File(aarTemp, "classes.dex");

        //dx --dex --output out.dex in.jar  //需要配置环境变量(D:\Develop\Android\AndroidSDK\build-tools\28.0.2)，然后重启AS
        //cmd /c dx --dex --output D:\Develop\Android\Code\Personal_Code\LearningCode\ESKY\proxy_tool\temp\classes.dex D:\Develop\Android\Code\Personal_Code\LearningCode\ESKY\proxy_tool\temp\classes.jar
        StringBuilder stringBuilder = new StringBuilder();
        //windows上需要加上 cmd /c
        stringBuilder.append("cmd /c ").append(buildToolsPath).append("dx --dex --output ").append(classesDex.getAbsolutePath()).append(" ").append(classesJar.getAbsolutePath());
        System.out.println("DMLog*******  " + stringBuilder.toString());
        Process process = Runtime.getRuntime().exec(stringBuilder.toString());
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new RuntimeException("dex error");
        }
        System.out.println("DMLog*******  first step success");
    }

    /**
     * 2、加密APK中所有的dex文件
     */
    private static void exeSecondStep() throws Exception {
        File apkFile = new File("app/build/outputs/apk/debug/app-debug.apk");
        File apkTemp = new File("app/build/outputs/apk/debug/temp");
        ZipUtils.unZip(apkFile, apkTemp);
        //只要dex文件拿出来加密
        File[] dexFiles = apkTemp.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".dex");
            }
        });

        //AES加密
        AESUtils.init(AESUtils.DEFAULT_PWD);
        System.out.println("DMLog*******  " + dexFiles.length);
        for (File file : dexFiles) {
            byte[] result = AESUtils.encrypt(ReflectUtils.getBytes(file));
            FileOutputStream fos = new FileOutputStream(new File(apkTemp, "secret-" + file.getName()));
            fos.write(result);
            fos.flush();
            fos.close();
            file.delete();
        }
        System.out.println("DMLog*******  AES encrypt success");
    }

    /**
     * 3、把dex放入apk解压目录，重新压成apk文件
     */
    private static void exeThirdStep() {
        File apkTemp = new File("app/build/outputs/apk/debug/temp");

        File classesDex = new File("proxy_tool/temp/classes.dex");
        classesDex.renameTo(new File(apkTemp, "classes.dex"));
        File unSignedApk = new File("app/build/outputs/apk/debug/app-unsigned.apk");
        ZipUtils.zipFile(apkTemp, unSignedApk);
        System.out.println("DMLog*******  re zip apk success");
    }

    /**
     * 4、对齐和签名
     */
    private static void exeFourthStep() throws Exception {
        //zipalign -v -p 4 my-app-unsigned.apk my-app-unsigned-aligned.apk
        //cmd /c zipalign -v -p 4 D:\Develop\Android\Code\Personal_Code\LearningCode\ESKY\app\build\outputs\apk\debug\app-unsigned.apk D:\Develop\Android\Code\Personal_Code\LearningCode\ESKY\app\build\outputs\apk\debug\app-unsigned-aligned.apk
        //对齐apk
        File alignedApk = new File("app/build/outputs/apk/debug/app-unsigned-aligned.apk");
        File unSignedApk = new File("app/build/outputs/apk/debug/app-unsigned.apk");
        StringBuilder sb = new StringBuilder();
        sb.append("cmd /c zipalign -v -p 4 ").append(unSignedApk.getAbsolutePath()).append(" ").append(alignedApk.getAbsolutePath());
        System.out.println("DMLog*******  " + sb.toString());
        Process process = Runtime.getRuntime().exec(sb.toString());
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new RuntimeException("zipalign error");
        }
        System.out.println("DMLog*******  align apk success");
    }

    /**
     * 5、重新签名apk
     * 正式apk：  apksigner sign --ks my-release-key.jks --out my-app-release.apk my-app-unsigned-aligned.apk
     * debug apk:  apksigner sign  --ks jks文件地址 --ks-key-alias 别名 --ks-pass pass:jsk密码 --key-pass pass:别名密码 --out  out.apk in.apk
     * cmd /c apksigner sign --ks D:\Develop\Android\Code\Personal_Code\LearningCode\ESKY\app\esky.jks --ks-key-alias esky --ks-pass pass:123456 --key-pass pass:123456 --out D:\Develop\Android\Code\Personal_Code\LearningCode\ESKY\app\build\outputs\apk\debug\app-signed-aligned.apk D:\Develop\Android\Code\Personal_Code\LearningCode\ESKY\app\build\outputs\apk\debug\app-unsigned-aligned.apk
     */
    private static void exeFifthStep() throws Exception {
        File signedApk = new File("app/build/outputs/apk/debug/app-signed-aligned.apk");
        File alignedApk = new File("app/build/outputs/apk/debug/app-unsigned-aligned.apk");

        File jks = new File("app/esky.jks");
        StringBuilder sb1 = new StringBuilder();
        sb1.append("cmd /c apksigner sign --ks ").append(jks.getAbsolutePath())
                .append(" --ks-key-alias esky --ks-pass pass:123456 --key-pass pass:123456 --out ")
                .append(signedApk.getAbsolutePath()).append(" ").append(alignedApk.getAbsolutePath());
        Process process = Runtime.getRuntime().exec(sb1.toString());
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new RuntimeException("resign apk error");
        }
        System.out.println("DMLog*******  all process success");
    }
}
