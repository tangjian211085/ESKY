package com.bhesky.app.services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.text.TextUtils;

import java.util.List;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2019/4/27.
 * Description:
 * Modified:
 */
public class Watcher {

    static {
        System.loadLibrary("native-lib");
    }

    private static native void createWatcher(String userId);

    private static native void connectMonitor();

    public static void startService(Context context) {
        if (!isServiceRunning(context, RemoteService.class.getName())) {
            context.startService(new Intent(context, RemoteService.class));
        }
        createWatcher(String.valueOf(Process.myUid()));
        connectMonitor();

//        DMLog.e( "isServiceRunning() = " + isServiceRunning(context, LocalService.class.getName()));
//        if (!isServiceRunning(context, LocalService.class.getName())) {
//            createWatcher(String.valueOf(Process.myUid()));
//            connectMonitor();
//        }
    }

    private static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null != am) {
            List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(10);
            for (ActivityManager.RunningServiceInfo runningService : runningServices) {
                if (TextUtils.equals(runningService.service.getClassName(), serviceName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
