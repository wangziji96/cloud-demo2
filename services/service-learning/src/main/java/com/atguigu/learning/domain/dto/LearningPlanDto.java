package com.atguigu.learning.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LearningPlanDto {
    @NotNull(message = "课程ID不能为空")
    private Long lessonId;
    @NotNull(message = "计划学习频率不能为空")
    @Min(value = 1, message = "计划学习频率不能小于1")
    private Integer freq;
}
