package com.huanghe.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelfCheck {
    String check();

    String condition() default "";

    String msg() default "";
}
