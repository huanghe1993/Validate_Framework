package com.huanghe.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface ParameterCheck {

    Class<?> exceptionHandler();

    String condition() default "";

    String msg() default "";

    String selfCheck() default "";
}