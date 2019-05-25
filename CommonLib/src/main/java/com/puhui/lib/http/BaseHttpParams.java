package com.puhui.lib.http;

import com.puhui.lib.utils.DMLog;
import com.puhui.lib.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/12.
 * Description:
 * Modified:
 */
@SuppressWarnings("all")
public class BaseHttpParams {
    protected JSONObject requestParams = new JSONObject();
    protected boolean useBaseParams = true;

    private void putParams(Map<String, Object> params) {
        try {
            Set<String> paramsKey = params.keySet();
            for (String key : paramsKey) {
                this.requestParams.put(key, params.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, Object value) {
        if (null != key && null != value) {
            try {
                this.requestParams.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public RequestBody getRequestBody() {
        DMLog.e(this.getClass().getSimpleName(), requestParams.toString());
        return RequestBody.create(MediaType.parse("application/json"), requestParams.toString());
    }

    public static <T> RequestBody getRequestBody(String token, T params) {
//        BaseRequestModel<T> baseRequestModel = new BaseRequestModel<>();
//        baseRequestModel.setToken(token);
//        baseRequestModel.setParams(params);
//        return RequestBody.create(
//                MediaType.parse("application/json"), new Gson().toJson(baseRequestModel));
        return null;
    }

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

    @Override
    public String toString() {
        if (null != requestParams) {
            return requestParams.toString();
        }
        return super.toString();
    }
}
