package com.tuwq.validator;

import com.tuwq.util.RegexUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 验证手机号格式
 */
public class TelePhoneConstraintValidator implements ConstraintValidator<TelePhoneConstraint, Object> {
    // 注解初始化时被调用
    @Override
    public void initialize(TelePhoneConstraint constraintAnnotation) {

    }

    // 注解验证时的逻辑
    // 第一个参数是注解挂载的对象,值
    // 第二个参数是验证上下文对象,功能作用看源码上的注释说明
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return false;
        }
        if (!RegexUtil.regexPhone(value.toString())) {
            return false;
        }
        return true;
    }

}
