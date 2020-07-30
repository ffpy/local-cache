package com.tom.common.localcache.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

interface Action {

    /** 操作成功时的返回信息 */
    String RESULT_SUCCESS = "success";

    /**
     * 创建响应成功结果
     *
     * @return 结果
     */
    default Result success() {
        return success(RESULT_SUCCESS);
    }

    /**
     * 创建响应成功结果
     *
     * @param data 响应数据
     * @return 结果
     */
    default Result success(Object data) {
        return new Result(HttpStatus.OK, data);
    }

    /**
     * 创建响应失败结果
     *
     * @param msg 错误信息
     * @return 结果
     */
    default Result error(String msg) {
        return new Result(HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * 动作执行结果
     */
    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class Result {

        /** 响应码 */
        private final HttpStatus status;

        /** 响应数据 */
        private final Object data;
    }
}
