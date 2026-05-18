package com.atguigu.learning.domain.vo;


import com.atguigu.domain.query.PageDto;
import lombok.Data;

@Data
public class LearningPlanPageVO extends PageDto<LearningPlanVO> {
    private Integer weekPoints;//本周积分
    private Integer weekFinished;//本周完成数
    private Integer weekTotalPlan;//本周计划数
}
