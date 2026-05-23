package com.atguigu.learning.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.exception.BusinessException;
import com.atguigu.learning.domain.dto.LearningLessonDTO;
import com.atguigu.learning.domain.dto.LearningRecordDTO;
import com.atguigu.learning.domain.dto.LearningRecordFormDTO;
import com.atguigu.learning.domain.po.LearningLesson;
import com.atguigu.learning.domain.po.LearningRecord;
import com.atguigu.learning.enums.LessonStatus;
import com.atguigu.learning.mapper.LearningLessonMapper;
import com.atguigu.learning.mapper.LearningRecordMapper;
import com.atguigu.learning.service.ILearningLessonService;
import com.atguigu.learning.service.ILearningRecordService;
import com.atguigu.result.Result;
import com.atguigu.utils.UserContext;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 学习记录表 服务实现类
 * </p>
 *
 * @author wzj
 * @since 2026-05-18
 */
@Service
@AllArgsConstructor
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements ILearningRecordService {

    private final LearningRecordMapper learningRecordMapper;
    private final LearningLessonMapper learningLessonMapper;
    private final ILearningLessonService learningLessonService;
    @Override
    public Result<LearningLessonDTO> queryLearningRecordByCourse(Long courseId) {
        LearningLessonDTO learningLessonDTO = new LearningLessonDTO();
        if (courseId == null) {
            return null;
        }
        //1.获取用户Id
        String userId = UserContext.getUser().getUserId();
        //2.查询课表
        LearningLesson learningLesson = learningLessonMapper.queryByUserAndCourseId(userId, courseId);
        if (learningLesson == null) {
            return null;
        }
        //3.查询学习记录
        List<LearningRecord> learningRecords = lambdaQuery().eq(LearningRecord::getLessonId, learningLesson.getId()).list();
        List<LearningRecordDTO> learningRecordDTOS = BeanUtil.copyToList(learningRecords, LearningRecordDTO.class);

        //4.封装数据
        learningLessonDTO.setId(learningLesson.getId());
        learningLessonDTO.setLatestSectionId(learningLesson.getLatestSectionId());
        learningLessonDTO.setRecords(learningRecordDTOS);
        return Result.success(learningLessonDTO);
    }

    /**
     * 提交学习记录
     *
     * @param learningRecordFormDTO
     * @return
     */
    @Override
    @Transactional
    public Result<Void> submitLearningRecord(LearningRecordFormDTO learningRecordFormDTO) {
        //1.获取用户Id
        String userId = UserContext.getUser().getUserId();
        //2.判断类型
        //3.处理学习记录
        Boolean finished = false;
        if (learningRecordFormDTO.getSectionType() == 1) {
            //视频
            finished = handleVideoRecord(learningRecordFormDTO, userId);
        } else {
            //考试
            finished = handleExamRecord(learningRecordFormDTO, userId);
        }
        //4.处理课表
        handleLearningLessonsChanges(learningRecordFormDTO, finished);
        return Result.success();
    }

    private void handleLearningLessonsChanges(LearningRecordFormDTO learningRecordFormDTO, Boolean finished) {
        //查询课程总共有多少小节,这里直接是10
        Integer totalSections = 10;
        //查询课表
        LearningLesson lesson = learningLessonService.getById(learningRecordFormDTO.getLessonId());
        if (lesson == null) {
            throw new BusinessException(400, "课表不存在，无法更新课表");
        }
        //判断是否有完成小节
        boolean allLearned = false;
        if ( finished) {
            allLearned = lesson.getLearnedSections() + 1 >= totalSections;
        }
        learningLessonService.lambdaUpdate()
                .set(lesson.getLearnedSections() == 0, LearningLesson::getStatus, LessonStatus.LEARNING)
                .set(allLearned, LearningLesson::getStatus,  LessonStatus.FINISHED)
                .set(!finished,LearningLesson::getLatestSectionId, learningRecordFormDTO.getSectionId())
                .set(!finished,LearningLesson::getLatestLearnTime, learningRecordFormDTO.getCommitTime())
                .setSql(finished,"learned_sections = learned_sections + 1")

                //.set(finished,LearningLesson::getLearnedSections,lesson.getLearnedSections()+1)
                .eq(LearningLesson::getId, learningRecordFormDTO.getLessonId())
                .update();
    }

    private Boolean handleVideoRecord(LearningRecordFormDTO learningRecordFormDTO, String userId) {
        //1.学习记录是否存在
        LearningRecord old = lambdaQuery()
                .eq(LearningRecord::getLessonId, learningRecordFormDTO.getLessonId())
                .eq(LearningRecord::getSectionId, learningRecordFormDTO.getSectionId())
                .one();
        //2.不存在，新增
        if (old == null) {
            LearningRecord learningRecord = BeanUtil.copyProperties(learningRecordFormDTO, LearningRecord.class);
            learningRecord.setUserId(Long.valueOf(userId));
            learningRecord.setCreateTime(LocalDateTime.now());
            learningRecord.setUpdateTime(LocalDateTime.now());
            learningRecord.setFinished(false);
            boolean save = save(learningRecord);
            if (!save) {
                throw new BusinessException(400, "保存学习记录失败");
            }
            return false;
        }
        //3.存在，更新
        //3.1 判断是否是第一次学完
        boolean finished =!old.getFinished()&& learningRecordFormDTO.getMoment() * 2 >= learningRecordFormDTO.getDuration();
        //3.2 更新数据
        boolean success = lambdaUpdate()
                .set(LearningRecord::getMoment, learningRecordFormDTO.getMoment())
                .set(finished, LearningRecord::getFinished, finished)
                .set(finished, LearningRecord::getFinishTime, learningRecordFormDTO.getCommitTime())
                .eq(LearningRecord::getId, old.getId())
                .update();
        if (!success) {
            throw new BusinessException(400, "更新学习记录失败");
        }
        return finished;
    }

    private Boolean handleExamRecord(LearningRecordFormDTO learningRecordFormDTO, String userId) {
        //新增学习记录
        LearningRecord learningRecord = BeanUtil.copyProperties(learningRecordFormDTO, LearningRecord.class);
        learningRecord.setUserId(Long.valueOf(userId));
        learningRecord.setFinished(true);
        learningRecord.setFinishTime(learningRecordFormDTO.getCommitTime());
        learningRecord.setCreateTime(LocalDateTime.now());
        learningRecord.setUpdateTime(LocalDateTime.now());
        boolean save = save(learningRecord);
        if (!save) {
            throw new BusinessException(400, "保存学习记录失败");
        }
        return true;
    }
}
