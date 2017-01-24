package com.whatakitty;

import com.whatakitty.config.Configuration;
import com.whatakitty.druid.DruidConfiguration;
import com.whatakitty.log.Logger;
import com.whatakitty.utils.PropertiesLoaderUtils;
import com.whatakitty.utils.ResourceUtils;

import java.io.FileNotFoundException;
import java.net.URL;
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

    private static final String DB_PROPERTIES_LOCATION = "classpath: META-INF/db.properties";

    public ActiveRecordConfiguration() {
        URL dbPropertiesFile = null;
        try {
            dbPropertiesFile = ResourceUtils.getURL(DB_PROPERTIES_LOCATION);
        } catch (FileNotFoundException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Db properties file %s is not exists.", DB_PROPERTIES_LOCATION), e);
            }
        }
        Properties properties = PropertiesLoaderUtils.loadProperties(dbPropertiesFile);

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
