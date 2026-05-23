package com.atguigu.learning.domain.dto;

import lombok.Data;

import java.util.List;

@Data

public class LearningLessonDTO {

    private Long id;

    private Long latestSectionId;

    private List<LearningRecordDTO> records;
}