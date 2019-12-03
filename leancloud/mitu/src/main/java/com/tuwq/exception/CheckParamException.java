package com.tuwq.exception;

/**
 * 检查参数异常
 */
public class CheckParamException extends BaseException {


    public CheckParamException(int errorcode, String msg) {
        super(errorcode, msg);
    }
}
