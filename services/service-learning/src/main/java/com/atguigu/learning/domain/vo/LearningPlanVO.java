package com.atguigu.learning.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class LearningPlanVO {



    /**
     * 课程id
     */
    private Long courseId;


    /**
     * 每周学习频率，例如每周学习6小节，则频率为6
     */
    private Integer weekFreq;

    /**
     * 本周学习数量
     */
    private Integer weekLearnedSections;


    /**
     * 已学习小节数量
     */
    private Integer learnedSections;

    /**
     * 总课程数量
     */
    private Integer sections;

    /**
     * 最近一次学习的时间
     */
    private LocalDateTime latestLearnTime;


    /**
     * 课程名称
     */

    private String courseName;



}
