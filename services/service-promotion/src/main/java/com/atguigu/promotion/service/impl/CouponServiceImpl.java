package com.atguigu.promotion.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.hash.Hash;
import com.atguigu.domain.query.PageDto;
import com.atguigu.exception.BusinessException;
import com.atguigu.promotion.domain.dto.CouponFormDTO;
import com.atguigu.promotion.domain.dto.CouponIssueFormDTO;
import com.atguigu.promotion.domain.po.Coupon;
import com.atguigu.promotion.domain.po.CouponScope;
import com.atguigu.promotion.domain.query.CouponQuery;
import com.atguigu.promotion.domain.vo.CouponDetail;
import com.atguigu.promotion.domain.vo.CouponPageVO;
import com.atguigu.promotion.domain.vo.CouponScopeVO;
import com.atguigu.promotion.enums.CouponStatus;
import com.atguigu.promotion.mapper.CouponMapper;
import com.atguigu.promotion.service.ICouponService;
import com.atguigu.result.Result;
import com.atguigu.utils.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券的规则信息 服务实现类
 * </p>
 *
 * @author wzj
 * @since 2026-05-25
 */
@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    private final CouponMapper couponMapper;
    private final CouponScopeServiceImpl couponScopeService;

    /**
     * 新增优惠券
     *
     * @param couponFormDTO
     * @return
     */
    @Override
    @Transactional
    public Result saveCoupon(CouponFormDTO couponFormDTO) {
        //1.转换成po
        Coupon coupon = BeanUtil.copyProperties(couponFormDTO, Coupon.class);
        String userId = UserContext.getUser().getUserId();
        coupon.setCreater(Long.valueOf(userId)).setUpdater(Long.valueOf(userId));
        save(coupon);
        //2.判断是否有指定范围
        if (!couponFormDTO.getSpecific()) {
            //没有指定范围
            return Result.success();
        }
        //3.保存范围
        List<Long> scopes = couponFormDTO.getScopes();
        if (CollectionUtil.isEmpty(scopes)) {
            throw new BusinessException(400, "请选择优惠券作用范围");
        }
        List<CouponScope> collect = scopes.stream()
                .map(cs -> new CouponScope().setCouponId(coupon.getId()).setType(1).setBizId(cs))
                .collect(Collectors.toList());
        couponScopeService.saveBatch(collect);
        return Result.success();
    }

    /**
     * 分页条件查询优惠券
     *
     * @param query
     * @return
     */
    @Override
    public PageDto<CouponPageVO> queryCouponPage(CouponQuery query) {
        //1.取出查询条件
        Integer type = query.getType();
        Integer status = query.getStatus();
        String name = query.getName();

        //2.分页查询
        Page<Coupon> page = new Page<>(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null, Coupon::getType, type)
                .eq(status != null, Coupon::getStatus, status)
                .like(name != null, Coupon::getName, name)
                .orderByDesc(Coupon::getCreateTime);
        Page<Coupon> couponPage = couponMapper.selectPage(page, wrapper);
        List<Coupon> coupons = couponPage.getRecords();
        if (CollectionUtil.isEmpty(coupons)) {
            PageDto<CouponPageVO> pageDto = new PageDto<>();
            pageDto.setTotal(0L);
            pageDto.setTotalPage(0);
            return pageDto;
        }

        //3.转换成VO
        List<CouponPageVO> couponPageVOS = BeanUtil.copyToList(coupons, CouponPageVO.class);

        PageDto<CouponPageVO> pageDto = new PageDto<>();
        pageDto.setTotal(couponPage.getTotal());
        pageDto.setTotalPage(Math.toIntExact(couponPage.getPages()));
        pageDto.setRecords(couponPageVOS);
        return pageDto;
    }

    /**
     * 优惠券发放
     *
     * @param dto
     * @return
     */
    @Override
    public Result beginIssue(CouponIssueFormDTO dto) {
        //1.查询优惠券
        Coupon coupon = couponMapper.selectById(dto.getId());
        if (coupon == null) {
            throw new BusinessException(400, "优惠券不存在");
        }
        //2.判断优惠券状态，是否是未发放或暂停
        if (coupon.getStatus() != CouponStatus.DRAFT && coupon.getStatus() != CouponStatus.PAUSE) {
            throw new BusinessException(400, "优惠券状态错误,不能发放");
        }
        //3.根据是否有立刻发放
        LocalDateTime issueBeginTime = dto.getIssueBeginTime();
        LocalDateTime now = LocalDateTime.now();
        //开始发放时间为null，或者当前时间在开始发放时间之后，代表立刻发放
        boolean isBegin = issueBeginTime == null || now.isAfter(issueBeginTime);
        //4.更新优惠券
        Coupon c = BeanUtil.copyProperties(dto, Coupon.class);
        if (isBegin) {
            //立刻发放，将状态改为发放中
            c.setStatus(CouponStatus.ISSUING).setIssueBeginTime(now);
        } else {
            //非立刻发放，将状态改为未开始
            c.setStatus(CouponStatus.UN_ISSUE);
        }
        couponMapper.updateById(c);
        return null;
    }

    /**
     * 根据优惠券id查询优惠券详情
     *
     * @param id
     * @return
     */
    @Override
    public Result<CouponDetail> queryCouponDetailById(Long id) {
        //1.查询优惠券
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException(400, "优惠券不存在");
        }

        List<CouponScopeVO> couponScopeVOS = null;

        //2.优惠券是否指定范围
        if (coupon.getSpecific()) {
            List<CouponScope> couponScopes = couponScopeService.lambdaQuery()
                    .eq(CouponScope::getCouponId, coupon.getId())
                    .list();

            if (CollectionUtil.isEmpty(couponScopes)) {
                throw new BusinessException(400, "优惠券没有指定范围");
            }

            //远程查询课程分类Id及分类名
            Map<Long, String> courseCategoryMap = getCouserCategoryIdAndName();

            if (courseCategoryMap != null) {
                couponScopeVOS = couponScopes.stream()
                        .map(scope -> {
                            CouponScopeVO vo = new CouponScopeVO();
                            vo.setId(scope.getBizId());
                            vo.setName(courseCategoryMap.get(scope.getBizId()));
                            return vo;
                        })
                        .collect(Collectors.toList());
            }
        }

        CouponDetail couponDetail = BeanUtil.copyProperties(coupon, CouponDetail.class);
        couponDetail.setCouponScope(couponScopeVOS);
        return Result.success(couponDetail);
    }


    private Map<Long, String> getCouserCategoryIdAndName() {
        HashMap<Long, String> map = new HashMap<>();
        map.put(1L, "Java");
        map.put(2L, "Python");
        map.put(3L, "C++");
        map.put(4L, "语文");
        return map;
    }
}
