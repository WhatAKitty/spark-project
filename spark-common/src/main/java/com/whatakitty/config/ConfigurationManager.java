package com.whatakitty.config;

import com.whatakitty.utils.ClassUtils;
import com.whatakitty.log.Logger;
import org.joor.Reflect;

import java.util.*;

/**
 * Configuration Manager
 *
 * Created by WhatAKitty on 2017/1/21.
 */
public class ConfigurationManager {

    private static final Logger logger = Logger.getLogger(ConfigurationManager.class);

    public static void enableAutoConfiguration() {
        ConfigurationManager.SingletonHolder.INSTANCE().start();
    }

    public static <T> T getConfiguration(String configurationName) {
        return (T) ConfigurationManager.configurationsHolder.get(configurationName);
    }

    private static class SingletonHolder {
        public static ConfigurationManager INSTANCE() {
            return new ConfigurationManager();
        }
    }

    private static final Map<String, ConfigurationWrapper> configurationsHolder = new HashMap<>();

    private ConfigurationManager() {}

    /**
     * Search all configuration files.
     *
     * @return the array of configuration files' name.
     */
    String[] searchConfigurations() {
        List<String> configurations = SparkFactoriesLoader.loadFactoryNames(ConfigurationManager.class, getClass().getClassLoader());
        configurations = removeDuplicates(configurations);
        // TODO sort configurations.
        return configurations.toArray(new String[configurations.size()]);
    }

    /**
     * initial the configurationsHolder.
     *
     * @param configurations the name of all configurations from 'META-INF/spark.factories'.
     */
    void initial(String[] configurations) {
        for (String configuration : configurations) {
            ConfigurationWrapper configurationWrapper = new ConfigurationWrapper(configuration);
            if (ConfigurationManager.configurationsHolder.containsKey(configurationWrapper.getName()) && !configurationWrapper.withMissing()) {
                // The configurations has the same key with this configuration and will not be overwritten.
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("There has the same key named %s in configurations holder.", configurationWrapper.getName()));
                }
                continue;
            }
            ConfigurationManager.configurationsHolder.put(configurationWrapper.getName(), configurationWrapper.getConfiguration());
        }
    }

    /**
     * Start auto configurations.
     */
    void start() {
        String[] configurations = searchConfigurations();
        initial(configurations);
    }

    /**
     * remove multiply configurations.
     *
     * @param list
     * @param <T>
     * @return
     */
    protected final <T> List<T> removeDuplicates(List<T> list) {
        return new ArrayList<T>(new LinkedHashSet<T>(list));
    }

}
