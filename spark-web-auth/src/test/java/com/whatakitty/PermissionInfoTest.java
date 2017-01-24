package com.whatakitty;

import static spark.Spark.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by WhatAKitty on 2017/1/21.
 */
public class PermissionInfoTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PermissionInfoTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static junit.framework.Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        PermissionInfo.permissionBased("admin:main:view").permitted().complete(() -> {
            get("/", (req, res) -> {
                return "hello world!";
            });
        });
    }

}
