package com.atguigu.learning.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习记录
 */
@Data
public class LearningRecordFormDTO {
    /**
     * 小节类型：1视频 2考试
     */
    @NotNull(message = "小节类型不能为空")
    @Min(value = 1, message = "小节类型不能小于1")
    @Max(2)
    private Integer sectionType;

    /**
     * 课表id
     */
    @NotNull(message = "课表id不能为空")
    private Long lessonId;

    /**
     * 对应的小节id
     */
    @NotNull(message = "对应小节id不能为空")
    private Long sectionId;

    /**
     * 视频总时长，单位秒
     */
    @NotNull(message = "视频总时长不能为空")
    @Min(value = 1, message = "视频总时长不能小于1")
    private Integer duration;

    /**
     * 视频当前观看时长，单位秒，第一次提交值0
     */
    @NotNull(message = "视频当前观看时长不能为空")
    @Min(value = 0, message = "视频当前观看时长不能小于0")
    private Integer moment;

    /**
     * 提交时间
     */
    @NotNull(message = "提交时间不能为空")
    private LocalDateTime commitTime;
}
