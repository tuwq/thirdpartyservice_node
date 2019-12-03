package com.tuwq.common;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * http统一返回包装对象
 * @param <T>
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "返回json对象")
public class JsonResult<T> {

    @ApiModelProperty(value = "错误消息")
    private String msg; // 错误消息
    @ApiModelProperty(value = "返回码")
    private int code; // 返回码
    @ApiModelProperty(value = "是否成功")
    private boolean flag;   // 是否成功
    @ApiModelProperty(value = "返回主体")
    private T result;  // 返回主体

    public static <T> JsonResult<T> success(T result) {
        return JsonResult.<T>builder().result(result).flag(true).code(ResultCode.REQUEST_SUCCESS).build();
    }

    public static <T> JsonResult<T> success(T result, String msg) {
        return JsonResult.<T>builder().result(result).flag(true).code(ResultCode.REQUEST_SUCCESS).msg(msg).build();
    }

    public static JsonResult<Void> success() {
        return JsonResult.<Void>builder().flag(true).code(ResultCode.REQUEST_SUCCESS).build();
    }

    public static JsonResult<Void> error(int code, String msg) {
        return JsonResult.<Void>builder().result(null).code(code).msg(msg).build();
    }

    public static JsonResult<Void> error(String msg) {
        return JsonResult.<Void>builder().msg(msg).code(ResultCode.PARAM_ERROR).build();
    }

    public static JsonResult<Void> error(int code) {
        return JsonResult.<Void>builder().result(null).code(code).build();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("flag", flag);
        map.put("msg", msg);
        map.put("result", result);
        return map;
    }
}
