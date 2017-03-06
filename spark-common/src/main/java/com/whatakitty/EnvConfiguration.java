package com.whatakitty;

import com.whatakitty.config.Configuration;

/**
 * Created by WhatAKitty on 2017/3/6.
 */
@Configuration(order = 0)
public class EnvConfiguration {

    public EnvConfiguration() {
        // init env
        Env.SingletonHolder.getInstance();
    }

}
