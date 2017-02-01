package com.whatakitty.utils;

import com.whatakitty.log.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/**
 * properties loader utils
 *
 * Created by WhatAKitty on 2017/1/22.
 */
public class PropertiesLoaderUtils {

    private static final Logger logger = Logger.getLogger(PropertiesLoaderUtils.class);

    private PropertiesLoaderUtils() {}

    public static Properties loadProperties(URL url) {
        Properties properties = new Properties();

        File file = null;
        InputStream stream = null;
        try {
            if (ResourceUtils.isJarURL(url)) {
                String propertiesUrl = url.getPath().split(ResourceUtils.JAR_URL_SEPARATOR)[1];
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                stream = classLoader.getResourceAsStream(propertiesUrl);
            } else if (ResourceUtils.isFileURL(url)) {
                file = ResourceUtils.getFile(url);
                stream = new FileInputStream(file);
            }

            properties.load(stream);

        } catch (FileNotFoundException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("File %s is not found.", url.toString()), e);
            }
            return properties;
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("File %s read error.", url.toString()), e);
            }
            return properties;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("stream closed error", e);
                }
            }

        }
        return properties;
    }

}
