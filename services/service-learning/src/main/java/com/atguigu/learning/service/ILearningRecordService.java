package com.atguigu.learning.service;

import com.atguigu.learning.domain.dto.LearningLessonDTO;
import com.atguigu.learning.domain.dto.LearningRecordFormDTO;
import com.atguigu.learning.domain.po.LearningRecord;
import com.atguigu.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学习记录表 服务类
 * </p>
 *
 * @author wzj
 * @since 2026-05-18
 */
public interface ILearningRecordService extends IService<LearningRecord> {

    Result<LearningLessonDTO> queryLearningRecordByCourse(Long courseId);

    /**
     * 提交学习记录
     * @param learningRecordFormDTO
     * @return
     */
    Result<Void> submitLearningRecord(LearningRecordFormDTO learningRecordFormDTO);
}
