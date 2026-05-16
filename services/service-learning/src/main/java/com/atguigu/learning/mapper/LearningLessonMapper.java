package com.atguigu.learning.mapper;

import com.atguigu.learning.domain.po.LearningLesson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 学生课程表 Mapper 接口
 * </p>
 *
 * @author wzj
 * @since 2026-05-09
 */
@Mapper
public interface LearningLessonMapper extends BaseMapper<LearningLesson> {

}
