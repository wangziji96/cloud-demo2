package com.atguigu.learning.service;

import com.atguigu.domain.query.PageQuery;
import com.atguigu.learning.domain.dto.LearningPlanDto;
import com.atguigu.learning.domain.po.LearningLesson;
import com.atguigu.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 学生课程表 服务类
 * </p>
 *
 * @author wzj
 * @since 2026-05-09
 */
public interface ILearningLessonService extends IService<LearningLesson> {

    void addUserLessons(Long userId, List<Long> courseIds);

    /**
     * 分页查询我的课表
     * @param pageQuery
     * @return
     */
    Result queryMyLessons(PageQuery pageQuery);

    /**
     * 创建学习计划
     */
    Result createLearningPlan(LearningPlanDto learningPlanDto);

    /**
     * 查询学习计划
     * @param pageQuery
     * @return
     */
    Result queryMyPlans(PageQuery pageQuery);
}
