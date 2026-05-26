package com.atguigu.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果对象")
public class PageDto<E> {
    @Schema(description = "总条数", example = "100")
    private Long total;
    @Schema(description = "总页数", example = "10")
    private Integer totalPage;
    @Schema(description = "当前页数据列表")
    private List<E> records;
}
