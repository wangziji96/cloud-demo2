package com.atguigu.domain.query;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageQuery {
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Integer DEFAULT_PAGE_NUM = 1;

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = DEFAULT_PAGE_NUM;

    @Min(value = 1, message = "每页查询数量不能小于1")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    //是否升序
    private Boolean isAsc = true;

    //排序字段
    private String sortBy;
}
