package com.atguigu.promotion.service;

import com.atguigu.promotion.domain.po.Coupon;
import com.atguigu.promotion.domain.po.UserCoupon;
import com.atguigu.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 服务类
 * </p>
 *
 * @author wzj
 * @since 2026-06-06
 */
public interface IUserCouponService extends IService<UserCoupon> {

    /**
     * 用户领取优惠券
     * @param couponId 优惠券id
     * @return
     */
    Result<Void> receiveCoupon(Long couponId);

    /**
     * 用户兑换优惠券
     * @param code
     * @return
     */
    Result<Void> exchangeCoupon(String code);

    void checkAndCreateUserCoupon(String userIdStr, Coupon coupon);
}
