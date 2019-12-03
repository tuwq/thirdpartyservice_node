package com.tuwq.model;

import lombok.Data;

@Data
public class UserDo {

    public static String modelName = "_User"; // leancloud中的_User名称

    public static String username = "username"; // 用户名

    public static String mobilePhoneNumber = "mobilePhoneNumber"; // 手机号码

    public static String mobilePhoneVerified = "mobilePhoneVerified"; // 默认情况下，当用户注册或变更手机号后，mobilePhoneVerified 会被设为 false

}
