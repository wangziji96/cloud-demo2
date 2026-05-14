package com.atguigu.domain.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<E> {
    //总条数
    private Long total;
    //总页数
    private Integer totalPage;
    //当前页
    private List<E> records;
}
