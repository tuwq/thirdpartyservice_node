package com.tuwq.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 异常返回json
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionResult {
    // 错误状态码
    private int code;
    // 错误信息
    private String msg;

    public static ExceptionResult error(int code, String msg) {
        return ExceptionResult.builder().code(code).msg(msg).build();
    }
}
