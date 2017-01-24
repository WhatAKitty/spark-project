package com.whatakitty;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.subject.Subject;
import sun.rmi.runtime.Log;

import java.util.Arrays;

import static spark.Spark.halt;


/**
 * 权限信息
 *
 * Created by WhatAKitty on 2017/1/21.
 */
public class PermissionInfo {

    private TYPE type;
    private String[] permissions;
    private String[] roles;
    private Logical logical = Logical.AND;
    private boolean isPermitted;

    private static enum TYPE {
        ROLE, PERMISSION
    }

    private PermissionInfo(TYPE t) {
        this.type = t;
    }

    public static PermissionInfo roleBased(String...roles) {
        return new PermissionInfo(TYPE.ROLE).setRoles(roles);
    }

    public static PermissionInfo permissionBased(String...permissions) {
        return new PermissionInfo(TYPE.PERMISSION).setPermissions(permissions);
    }

    private PermissionInfo setPermissions(String...permissions) {
        this.permissions = permissions;
        return this;
    }

    private PermissionInfo setRoles(String...roles) {
        this.roles = roles;
        return this;
    }

    public PermissionInfo logical(Logical logical) {
        this.logical = logical;
        return this;
    }

    public PermissionInfo permitted() {
        Subject currentUser = SecurityUtils.getSubject();
        if (TYPE.ROLE.equals(this.type)) {
            // role based
            if (Logical.AND.equals(this.logical)) {
                isPermitted = currentUser.hasAllRoles(Arrays.asList(roles));
                return this;
            }
            isPermitted = ArrayUtils.contains(currentUser.hasRoles(Arrays.asList(roles)), true);
            return this;
        }
        // permission based
        if (Logical.AND.equals(this.logical)) {
            isPermitted = currentUser.isPermittedAll(permissions);
            return this;
        }
        isPermitted = ArrayUtils.contains(currentUser.isPermitted(permissions), true);
        return this;
    }

    public void complete(Invoke i) {
        if (!isPermitted) {
            halt(403, "You don't have permission to access it.");
        }
        i.invoke();
    }

    @FunctionalInterface
    public static interface Invoke {

        void invoke();

    }

}
