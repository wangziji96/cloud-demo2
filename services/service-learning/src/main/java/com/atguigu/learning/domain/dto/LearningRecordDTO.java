package com.atguigu.learning.domain.dto;

import lombok.Data;

@Data
public class LearningRecordDTO {

    private Long sectionId;

    private Integer moment;

    private Boolean finished;
}