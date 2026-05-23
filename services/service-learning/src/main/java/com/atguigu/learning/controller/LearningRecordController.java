package com.atguigu.learning.controller;


import com.atguigu.common.R;
import com.atguigu.learning.domain.dto.LearningLessonDTO;
import com.atguigu.learning.domain.dto.LearningRecordFormDTO;
import com.atguigu.learning.service.ILearningRecordService;
import com.atguigu.result.Result;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 学习记录表 前端控制器
 * </p>
 *
 * @author wzj
 * @since 2026-05-18
 */
@RestController
@RequestMapping("/learning-records")
@RequiredArgsConstructor
public class LearningRecordController {
    private final ILearningRecordService learningRecordService;
    /**
     * 查询指定课程的记录
     * @param courseId 课程ID
     * @return 记录列表
     */
    @GetMapping("/course/{courseId}")
    public Result<LearningLessonDTO> queryLearningRecordByCourse(@PathVariable("courseId") Long courseId) {
        return learningRecordService.queryLearningRecordByCourse(courseId);
    }

    /**
     * 提交学习记录
     */
    @PostMapping
    public Result<Void> submitLearningRecord(@Valid @RequestBody LearningRecordFormDTO learningRecordFormDTO) {
        return learningRecordService.submitLearningRecord(learningRecordFormDTO);
    }
}
