package com.whatakitty.config;

import com.whatakitty.log.Logger;
import com.whatakitty.utils.*;
import com.whatakitty.utils.ClassUtils;
import org.apache.commons.lang3.*;
import org.joor.Reflect;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * The wrapper of configuration.
 *
 * Created by WhatAKitty on 2017/1/24.
 */
class ConfigurationWrapper {

    private static final Logger logger = Logger.getLogger(ConfigurationManager.class);

    private Object configuration;
    private Configuration configurationAnnotation;
    private Map<String, Properties> configurationProperties = new HashMap<>();

    ConfigurationWrapper(String configurationName) {
        // get class of configuration
        Class<?> clazz = null;
        try {
            clazz = ClassUtils.forName(configurationName, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Target configuration named %s is not found.", configurationName), e);
            }
        }

        // get configuration annotation from configuration class
        if (!clazz.isAnnotationPresent(Configuration.class)) {
            throw new RuntimeException(String.format("Target class %s is not configurable.", configurationName));
        }
        this.configurationAnnotation = clazz.getAnnotation(Configuration.class);

        // Need properties file to inject.
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 1) {
            // only one constructor can be instantiated.
            throw new RuntimeException("Target configuration can't be instantiated: There are too many constructor, please reduce constructor's count. Only one constructor can be instantiated.");
        }
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(ConfigurationProperties.class)) {
                // Need properties file to inject.
                ConfigurationProperties configurationProperties = constructor.getAnnotation(ConfigurationProperties.class);

                int propertyLength = 0;
                String[] propertiesLocations = configurationProperties.propertiesLocation();
                Parameter[] parameters = constructor.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    if (parameter.getType().equals(Properties.class)) {
                        propertyLength++;
                    }
                }
                if (propertiesLocations.length != propertyLength) {
                    throw new RuntimeException(String.format("Target configuration %s can not be instantiated: Properties' size is not equal to configuration locations' size.", configurationName));
                }

                // inject parameters
                Object[] parameterValues = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    String propertiesLocation = propertiesLocations[i];
                    Parameter parameter = parameters[i];

                    Object parameterValue;
                    if (parameter.getType().equals(Properties.class)) {
                        // load properties
                        parameterValue = loadProperties(clazz, propertiesLocation);
                    } else {
                        parameterValue = null;
                    }
                    parameterValues[i] = parameterValue;
                }

                // inject to constructor
                try {
                    this.configuration = constructor.newInstance(parameterValues);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Target configuration %s can not be instantiated.", configurationName), e);
                    }
                }
            } else {
                this.configuration = Reflect.on(clazz).create();
            }
        }
    }

    /**
     * load properties from properties file.
     *
     * @param propertiesLocation properties file location
     * @return the configuration properties
     */
    private Properties loadProperties(Class<?> clazz, String propertiesLocation) {
        URL propertiesUrl = null;
        try {
            propertiesUrl = ResourceUtils.getURL(propertiesLocation);
        } catch (FileNotFoundException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The %s configuration file properties %s is not exists. ", clazz.getName(), propertiesLocation), e);
            }
            throw new RuntimeException(e);
        }

        return PropertiesLoaderUtils.loadProperties(propertiesUrl);
    }

    /**
     * Get the name of this configuration.
     *
     * @return the name of the configuration
     */
    final String getName() {
        return this.configuration.getClass().getName();
    }

    /**
     * Get the configuration content.
     *
     * @param <T> Configuration Class Type
     * @return the content of configuration
     */
    final <T> T getConfiguration() {
        return (T) this.configuration;
    }

    /**
     * Get the configuration annotation content.
     *
     * @return the content of configuration annotation
     */
    final Configuration getConfigurationAnnotation() {
        return this.configurationAnnotation;
    }

    final Properties getProperties(String propertiesName) {
        return configurationProperties.get(propertiesName);
    }

    /**
     * If this configuration could be overwritten.
     *
     * @return
     */
    final boolean withMissing() {
        return configurationAnnotation.withMissing();
    }

}
