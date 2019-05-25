package com.puhui.lib.http.nativeapi;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;


import com.puhui.lib.http.BaseHttpParams;
import com.puhui.lib.http.BaseHttpProxy;
import com.puhui.lib.http.HttpCallBack;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Http操作处理类
 *
 * @author tangjina
 */
public class HttpUrlConnectionProxy implements BaseHttpProxy {

    private static HttpUrlConnectionProxy instance = null;

    private HttpUrlConnectionProxy() {
    }

    public static HttpUrlConnectionProxy getInstance() {
        if (instance == null) {
            synchronized (HttpUrlConnectionProxy.class) {
                if (instance == null) {
                    instance = new HttpUrlConnectionProxy();
                }
            }
        }
        return instance;
    }

    /**
     * @return 返回 defaultParams
     */
    BaseHttpParams getDefaultParams() {
        return new BaseHttpParams();
    }

    @Override
    public void post(Context context, String url, BaseHttpParams params, HttpCallBack callBack) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        HttpTask task = new HttpTask(params, callBack, weakReference);
        int sdK = Build.VERSION.SDK_INT;
        if (sdK < 11) {
            task.execute(url);
        } else {
            task.executeOnExecutor(ThreadsPool.THREAD_POOL_EXECUTOR, url);
        }
    }

    @Override
    public void post(Context context, String url, HttpCallBack callBack) {
        post(context, url, null, callBack);
    }

    @Override
    public void get(Context context, String url, BaseHttpParams params, HttpCallBack callBack) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        HttpTask task = new HttpTask(params, callBack, weakReference, HttpTask.GET);
        int sdK = Build.VERSION.SDK_INT;
        if (sdK < 11) {
            task.execute(url);
        } else {
            task.executeOnExecutor(ThreadsPool.THREAD_POOL_EXECUTOR, url);
        }
    }

    @Override
    public void get(Context context, String url, HttpCallBack callBack) {
        get(context, url, null, callBack);
    }

    /**
     * 初始化https
     */
    public static void initSSL1() {
        try {
            MyHostnameVerifier myHostnameVerifier = new MyHostnameVerifier();
            MyX509TrustManager myX509TrustManager = new MyX509TrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager[] xtmArray = new X509TrustManager[]{myX509TrustManager};
            sslContext.init(null, xtmArray, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(myHostnameVerifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化https
     */
    public static void initSSL2(Context context) {
        try {
            //取得SSL的SSLContext实例
            SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
            //取得KeyManagerFactory实例
            KeyManagerFactory keyManager = KeyManagerFactory.getInstance(CLIENT_KEY_MANAGER);
            //取得TrustManagerFactory的X509密钥管理器
            TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);

            //取得BKS密库实例
            KeyStore keyKeyStore = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
            KeyStore trustKeyStore = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);

            //加载证书和私钥,通过读取资源文件的方式读取密钥和信任证书（kclient:密钥;lt_client:信任证书）
            if (mAssetManager == null) {
                mAssetManager = context.getAssets();
            }
            InputStream is = mAssetManager.open("server_trust.keystore");
            //kclient:密钥
            keyKeyStore.load(is, CLIENT_KET_PASSWORD.toCharArray());
            is.reset();
            //lt_client:信任证书
            trustKeyStore.load(is, CLIENT_TRUST_PASSWORD.toCharArray());
            is.close();

            //初始化密钥管理器、信任证书管理器
            keyManager.init(keyKeyStore, CLIENT_KET_PASSWORD.toCharArray());
            trustManager.init(trustKeyStore);

            //初始化SSLContext
            sslContext.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 私钥密码
     */
    private static final String CLIENT_KET_PASSWORD = "123456";

    /**
     * 信任证书密码
     */
    private static final String CLIENT_TRUST_PASSWORD = "123456";

    /**
     * 使用协议
     */
    private static final String CLIENT_AGREEMENT = "TLS";

    /**
     * 密钥管理器
     */
    private static final String CLIENT_KEY_MANAGER = "X509";

    /**
     * 信任证书管理器
     */
    private static final String CLIENT_TRUST_MANAGER = "X509";

    /**
     * 密库，这里用的是BouncyCastle密库
     */
    private static final String CLIENT_KEY_KEYSTORE = "BKS";

    /**
     * 密库，这里用的是BouncyCastle密库
     */
    private static final String CLIENT_TRUST_KEYSTORE = "BKS";

    //    private static final String HTTS_URL = "https://192.168.7.39:8443/";

    private static AssetManager mAssetManager = null;

    //要实现x209证书认证
    static class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {

            return null;
        }

    }

    //要实现主机名验证
    static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {

            return true;
        }

    }
}
