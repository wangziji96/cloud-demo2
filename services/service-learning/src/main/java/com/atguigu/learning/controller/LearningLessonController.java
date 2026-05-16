package com.atguigu.learning.controller;


import com.atguigu.domain.query.PageQuery;
import com.atguigu.learning.domain.dto.LearningPlanDto;
import com.atguigu.learning.service.ILearningLessonService;
import com.atguigu.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 学生课程表 前端控制器
 * </p>
 *
 * @author wzj
 * @since 2026-05-09
 */
@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LearningLessonController {
    private final ILearningLessonService learningLessonService;

    /**
     * 分页查询我的课表
     * @param pageQuery
     * @return
     */
    @GetMapping("page")
    public Result queryMyLessons(PageQuery pageQuery) {
        return learningLessonService.queryMyLessons(pageQuery);
    }

    /**
     * 创建学习计划
     */
    @PostMapping("/plans")
    public Result createLearningPlan(@Valid @RequestBody LearningPlanDto learningPlanDto) {
        return learningLessonService.createLearningPlan(learningPlanDto);
    }
}
