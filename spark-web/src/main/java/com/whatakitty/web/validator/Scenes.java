package com.whatakitty.web.validator;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by WhatAKitty on 2017/2/14.
 */
public abstract class Scenes {

    private String scenesName;
    private Object dataToValidate;

    protected Scenes(String scenesName, Object dataToValidate) {
        this.scenesName = scenesName;
        this.dataToValidate = dataToValidate;
    }

    protected Scenes required() throws ScenesException {
        if (null == dataToValidate) {
            throw new ScenesException("Target data is null while it shouldn't.");
        }
        return this;
    }

    protected Scenes isInteger() throws ScenesException {
        try {
            Integer.parseInt(dataToValidate.toString());
        } catch(NullPointerException | NumberFormatException e) {
            throw new ScenesException("Target data is not an integer while it should.");
        }
        return this;
    }

    protected Scenes isDate() throws ScenesException {

//        Date temp = new SimpleDateFormat(datePattern).parse(value);
        return null;
    }

    public static class ScenesException extends Exception {

        public ScenesException(String msg) {
            super(msg);
        }

        public ScenesException(String msg, Throwable t) {
            super(msg, t);
        }

    }

}
