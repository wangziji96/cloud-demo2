package com.atguigu.promotion.service;

import com.atguigu.promotion.domain.po.Coupon;
import com.atguigu.promotion.domain.po.ExchangeCode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 兑换码 服务类
 * </p>
 *
 * @author wzj
 * @since 2026-06-03
 */
public interface IExchangeCodeService extends IService<ExchangeCode> {

    /**
     * 异步生成兑换码
     * @param coupon
     */
    void asyncGenerateCode(Coupon coupon);
}
