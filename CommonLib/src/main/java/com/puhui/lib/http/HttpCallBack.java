package com.puhui.lib.http;

import android.app.Activity;

import com.puhui.lib.utils.AppManager;
import com.puhui.lib.utils.DMLog;
import com.puhui.lib.utils.JsonUtils;
import com.puhui.lib.utils.NetworkUtils;
import com.puhui.lib.utils.ToastUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Http回调接口
 *
 * @author tangjian
 */
@SuppressWarnings("all")
public abstract class HttpCallBack {

    public void onRequestSuccess(String result, String requestUrl) {
        try {
            DMLog.e(result + "\n" + requestUrl);
            JSONObject jsonObject = new JSONObject(result.toString());

            String code = JsonUtils.getString(jsonObject, "code");
            String message = JsonUtils.getString(jsonObject, "description");
            String resultJson = JsonUtils.getString(jsonObject, "data");
            if (null == resultJson) {
                resultJson = "";
            }

//            final Activity activity = AppManager.getAppManager().currentActivity();
//            if (activity instanceof BaseActivity && null != activity && !activity.isFinishing()) {
//                ((BaseActivity) activity).stopLoading();
//            }

            onSuccess(code, message, resultJson);
        } catch (Exception e) {
            e.printStackTrace();
            onFailure(null);
        }
    }

    public abstract void onSuccess(String code, String message, String resultJson);

    public void onFailure(Throwable t) {
        Activity context = AppManager.getAppManager().currentActivity();
        if (null != context && !context.isFinishing()) {
            Activity activity = AppManager.getAppManager().currentActivity();
            String message;
            if (null != activity && null != t) {
                ToastUtil.getInstant().show(activity, "网络连接失败，请稍后重试");
                if (!NetworkUtils.isNetworkAvailable(activity)) {
                    onNetworkError();
                } else if (t instanceof HttpException) {
                    DMLog.e(this.getClass().getSimpleName(), "连接服务器失败：" + t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    message = "请求超时，请稍后再试";
                    ToastUtil.getInstant().show(activity, message);
                } else if (t instanceof ConnectException) {
                    DMLog.e(this.getClass().getSimpleName(), "连接服务器失败：" + t.getMessage());
                } else if (t instanceof IOException) {
                    DMLog.e(this.getClass().getSimpleName(), "连接服务器失败：" + t.getMessage());
                }
            }
        }
    }

    public void onNetworkError() {
    }
}
