package com.tuwq.base;

import com.tuwq.common.JsonResult;

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
