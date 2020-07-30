package com.tom.common.localcache.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串处理工具类
 *
 * @author 温龙盛
 * @date 2020/7/30 9:14
 */
public class MyStringUtils {

    /**
     * 中划线转驼峰
     * <p>
     * 示例：
     * hello-world => helloWorld
     * hello--world => helloWorld
     * hello-world- => helloWorld
     * hello-World => helloWorld
     * Hello-World => helloWorld
     *
     * @param str 要处理的字符串
     * @return 转换后的字符串
     */
    public static String kebabCaseToCamelCase(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        final char separate = '-';
        StringBuilder sb = new StringBuilder();
        if (str.charAt(0) != separate) {
            sb.append(Character.toLowerCase(str.charAt(0)));
        }
        for (int i = 1, len = str.length(); i < len; i++) {
            char ch = str.charAt(i);
            if (ch == separate) {
                continue;
            }
            if (str.charAt(i - 1) == separate) {
                sb.append(Character.toUpperCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}

