package com.puhui.lib.http.nativeapi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.puhui.lib.http.BaseHttpParams;
import com.puhui.lib.http.CookieUtils;
import com.puhui.lib.http.HttpCallBack;
import com.puhui.lib.utils.DMLog;
import com.puhui.lib.utils.NetworkUtils;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

@SuppressWarnings("deprecation")
class HttpTask extends AsyncTask<String, Integer, String> {
    private static final String Accept_Encoding = "gzip";  //接收编码
    private static int TIME_OUT = 30 * 1000;  //超时时间，默认15秒
    private static final String POST = "POST";  //以POST方法发出请求
    static final String GET = "GET";

    private HttpCallBack callBack;
    private BaseHttpParams params;
    private int resultCode;  //0成功 1失败
    private Throwable throwable;
    private WeakReference<Context> mContext;
    private String requestMethod = POST;
    private String requestUrl;

    HttpTask(BaseHttpParams params, HttpCallBack callBack, WeakReference<Context> context) {
        super();
        this.callBack = callBack;
        if (params == null) {
            this.params = HttpUrlConnectionProxy.getInstance().getDefaultParams();
        } else {
            this.params = params;
        }
        mContext = context;
    }

    HttpTask(BaseHttpParams params, HttpCallBack callBack, WeakReference<Context> context, String requestMethod) {
        super();
        this.callBack = callBack;
        if (params == null) {
            this.params = HttpUrlConnectionProxy.getInstance().getDefaultParams();
        } else {
            this.params = params;
        }
        mContext = context;
        this.requestMethod = requestMethod;
    }

    @Override
    protected String doInBackground(String... urls) {
        publishProgress(0);
        requestUrl = urls[0];
        String resultStr = null;
        if (!NetworkUtils.isNetworkAvailable(mContext.get())) {
            resultCode = 1;
        } else {
            try {
                if (requestUrl.startsWith("https://")) {
                    if (GET.equals(requestMethod)) {
                        resultStr = httpsGet(requestUrl + params.toGetMethodStr());
                    } else {
                        resultStr = httpsPost(requestUrl, params.toString());
                    }
                } else {
                    if (GET.equals(requestMethod)) {
                        resultStr = httpGet(requestUrl + params.toGetMethodStr());
                    } else {
                        resultStr = httpPost(requestUrl, params.toString());
                    }
                }
            } catch (JSONException e) {
                resultCode = 1;
                throwable = e;
            }
        }
        return resultStr;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (callBack != null) {
            callBack.onFailure(null);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //如果界面已结束，则不处理
        if (null != mContext && null != mContext.get() && mContext.get() instanceof AppCompatActivity
                && ((AppCompatActivity) mContext.get()).isFinishing()) {
            DMLog.e(this.getClass().getSimpleName(),
                    "last post activity has finished --- name is " + mContext.getClass().getSimpleName());
            mContext.clear();
            return;
        }

        if (callBack != null) {
            switch (resultCode) {
                case 0:
                    callBack.onRequestSuccess(result, requestUrl);
                    break;
                case 1:
                    callBack.onFailure(throwable);
                    break;
                default:
                    callBack.onRequestSuccess(result, requestUrl);
                    break;
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     * Http Post请求方法
     */
    private String httpPost(String url, String data) throws JSONException {
        String jsonStr = "";
        System.setProperty("http.keepAlive", "false");
        HttpURLConnection connection = null;
        try {
            String proxyHost = android.net.Proxy.getDefaultHost();
            if (proxyHost != null) {
                java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                        new InetSocketAddress(proxyHost, android.net.Proxy.getDefaultPort()));
                connection = (HttpURLConnection) new URL(url).openConnection(proxy);
            } else {
                connection = (HttpURLConnection) new URL(url).openConnection();
            }

            jsonStr = doPostRequest(connection, data);
        } catch (Exception e) {
            resultCode = 1;
            throwable = e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        resultCode = 0;
        return jsonStr;
    }

    /**
     * Http Post请求方法
     */
    @SuppressLint("TrulyRandom")
    private String httpsPost(String url, String data) throws JSONException {
        String jsonStr = "";
        HttpsURLConnection connection = null;
        try {
            String proxyHost = android.net.Proxy.getDefaultHost();
            if (proxyHost != null) {
                java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                        new InetSocketAddress(proxyHost, android.net.Proxy.getDefaultPort()));
                connection = (HttpsURLConnection) new URL(url).openConnection(proxy);
            } else {
                connection = (HttpsURLConnection) new URL(url).openConnection();
            }

            jsonStr = doPostRequest(connection, data);
        } catch (Exception e) {
            resultCode = 1;
            throwable = e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return jsonStr;
    }

    private String doPostRequest(HttpURLConnection connection, String data) throws Exception {
        DMLog.e(this.getClass().getSimpleName(), data);
        connection.setDoInput(true);// 设置可以读取返回的数据
        connection.setDoOutput(true); // 设置可以提交数据
        connection.setUseCaches(false);// 不用cache

        setCommonProperty(connection, POST);

        connection.getOutputStream().write(data.getBytes());
        connection.getOutputStream().flush();
        connection.getOutputStream().close();

        return getResultJson(connection, data);
    }

    private String doGetRequest(HttpURLConnection connection) throws Exception {
        connection.setRequestProperty("Connection", "close");

        setCommonProperty(connection, GET);

        return getResultJson(connection, null);
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        System.setProperty("http.keepAlive", "false");
        HttpURLConnection connection;
        String proxyHost = android.net.Proxy.getDefaultHost();
        if (proxyHost != null) {
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                    new InetSocketAddress(android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
            connection = (HttpURLConnection) new URL(url).openConnection(proxy);
        } else {
            connection = (HttpURLConnection) new URL(url).openConnection();
        }

        connection.setDoInput(true);// 设置可以读取返回的数据
        connection.setDoOutput(true); // 设置可以提交数据
        connection.setChunkedStreamingMode(0);
        connection.setRequestProperty("Connection", "close");

        connection.setUseCaches(false);// 不用cache
        connection.setDefaultUseCaches(false);
        // 设置超时时间
        connection.setConnectTimeout(TIME_OUT);
        connection.setReadTimeout(TIME_OUT);
        //
        connection.setRequestMethod(POST);
        connection.setRequestProperty("User-Agent", "Android/1.0");
        connection.setRequestProperty("Accept-Encoding", Accept_Encoding);
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        String sCookie = CookieUtils.getCookie(mContext.get());
        if (sCookie != null && sCookie.length() > 0) {
            connection.setRequestProperty("Cookie", sCookie);
        }
        connection.setInstanceFollowRedirects(true);
        return connection;
    }

    private String getResultJson(HttpURLConnection connection, String data) throws IOException {
        int redirectCount = 0;
        while (connection.getResponseCode() / 100 == 3 && redirectCount < 3) {
            connection = createConnection(connection.getHeaderField("Location"));
            connection.getOutputStream().write(data.getBytes());
            connection.getOutputStream().flush();
            connection.getOutputStream().close();
            redirectCount++;
        }

        int code = connection.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            InputStream in = connection.getInputStream();
            BufferedReader rd;
            String contentEncoding = connection.getContentEncoding();

            String charset = "UTF-8";  //使用的字符集
            if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(in), charset), 8192);
            } else {
                rd = new BufferedReader(new InputStreamReader(in, charset), 8192); // 对应的字符编码转换
            }
            String tempLine = rd.readLine();
            StringBuilder temp = new StringBuilder();
            while (tempLine != null) {
                temp.append(tempLine);
                tempLine = rd.readLine();
            }
            String jsonStr = temp.toString();

//            String cookie = connection.getHeaderField("set-cookie");
//            StringBuilder sb = new StringBuilder().append("cookie = ").append(cookie);
//            DMLog.e(this.getClass().getSimpleName(), sb.toString());

            Map<String, List<String>> maps = connection.getHeaderFields();
            List<String> cookieList = maps.get("Set-Cookie");  //Set-Cookie
            if (cookieList != null && cookieList.size() > 0) {
                CookieUtils.setCookie(cookieList, requestUrl);
            }
            rd.close();
            in.close();

            return jsonStr;
        } else {
            resultCode = 1;
        }
//        Map<String, List<String>> maps = connection.getHeaderFields();
//        List<String> cookieList = maps.get("Set-Cookie");  //Set-Cookie
//        if (cookieList != null && cookieList.size() > 0) {
//            CookieUtil.setCookie(cookieList, requestUrl);
//        }
        return null;
    }

    private void setCommonProperty(HttpURLConnection connection, String requestMethod) throws IOException {
        // 设置超时时间
        connection.setConnectTimeout(TIME_OUT);
        connection.setReadTimeout(TIME_OUT);

        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("User-Agent", "Android/1.0");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Accept-Encoding", Accept_Encoding);
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

        String sCookie = CookieUtils.getCookie(mContext.get());
        if (sCookie != null && sCookie.length() > 0) {
            connection.setRequestProperty("Cookie", sCookie);
        }
    }

    private String httpGet(String url) throws JSONException {
        String jsonStr = "";
        System.setProperty("http.keepAlive", "false");
        HttpURLConnection connection = null;
        try {
            String proxyHost = android.net.Proxy.getDefaultHost();
            if (proxyHost != null) {
                java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                        new InetSocketAddress(proxyHost, android.net.Proxy.getDefaultPort()));
                connection = (HttpURLConnection) new URL(url).openConnection(proxy);
            } else {
                connection = (HttpURLConnection) new URL(url).openConnection();
            }

            jsonStr = doGetRequest(connection);
        } catch (Exception e) {
            resultCode = 1;
            throwable = e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        resultCode = 0;
        return jsonStr;
    }

    @SuppressLint("TrulyRandom")
    private String httpsGet(String url) throws JSONException {
        String jsonStr = "";
        HttpsURLConnection connection = null;

        try {
            String proxyHost = android.net.Proxy.getDefaultHost();
            if (proxyHost != null) {
                java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                        new InetSocketAddress(proxyHost, android.net.Proxy.getDefaultPort()));
                connection = (HttpsURLConnection) new URL(url).openConnection(proxy);
            } else {
                connection = (HttpsURLConnection) new URL(url).openConnection();
            }

            jsonStr = doGetRequest(connection);
        } catch (Exception e) {
            resultCode = 1;
            throwable = e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return jsonStr;
    }
}
