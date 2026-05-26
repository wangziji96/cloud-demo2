package com.atguigu.promotion.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(description = "优惠券发放的表单实体")
public class CouponIssueFormDTO {
    @Schema(description = "优惠券id")
    private Long id;

    @Schema(description = "发放开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "发放开始时间必须晚于当前时间")
    private LocalDateTime issueBeginTime;

    @Schema(description = "发放结束时间")
    @Future(message = "发放结束时间必须晚于当前时间")
    @NotNull(message = "发放结束时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime issueEndTime;

    @Schema(description = "有效天数")
    private Integer termDays;

    @Schema(description = "使用有效期开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime termBeginTime;

    @Schema(description = "使用有效期结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime termEndTime;
}
