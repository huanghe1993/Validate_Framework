package com.huanghe.annotation;

import java.lang.annotation.*;

/**
 * 不需要检查
 */
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Uncheck {}

