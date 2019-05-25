package com.puhui.lib.http;

import android.content.Context;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2019/4/17.
 * Description:
 * Modified:
 */

public interface BaseHttpProxy {
    void post(Context context, String url, BaseHttpParams params, HttpCallBack callBack);

    void post(Context context, String url, HttpCallBack callBack);

    void get(Context context, String url, BaseHttpParams params, HttpCallBack callBack);

    void get(Context context, String url, HttpCallBack callBack);
}
