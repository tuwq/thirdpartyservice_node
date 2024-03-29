package com.xcyy.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义验证注解,验证手机格式是否正确
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= TelePhoneConstraintValidator.class)
public @interface TelePhoneConstraint {
    String message();
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
