package com.atguigu.promotion.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
@Slf4j
@Configuration
public class PromotionConfig {

    /**
     * 生成兑换码专属线程池
     */
    @Bean("exchangeCodeExecutor")
    public Executor exchangeCodeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 1. 动态获取CPU核心数，适应不同部署环境
        int cpuCount = Runtime.getRuntime().availableProcessors();

        // 2. 核心线程数：兑换码生成涉及数据库/Redis等IO操作，建议 CPU * 2
        executor.setCorePoolSize(cpuCount * 2);

        // 3. 最大线程数：应对突发流量的应急线程数，建议 CPU * 4
        executor.setMaxPoolSize(cpuCount * 4);

        // 4. 任务队列容量：必须使用有界队列，防止任务无限堆积导致OOM
        executor.setQueueCapacity(200);

        // 5. 线程名称前缀：方便在日志或 jstack 中快速定位是哪个业务的线程
        executor.setThreadNamePrefix("exchange-code-handler-");

        // 6. 拒绝策略：当队列和线程池都满时，由调用者线程自己执行，起到背压降速作用
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 7. 【生产必备】优雅停机：应用关闭时，等待队列中的任务执行完毕，防止数据丢失
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间上限，防止程序一直不退出
        executor.setAwaitTerminationSeconds(60);

        // 初始化线程池
        executor.initialize();

        log.info("初始化生成兑换码的线程池结束...");
        return executor;
    }
}
