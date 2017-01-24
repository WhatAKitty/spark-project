package com.whatakitty.config;

import java.lang.annotation.*;

/**
 * 配置文件声明
 *
 * Created by WhatAKitty on 2017/1/21.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Configuration {

    public static final int DEFAULT_ORDER = 10;

    /**
     * 配置文件加载顺序，默认顺序值#DEFAULT_ORDER
     *
     * @return 配置顺序
     */
    int order() default DEFAULT_ORDER;

}
