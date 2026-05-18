package com.atguigu.learning.mapper;

import com.atguigu.learning.domain.dto.LessonFinishedCountDto;
import com.atguigu.learning.domain.po.LearningRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 学习记录表 Mapper 接口
 * </p>
 *
 * @author wzj
 * @since 2026-05-18
 */
@Mapper
public interface LearningRecordMapper extends BaseMapper<LearningRecord> {
    /**
     * 统计用户在指定时间范围内已完成的学习记录数量
     * @param userId    用户ID
     * @param finished  是否完成（true=已完成）
     * @param startTime 开始时间（包含）
     * @param endTime   结束时间（包含）
     * @return 记录总数
     */
    Integer countFinishedRecords(@Param("userId") Integer userId,
                              @Param("finished") Boolean finished,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);
    /**
     * 查询指定用户在某时间范围内已完成学习的记录，按课程分组统计数量
     * @param userId    用户ID
     * @param finished  是否完成（true/false）
     * @param startTime 开始时间（包含）
     * @param endTime   结束时间（包含）
     * @return 每个课程的完成数量列表
     */
    List<LessonFinishedCountDto> countFinishedRecordsByLesson(
            @Param("userId") Integer userId,
            @Param("finished") Boolean finished,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
