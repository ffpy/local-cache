package com.tom.common.localcache.cache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析配置文件中的时间字符串
 * <p>
 * 例如：20m解析为{ value = 20, unit = {@link TimeUnit#MINUTES} }，
 * 如果没有写时间单位字符串，则默认单位为{@link TimeUnit#MINUTES}。
 * 支持的时间单位字符串如下：
 * ms: {@link TimeUnit#MILLISECONDS}
 * s: {@link TimeUnit#SECONDS}
 * m: {@link TimeUnit#MINUTES}
 * h: {@link TimeUnit#HOURS}
 * d: {@link TimeUnit#DAYS}
 *
 * @author 温龙盛
 * @date 2020/7/29 9:28
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TimeValue {

    /** 时间字符串解析表达式 */
    private static final Pattern PATTERN = Pattern.compile("(\\d+)(\\w*)");

    /** 没有指定时间单位时的默认单位 */
    private static final TimeUnit DEFAULT_UNIT = TimeUnit.MINUTES;

    /** 时间单位缩写到时间单位的映射 */
    private static final Map<String, TimeUnit> UNIT_MAP;

    static {
        Map<String, TimeUnit> map = new HashMap<>();
        map.put("ms", TimeUnit.MILLISECONDS);
        map.put("s", TimeUnit.SECONDS);
        map.put("m", TimeUnit.MINUTES);
        map.put("h", TimeUnit.HOURS);
        map.put("d", TimeUnit.DAYS);
        UNIT_MAP = Collections.unmodifiableMap(map);
    }

    /** 时间值 */
    private final int value;

    /** 时间单位 */
    @NonNull
    private final TimeUnit unit;

    /**
     * 解析时间字符串
     *
     * @param timeStr 时间字符串
     * @return 解析结果
     */
    public static TimeValue parse(String timeStr) {
        Matcher matcher = PATTERN.matcher(timeStr);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid time value: " + timeStr);
        }
        int value = Integer.parseInt(matcher.group(1));

        String unitStr = matcher.group(2);
        TimeUnit unit;
        if (StringUtils.isNotEmpty(unitStr)) {
            unit = UNIT_MAP.get(matcher.group(2));
        } else {
            unit = DEFAULT_UNIT;
        }

        return new TimeValue(value, unit);
    }

    public TimeValue(int value, TimeUnit unit) {
        if (value < 0) {
            throw new IllegalArgumentException("value必须不小于0");
        }
        this.value = value;
        this.unit = Objects.requireNonNull(unit);
    }
}
