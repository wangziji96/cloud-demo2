package com.atguigu.learning.utils;

import lombok.Data;

import java.time.Duration;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延迟任务封装类
 * 用于实现延迟队列中的任务,支持在指定延迟时间后执行
 * 实现了Delayed接口,可用于DelayQueue或PriorityQueue等延迟/优先队列
 *
 * @param <D> 任务携带的数据类型
 */
@Data
public class DelayTask<D> implements Delayed {
    /** 任务携带的业务数据 */
    private D data;
    
    /** 任务到期时间的纳秒时间戳(基于System.nanoTime) */
    private long deadlineNanos;

    /**
     * 构造延迟任务
     *
     * @param data      任务携带的业务数据
     * @param delayTime 延迟时间,从当前时刻开始计算的延迟时长
     */
    public DelayTask(D data, Duration delayTime) {
        this.data = data;
        // 计算任务的截止时间点(当前时间 + 延迟时间)
        this.deadlineNanos = System.nanoTime() + delayTime.toNanos();
    }
    
    /**
     * 获取剩余延迟时间
     * 根据指定的时间单位返回距离任务到期的剩余时间
     *
     * @param unit 时间单位
     * @return 剩余延迟时间,如果已到期则返回0或负数
     */
    @Override
    public long getDelay(TimeUnit unit) {
        // 确保不返回负数,已到期的任务统一返回0
        return unit.convert(Math.max(0, deadlineNanos - System.nanoTime()), TimeUnit.NANOSECONDS);
    }

    /**
     * 比较延迟任务的优先级
     * 用于延迟队列中的排序,到期时间越早的任务优先级越高
     *
     * @param o 待比较的另一个延迟任务
     * @return 当前任务剩余时间大于对方返回1,小于返回-1,相等返回0
     */
    @Override
    public int compareTo(Delayed o) {
        // 计算两个任务剩余时间的差值
        long l = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        if (l > 0) {
            return 1;
        } else if (l < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
