package com.puhui.lib.utils;

import android.content.Context;
import android.text.TextUtils;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/2/14.
 * Description: 根据资源名字获取相应的文件
 * Modified: by TangJian on 2017/2/14.
 */
@SuppressWarnings("unused")
public class ResourceUtils {

    /***
     * 根据字符串名称获取对应的ID
     *
     * @param resType drawable  string  array layout  layout......
     */
    public static int getResId(Context context, String resType, String resName) {
        int resId = 0;
        if (context != null && !TextUtils.isEmpty(resType) && !TextUtils.isEmpty(resName)) {

            String packageName = context.getPackageName();
            if (TextUtils.isEmpty(packageName)) {
                return resId;
            }

            resId = context.getResources().getIdentifier(resName, resType, packageName);
            if (resId <= 0) {
                resId = context.getResources().getIdentifier(resName.toLowerCase(), resType, packageName);
            }
        }
        if (resId <= 0) {
            System.err.println("failed to parse " + resType + " resource \"" + resName + "\"");
        }
        return resId;
    }

    /****
     * 获取drawable文件夹下的ID
     */
    public static int getBitmapRes(Context context, String resName) {
        return getResId(context, "drawable", resName);
    }

    /****
     * 获取string文件夹下的ID
     */
    public static int getStringRes(Context context, String resName) {
        return getResId(context, "string", resName);
    }

    /****
     * 获取array文件夹下的ID
     */
    public static int getStringArrayRes(Context context, String resName) {
        return getResId(context, "array", resName);
    }

    /****
     * 获取layout文件夹下的ID
     */
    public static int getLayoutRes(Context context, String resName) {
        return getResId(context, "layout", resName);
    }

    /****
     * 获取style文件夹下的ID
     */
    public static int getStyleRes(Context context, String resName) {
        return getResId(context, "style", resName);
    }

    /****
     * 获取id文件夹下的ID
     */
    public static int getIdRes(Context context, String resName) {
        return getResId(context, "id", resName);
    }

    /****
     * 获取color文件夹下的ID
     */
    public static int getColorRes(Context context, String resName) {
        return getResId(context, "color", resName);
    }

    /****
     * 获取raw文件夹下的ID
     */
    public static int getRawRes(Context context, String resName) {
        return getResId(context, "raw", resName);
    }

    /****
     * plurals
     */
    public static int getPluralsRes(Context context, String resName) {
        return getResId(context, "plurals", resName);
    }

    /****
     * 获取anim文件夹下的ID
     */
    public static int getAnimRes(Context context, String resName) {
        return getResId(context, "anim", resName);
    }

}
