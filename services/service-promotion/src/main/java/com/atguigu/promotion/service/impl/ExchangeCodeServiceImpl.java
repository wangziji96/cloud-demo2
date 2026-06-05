package com.atguigu.promotion.service.impl;

import com.atguigu.promotion.constants.PromotionConstants;
import com.atguigu.promotion.domain.po.Coupon;
import com.atguigu.promotion.domain.po.ExchangeCode;
import com.atguigu.promotion.mapper.ExchangeCodeMapper;
import com.atguigu.promotion.service.IExchangeCodeService;
import com.atguigu.promotion.utils.CodeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 兑换码 服务实现类
 * </p>
 *
 * @author wzj
 * @since 2026-06-03
 */
@Slf4j
@Service
public class ExchangeCodeServiceImpl extends ServiceImpl<ExchangeCodeMapper, ExchangeCode> implements IExchangeCodeService {
    BoundValueOperations<String, String> serialOps;
    public ExchangeCodeServiceImpl(StringRedisTemplate redisTemplate) {
        serialOps = redisTemplate.boundValueOps(PromotionConstants.COUPON_CODE_SERIAL_KEY);
    }

    /**
     * 异步生成兑换码
     *
     * @param coupon
     */
    @Override
    @Async("exchangeCodeExecutor")
    public void asyncGenerateCode(Coupon coupon) {
        //1.获取优惠券发行量
        Integer totalNum = coupon.getTotalNum();
        List<ExchangeCode> exchangeCodes = new ArrayList<>(totalNum);

        //2.获取edis自增序号
        Long result = serialOps.increment(totalNum);
        if (result == null) {
            return;
        }
        int maxSerialNum = result.intValue();
        for (int i = maxSerialNum - totalNum + 1; i <=maxSerialNum; i++) {
            //3.根据优惠券id和序号生成兑换码
            String code = CodeUtil.generateCode(i, coupon.getId());
            //3.将兑换码保存到数据库
            ExchangeCode exchangeCode = new ExchangeCode();
            exchangeCode
                    .setId(i)
                    .setExchangeTargetId(coupon.getId())
                    .setCode(code)
                    .setExpiredTime(coupon.getIssueEndTime());
            exchangeCodes.add(exchangeCode);
        }
        saveBatch(exchangeCodes);
    }
}
