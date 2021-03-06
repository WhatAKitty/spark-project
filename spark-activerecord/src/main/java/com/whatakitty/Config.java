package com.whatakitty;

import com.whatakitty.dialect.Dialect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by WhatAKitty on 2017/3/6.
 */
public class Config extends AbstractConfig {

    /**
     * For DbKit.brokenConfig = new Config();
     */
    Config() {
    }

    /**
     * Constructor with DataSource
     * @param dataSource the dataSource, can not be null
     */
    public Config(String name, DataSource dataSource) {
        super(name, dataSource);
    }

    /**
     * Constructor with DataSource and Dialect
     * @param dataSource the dataSource, can not be null
     * @param dialect the dialect, can not be null
     */
    public Config(String name, DataSource dataSource, Dialect dialect) {
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
    public Config(String name,
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
		Connection conn = threadLocal.get();
		if (conn != null)
			return conn;
		return showSql ? new SqlReporter(dataSource.getConnection()).getConnection() : dataSource.getConnection();
    }

    @Override
    public Connection getThreadLocalConnection() {
        return threadLocal.get();
    }

    @Override
    public void close(ResultSet rs, Statement st, Connection conn) {
        if (rs != null) {try {rs.close();} catch (SQLException e) {}}
		if (st != null) {try {st.close();} catch (SQLException e) {}}

		if (threadLocal.get() == null) {	// in transaction if conn in threadlocal
			if (conn != null) {try {conn.close();}
			catch (SQLException e) {throw new ActiveRecordException(e);}}
		}
    }

    @Override
    public void close(Statement st, Connection conn) {
        if (st != null) {try {st.close();} catch (SQLException e) {}}

		if (threadLocal.get() == null) {	// in transaction if conn in threadlocal
			if (conn != null) {try {conn.close();}
			catch (SQLException e) {throw new ActiveRecordException(e);}}
		}
    }

    @Override
    public void close(Connection conn) {
        if (threadLocal.get() == null)		// in transaction if conn in threadlocal
			if (conn != null)
				try {conn.close();} catch (SQLException e) {throw new ActiveRecordException(e);}
    }
}
