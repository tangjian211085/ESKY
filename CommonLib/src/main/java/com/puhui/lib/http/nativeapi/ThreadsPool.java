package com.puhui.lib.http.nativeapi;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池
 *
 * @author tangjian
 */
class ThreadsPool {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static int threadCount = CPU_COUNT * 2 + 3;// 线程池数量

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread tread = new Thread(r, "Dimeng theads #" + mCount.getAndIncrement());
            // 设置线程优先级
            tread.setPriority(Thread.NORM_PRIORITY - 1);
            return tread;
        }
    };

    static final Executor THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(threadCount, sThreadFactory);

}
