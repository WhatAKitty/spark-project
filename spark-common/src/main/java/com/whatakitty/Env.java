package com.whatakitty;

/**
 * Created by WhatAKitty on 2017/3/6.
 */
public class Env {

    private boolean isBasic;
    private boolean isWeb;
    private boolean isSpring;

    static class SingletonHolder {
        static Env getInstance() {
            return new Env();
        }
    }

    Env() {
        this.isBasic = true;
        this.isWeb = false;
        this.isSpring = false;
    }

    void setWeb() {
        this.isWeb = true;
    }

    void setSpring() {
        this.isSpring = true;
    }

    public boolean isWeb() {
        return this.isWeb;
    }

    public boolean isSpring() {
        return this.isSpring;
    }

}
