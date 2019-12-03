package com.tuwq.param.registration;

import com.tuwq.validator.TelePhoneConstraint;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterParam {

    private String phonePrefix = "+86"; // 手机号码国家地区前缀

    @NotBlank(message = "手机号不可以为空")
    @TelePhoneConstraint(message = "手机号格式错误")
    private String phoneNumber; // 注册手机号码

    private String smsCode; // 手机验证码

    private String invitationCode; // 邀请码

}
