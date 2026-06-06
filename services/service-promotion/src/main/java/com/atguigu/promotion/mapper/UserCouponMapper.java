package com.atguigu.promotion.mapper;

import com.atguigu.promotion.domain.po.UserCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 Mapper 接口
 * </p>
 *
 * @author wzj
 * @since 2026-06-06
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

}
