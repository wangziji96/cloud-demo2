package com.atguigu.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "分页参数")
public class PageQuery {
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Integer DEFAULT_PAGE_NUM = 1;

    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "页码", example = "1")
    private Integer pageNo = DEFAULT_PAGE_NUM;

    @Min(value = 1, message = "每页查询数量不能小于1")
    @Schema(description = "每页查询数量", example = "20")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    //是否升序
    @Schema(description = "是否升序", example = "true")
    private Boolean isAsc = true;

    //排序字段
    @Schema(description = "排序字段", example = "id")
    private String sortBy;
}
