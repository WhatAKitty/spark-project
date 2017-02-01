package com.whatakitty.web.utils;

import org.apache.commons.beanutils.ConvertUtils;
import spark.Request;

/**
 * Created by xuqiang on 2017/2/1.
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

}
