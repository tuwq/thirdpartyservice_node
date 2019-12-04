package com.xcyy.exception;

public class BaseException extends RuntimeException {
    protected int ResultCode;

    protected String msg;

    public BaseException(int ResultCode,String msg) {
        this.ResultCode = ResultCode;
        this.msg = msg;
    }

    public int getResultCode() {
        return ResultCode;
    }
    public String getMsg() {
        return msg;
    }

    public BaseException() {
        super();
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public static void main(String[] args) {
        System.out.println("123");
    }
}
