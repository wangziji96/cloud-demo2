package com.atguigu.learning.domain.dto;

import lombok.Data;

@Data
public class LessonFinishedCountDto {
    private Long lessonId;
    private Integer finishedCount;
}
