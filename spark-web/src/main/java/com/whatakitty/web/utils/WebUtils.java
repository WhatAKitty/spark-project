package com.whatakitty.web.utils;

import org.apache.commons.beanutils.ConvertUtils;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by WhatAKitty on 2017/2/1.
 */
public class WebUtils {

    private WebUtils() {}

    public static <T> T defaultValue(Request request, String paramName, T defaultValue) {
        String paramValue = request.queryParams(paramName);
        if (paramValue == null) {
            return defaultValue;
        }
        return (T) ConvertUtils.convert(paramValue, defaultValue.getClass());
    }

    public static Map<String, Object> getModel(Request request) {
        Map<String, Object> result = new HashMap<>();
        request.queryParams().stream().forEach(key -> {
            String[] values = request.queryParamsValues(key);
            result.put(key, values.length > 1 ? values : values[0]);
        });
        return result;
    }

//    public static Map<String, Object> bodyParser() {
//
//    }

}
