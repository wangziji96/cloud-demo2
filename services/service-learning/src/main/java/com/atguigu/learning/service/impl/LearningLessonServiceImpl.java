package com.atguigu.learning.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.atguigu.course.CourseSimpleInfoDTO;
import com.atguigu.domain.query.PageDto;
import com.atguigu.domain.query.PageQuery;
import com.atguigu.exception.BusinessException;
import com.atguigu.learning.domain.dto.LearningPlanDto;
import com.atguigu.learning.domain.dto.LessonFinishedCountDto;
import com.atguigu.learning.domain.po.LearningLesson;
import com.atguigu.learning.domain.po.LearningRecord;
import com.atguigu.learning.domain.vo.LearningLessonVO;
import com.atguigu.learning.domain.vo.LearningPlanPageVO;
import com.atguigu.learning.domain.vo.LearningPlanVO;
import com.atguigu.learning.enums.LessonStatus;
import com.atguigu.learning.mapper.LearningLessonMapper;
import com.atguigu.learning.mapper.LearningRecordMapper;
import com.atguigu.learning.service.ILearningLessonService;
import com.atguigu.result.Result;
import com.atguigu.user.LoginUser;
import com.atguigu.utils.DateUtils;
import com.atguigu.utils.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生课程表 服务实现类
 * </p>
 *
 * @author wzj
 * @since 2026-05-09
 */
@Service
@Slf4j
@AllArgsConstructor
public class LearningLessonServiceImpl extends ServiceImpl<LearningLessonMapper, LearningLesson> implements ILearningLessonService {

    private final LearningLessonMapper learningLessonMapper;
    private final LearningRecordMapper learningRecordMapper;
    @Override
    @Transactional
    public void addUserLessons(Long userId, List<Long> courseIds) {
        //1.查询课程信息
        List<CourseSimpleInfoDTO> courseSimpleInfoDTOs = getSimpleInfoDTOs(courseIds);
        if (CollectionUtils.isEmpty(courseSimpleInfoDTOs)) {
            log.info("课程不存在,无法添加到课表");
            return;
        }
        //2. 添加到课表
        List<LearningLesson> learningLessons = new ArrayList<>(courseSimpleInfoDTOs.size());
        for (CourseSimpleInfoDTO courseSimpleInfoDTO : courseSimpleInfoDTOs) {
            LearningLesson learningLesson = new LearningLesson();
            learningLesson.setUserId(userId);
            learningLesson.setCourseId(courseSimpleInfoDTO.getId());
            //判断课程有效期是否存在
            Integer validDuration = courseSimpleInfoDTO.getValidDuration();
            if (validDuration != null) {
                learningLesson.setExpireTime(LocalDateTime.now().plusMonths(validDuration));
                learningLesson.setCreateTime(LocalDateTime.now());
            }
            learningLessons.add(learningLesson);
        }
        saveBatch(learningLessons);
    }

    @Override
    public Result queryMyLessons(PageQuery pageQuery) {
        //1.获取用户Id
        LoginUser user = UserContext.getUser();
        String userId = user.getUserId();
        //2.分页查询我的课表
        Page<LearningLesson> page = new Page<>(pageQuery.getPageNo(), pageQuery.getPageSize());
        //2.1设置排序字段
        if (StrUtil.isNotBlank(pageQuery.getSortBy())) {
            if (pageQuery.getIsAsc()) {
                //升序
                page.addOrder(OrderItem.asc(pageQuery.getSortBy()));
            }else {
                //降序
                page.addOrder(OrderItem.desc(pageQuery.getSortBy()));
            }
        }else {
            //默认排序
            if (pageQuery.getIsAsc()){
                page.addOrder(OrderItem.asc("create_time"));
            } else {
                page.addOrder(OrderItem.desc("create_time"));
            }
        }
        LambdaQueryWrapper<LearningLesson> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningLesson::getUserId, userId);
        page = learningLessonMapper.selectPage(page, queryWrapper);
        List<LearningLesson> records = page.getRecords();
        if (CollectionUtil.isEmpty( records)) {
            return Result.success(new PageDto<>());
        }
        //3.查询课程信息
        List<Long> courseIds = records.stream().map(LearningLesson::getCourseId).toList();
        List<CourseSimpleInfoDTO> simpleInfoDTOs = getSimpleInfoDTOs(courseIds);
        if (CollectionUtil.isEmpty(simpleInfoDTOs)) {
            throw new BusinessException(400, "课程不存在");
        }
        //将simpleInfoDTOs转为Map，key是id，value是CourseSimpleInfoDTO
        Map<Long, CourseSimpleInfoDTO> map = simpleInfoDTOs.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, v -> v));
        //4.封装课程信息到我的课表
        List<LearningLessonVO> learningLessonVOS = new ArrayList<>(records.size());
        for (LearningLesson record : records) {
            LearningLessonVO learningLessonVO = BeanUtil.copyProperties(record, LearningLessonVO.class);
            learningLessonVO.setCourseName(map.get(record.getCourseId()).getName());
            learningLessonVO.setCourseCoverUrl(map.get(record.getCourseId()).getCoverUrl());
            learningLessonVOS.add(learningLessonVO);
        }
        //5.返回结果
        PageDto<LearningLessonVO> pageDto = new PageDto<>();
        pageDto.setTotal(page.getTotal());
        pageDto.setTotalPage(Math.toIntExact(page.getPages()));
        pageDto.setRecords(learningLessonVOS);
        return Result.success(pageDto);
    }

    /**
     * 创建学习计划
     *
     * @param learningPlanDto
     */
    @Transactional
    @Override
    public Result createLearningPlan(LearningPlanDto learningPlanDto) {
        //1.获取用户Id
        String userId = UserContext.getUser().getUserId();
        //2.创建学习计划，更新课表
        LambdaUpdateWrapper<LearningLesson> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, learningPlanDto.getLessonId())
                .set(LearningLesson::getWeekFreq, learningPlanDto.getFreq())
                .set(LearningLesson::getPlanStatus, LessonStatus.LEARNING);
        int update = learningLessonMapper.update(null, updateWrapper);
        if (update > 0) {
            return Result.success("创建学习计划成功");
        }
        log.warn("更新课表失败,用户id{},课程Id{}", userId, learningPlanDto.getLessonId());
        return Result.error("创建学习计划失败");
    }

    @Override
    public Result queryMyPlans(PageQuery pageQuery) {
        LearningPlanPageVO learningPlanPageVO = new LearningPlanPageVO();
        //1.获取用户Id
        String userId = UserContext.getUser().getUserId();
        //2.分页查询我的课表
        Page<LearningLesson> page = new Page<>(pageQuery.getPageNo(), pageQuery.getPageSize());
        LambdaQueryWrapper<LearningLesson> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningLesson::getUserId, userId)
                .in(LearningLesson::getStatus, LessonStatus.LEARNING, LessonStatus.FINISHED);
        page = learningLessonMapper.selectPage(page, queryWrapper);
        //2.1查询不到课表信息，返回空
        List<LearningLesson> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            learningPlanPageVO.setTotal(0L);
            learningPlanPageVO.setTotalPage(0);
            return Result.success(learningPlanPageVO);
        }
        //3.查询课程信息 获取课程名
        List<Long> courseIds = records.stream().map(LearningLesson::getCourseId).toList();
        List<CourseSimpleInfoDTO> simpleInfoDTOs = getSimpleInfoDTOs(courseIds);
        if (CollectionUtil.isEmpty(simpleInfoDTOs)) {
            throw new BusinessException(400, "课程不存在");
        }
        //3.1 key是id，values是CourseSimpleInfoDTO
        Map<Long, CourseSimpleInfoDTO> map = simpleInfoDTOs.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, v -> v));
        //4.查询课程记录
        //4.1获取本周起始和结束时间
        LocalDateTime weekStart = DateUtils.getWeekBeginTime(LocalDate.now());
        LocalDateTime weekEnd = DateUtils.getWeekEndTime(LocalDate.now());
        //4.2查询本周已完成课程数
        /*LambdaQueryWrapper<LearningRecord> learningRecordQueryWrapper = new LambdaQueryWrapper<>();
        learningRecordQueryWrapper.eq(LearningRecord::getUserId, userId)
                .eq(LearningRecord::getFinished, true)
                        .ge(LearningRecord::getFinishTime,weekStart)
                                .le(LearningRecord::getFinishTime,weekEnd);
        Long weekFinished = learningRecordMapper.selectCount(learningRecordQueryWrapper);*/
        Integer weekFinished = learningRecordMapper.countFinishedRecords(Integer.parseInt(userId), true, weekStart, weekEnd);
        //4.3本周已学习小结数量
        List<LessonFinishedCountDto> lessonFinishedCountDtos = learningRecordMapper.countFinishedRecordsByLesson(Integer.parseInt(userId), true, weekStart, weekEnd);
       //将lessonFinishedCountDtos转为Map，key是lessonId，value是LessonFinishedCountDto
        Map<Long, LessonFinishedCountDto> lessonFinishedCountDtoMap = lessonFinishedCountDtos.stream().collect(Collectors.toMap(LessonFinishedCountDto::getLessonId, v -> v));
        //5. todo 积分
        //6.组装数据
        learningPlanPageVO.setWeekTotalPlan(records.size());
        learningPlanPageVO.setWeekFinished(weekFinished);//本周完成数
        learningPlanPageVO.setTotal(page.getTotal());
        learningPlanPageVO.setTotalPage(Math.toIntExact(page.getPages()));

        List<LearningPlanVO> learningPlanVOS = new ArrayList<>(records.size());
        for (LearningLesson record : records) {
            LearningPlanVO learningPlanVO = BeanUtil.copyProperties(record, LearningPlanVO.class);
            CourseSimpleInfoDTO courseSimpleInfoDTO = map.get(record.getCourseId());
            if (courseSimpleInfoDTO != null) {
                //设置课程名
                learningPlanVO.setCourseName(courseSimpleInfoDTO.getName());
                //设置课程章节量
                learningPlanVO.setSections(courseSimpleInfoDTO.getSectionNum());
            }
            //设置本周已学习数量
            LessonFinishedCountDto lessonFinishedCountDto = lessonFinishedCountDtoMap.get(record.getCourseId());
            if (lessonFinishedCountDto != null) {
                learningPlanVO.setWeekLearnedSections(lessonFinishedCountDto.getFinishedCount());
            }
            learningPlanVOS.add(learningPlanVO);
        }
        learningPlanPageVO.setRecords(learningPlanVOS);

        return Result.success(learningPlanPageVO);
    }

    private List<CourseSimpleInfoDTO> getSimpleInfoDTOs(List<Long> courseIds) {
        List<CourseSimpleInfoDTO> courseSimpleInfoDTOS = new ArrayList<>(courseIds.size());
        for (Long courseId : courseIds) {
            CourseSimpleInfoDTO courseSimpleInfoDTO = new CourseSimpleInfoDTO();
            courseSimpleInfoDTO.setId(courseId);
            courseSimpleInfoDTO.setValidDuration(4);
            courseSimpleInfoDTO.setCoverUrl("https://img.alicdn.com/imgextra/i1/O1CN01yjyXqh1JXwQyjyXqh_!!6000000000081-0-tps-1200-1200.jpg");
            courseSimpleInfoDTO.setName("java" + courseId);
            courseSimpleInfoDTO.setSectionNum(10);
            courseSimpleInfoDTOS.add(courseSimpleInfoDTO);
        }
        return courseSimpleInfoDTOS;
    }
}
