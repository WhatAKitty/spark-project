package com.whatakitty.config;

import com.whatakitty.utils.ClassUtils;
import com.whatakitty.log.Logger;
import org.joor.Reflect;

import java.util.*;

/**
 * 配置文件管理器
 * <p>
 * Created by WhatAKitty on 2017/1/21.
 */
public class ConfigurationManager {

    private static final Logger logger = Logger.getLogger(ConfigurationManager.class);

    public static void enableAutoConfiguration() {
        ConfigurationManager.SingletonHolder.INSTANCE().start();
    }

    private static class SingletonHolder {
        public static ConfigurationManager INSTANCE() {
            return new ConfigurationManager();
        }
    }

    private static final Map<String, Object> configurations = new HashMap<>();

    public ConfigurationManager() {
    }

    /**
     * Search all configuration files.
     *
     * @return
     */
    String[] searchConfigurations() {
        List<String> configurations = SparkFactoriesLoader.loadFactoryNames(Configuration.class, getClass().getClassLoader());
        configurations = removeDuplicates(configurations);
        // TODO sort configurations.
        return configurations.toArray(new String[configurations.size()]);
    }

    /**
     * Start auto configurations.
     */
    void start() {
        String[] configurations = searchConfigurations();
        for (String configuration : configurations) {
            Class<?> clazz = null;
            try {
                clazz = ClassUtils.forName(configuration, getClass().getClassLoader());
                ConfigurationManager.configurations.put(clazz.getSimpleName(), Reflect.on(clazz).get());
            } catch (ClassNotFoundException e) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Target configuration named %s is not found.", configuration), e);
                }
                continue;
            }
        }
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
