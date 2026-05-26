package com.atguigu.promotion.mapper;

import com.atguigu.promotion.domain.po.Promotion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 促销活动，形式多种多样，例如：优惠券 Mapper 接口
 * </p>
 *
 * @author wzj
 * @since 2026-05-25
 */
@Mapper
public interface PromotionMapper extends BaseMapper<Promotion> {

}
