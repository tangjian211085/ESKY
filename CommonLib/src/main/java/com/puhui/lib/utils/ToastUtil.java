package com.puhui.lib.utils;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/***
 * Created by tangjian on 16-5-17.
 */

@SuppressWarnings("all")
public class ToastUtil {
    private static ToastUtil util;

    public static ToastUtil getInstant() {
        if (util == null) {
            synchronized (ToastUtil.class) {
                if (null == util) {
                    util = new ToastUtil();
                }
            }
        }
        return util;
    }

    private Toast toast;

    /**
     * 显示Toast
     */
    public void show(Context context, CharSequence text) {
        show(context, text, false);
    }

    /**
     * 显示Toast
     */
    public void show(Context context, CharSequence text, boolean showAtCenter) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            //CalledFromWrongThreadException
            //Only the original thread that created a view hierarchy can touch its views
            return;
        }

        if (TextUtils.isEmpty(text)) {
            return;
        }

        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        if (showAtCenter) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.show();
    }

    /**
     * 显示Toast
     */
    public void show(Context context, int resId) {
        show(context, resId, false);
    }

    /**
     * 显示Toast
     */
    public void show(Context context, int resId, boolean showAtCenter) {
        try {
            String toastStr = context.getString(resId);
            show(context, toastStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
