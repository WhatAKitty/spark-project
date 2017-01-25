package com.whatakitty;

import com.whatakitty.config.ConfigurationManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by WhatAKitty on 2017/1/24.
 */
public class ActiveRecordConfigurationTest {

    @Test
    public void test() {
        ConfigurationManager.enableAutoConfiguration();
        ActiveRecordConfiguration activeRecordConfiguration = ConfigurationManager.getConfiguration(ActiveRecordConfiguration.class.getName());
        Assert.assertNotNull(activeRecordConfiguration);
        Assert.assertNotNull(Db.findFirst("select * from test limit 1"));
    }

}
