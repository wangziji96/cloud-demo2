package com.atguigu.learning.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.DelayQueue;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class DelayTaskTest {
    @Test
    void testDelayQueue() {
        //1.初始化延迟队列
        DelayQueue<DelayTask<String>> queue = new DelayQueue<>();
        //2.向队列添加延迟执行的任务
        log.info("开始初始化延迟任务。。。");
        queue.add(new DelayTask<>("延迟任务3",Duration.ofSeconds(3)));
        queue.add(new DelayTask<>("延迟任务1",Duration.ofSeconds(1)));
        queue.add(new DelayTask<>("延迟任务2",Duration.ofSeconds(2)));
        //3.尝试执行任务
        while (true) {
            try {
                DelayTask<String> task = queue.take();
                log.info("执行延迟任务：{}",task.getData());
            } catch (InterruptedException e) {
                log.error("延迟任务执行异常",e);
            }
        }
    }
}