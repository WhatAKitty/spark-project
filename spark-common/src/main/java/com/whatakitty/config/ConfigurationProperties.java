package com.whatakitty.config;

import java.lang.annotation.*;

/**
 * Configuration properties location specific.
 *
 * Created by WhatAKitty on 2017/1/24.
 */
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigurationProperties {

    /**
     * Set the properties to this field.
     *
     * @return the location of properties file.
     */
    String[] propertiesLocation();

}
