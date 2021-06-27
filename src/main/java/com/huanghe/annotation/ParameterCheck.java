package com.huanghe.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParameterCheck {

    Class<?> exceptionHandler();

    String condition() default "";

    String msg() default "";

    String selfCheck() default "";
}