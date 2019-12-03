package com.tuwq.validator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 验证必须是数字
 */
public class IsNumberConstraintValidator implements ConstraintValidator<IsNumberConstraint, Object> {
    // 注解初始化时被调用
    @Override
    public void initialize(IsNumberConstraint constraintAnnotation) {

    }

    // 注解验证时的逻辑
    // 第一个参数是注解挂载的对象,值
    // 第二个参数是验证上下文对象,功能作用看源码上的注释说明
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!StringUtils.isNumeric(value.toString())) {
            return false;
        }
        return true;
    }

}
