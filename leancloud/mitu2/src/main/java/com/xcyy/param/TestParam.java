package com.xcyy.param;

import com.xcyy.validator.TelePhoneConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "测试接收参数")
@Data
public class TestParam {

    @ApiModelProperty(value = "手机号码,必须为正确的手机号码格式", required = true)
    @NotBlank(message = "手机号不可以为空")
    @TelePhoneConstraint(message = "手机号格式错误")
    private String phoneNumber;

}
