package com.atguigu.promotion.controller;


import com.atguigu.common.R;
import com.atguigu.domain.query.PageDto;
import com.atguigu.promotion.domain.dto.CouponFormDTO;
import com.atguigu.promotion.domain.dto.CouponIssueFormDTO;
import com.atguigu.promotion.domain.po.Coupon;
import com.atguigu.promotion.domain.query.CouponQuery;
import com.atguigu.promotion.domain.vo.CouponDetail;
import com.atguigu.promotion.domain.vo.CouponPageVO;
import com.atguigu.promotion.service.ICouponService;
import com.atguigu.promotion.service.IExchangeCodeService;
import com.atguigu.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 优惠券的规则信息 前端控制器
 * </p>
 *
 * @author wzj
 * @since 2026-05-25
 */
@RestController
@RequestMapping("/coupons")
@Tag(name = "优惠券接口")
public class CouponController {
    @Autowired
    private ICouponService couponService;
    @Autowired
    private IExchangeCodeService exchangeCodeService;

    /**
     * 新增优惠券
     */
    @PostMapping
    @Operation(summary = "新增优惠券", description = "创建新的优惠券规则信息")
    public Result saveCoupon(@RequestBody @Valid CouponFormDTO couponFormDTO) {
        return couponService.saveCoupon(couponFormDTO);
    }

    /**
     * 分页条件查询优惠券
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询优惠券", description = "根据条件分页查询优惠券列表")
    public PageDto<CouponPageVO> queryCouponPage(CouponQuery query) {
        return couponService.queryCouponPage(query);
    }

    @PutMapping("/{id}/issue")
    @Operation(summary = "开始发放优惠券", description = "开始发放优惠券")
    public Result beginIssue(@RequestBody @Valid CouponIssueFormDTO dto) {
        return couponService.beginIssue(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据优惠券id查询优惠券详情", description = "根据ID查询优惠券详情")
    public Result<CouponDetail> queryCouponDetailById(@PathVariable Long id) {
        return couponService.queryCouponDetailById(id);
    }

}
