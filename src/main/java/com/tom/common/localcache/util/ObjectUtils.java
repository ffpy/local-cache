package com.tom.common.localcache.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ObjectUtils {

    /**
     * 将字符串转换为指定类型的实例，只支持基本类型
     *
     * @param str  要转换的字符串
     * @param type 目标类型
     * @param <T>  目标类型
     * @return 目标类型的实例
     * @throws IllegalArgumentException 如果不支持指定类型
     * @throws NumberFormatException    如果数字格式不正确
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(String str, Class<T> type) {
        if (type == String.class) {
            return (T) str;
        }

        Function<String, ?> f = ParseMapHolder.MAP.get(Objects.requireNonNull(type));
        if (f == null) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
        if (str == null || str.isEmpty()) {
            return null;
        }
        return (T) f.apply(str);
    }

    protected static class ParseMapHolder {
        public static final Map<Class<?>, Function<String, ?>> MAP;

        static {
            MAP = new HashMap<>(32);
            MAP.put(Byte.class, Byte::valueOf);
            MAP.put(Short.class, Short::valueOf);
            MAP.put(Integer.class, Integer::valueOf);
            MAP.put(Long.class, Long::valueOf);
            MAP.put(Float.class, Float::valueOf);
            MAP.put(Double.class, Double::valueOf);
            MAP.put(Boolean.class, Boolean::valueOf);
            MAP.put(Character.class, str -> StringUtils.isEmpty(str) ? null : str.charAt(0));

            MAP.put(byte.class, Byte::parseByte);
            MAP.put(short.class, Short::parseShort);
            MAP.put(int.class, Integer::parseInt);
            MAP.put(long.class, Long::parseLong);
            MAP.put(float.class, Float::parseFloat);
            MAP.put(double.class, Double::parseDouble);
            MAP.put(boolean.class, Boolean::parseBoolean);
            MAP.put(char.class, str -> StringUtils.isEmpty(str) ? null : str.charAt(0));
        }
    }
}
