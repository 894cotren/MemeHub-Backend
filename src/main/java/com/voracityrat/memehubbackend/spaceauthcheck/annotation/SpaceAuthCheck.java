package com.voracityrat.memehubbackend.spaceauthcheck.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author grey
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpaceAuthCheck {

    /**
     * 指定必须具有的权限
     */
    String mustPermission() default "";
}
