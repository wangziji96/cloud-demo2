package com.atguigu.promotion.service.impl;

import com.atguigu.promotion.domain.po.Promotion;
import com.atguigu.promotion.mapper.PromotionMapper;
import com.atguigu.promotion.service.IPromotionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 促销活动，形式多种多样，例如：优惠券 服务实现类
 * </p>
 *
 * @author wzj
 * @since 2026-05-25
 */
@Service
public class PromotionServiceImpl extends ServiceImpl<PromotionMapper, Promotion> implements IPromotionService {

}
