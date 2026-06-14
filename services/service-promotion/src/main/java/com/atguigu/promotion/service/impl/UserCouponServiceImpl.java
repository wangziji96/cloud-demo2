package com.atguigu.promotion.service.impl;

import com.atguigu.exception.BusinessException;
import com.atguigu.promotion.domain.po.Coupon;
import com.atguigu.promotion.domain.po.ExchangeCode;
import com.atguigu.promotion.domain.po.UserCoupon;
import com.atguigu.promotion.enums.ExchangeCodeStatus;
import com.atguigu.promotion.mapper.CouponMapper;
import com.atguigu.promotion.mapper.UserCouponMapper;
import com.atguigu.promotion.service.IExchangeCodeService;
import com.atguigu.promotion.service.IUserCouponService;
import com.atguigu.promotion.utils.CodeUtil;
import com.atguigu.result.Result;
import com.atguigu.utils.UserContext;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 服务实现类
 * </p>
 *
 * @author wzj
 * @since 2026-06-06
 */
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements IUserCouponService {

    private final CouponMapper couponMapper;
    private final IExchangeCodeService codeService;

    /**
     * 用户领取优惠券
     *
     * @param couponId 优惠券id
     * @return
     */

    @Override
    public Result<Void> receiveCoupon(Long couponId) {
        //1. 查询优惠券信息
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException(400, "优惠券不存在");
        }
        //2. 判断优惠券领取时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getIssueBeginTime()) || now.isAfter(coupon.getIssueEndTime())) {
            throw new BusinessException(400, "优惠券领取时间已过");
        }
        //3. 判断优惠券领取数量
        if (coupon.getIssueNum() >= coupon.getTotalNum()) {
            throw new BusinessException(400, "优惠券已领取完");
        }
        //4. 校验并生成用户券
        String userIdStr = UserContext.getUser().getUserId();
        synchronized (userIdStr.intern()) {
            IUserCouponService userCouponService = (IUserCouponService)AopContext.currentProxy();
            userCouponService.checkAndCreateUserCoupon(userIdStr, coupon);
        }
        return Result.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void checkAndCreateUserCoupon(String userId, Coupon coupon) {
        //4. 判断优惠券每人领取数量
        Long count = lambdaQuery().eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, coupon.getId()).count();
        if (count != null && count >= coupon.getUserLimit()) {
            throw new BusinessException(400, "您已超过领取次数");
        }
        //5. 更新优惠券领取数量
        int r = couponMapper.incrIssueNum(coupon.getId());
        if (r == 0) {
            throw new BusinessException(400, "优惠券库存不足");
        }
        //6. 插入用户优惠券信息
        saveUserCoupon(coupon, Long.parseLong(userId));
    }

    /**
     * 用户兑换优惠券
     *
     * @param code
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<Void> exchangeCoupon(String code) {
        //1. 解析兑换码
        long seriaNum = CodeUtil.parseCode(code);
        //2. 判断兑换码是否兑换 set bitmap会返回旧值，可以根据旧值判断是否兑换
        boolean isExchange = codeService.updateExchangeCodeMark(seriaNum, true);
        if (isExchange) {
            throw new BusinessException(400, "兑换码已兑换");
        }
        try {
            //3. 查询兑换码
            ExchangeCode exchangeCode = codeService.getById(seriaNum);
            if (exchangeCode == null) {
                throw new BusinessException(400, "兑换码不存在");
            }
            //4. 判断兑换码是否过期
            if (exchangeCode.getExpiredTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException(400, "兑换码已过期");
            }
            //5.校验优惠券，更新优惠券，领取优惠券
            Coupon coupon = couponMapper.selectById(exchangeCode.getExchangeTargetId());
            checkAndCreateUserCoupon(UserContext.getUser().getUserId(), coupon);
            //6. 更新兑换码状态
            Long userId = Long.parseLong(UserContext.getUser().getUserId());
            codeService.lambdaUpdate().eq(ExchangeCode::getId, seriaNum)
                    .set(ExchangeCode::getUserId, userId)
                    .set(ExchangeCode::getUpdateTime, LocalDateTime.now())
                    .set(ExchangeCode::getStatus, ExchangeCodeStatus.USED)
                    .update();
        } catch (Exception e) {
            //出现异常，恢复兑换码状态
            codeService.updateExchangeCodeMark(seriaNum, false);
            throw e;
        }
        return Result.success();
    }

    private void saveUserCoupon(Coupon coupon, Long userId) {
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(coupon.getId());
        //判断优惠券有效期
        LocalDateTime termBeginTime = coupon.getTermBeginTime();
        LocalDateTime termEndTime = coupon.getTermEndTime();
        //termBeginTime为null，说明优惠券有效期是按天算算
        if (termBeginTime == null) {
            termBeginTime = LocalDateTime.now();
            termEndTime = termBeginTime.plusDays(coupon.getTermDays());
        }
        userCoupon.setTermBeginTime(termBeginTime);
        userCoupon.setTermEndTime(termEndTime);
        save(userCoupon);
    }
}
