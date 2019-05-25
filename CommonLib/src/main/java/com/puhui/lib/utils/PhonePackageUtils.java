package com.puhui.lib.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.math.BigInteger;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2019/4/19.
 * Description:
 * Modified:
 */

@SuppressWarnings(value = {"unused", "unchecked"})
public class PhonePackageUtils {
    public static final String DEFAULT_APK_VERSION = "1.0.0";

    public static String getAPKVersion(Context context) {
        String versionName = DEFAULT_APK_VERSION;
        try {
            PackageManager manager = context.getPackageManager();
            android.content.pm.PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getAPKVersionCode(Context context) {
        int versionName = 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getPackageName(Context context) {
        String packageName = DEFAULT_APK_VERSION;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            packageName = info.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageName;
    }

    /**
     * 判断应用安装时间与最近更新的时间
     */
    public static long checkApkInstallTime(Context context, String packageName) {
        try {
            //QQ：com.tencent.mobileqq     微信：com.tencent.mm
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            //应用装时间
            long firstInstallTime = packageInfo.firstInstallTime;
            //应用最后一次更新时间
            long lastUpdateTime = packageInfo.lastUpdateTime;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            DMLog.e("first install time : " + simpleDateFormat.format(new Date(firstInstallTime)) +
                    " last update time :" + simpleDateFormat.format(new Date(lastUpdateTime)));

            return firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 判断是否安装了支付宝
     *
     * @param applicationName 应用名称 Uri  如支付宝：alipays://
     * @return true 为已经安装
     */
    public static boolean hasApplication(Context context, String applicationName) {
        PackageManager manager = context.getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse(applicationName));
        List<ResolveInfo> list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }

    /***
     * 关闭软键盘
     */
    public static void closeKeyboard(Activity context, EditText... editTexts) {
        View view = context.getWindow().getDecorView();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && inputMethodManager != null) {
            if (null != editTexts) {
                for (EditText editText : editTexts) {
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            } else {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    public static void hideKeyboard(Activity context, IBinder token) {
        if (token != null) {
            InputMethodManager mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (mInputMethodManager != null) {
                mInputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 弹出键盘
     */
    public static void showKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, 0);
        }
    }

    /**
     * 判断键盘是否显示
     * 经测试，这个inputMethodManager.isActive(editText)方法，只要editText有焦点，它就返回true... 但是并不是EditText获得焦点，键盘就会弹出
     * 所以想要判断软键盘是否在显示，总的通过动态计算布局来解决。
     */
    public static boolean isSoftShowing(Activity context) {
        //获取当前屏幕内容的高度
        int screenHeight = context.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom - getSoftButtonsBarHeight(context) != 0;
    }

    /**
     * 底部虚拟按键栏的高度
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getSoftButtonsBarHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

//        Uri uri;
//        if (Build.VERSION.SDK_INT >= 24) {
//            //Android7.0打开本地文件失败android.os.FileUriExposedException
//            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);  //增加读写权限
//            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.navston.shaxi.fileprovider", new File(apkPath));
//        } else {
//            uri = Uri.fromFile(new File(apkPath));
//        }
        Uri uri = Uri.fromFile(new File(apkPath));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        context.startActivity(intent);
    }

    /**
     * <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
     * 增加申请权限，有这一句足矣，如果想写的更好的话，加上权限判断呗
     * 判断是否是8.0系统,是的话需要获取此权限，判断开没开，没开的话处理未知应用来源权限问题,否则直接安装 
     */
    private void checkCanInstall(Activity context, String apkPath) {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean canInstall = context.getPackageManager().canRequestPackageInstalls();
            if (canInstall) {
                installApk(context, apkPath);//安装应用的逻辑(写自己的就可以)  
            } else {
                //请求安装未知应用来源的权限  
                ActivityCompat.requestPermissions(context, new String[]{
                        Manifest.permission.REQUEST_INSTALL_PACKAGES}, 0);
            }
        } else {
            installApk(context, apkPath);
        }
    }

    public static void startNewApp(Context context, String packageName, String className) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startNewApp(Context context, String packageName) {
        // 通过包名获取要跳转的app，创建intent对象
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        // 这里如果intent为空，就说名没有安装要跳转的应用嘛
        if (intent != null) {
            // 这里跟Activity传递参数一样的嘛，不要担心怎么传递参数，还有接收参数也是跟Activity和Activity传参数一样
//            intent.putExtra("name", "Liu xiang");
//            intent.putExtra("birthday", "1983-7-13");
            context.startActivity(intent);
        }
    }

//    public static void startNewApp(Context context, String packageName) {
//        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
//        PackageInfo packageinfo = null;
//        try {
//            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        if (packageinfo == null) {
//            return;
//        }
//
//        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
//        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
//        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        resolveIntent.setPackage(packageinfo.packageName);
//
//        // 通过getPackageManager()的queryIntentActivities方法遍历
//        List<ResolveInfo> resolveinfoList = context.getPackageManager()
//                .queryIntentActivities(resolveIntent, 0);
//
//        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
//        if (resolveinfo != null) {
//            // packagename = 参数packname
////            String packageName = resolveinfo.activityInfo.packageName;
//            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
//            String className = resolveinfo.activityInfo.name;
//            // LAUNCHER Intent
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//            // 设置ComponentName参数1:packagename参数2:MainActivity路径
//            ComponentName cn = new ComponentName(packageName, className);
//
//            intent.setComponent(cn);
//            context.startActivity(intent);
//        }
//    }

    /**
     * 根据包名判断是否已安装APP
     */
    public static boolean hasInstalledApk(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                if (packageInfos.get(i).packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * 跳转到当前应用的详情界面，然后进入该应用的权限管理界面
     */
    public static void gotoSecuritySetting(Context activity) {
        if (null != activity) {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));

//            localIntent.setAction(Settings.ACTION_SETTINGS);
            activity.startActivity(localIntent);
        }
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    /**
     * 获取通知栏权限是否开启
     * return  true:开启了通知权限; false:未开启
     */
    public static boolean isNotificationEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();

//        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        ApplicationInfo appInfo = context.getApplicationInfo();
//        String pkg = context.getApplicationContext().getPackageName();
//        int uid = appInfo.uid;
//
//        Class appOpsClass;
//        try {
//            appOpsClass = Class.forName(AppOpsManager.class.getName());
//            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
//            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
//
//            int value = (Integer) opPostNotificationValue.get(Integer.class);
//            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
    }

    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     * 经测：华为P7手机、红米note4可用
     */
    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public static boolean ishasSimCard(Context context) {
        try {
            TelephonyManager telMgr = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telMgr.getSimState();
            boolean result = true;
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    result = false; // 没有SIM卡
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    result = false;
                    break;
            }
            DMLog.e("ishasSimCard", result ? "有SIM卡" : "无SIM卡");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String intToIp(int ip) {
//        return (ip & 0xFF) + "." +
//                ((ip >> 8) & 0xFF) + "." +
//                ((ip >> 16) & 0xFF) + "." +
//                (ip >> 24 & 0xFF);
//        return Formatter.formatIpAddress(ip);

        try {
            byte[] ipAddress = BigInteger.valueOf(ip).toByteArray();
            InetAddress inetAddress = InetAddress.getByAddress(ipAddress);
            return inetAddress.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Formatter.formatIpAddress(ip);
    }

    public static long ipAddressToInt(String ip) {
        String[] items = ip.split("\\.");
        return Long.valueOf(items[0]) << 24
                | Long.valueOf(items[1]) << 16
                | Long.valueOf(items[2]) << 8
                | Long.valueOf(items[3]);
    }

    @SuppressLint("HardwareIds")
    public static boolean checkDeviceInfo(final Activity activity) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(TELEPHONY_SERVICE);
            String imei = "";
            if (null != telephonyManager) {
                imei = telephonyManager.getDeviceId();
            }
            WifiManager wifiManager = (WifiManager)
                    activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            String macAddress;
            String phoneIp;
            if (null != wifiManager) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                macAddress = wifiInfo.getMacAddress();
                phoneIp = intToIp(wifiInfo.getIpAddress());
            }

            //华为手机如果没有手机设备信息权限，则返回的是0000...
            if (TextUtils.isEmpty(imei) || imei.contains("00000000")) {
                return false;
            }
        } catch (SecurityException e) {
            return false;
        }
        String deviceName = Build.MODEL; // 设备型号
        int version_sdk = Build.VERSION.SDK_INT; // 设备SDK版本
        String version_release = Build.VERSION.RELEASE; // 设备的系统版本

        return true;
    }

    /**
     * 获取当前可连接Wifi列表
     */
    public static List<?> getAvailableNetworks(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiList = null;
        if (wifiManager != null) {
            wifiList = wifiManager.getScanResults();
        }
        return wifiList;
    }

    /**
     * 获取已连接的Wifi路由器的Mac地址
     */
    public static String getConnectedWifiMacAddress(Context context) {
        String connectedWifiMacAddress = null;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiList;

        if (wifiManager != null) {
            wifiList = wifiManager.getScanResults();
            WifiInfo info = wifiManager.getConnectionInfo();
            if (wifiList != null && info != null) {
                for (int i = 0; i < wifiList.size(); i++) {
                    ScanResult result = wifiList.get(i);
                    if (info.getBSSID().equals(result.BSSID)) {
                        connectedWifiMacAddress = result.BSSID;
                        //Mac地址 + Wifi名称  基本(99.95%)可以做到不会重复
                        DMLog.e(PhonePackageUtils.class.getSimpleName(), connectedWifiMacAddress);
                        DMLog.e(PhonePackageUtils.class.getSimpleName(), "wifiInfo.getSSID() = " + info.getSSID());
                    }
                }
            }
        }
        return connectedWifiMacAddress;
    }


    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
}
