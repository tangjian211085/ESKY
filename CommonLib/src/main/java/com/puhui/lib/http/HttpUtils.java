package com.puhui.lib.http;

import android.content.Context;

import com.puhui.lib.http.nativeapi.HttpUrlConnectionProxy;
import com.puhui.lib.utils.NetworkUtils;
import com.puhui.lib.utils.ToastUtil;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2019/4/17.
 * Description:
 * Modified:
 */

public class HttpUtils {
    private static HttpUtils instance;
    private BaseHttpProxy baseHttpProxy;
    public static final String POST = "POST";
    public static final String GET = "GET";

    private HttpUtils() {
    }

    public static HttpUtils newInstance() {
        if (null == instance) {
            synchronized (HttpUtils.class) {
                if (null == instance) {
                    instance = new HttpUtils();
                }
            }
        }
        return instance;
    }

    public <T extends BaseHttpProxy> void initProxy(Class<T> proxy) {
        if (proxy.isAssignableFrom(HttpUrlConnectionProxy.class)) {
            baseHttpProxy = HttpUrlConnectionProxy.getInstance();
        }
    }

    private boolean canSendRequest(Context context) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            ToastUtil.getInstance().show(context, "网络连接异常");
            return false;
        }
        return true;
    }

    public void doRequest(Context context, String url, String requestType, BaseHttpParams params, HttpCallBack callBack){

    }

    public void post(Context context, String url, BaseHttpParams params, HttpCallBack callBack) {
        if (canSendRequest(context))
            if (null != baseHttpProxy) {
                baseHttpProxy.post(context, url, params, callBack);
            }
    }

    public void post(Context context, String url, HttpCallBack callBack) {
        if (canSendRequest(context))
            if (null != baseHttpProxy) {
                baseHttpProxy.post(context, url, callBack);
            }
    }

    public void get(Context context, String url, BaseHttpParams params, HttpCallBack callBack) {
        if (canSendRequest(context))
            if (null != baseHttpProxy) {
                baseHttpProxy.get(context, url, params, callBack);
            }
    }

    public void get(Context context, String url, HttpCallBack callBack) {
        if (canSendRequest(context))
            if (null != baseHttpProxy) {
                baseHttpProxy.get(context, url, callBack);
            }
    }
}
