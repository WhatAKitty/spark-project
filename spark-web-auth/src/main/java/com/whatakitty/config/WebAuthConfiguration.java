package com.whatakitty.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;

/**
 * Created by WhatAKitty on 2017/1/21.
 */
@Configuration(withMissing = true)
public class WebAuthConfiguration {

    private static final String INI_LOCATION = "META-INF/shiro.ini";

    public WebAuthConfiguration() {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(INI_LOCATION);
        SecurityManager securityManager=factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

}
