package com.example.localcache.util;

import java.text.DecimalFormat;

/**
 * 数字工具类
 *
 * @author 温龙盛
 * @date 2020/7/16 18:15
 */
public class NumberUtils {

    private static DecimalFormat PERCENT_FORMAT;

    /**
     * 转为百分比字符串，如输入0.7536，输出75.36%
     *
     * @param rate  百分率
     * @return 百分比字符串
     */
    public static String toPercentString(double rate) {
        if (PERCENT_FORMAT == null) {
            PERCENT_FORMAT = new DecimalFormat("##.##");
        }

        return PERCENT_FORMAT.format(rate * 100) + "%";
    }
}
