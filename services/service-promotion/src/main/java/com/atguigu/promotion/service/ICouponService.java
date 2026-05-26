package com.atguigu.promotion.service;

import com.atguigu.domain.query.PageDto;
import com.atguigu.promotion.domain.dto.CouponFormDTO;
import com.atguigu.promotion.domain.dto.CouponIssueFormDTO;
import com.atguigu.promotion.domain.po.Coupon;
import com.atguigu.promotion.domain.query.CouponQuery;
import com.atguigu.promotion.domain.vo.CouponDetail;
import com.atguigu.promotion.domain.vo.CouponPageVO;
import com.atguigu.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 优惠券的规则信息 服务类
 * </p>
 *
 * @author wzj
 * @since 2026-05-25
 */
public interface ICouponService extends IService<Coupon> {

    /**
     * 新增优惠券
     * @param couponFormDTO
     * @return
     */
    Result saveCoupon(CouponFormDTO couponFormDTO);

    /**
     * 分页条件查询优惠券
     * @param query
     * @return
     */
    PageDto<CouponPageVO> queryCouponPage(CouponQuery query);

    /**
     * 优惠券发放
     * @param dto
     * @return
     */
    Result beginIssue(CouponIssueFormDTO dto);


    /**
     * 根据优惠券id查询优惠券详情
     * @param id
     * @return
     */
    Result<CouponDetail> queryCouponDetailById(Long id);
}
