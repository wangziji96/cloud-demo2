package com.atguigu.learning.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.atguigu.learning.domain.po.LearningLesson;
import com.atguigu.learning.domain.po.LearningRecord;
import com.atguigu.learning.mapper.LearningRecordMapper;
import com.atguigu.learning.service.ILearningLessonService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.DelayQueue;

@Component
@Slf4j
@RequiredArgsConstructor
public class LearningRecordDelayTaskHandle {
    private final RedisTemplate<String, Object> redisTemplate;
    private final LearningRecordMapper learningRecordMapper;
    private final ILearningLessonService learningLessonService;
    private final DelayQueue<DelayTask<RecordTaskData>> queue = new DelayQueue<>();
    private static final String RECORD_KEY_TEMPLATE = "learning:record:{}";

    private static volatile boolean begin = true;

    @PostConstruct
    public void init() {
        //异步调用handleDelayTask，不然会阻塞spring生命周期
        CompletableFuture.runAsync(() -> {
            log.info("延迟处理任务启动");
            handleDelayTask();
        });
    }

    @PreDestroy
    public void destroy() {
        begin = false;
        log.info("延迟处理任务结束");
    }

    //异步延迟处理任务
    public void handleDelayTask() {
        while (begin) {
            try {
                //1.从延迟队列取出数据
                DelayTask<RecordTaskData> task = queue.take();
                //2.从Redis取出学习记录
                LearningRecord record = readRecordCache(task.getData().getLessonId(), task.getData().getSectionId());
                if (record == null) {
                    continue;
                }
                //3.比较moment值是否变化
                if (!Objects.equals(task.getData().getMoment(), record.getMoment())) {
                    //3.1 变化，说明用户还在看视频
                    continue;
                }
                //4 不变，说明用户已结束学习
                //4.1 更新学习记录
                record.setFinished(null);
                learningRecordMapper.updateById(record);
                //4.2 更新课程记录
                LearningLesson learningLesson = new LearningLesson();
                learningLesson.setId(record.getLessonId());
                learningLesson.setLatestSectionId(record.getSectionId());
                learningLesson.setLatestLearnTime(LocalDateTime.now());
                learningLessonService.updateById(learningLesson);
            } catch (Exception e) {
                log.error("处理延迟任务异常: ", e);
            }
        }
    }

    //添加学习记录到缓存和延迟队列
    public void addLearningRecordTask(LearningRecord record) {
        //1.添加到Redis
        writeRecoedCache(record);
        //2.添加到延迟队列
        queue.add(new DelayTask<>(new RecordTaskData(record), Duration.ofSeconds(20)));
    }

    //取出学习记录
    public LearningRecord readRecordCache(Long lessonId, Long sectionId) {
        try {
            String key = StrUtil.format(RECORD_KEY_TEMPLATE, lessonId);
            //根据key和sectionId取出数据
            RecordCacheData recordCacheData = (RecordCacheData) redisTemplate.opsForHash().get(key, sectionId.toString());
            if (recordCacheData == null) {
                return null;
            }
            //转换数据
            return BeanUtil.copyProperties(recordCacheData, LearningRecord.class);
        } catch (Exception e) {
            log.error("取出学习记录异常: ", e);
            return null;
        }
    }

    //清理学习记录缓存
    public void clearRecordCache(Long lessonId, Long sectionId) {
        log.info("清理学习记录缓存：{},{}", lessonId, sectionId);
        String key = StrUtil.format(RECORD_KEY_TEMPLATE, lessonId);
        redisTemplate.opsForHash().delete(key, sectionId.toString());
    }

    public void writeRecoedCache(LearningRecord record) {
        log.info("添加学习记录到缓存：{}", record);
        try {
            //1.数据转换
            RecordCacheData recordCacheData = new RecordCacheData(record);
            //2.写入Redis
            String key = StrUtil.format(RECORD_KEY_TEMPLATE, record.getLessonId());
            //key,hashkey,value
            redisTemplate.opsForHash().put(key, record.getSectionId().toString(), recordCacheData);
            //3.设置过期时间
            redisTemplate.expire(key, Duration.ofMinutes(1));
        } catch (Exception e) {
            log.error("添加学习记录到缓存异常: ", e);
        }
    }

    @Data
    @NoArgsConstructor
    public static class RecordCacheData {
        private Long id;
        private Integer moment;
        private Boolean finished;

        public RecordCacheData(LearningRecord record) {
            this.id = record.getId();
            this.moment = record.getMoment();
            this.finished = record.getFinished();
        }
    }

    @Data
    @NoArgsConstructor
    public static class RecordTaskData {
        private Long lessonId;
        private Long sectionId;
        private Integer moment;

        public RecordTaskData(LearningRecord record) {
            this.lessonId = record.getLessonId();
            this.sectionId = record.getSectionId();
            this.moment = record.getMoment();
        }
    }
}
