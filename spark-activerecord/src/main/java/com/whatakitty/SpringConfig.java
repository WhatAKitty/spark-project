package com.whatakitty;

import com.whatakitty.AbstractConfig;
import com.whatakitty.IContainerFactory;
import com.whatakitty.dialect.Dialect;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by WhatAKitty on 2017/3/6.
 */
public class SpringConfig extends AbstractConfig {

    /**
     * For DbKit.brokenConfig = new Config();
     */
    SpringConfig() {
    }

    /**
     * Constructor with DataSource
     * @param dataSource the dataSource, can not be null
     */
    public SpringConfig(String name, DataSource dataSource) {
        super(name, dataSource);
    }

    /**
     * Constructor with DataSource and Dialect
     * @param dataSource the dataSource, can not be null
     * @param dialect the dialect, can not be null
     */
    public SpringConfig(String name, DataSource dataSource, Dialect dialect) {
        super(name, dataSource, dialect);
    }

    /**
     * Constructor with full parameters
     * @param dataSource the dataSource, can not be null
     * @param dialect the dialect, set null with default value: new MysqlDialect()
     * @param showSql the showSql,set null with default value: false
     * @param devMode the devMode, set null with default value: false
     * @param transactionLevel the transaction level, set null with default value: Connection.TRANSACTION_READ_COMMITTED
     * @param containerFactory the containerFactory, set null with default value: new IContainerFactory(){......}
     */
    public SpringConfig(String name,
                  DataSource dataSource,
                  Dialect dialect,
                  Boolean showSql,
                  Boolean devMode,
                  Integer transactionLevel,
                  IContainerFactory containerFactory) {
        super(name, dataSource, dialect, showSql, devMode, transactionLevel, containerFactory);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public Connection getThreadLocalConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public void close(ResultSet rs, Statement st, Connection conn) {
        if (rs != null) JdbcUtils.closeResultSet(rs);
        if (st != null) JdbcUtils.closeStatement(st);
        DataSourceUtils.releaseConnection(conn, getDataSource());
    }

    @Override
    public void close(Statement st, Connection conn) {
        if (st != null) JdbcUtils.closeStatement(st);
        DataSourceUtils.releaseConnection(conn, getDataSource());
    }

    @Override
    public void close(Connection conn) {
        DataSourceUtils.releaseConnection(conn, getDataSource());
    }

}
