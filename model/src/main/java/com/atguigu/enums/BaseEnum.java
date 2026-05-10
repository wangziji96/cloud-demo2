package com.atguigu.enums;

/**
 * 通用枚举接口，所有枚举必须实现此接口
 * @param <T> 枚举值的数据类型（通常为 Integer 或 String）
 */
public interface BaseEnum<T> {

    /**
     * 获取枚举的存储值（数据库/JSON存此值）
     */
    T getValue();

    /**
     * 获取枚举的描述（前端展示用）
     */
    String getDesc();

    /**
     * 根据存储值获取枚举实例（工具方法，方便调用）
     */
    static <E extends BaseEnum<?>> E of(Class<E> enumClass, Object value) {
        if (value == null) {
            return null;
        }
        for (E enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.getValue().equals(value)) {
                return enumConstant;
            }
        }
        return null;
    }
    default boolean equalsValue(Integer value){
        if (value == null) {
            return false;
        }
        return getValue() == value;
    }
}