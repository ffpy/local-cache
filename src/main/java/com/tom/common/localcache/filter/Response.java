package com.tom.common.localcache.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class Response {
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR = -1;
    public static final String MSG_SUCCESS = "success";

    /** 码值 */
    private final int code;

    /** 返回数据 */
    private final Object data;

    public static Response success() {
        return success(MSG_SUCCESS);
    }

    public static Response success(Object data) {
        return new Response(CODE_SUCCESS, data);
    }

    public static Response error(Object data) {
        return new Response(CODE_ERROR, data);
    }
}

