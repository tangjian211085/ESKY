package com.puhui.lib.http;

import android.content.Context;

import com.puhui.lib.utils.AppManager;
import com.puhui.lib.utils.SharedPreferenceUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 管理Cookie
 *
 * @author tangjina
 */

@SuppressWarnings("unused")
public class CookieUtils {

    public static String getCookie(Context context) {
        return null == context ? "" : (String) SharedPreferenceUtils.get(context, "access_token", "");
    }

    public static void setCookie(List<String> cookieList, String requestUrl) {
//        if (requestUrl.equals(PHConstant.API_URL.LOGIN)) {
            String cookie = encodeCookie(cookieList);  //cmUserAccessToken=deleteMe;rememberMe=deleteMe;
            cookie = cookie.replace("cmUserAccessToken=deleteMe;", "");
            cookie = cookie.replace("rememberMe=deleteMe;", "");

            Context context = AppManager.getAppManager().currentActivity();
            if (null != context) {
                SharedPreferenceUtils.put(context, "access_token", cookie);
            }
//        }
    }

    public static void setCookie(String accessToken, Context context) {
        if (null != context) {
//        String cookie = getCookie(context);
//        if (!cookie.contains("accessToken")) {
//            cookie += ";accessToken=" + accessToken;
//        }
            String cookie = "accessToken=" + accessToken;
            SharedPreferenceUtils.put(context, "access_token", cookie);
        }
    }

    //整合cookie为唯一字符串
    private static String encodeCookie(List<String> cookies) {
        if (cookies.size() >= 4) {
            return cookies.get(2) + ";" + cookies.get(3);
        }

//        //如果token出现问题，可以试试
//        if (cookies.size() == 3) {
//            return cookies.get(1);
//        }

        StringBuilder sb = new StringBuilder();
        Set<String> set = new HashSet<>();
        for (String cookie : cookies) {
            String[] arr = cookie.split(";");
            for (String s : arr) {
                if (set.contains(s)) continue;
                set.add(s);
            }
        }

        Iterator<String> ite = set.iterator();
        while (ite.hasNext()) {
            String cookie = ite.next();
            sb.append(cookie).append(";");
        }

        int last = sb.lastIndexOf(";");
        if (sb.length() - 1 == last) {
            sb.deleteCharAt(last);
        }

        return sb.toString();
    }
}
