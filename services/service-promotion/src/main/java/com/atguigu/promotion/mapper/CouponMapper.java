package com.atguigu.promotion.mapper;

import com.atguigu.promotion.domain.po.Coupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 优惠券的规则信息 Mapper 接口
 * </p>
 *
 * @author wzj
 * @since 2026-05-25
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    int incrIssueNum(@Param("couponId") Long couponId);
}
