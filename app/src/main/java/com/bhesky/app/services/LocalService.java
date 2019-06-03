package com.bhesky.app.services;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bhesky.app.IGuardAidlInterface;
import com.puhui.lib.utils.DMLog;

import java.util.Timer;
import java.util.TimerTask;

public class LocalService extends Service {
    private LocalBinder mBinder;
    private LocalServiceConnection mConnection;
    private int i = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DMLog.e(this.getClass().getCanonicalName(), "onCreate  onCreate  onCreate");
        Watcher.startService(this.getApplicationContext());

        mBinder = new LocalBinder();
        mConnection = new LocalServiceConnection();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("deamon", "deamon",
//                    NotificationManager.IMPORTANCE_LOW);
//            NotificationManager manager = (NotificationManager) getSystemService(
//                    Context.NOTIFICATION_SERVICE);
//            if (manager == null)
//                return;
//            manager.createNotificationChannel(channel);
//
//            Notification notification = new NotificationCompat.Builder(this, "deamon")
//                    .setAutoCancel(true)
//                    .setSmallIcon(R.mipmap.icon_return)  //通过设置icon，来伪装状态栏上的通知图标
//                    .setCategory(Notification.CATEGORY_SERVICE)
//                    .setOngoing(true)
//                    .setPriority(NotificationManager.IMPORTANCE_LOW)
//                    .build();
//            startForeground(10, notification);
//        } else

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //如果 18 以上的设备 启动一个Service startForeground给相同的id
            //然后结束那个Service
            startForeground(10, new Notification());
            startService(new Intent(this, InnerService.class));
        } else {
            startForeground(10, new Notification());
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                DMLog.e("服务开启中 " + i++);
            }
        }, 2000, 3000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DMLog.e(this.getClass().getCanonicalName(), "onStartCommand  onStartCommand");
        bindService(new Intent(this, RemoteService.class), mConnection, BIND_AUTO_CREATE);
        return super.onStartCommand(intent, flags, startId);
    }

    class LocalBinder extends IGuardAidlInterface.Stub {
        @Override
        public String getServiceName() {
            return LocalService.class.getName();
        }
    }

    class LocalServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DMLog.e(this.getClass().getCanonicalName(), "LocalService onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            startService(new Intent(LocalService.this, RemoteService.class));
            bindService(new Intent(LocalService.this, RemoteService.class), mConnection, BIND_AUTO_CREATE);
        }
    }

    public static class InnerService extends Service {

        @Override
        public void onCreate() {
            super.onCreate();
            startForeground(10, new Notification());
            stopSelf();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
