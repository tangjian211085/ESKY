package com.bhesky.app;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2019/4/27.
 * Description:
 * Modified:
 */
public class Watcher {

    static {
        System.loadLibrary("native-lib");
    }


    public native void createWatcher(String userId);

    public native void connectMonitor();
}
