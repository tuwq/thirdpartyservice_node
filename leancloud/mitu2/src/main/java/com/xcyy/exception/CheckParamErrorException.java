package com.xcyy.exception;

/**
 * 检查参数异常
 */
public class CheckParamErrorException extends BaseException {

    public CheckParamErrorException(String msg) {
        super(com.xcyy.common.ResultCode.PARAM_ERROR, msg);
    }
}
