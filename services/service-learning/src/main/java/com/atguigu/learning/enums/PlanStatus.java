package com.atguigu.learning.enums;

import com.atguigu.enums.BaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PlanStatus implements BaseEnum<Integer> {
    NO_PLAN(0, "没有计划"),
    PLAN_RUNNING(1, "计划进行中"),
    ;
    @JsonValue
    @EnumValue
    int value;
    String desc;

    PlanStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PlanStatus of(Integer value){
        if (value == null) {
            return null;
        }
        for (PlanStatus status : values()) {
            if (status.equalsValue(value)) {
                return status;
            }
        }
        return null;
    }
}
