package com.tuwq.common;

/**
 * http请求返回码
 */
public interface ResultCode {
    // 请求正常,返回正确数据
    int REQUEST_SUCCESS = 200;
    // 请求参数格式校验不正确
    int PARAM_FORMAT_ERROR = 401;
    // 请求参数错误,如密码错误
    int PARAM_ERROR = 402;
    // 未携带token
    int TOKEN_NO_CARRY = 410;
    // 不存在的token
    int TOKEN_NO_EXIST = 411;
    // 伪造的Token
    int TOKEN_IS_COUNTERFEIT = 412;
    // Token无法被正常解析
    int TOKEN_NO_ANALYSIS = 413;
    // Token过期
    int TOKEN_EXPIRED = 414;
    // 访问操作权限
    int PERMISSION_ERROR= 440;
    // 服务器未知错误
    int SERVER_ERROR = 504;

}
