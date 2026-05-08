package com.ran.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    int DEFAULT_WINDOW = 60;
    int DEFAULT_LIMIT = 10;

    String key() default "";

    int window() default DEFAULT_WINDOW;

    int limit() default DEFAULT_LIMIT;

    String dimension() default "USER";
}
