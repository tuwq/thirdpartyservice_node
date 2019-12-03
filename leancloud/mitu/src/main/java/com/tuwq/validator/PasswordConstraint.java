package com.tuwq.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义验证注解,验证密码格式是否正确
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= PasswordConstraintValidator.class)
public @interface PasswordConstraint {
    String message();
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
