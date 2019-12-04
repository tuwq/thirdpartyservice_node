package com.xcyy.exception;

public class CheckParamFormatException extends BaseException {
    public CheckParamFormatException(String msg) {
        super(com.xcyy.common.ResultCode.PARAM_FORMAT_ERROR, msg);
    }
}
