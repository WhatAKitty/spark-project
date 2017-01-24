package com.whatakitty;

import com.whatakitty.config.Configuration;
import com.whatakitty.config.ConfigurationProperties;
import com.whatakitty.druid.DruidConfiguration;
import com.whatakitty.log.Logger;
import java.util.Properties;

/**
 * ActiveRecord Configuration.
 * // TODO customize the db properties file and other configurations.
 *
 * Created by WhatAKitty on 2017/1/23.
 */
@Configuration
public class ActiveRecordConfiguration {

    private static final Logger logger = Logger.getLogger(ActiveRecordConfiguration.class);

    @ConfigurationProperties(propertiesLocation = "classpath:META-INF/db.properties")
    public ActiveRecordConfiguration(Properties properties) {
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String driverClass = properties.getProperty("driverClass", "com.mysql.jdbc.Driver");
        DruidConfiguration druidConfiguration = new DruidConfiguration(url, username, password, driverClass);
        druidConfiguration.start();

        ActiveRecord activeRecord = new ActiveRecord(druidConfiguration.getDataSource());
        activeRecord.start();
    }

}
