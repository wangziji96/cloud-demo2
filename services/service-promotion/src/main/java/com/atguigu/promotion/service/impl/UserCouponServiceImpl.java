package com.atguigu.promotion.service.impl;

import com.atguigu.promotion.domain.po.UserCoupon;
import com.atguigu.promotion.mapper.UserCouponMapper;
import com.atguigu.promotion.service.IUserCouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 服务实现类
 * </p>
 *
 * @author wzj
 * @since 2026-06-06
 */
@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements IUserCouponService {

}
