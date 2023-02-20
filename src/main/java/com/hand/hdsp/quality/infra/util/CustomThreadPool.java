package com.hand.hdsp.quality.infra.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author zhilong.deng@hand-china.com
 * @version 0.01
 * @date 2019/11/5
 */
public  class CustomThreadPool {
    private CustomThreadPool() {
    }

    /**
     * 线程构造
     */
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder()
        .setNameFormat("quality-%d").setDaemon(true).build();
    /**
     * 线程池
     */
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
        20,
        30,
        10L,
        TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            THREAD_FACTORY,
        new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 获取线程池
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getExecutor() {
        return EXECUTOR;
    }
}
