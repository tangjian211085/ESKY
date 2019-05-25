package com.puhui.lib.utils;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        //		Activity aa = getActivity(activity.getClass());
        //		if (aa != null)
        //		{
        //			//去除多余的实例
        //			finishActivity(aa);
        //		}
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (null != activityStack) {
            try {
                return activityStack.lastElement();
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishCurrentActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        Activity tempActivity = null;
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                activity.finish();
                tempActivity = activity;
                break;
            }
        }

        if (null != tempActivity) {
            activityStack.remove(tempActivity);
        }
    }

    /**
     * 返回activity数量
     */
    public int getCount() {
        if (activityStack != null) {
            return activityStack.size();
        } else {
            return 0;
        }

    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束所有Activity, 除了指定的Activity
     */
    public void finishActivityWithout(Class<?> mainActivity) {
        List<Activity> temp = new ArrayList<Activity>();
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            Class<?> clazz = activityStack.get(i).getClass();
            //MainActivity.class
            if (null != clazz && !clazz.equals(mainActivity)) {
                activityStack.get(i).finish();
                temp.add(activityStack.get(i));
            }
        }

        for (int i = 0; i < temp.size(); i++) {
            activityStack.remove(temp.get(i));
        }
    }

    /**
     * 从任务栈中删除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 获取指定的Activity
     *
     * @author kymjs
     */
    public Activity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            // 杀死该应用进程
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
