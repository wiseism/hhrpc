package com.hhrpc.hhrpc.core.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/5/26
 * @Author lifei
 */
public class HhHealthChecker {

    private static final Logger log = LoggerFactory.getLogger(HhHealthChecker.class);

    private ScheduledExecutorService consumerExecutor;
    private ScheduledExecutorService providerExecutor;

    public void start() {
        log.debug("===> [hhregister] start health check...");
        consumerExecutor = Executors.newSingleThreadScheduledExecutor();
        providerExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public void stop() {
        log.debug("===> [hhregister] stop...");
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(providerExecutor);
    }

    private void gracefulShutdown(ScheduledExecutorService executor) {
        executor.shutdown();
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }catch (Exception e){
            // ignore
        }
    }

    public void providerHealthCheck(Runnable runnable) {
        providerExecutor.scheduleWithFixedDelay(()->{
            try {
                runnable.run();
            }catch (Exception e){
                log.error("===> [hhregister] provider health check error", e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void consumerHealthCheck(Runnable runnable) {
        consumerExecutor.scheduleWithFixedDelay(()->{
            try {
                runnable.run();
            }catch (Exception e){
                log.error("===> [hhregister] consumer health check error", e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
}
