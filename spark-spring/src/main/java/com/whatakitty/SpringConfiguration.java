package com.whatakitty;

import com.whatakitty.config.Configuration;

/**
 * Created by WhatAKitty on 2017/3/6.
 */
@Configuration(withMissing = true, order = Configuration.DEFAULT_ORDER + 10)
public class SpringConfiguration {

    public SpringConfiguration() {
        Env.SingletonHolder.getInstance().setSpring();
    }

}
