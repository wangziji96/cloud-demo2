package com.atguigu.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseSimpleInfoDTO {

    private Long id;

    private String name;

    private String coverUrl;

    private Integer price;

    private Integer status;

    private Boolean free;

    private Long firstCateId;

    private Long secondCateId;

    private Long thirdCateId;

    private Integer sectionNum;

    private LocalDateTime purchaseEndTime;
    private Integer validDuration;
    @JsonIgnore
    public List<Long> getCategoryIds(){
        return List.of(firstCateId, secondCateId, thirdCateId);
    }
}