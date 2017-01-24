package com.whatakitty.config;

import java.lang.annotation.*;

/**
 * Configuration Annotation
 *
 * Created by WhatAKitty on 2017/1/21.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Configuration {

    int DEFAULT_ORDER = 10;

    /**
     * Configuration name.
     *
     * @return the name of this configuration.
     */
    String name() default "";

    /**
     * Configuration load order, default order:#DEFAULT_ORDER
     *
     * @return 配置顺序
     */
    int order() default DEFAULT_ORDER;

    /**
     * The configuration could be overwritten by the other configuration which has the same configuration name.
     *
     * <ul>
     *     <ol>true: will be overwritten</ol>
     *     <ol>false: will not be overwritten</ol>
     * </ul>
     * @return can be overwritten.
     */
    boolean withMissing() default false;

}
