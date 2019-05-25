package com.puhui.lib.http;

import android.util.Base64;

import com.puhui.lib.utils.DMLog;
import com.puhui.lib.utils.JsonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2019/4/17.
 * Description:
 * Modified:
 */

public class HttpParams extends BaseHttpParams {
    private static final String TAG = HttpParams.class.getCanonicalName();

    @Override
    public String toString() {
        String data = requestParams.toString();
        DMLog.e(TAG, data);
        String sign = base64Encrypt(data, "fdsjalkfhjr3120o"); //网络请求参数加密使用的Key
        String result = "";
        try {
            //防止乱码
            result = "sign=" + URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toGetMethodStr() {
        String paramStr = "?";
        Iterator iterator = requestParams.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = JsonUtils.getString(requestParams, key);
            if (null != value) {
                paramStr = paramStr + key + "=" + value + "&";
            }
        }
        if (paramStr.contains("&")) {
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        } else {
            paramStr = "";
        }
        return paramStr;
    }

    public static String base64Encrypt(String data, String key) {
        if (data == null) {
            return "";
        }
        String encode = "";
        try {
            encode = new String(Base64.encode(data.getBytes("utf-8"), Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        encode += key;
        byte[] b = encode.getBytes();
        for (int i = 0; i < b.length / 2; i++) {
            byte temp = b[i];
            b[i] = b[b.length - 1 - i];
            b[b.length - 1 - i] = temp;
        }
        return new String(b);

    }
}
