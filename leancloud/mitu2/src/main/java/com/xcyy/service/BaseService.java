package com.xcyy.service;

import com.xcyy.common.JsonResult;

/**
 * 基类Service
 */
public class BaseService {
    protected JsonResult<Void> success() {
        return JsonResult.<Void>success();
    }

    protected <T> JsonResult<T> success(T data) {
        return JsonResult.<T>success(data);
    }

    protected JsonResult<Void> error(int code, String msg) {
        return JsonResult.<Void>error(code, msg);
    }
}
