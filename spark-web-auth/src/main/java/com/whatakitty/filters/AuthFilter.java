package com.whatakitty.filters;

import com.whatakitty.PermissionInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import spark.Filter;
import spark.QueryParamsMap;
import spark.Route;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static spark.Spark.*;

/**
 * Created by WhatAKitty on 2017/1/21.
 */
public class AuthFilter {

    public static void auth(final String[] filters) {
        final Filter f = (req, res) -> {
            final Subject currentUser = SecurityUtils.getSubject();
            if (!currentUser.isAuthenticated()) {
                // 未授权
                halt(401, "You don't have permission to access it.");
            }
        };


        // add auth before protected routes.
        for (String filter : filters) {
            before(filter, f);
        }
    }


}
