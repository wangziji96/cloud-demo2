package com.atguigu.promotion.controller;


import com.atguigu.promotion.service.IUserCouponService;
import com.atguigu.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 前端控制器
 * </p>
 *
 * @author wzj
 * @since 2026-06-06
 */
@RestController
@RequestMapping("/user-coupons")
@RequiredArgsConstructor
@Tag(name = "用户优惠券接口")
public class UserCouponController {
    private final IUserCouponService userCouponService;

    @PostMapping("/{couponId}/receive")
    @Operation(description = "用户领取优惠券")
    public Result<Void> receiveCoupon(@PathVariable("couponId") Long couponId) {
        return userCouponService.receiveCoupon(couponId);
    }

    @Operation(description = "兑换码兑换优惠券")
    @PostMapping("/{code}/exchange")
    public Result<Void> exchangeCoupon(@PathVariable("code") String code) {
        return userCouponService.exchangeCoupon(code);
    }
}
