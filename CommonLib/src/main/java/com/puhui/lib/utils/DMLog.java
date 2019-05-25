package com.puhui.lib.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * 日志工具类
 *
 * @author tangjian
 */
public class DMLog {
    /**
     * app日志标签
     */
    private static final String TAG = DMLog.class.getSimpleName() + "********";

    /**
     * 是否打印日志
     */
    private static boolean isUseLog = PHConstant.Config.isUseLog;

    public static void v(String tag, String msg, Throwable th) {
        if (isUseLog) {
            Log.v(tag, msg, th);
        }
    }

    public static void d(String tag, String msg, Throwable th) {
        if (isUseLog) {
            Log.d(tag, msg, th);
        }
    }

    public static void i(String tag, String msg, Throwable th) {
        if (isUseLog) {
            Log.i(tag, msg, th);
        }
    }

    public static void w(String tag, String msg, Throwable th) {
        if (isUseLog) {
            Log.w(tag, msg, th);
        }
    }

    public static void e(String tag, String msg, Throwable th) {
        if (isUseLog) {
            Log.e(tag, msg, th);
        }
    }

    public static void v(String tag, String msg) {
        if (isUseLog) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isUseLog) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isUseLog) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isUseLog) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isUseLog) {
            tag = TextUtils.isEmpty(tag) ? TAG : tag;
            Log.e(tag, TAG + msg);
        }
    }

    public static void v(String msg) {
        if (isUseLog) {
            Log.v(DMLog.TAG, msg);
        }
    }

    public static void d(String msg) {
        if (isUseLog) {
            Log.d(DMLog.TAG, msg);
        }
    }

    public static void i(String msg) {
        if (isUseLog) {
            Log.i(DMLog.TAG, msg);
        }
    }

    public static void w(String msg) {
        if (isUseLog) {
            Log.w(DMLog.TAG, msg, null);
        }
    }

    public static void e(String msg) {
        if (isUseLog) {
            Log.e(DMLog.TAG, msg, null);
        }
    }
}