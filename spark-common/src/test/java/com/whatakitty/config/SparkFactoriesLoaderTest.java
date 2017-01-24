package com.whatakitty.config;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by WhatAKitty on 2017/1/22.
 */
public class SparkFactoriesLoaderTest {

    @Test
    public void searchTest() {
        List<String> configurations = SparkFactoriesLoader.loadFactoryNames(Configuration.class, null);
        Assert.assertEquals(1, configurations.size());
    }

}
