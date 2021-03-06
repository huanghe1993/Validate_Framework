package com.huanghe.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MinNum {
    String check() default "MinSizeCheck";

    String msg() default "";

    int value() default 0;
}
