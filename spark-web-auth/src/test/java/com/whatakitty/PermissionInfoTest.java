package com.whatakitty;

import static spark.Spark.*;

import com.whatakitty.config.ConfigurationManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by WhatAKitty on 2017/1/21.
 */
public class PermissionInfoTest {

    @AfterClass
    public static void tearDown() {
        stop();
    }

    @BeforeClass
    public static void setup() throws IOException {
        ConfigurationManager.enableAutoConfiguration();
        SecurityUtils.getSubject().login(new UsernamePasswordToken("admin", "1234"));
    }

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testApp() {
        PermissionInfo.permissionBased("admin:main:view").permitted().complete(() -> get("/", (req, res) -> "hello world!"));
    }

}
