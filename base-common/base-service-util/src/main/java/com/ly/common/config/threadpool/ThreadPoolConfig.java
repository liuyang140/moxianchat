package com.ly.common.config.threadpool;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThreadPoolConfig {

    private ThreadPoolExecutor customThreadPool;

    @Bean("customThreadPool")
    public Executor customThreadPool() {
        int corePoolSize = 4;
        int maxPoolSize = 10;
        int queueCapacity = 100;
        long keepAliveTime = 60;

        customThreadPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new CustomThreadFactory("chat-pool-"),
                new ThreadPoolExecutor.AbortPolicy()
        );

        return customThreadPool;
    }

    @PreDestroy
    public void shutDownThreadPool() {
        if (customThreadPool != null) {
            customThreadPool.shutdown(); // 禁止提交新任务
            try {
                if (!customThreadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    customThreadPool.shutdownNow(); // 强制终止
                }
            } catch (InterruptedException e) {
                customThreadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // 自定义线程工厂
    static class CustomThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger count = new AtomicInteger(1);

        public CustomThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, namePrefix + count.getAndIncrement());
        }
    }
}