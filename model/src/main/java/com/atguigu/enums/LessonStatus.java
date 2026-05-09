package com.atguigu.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum LessonStatus implements BaseEnum<Integer> {

    NOT_BEGIN(0, "未学习"),
    LEARNING(1, "学习中"),
    FINISHED(2, "已学完"),
    EXPIRED(3, "已过期");

    @EnumValue      // MyBatis-Plus 映射数据库值
    @JsonValue      // Jackson 序列化为 JSON 时使用此值
    private final Integer value;

    private final String desc;

    LessonStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 反序列化时，根据传入的整型值找到对应枚举
     * mode = DELEGATING 表示使用单一参数构造
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static LessonStatus of(Integer value) {
        if (value == null) {
            return null;
        }
        for (LessonStatus status : LessonStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 可选：根据 desc 或其他条件获取枚举，按需添加
     */
    public static LessonStatus ofDesc(String desc) {
        if (desc == null) {
            return null;
        }
        for (LessonStatus status : LessonStatus.values()) {
            if (status.getDesc().equals(desc)) {
                return status;
            }
        }
        return null;
    }
}