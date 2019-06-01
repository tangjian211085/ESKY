package com.bhesky.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2019/4/27.
 * Description:
 * Modified:
 */
public class ProcessService extends Service {
    private int i = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Watcher watcher = new Watcher();
        watcher.createWatcher(String.valueOf(Process.myUid()));
        watcher.connectMonitor();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("服务开启中 " + i);
                i++;

                /**
                 * 如果多次startService(new Intent(this, ProcessService.class));的话，就会多次调用
                 * watcher.createWatcher(String.valueOf(Process.myUid()));
                 *         watcher.connectMonitor();最终导致下面的log效果。
                 *         最好是能够做到判断是否有正在运行中的服务进程，没有的话才去重连。
                 *
                 2019-04-27 02:53:27.504 8902-8919/com.bhesky.app I/System.out: 服务开启中 9
                 2019-04-27 02:53:27.504 8902-8919/com.bhesky.app I/System.out: 服务开启中 9
                 2019-04-27 02:53:30.504 8902-8919/com.bhesky.app I/System.out: 服务开启中 10
                 2019-04-27 02:53:27.504 8902-8919/com.bhesky.app I/System.out: 服务开启中 10
                 2019-04-27 02:53:33.503 8902-8919/com.bhesky.app I/System.out: 服务开启中 11
                 2019-04-27 02:53:27.504 8902-8919/com.bhesky.app I/System.out: 服务开启中 11
                 ..........
                 *
                 */
            }
        }, 2000, 3000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
