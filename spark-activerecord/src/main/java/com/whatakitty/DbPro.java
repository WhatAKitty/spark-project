/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whatakitty;

import com.whatakitty.utils.JsonKit;
import com.whatakitty.log.Logger;
import org.apache.commons.lang.ArrayUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * DbPro. Professional database query and update tool.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DbPro {
	
    private final Logger logger = Logger.getLogger(Model.class);
  
	private final AbstractConfig config;
	private static final Map<String, DbPro> map = new HashMap<String, DbPro>();
	
	public DbPro() {
		if (DbKit.config == null)
			throw new RuntimeException("The main config is null, initialize ActiveRecordPlugin first");
		this.config = DbKit.config;
	}
	
	public DbPro(String configName) {
		this.config = DbKit.getConfig(configName);
		if (this.config == null)
			throw new IllegalArgumentException("Config not found by configName: " + configName);
	}
	
	public static DbPro use(String configName) {
		DbPro result = map.get(configName);
		if (result == null) {
			result = new DbPro(configName);
			map.put(configName, result);
		}
		return result;
	}
	
	public static DbPro use() {
		return use(DbKit.config.name);
	}
	
	<T> List<T> query(AbstractConfig config, Connection conn, String sql, Object... paras) throws SQLException {
		List result = new ArrayList();
		logger.info("sql:"+sql.toString()+"[ "+ ArrayUtils.toString(paras)+" ]");
		PreparedStatement pst = conn.prepareStatement(sql);
		config.dialect.fillStatement(pst, paras);
		ResultSet rs = pst.executeQuery();
		int colAmount = rs.getMetaData().getColumnCount();
		if (colAmount > 1) {
			while (rs.next()) {
				Object[] temp = new Object[colAmount];
				for (int i=0; i<colAmount; i++) {
					temp[i] = rs.getObject(i + 1);
				}
				result.add(temp);
			}
		}
		else if(colAmount == 1) {
			while (rs.next()) {
				result.add(rs.getObject(1));
			}
		}
		DbKit.closeQuietly(rs, pst);
		return result;
	}
	
	/**
	 * @see #query(String, Object...)
	 */
	public <T> List<T> query(String sql, Object... paras) {
		Connection conn = null;
		try {
			conn = config.getConnection();
			return query(config, conn, sql, paras);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			config.close(conn);
		}
	}
	
	/**
	 * @see #query(String, Object...)
	 * @param sql an SQL statement
	 */
	public <T> List<T> query(String sql) {		// return  List<object[]> or List<object>
		return query(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	/**
	 * Execute sql query and return the first result. I recommend add "limit 1" in your sql.
	 * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
	 * @param paras the parameters of sql
	 * @return Object[] if your sql has select more than one column,
	 * 			and it return Object if your sql has select only one column.
	 */
	public <T> T queryFirst(String sql, Object... paras) {
		List<T> result = query(sql, paras);
		return (result.size() > 0 ? result.get(0) : null);
	}
	
	/**
	 * @see #queryFirst(String, Object...)
	 * @param sql an SQL statement
	 */
	public <T> T queryFirst(String sql) {
		// return queryFirst(sql, NULL_PARA_ARRAY);
		List<T> result = query(sql, DbKit.NULL_PARA_ARRAY);
		return (result.size() > 0 ? result.get(0) : null);
	}
	
	// 26 queryXxx method below -----------------------------------------------
	/**
	 * Execute sql query just return one column.
	 * @param <T> the type of the column that in your sql's select statement
	 * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
	 * @param paras the parameters of sql
	 * @return List<T>
	 */
	public <T> T queryColumn(String sql, Object... paras) {
		List<T> result = query(sql, paras);
		if (result.size() > 0) {
			T temp = result.get(0);
			if (temp instanceof Object[])
				throw new ActiveRecordException("Only ONE COLUMN can be queried.");
			return temp;
		}
		return null;
	}
	
	public <T> T queryColumn(String sql) {
		return (T)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public String queryStr(String sql, Object... paras) {
		return (String)queryColumn(sql, paras);
	}
	
	public String queryStr(String sql) {
		return (String)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public Integer queryInt(String sql, Object... paras) {
		return (Integer)queryColumn(sql, paras);
	}
	
	public Integer queryInt(String sql) {
		return (Integer)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public Long queryLong(String sql, Object... paras) {
		return (Long)queryColumn(sql, paras);
	}
	
	public Long queryLong(String sql) {
		return (Long)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public Double queryDouble(String sql, Object... paras) {
		return (Double)queryColumn(sql, paras);
	}
	
	public Double queryDouble(String sql) {
		return (Double)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public Float queryFloat(String sql, Object... paras) {
		return (Float)queryColumn(sql, paras);
	}
	
	public Float queryFloat(String sql) {
		return (Float)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public java.math.BigDecimal queryBigDecimal(String sql, Object... paras) {
		return (java.math.BigDecimal)queryColumn(sql, paras);
	}
	
	public java.math.BigDecimal queryBigDecimal(String sql) {
		return (java.math.BigDecimal)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public byte[] queryBytes(String sql, Object... paras) {
		return (byte[])queryColumn(sql, paras);
	}
	
	public byte[] queryBytes(String sql) {
		return (byte[])queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public java.util.Date queryDate(String sql, Object... paras) {
		return (java.util.Date)queryColumn(sql, paras);
	}
	
	public java.util.Date queryDate(String sql) {
		return (java.util.Date)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public java.sql.Time queryTime(String sql, Object... paras) {
		return (java.sql.Time)queryColumn(sql, paras);
	}
	
	public java.sql.Time queryTime(String sql) {
		return (java.sql.Time)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public java.sql.Timestamp queryTimestamp(String sql, Object... paras) {
		return (java.sql.Timestamp)queryColumn(sql, paras);
	}
	
	public java.sql.Timestamp queryTimestamp(String sql) {
		return (java.sql.Timestamp)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public Boolean queryBoolean(String sql, Object... paras) {
		return (Boolean)queryColumn(sql, paras);
	}
	
	public Boolean queryBoolean(String sql) {
		return (Boolean)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	public Number queryNumber(String sql, Object... paras) {
		return (Number)queryColumn(sql, paras);
	}
	
	public Number queryNumber(String sql) {
		return (Number)queryColumn(sql, DbKit.NULL_PARA_ARRAY);
	}
	// 26 queryXxx method under -----------------------------------------------
	
	/**
	 * Execute sql update
	 */
	int update(AbstractConfig config, Connection conn, String sql, Object... paras) throws SQLException {
	    logger.info("sql:"+sql.toString()+"[ "+ArrayUtils.toString(paras)+" ]");
		PreparedStatement pst = conn.prepareStatement(sql);
		config.dialect.fillStatement(pst, paras);
		int result = pst.executeUpdate();
		DbKit.closeQuietly(pst);
		return result;
	}
	
	/**
	 * Execute update, insert or delete sql statement.
	 * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
	 * @param paras the parameters of sql
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>,
     *         or <code>DELETE</code> statements, or 0 for SQL statements 
     *         that return nothing
	 */
	public int update(String sql, Object... paras) {
		Connection conn = null;
		try {
			conn = config.getConnection();
			return update(config, conn, sql, paras);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			config.close(conn);
		}
	}
	
	/**
	 * @see #update(String, Object...)
	 * @param sql an SQL statement
	 */
	public int update(String sql) {
		return update(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	/**
	 * Get id after insert method getGeneratedKey().
	 */
	private Object getGeneratedKey(PreparedStatement pst) throws SQLException {
		ResultSet rs = pst.getGeneratedKeys();
		Object id = null;
		if (rs.next())
			 id = rs.getObject(1);
		rs.close();
		return id;
	}
	
	List<Record> find(AbstractConfig config, Connection conn, String sql, Object... paras) throws SQLException {
	    logger.info("sql:"+sql.toString()+"[ "+ArrayUtils.toString(paras)+" ]");
		PreparedStatement pst = conn.prepareStatement(sql);
		config.dialect.fillStatement(pst, paras);
		ResultSet rs = pst.executeQuery();
		List<Record> result = RecordBuilder.build(config, rs);
		DbKit.closeQuietly(rs, pst);
		return result;
	}
	
	/**
	 * @see #find(String, Object...)
	 */
	public List<Record> find(String sql, Object... paras) {
		Connection conn = null;
		try {
			conn = config.getConnection();
			return find(config, conn, sql, paras);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			config.close(conn);
		}
	}
	
	/**
	 * @see #find(String, Object...)
	 * @param sql the sql statement
	 */
	public List<Record> find(String sql) {
		return find(sql, DbKit.NULL_PARA_ARRAY);
	}
	
	/**
	 * Find first record. I recommend add "limit 1" in your sql.
	 * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
	 * @param paras the parameters of sql
	 * @return the Record object
	 */
	public Record findFirst(String sql, Object... paras) {
		List<Record> result = find(sql, paras);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	/**
	 * @see #findFirst(String, Object...)
	 * @param sql an SQL statement
	 */
	public Record findFirst(String sql) {
		List<Record> result = find(sql, DbKit.NULL_PARA_ARRAY);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	/**
	 * Find record by id.
	 * Example: Record user = DbPro.use().findById("user", 15);
	 * @param tableName the table name of the table
	 * @param idValue the id value of the record
	 */
	public Record findById(String tableName, Object idValue) {
		return findById(tableName, config.dialect.getDefaultPrimaryKey(), idValue, "*");
	}
	
	/**
	 * Find record by id. Fetch the specific columns only.
	 * Example: Record user = DbPro.use().findById("user", 15, "name, age");
	 * @param tableName the table name of the table
	 * @param idValue the id value of the record
	 * @param columns the specific columns separate with comma character ==> ","
	 */
	public Record findById(String tableName, Number idValue, String columns) {
		return findById(tableName, config.dialect.getDefaultPrimaryKey(), idValue, columns);
	}
	
	/**
	 * Find record by id.
	 * Example: Record user = DbPro.use().findById("user", "user_id", 15);
	 * @param tableName the table name of the table
	 * @param primaryKey the primary key of the table
	 * @param idValue the id value of the record
	 */
	public Record findById(String tableName, String primaryKey, Number idValue) {
		return findById(tableName, primaryKey, idValue, "*");
	}
	
	/**
	 * Find record by id. Fetch the specific columns only.
	 * Example: Record user = DbPro.use().findById("user", "user_id", 15, "name, age");
	 * @param tableName the table name of the table
	 * @param primaryKey the primary key of the table
	 * @param idValue the id value of the record
	 * @param columns the specific columns separate with comma character ==> ","
	 */
	public Record findById(String tableName, String primaryKey, Object idValue, String columns) {
		String sql = config.dialect.forDbFindById(tableName, primaryKey, columns);
		logger.info("sql:"+sql.toString()+"[ "+idValue+" ]");
		List<Record> result = find(sql, idValue);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	/**
	 * Delete record by id.
	 * Example: boolean succeed = DbPro.use().deleteById("user", 15);
	 * @param tableName the table name of the table
	 * @param id the id value of the record
	 * @return true if delete succeed otherwise false
	 */
	public boolean deleteById(String tableName, Object id) {
		return deleteById(tableName, config.dialect.getDefaultPrimaryKey(), id);
	}
	
	/**
	 * Delete record by id.
	 * Example: boolean succeed = DbPro.use().deleteById("user", "user_id", 15);
	 * @param tableName the table name of the table
	 * @param primaryKey the primary key of the table
	 * @param id the id value of the record
	 * @return true if delete succeed otherwise false
	 */
	public boolean deleteById(String tableName, String primaryKey, Object id) {
		if (id == null)
			throw new IllegalArgumentException("id can not be null");
		
		String sql = config.dialect.forDbDeleteById(tableName, primaryKey);
		return update(sql, id) >= 1;
	}
	
	/**
	 * Delete record.
	 * Example: boolean succeed = DbPro.use().delete("user", "id", user);
	 * @param tableName the table name of the table
	 * @param primaryKey the primary key of the table
	 * @param record the record
	 * @return true if delete succeed otherwise false
	 */
	public boolean delete(String tableName, String primaryKey, Record record) {
		return deleteById(tableName, primaryKey, record.get(primaryKey));
	}
	
	/**
	 * Example: boolean succeed = DbPro.use().delete("user", user);
	 * @see #delete(String, String, Record)
	 */
	public boolean delete(String tableName, Record record) {
		String defaultPrimaryKey = config.dialect.getDefaultPrimaryKey();
		return deleteById(tableName, defaultPrimaryKey, record.get(defaultPrimaryKey));
	}
	
	Page<Record> paginate(AbstractConfig config, Connection conn, int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) throws SQLException {
		if (pageNumber < 1 || pageSize < 1)
			throw new ActiveRecordException("pageNumber and pageSize must be more than 0");
		
		if (config.dialect.isTakeOverDbPaginate())
			return config.dialect.takeOverDbPaginate(conn, pageNumber, pageSize, select, sqlExceptSelect, paras);
		
		long totalRow = 0;
		int totalPage = 0;
		String sqlCount = "select count(*) " + DbKit.replaceFormatSqlOrderBy(sqlExceptSelect);
		logger.info("sqlCount:"+sqlCount+"[ "+ArrayUtils.toString(paras)+" ]");
		List result = query(config, conn, sqlCount, paras);
		int size = result.size();
		if (size == 1)
			totalRow = ((Number)result.get(0)).longValue();
		else if (size > 1)
			totalRow = result.size();
		else
			return new Page<Record>(new ArrayList<Record>(0), pageNumber, pageSize, 0, 0);
		
		totalPage = (int) (totalRow / pageSize);
		if (totalRow % pageSize != 0) {
			totalPage++;
		}
		
		// --------
		StringBuilder sql = new StringBuilder();
		config.dialect.forPaginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
		List<Record> list = find(config, conn, sql.toString(), paras);
		return new Page<Record>(list, pageNumber, pageSize, totalPage, (int)totalRow);
	}
	
	/**
	 * @see #paginate(int, int, String, String, Object...)
	 */
	public Page<Record> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
		Connection conn = null;
		try {
			conn = config.getConnection();
			return paginate(config, conn, pageNumber, pageSize, select, sqlExceptSelect, paras);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			config.close(conn);
		}
	}
	
	/**
	 * @see #paginate(int, int, String, String, Object...)
	 */
	public Page<Record> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
		return paginate(pageNumber, pageSize, select, sqlExceptSelect, DbKit.NULL_PARA_ARRAY);
	}
	
	boolean save(AbstractConfig config, Connection conn, String tableName, String primaryKey, Record record) throws SQLException {
		List<Object> paras = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		config.dialect.forDbSave(sql, paras, tableName, record);
		
		PreparedStatement pst;
		logger.info("sql:"+sql.toString());
		if (config.dialect.isOracle())
			pst = conn.prepareStatement(sql.toString(), new String[]{primaryKey});
		else
			pst = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			
		config.dialect.fillStatement(pst, paras);
		int result = pst.executeUpdate();
		record.set(primaryKey, getGeneratedKey(pst));
		DbKit.closeQuietly(pst);
		return result >= 1;
	}
	
	/**
	 * Save record.
	 * @param tableName the table name of the table
	 * @param primaryKey the primary key of the table
	 * @param record the record will be saved
	 * @return true if save succeed otherwise false
	 */
	public boolean save(String tableName, String primaryKey, Record record) {
		Connection conn = null;
		try {
			conn = config.getConnection();
			return save(config, conn, tableName, primaryKey, record);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			config.close(conn);
		}
	}
	
	/**
	 * @see #save(String, String, Record)
	 */
	public boolean save(String tableName, Record record) {
		return save(tableName, config.dialect.getDefaultPrimaryKey(), record);
	}
	
	boolean update(AbstractConfig config, Connection conn, String tableName, String primaryKey, Record record) throws SQLException {
		Object id = record.get(primaryKey);
		if (id == null)
			throw new ActiveRecordException("You can't update model without Primary Key.");
		
		StringBuilder sql = new StringBuilder();
		List<Object> paras = new ArrayList<Object>();
		config.dialect.forDbUpdate(tableName, primaryKey, id, record, sql, paras);
		
		if (paras.size() <= 1) {	// Needn't update
			return false;
		}
		
		return update(config, conn, sql.toString(), paras.toArray()) >= 1;
	}
	
	/**
	 * Update Record.
	 * @param tableName the table name of the Record save to
	 * @param primaryKey the primary key of the table
	 * @param record the Record object
	 * @return true if update succeed otherwise false
	 */
	public boolean update(String tableName, String primaryKey, Record record) {
		Connection conn = null;
		try {
			conn = config.getConnection();
			return update(config, conn, tableName, primaryKey, record);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			config.close(conn);
		}
	}
	
	/**
	 * Update Record. The primary key of the table is: "id".
	 * @see #update(String, String, Record)
	 */
	public boolean update(String tableName, Record record) {
		return update(tableName, config.dialect.getDefaultPrimaryKey(), record);
	}
	
	/**
	 * @see #execute(ICallback)
	 */
	public Object execute(ICallback callback) {
		return execute(config, callback);
	}
	
	/**
	 * Execute callback. It is useful when all the API can not satisfy your requirement.
	 * @param config the Config object
	 * @param callback the ICallback interface
	 */
	Object execute(AbstractConfig config, ICallback callback) {
		Connection conn = null;
		try {
			conn = config.getConnection();
			return callback.run(conn);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			config.close(conn);
		}
	}
	
	/**
	 * Execute transaction.
	 * @param config the Config object
	 * @param transactionLevel the transaction level
	 * @param atom the atom operation
	 * @return true if transaction executing succeed otherwise false
	 */
	boolean tx(AbstractConfig config, int transactionLevel, IAtom atom) {
		Connection conn = config.getThreadLocalConnection();
		if (conn != null) {	// Nested transaction support
			try {
				if (conn.getTransactionIsolation() < transactionLevel)
					conn.setTransactionIsolation(transactionLevel);
				boolean result = atom.run();
				if (result)
					return true;
				throw new NestedTransactionHelpException("Notice the outer transaction that the nested transaction return false");	// important:can not return false
			}
			catch (SQLException e) {
				throw new ActiveRecordException(e);
			}
		}
		
		Boolean autoCommit = null;
		try {
			conn = config.getConnection();
			autoCommit = conn.getAutoCommit();
			config.setThreadLocalConnection(conn);
			conn.setTransactionIsolation(transactionLevel);
			conn.setAutoCommit(false);
			boolean result = atom.run();
			if (result)
				conn.commit();
			else
				conn.rollback();
			return result;
		} catch (NestedTransactionHelpException e) {
			if (conn != null) try {conn.rollback();} catch (Exception e1) {e1.printStackTrace();}
			return false;
		} catch (Exception e) {
			if (conn != null) try {conn.rollback();} catch (Exception e1) {e1.printStackTrace();}
			throw e instanceof RuntimeException ? (RuntimeException)e : new ActiveRecordException(e);
		} finally {
			try {
				if (conn != null) {
					if (autoCommit != null)
						conn.setAutoCommit(autoCommit);
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();	// can not throw exception here, otherwise the more important exception in previous catch block can not be thrown
			} finally {
				config.removeThreadLocalConnection();	// prevent memory leak
			}
		}
	}
	
	public boolean tx(int transactionLevel, IAtom atom) {
		return tx(config, transactionLevel, atom);
	}
	
	/**
	 * Execute transaction with default transaction level.
	 * @see #tx(int, IAtom)
	 */
	public boolean tx(IAtom atom) {
		return tx(config, config.getTransactionLevel(), atom);
	}
	
	private int[] batch(AbstractConfig config, Connection conn, String sql, Object[][] paras) throws SQLException {
		if (paras == null || paras.length == 0)
			throw new IllegalArgumentException("The paras array length must more than 0.");
		int[] result = new int[paras.length];
		PreparedStatement pst = conn.prepareStatement(sql);
		for (int i=0; i<paras.length; i++) {
			for (int j=0; j<paras[i].length; j++) {
				Object value = paras[i][j];
				if (config.dialect.isOracle() && value instanceof java.sql.Date)
					pst.setDate(j + 1, (java.sql.Date)value);
				else
					pst.setObject(j + 1, value);
			}
			pst.addBatch();
		}
		result = pst.executeBatch();
		DbKit.closeQuietly(pst);
		return result;
	}
	
	/**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     * <p>
     * Example:
     * <pre>
     * String sql = "insert into user(name, cash) values(?, ?)";
     * int[] result = DbPro.use().batch("myConfig", sql, new Object[][]{{"James", 888}, {"zhanjin", 888}});
     * </pre>
     * @param sql The SQL to execute.
     * @param paras An array of query replacement parameters.  Each row in this array is one set of batch replacement values.
     * @return The number of rows updated per statement
     */
	public int[] batch(String sql, Object[][] paras) {
		Connection conn = null;
		Boolean autoCommit = null;
		try {
			conn = config.getConnection();
			logger.info("batch sql:"+sql.toString()+"[ " + JsonKit.toJson(paras) + " ] ");
			return batch(config, conn, sql, paras);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			if (autoCommit != null)
				try {conn.setAutoCommit(autoCommit);} catch (Exception e) {e.printStackTrace();}
			config.close(conn);
		}
	}
	
	private int[] batch(AbstractConfig config, Connection conn, String sql, Object[][] paras, int batchSize) throws SQLException {
		if (paras == null || paras.length == 0)
			throw new IllegalArgumentException("The paras array length must more than 0.");
		if (batchSize < 1)
			throw new IllegalArgumentException("The batchSize must more than 0.");
		int counter = 0;
		int pointer = 0;
		int[] result = new int[paras.length];
		PreparedStatement pst = conn.prepareStatement(sql);
		for (int i=0; i<paras.length; i++) {
			for (int j=0; j<paras[i].length; j++) {
				Object value = paras[i][j];
				if (config.dialect.isOracle() && value instanceof java.sql.Date)
					pst.setDate(j + 1, (java.sql.Date)value);
				else
					pst.setObject(j + 1, value);
			}
			pst.addBatch();
			if (++counter >= batchSize) {
				counter = 0;
				int[] r = pst.executeBatch();
				conn.commit();
				for (int k=0; k<r.length; k++)
					result[pointer++] = r[k];
			}
		}
		int[] r = pst.executeBatch();
		conn.commit();
		for (int k=0; k<r.length; k++)
			result[pointer++] = r[k];
		DbKit.closeQuietly(pst);
		return result;
	}
	
    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     * <p>
     * Example:
     * <pre>
     * String sql = "insert into user(name, cash) values(?, ?)";
     * int[] result = DbPro.use().batch("myConfig", sql, new Object[][]{{"James", 888}, {"zhanjin", 888}});
     * </pre>
     * @param sql The SQL to execute.
     * @param paras An array of query replacement parameters.  Each row in this array is one set of batch replacement values.
     * @return The number of rows updated per statement
     */
	public int[] batch(String sql, Object[][] paras, int batchSize) {
		Connection conn = null;
		Boolean autoCommit = null;
		try {
			conn = config.getConnection();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			if (logger.isDebugEnabled()) {
			    logger.debug("batch sql:"+sql.toString()+" batchSize:"+ batchSize);
			}
			return batch(config, conn, sql, paras, batchSize);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			if (autoCommit != null)
				try {conn.setAutoCommit(autoCommit);} catch (Exception e) {e.printStackTrace();}
			config.close(conn);
		}
	}
	
	private int[] batch(AbstractConfig config, Connection conn, String sql, String columns, List list, int batchSize) throws SQLException {
		if (list == null || list.size() == 0)
			return new int[0];
		Object element = list.get(0);
		if (!(element instanceof Record) && !(element instanceof Model))
			throw new IllegalArgumentException("The element in list must be Model or Record.");
		if (batchSize < 1)
			throw new IllegalArgumentException("The batchSize must more than 0.");
		boolean isModel = element instanceof Model;
		
		String[] columnArray = columns.split(",");
		for (int i=0; i<columnArray.length; i++)
			columnArray[i] = columnArray[i].trim();
		
		int counter = 0;
		int pointer = 0;
		int size = list.size();
		int[] result = new int[size];
		PreparedStatement pst = conn.prepareStatement(sql);
		for (int i=0; i<size; i++) {
			Map map = isModel ? ((Model)list.get(i)).getAttrs() : ((Record)list.get(i)).getColumns();
			for (int j=0; j<columnArray.length; j++) {
				Object value = map.get(columnArray[j]);
				if (config.dialect.isOracle() && value instanceof java.sql.Date)
					pst.setDate(j + 1, (java.sql.Date)value);
				else
					pst.setObject(j + 1, value);
			}
			pst.addBatch();
			if (++counter >= batchSize) {
				counter = 0;
				int[] r = pst.executeBatch();
				conn.commit();
				for (int k=0; k<r.length; k++)
					result[pointer++] = r[k];
			}
		}
		int[] r = pst.executeBatch();
		conn.commit();
		for (int k=0; k<r.length; k++)
			result[pointer++] = r[k];
		DbKit.closeQuietly(pst);
		return result;
	}
	
	/**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     * <p>
     * Example:
     * <pre>
     * String sql = "insert into user(name, cash) values(?, ?)";
     * int[] result = DbPro.use().batch("myConfig", sql, "name, cash", modelList, 500);
     * </pre>
	 * @param sql The SQL to execute.
	 * @param columns the columns need be processed by sql.
	 * @param modelOrRecordList model or record object list.
	 * @param batchSize batch size.
	 * @return The number of rows updated per statement
	 */
	public int[] batch(String sql, String columns, List modelOrRecordList, int batchSize) {
		Connection conn = null;
		Boolean autoCommit = null;
		try {
			conn = config.getConnection();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			return batch(config, conn, sql, columns, modelOrRecordList, batchSize);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			if (autoCommit != null)
				try {conn.setAutoCommit(autoCommit);} catch (Exception e) {e.printStackTrace();}
			config.close(conn);
		}
	}
	
	private int[] batch(AbstractConfig config, Connection conn, String sql, String columns, List list) throws SQLException {
        if (list == null || list.size() == 0)
            return new int[0];
        Object element = list.get(0);
        if (!(element instanceof Record) && !(element instanceof Model))
            throw new IllegalArgumentException("The element in list must be Model or Record.");
        boolean isModel = element instanceof Model;
        
        String[] columnArray = columns.split(",");
        for (int i=0; i<columnArray.length; i++)
            columnArray[i] = columnArray[i].trim();
        
        int pointer = 0;
        int size = list.size();
        int[] result = new int[size];
        PreparedStatement pst = conn.prepareStatement(sql);
        for (int i=0; i<size; i++) {
            Map map = isModel ? ((Model)list.get(i)).getAttrs() : ((Record)list.get(i)).getColumns();
            for (int j=0; j<columnArray.length; j++) {
                Object value = map.get(columnArray[j]);
                if (config.dialect.isOracle() && value instanceof java.sql.Date)
                    pst.setDate(j + 1, (java.sql.Date)value);
                else
                    pst.setObject(j + 1, value);
            }
            pst.addBatch();
        }
        int[] r = pst.executeBatch();
        for (int k=0; k<r.length; k++)
            result[pointer++] = r[k];
        DbKit.closeQuietly(pst);
        return result;
    }
	
	/**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     * <p>
     * Example:
     * <pre>
     * String sql = "insert into user(name, cash) values(?, ?)";
     * int[] result = DbPro.use().batch("myConfig", sql, "name, cash", modelList, 500);
     * </pre>
     * @param sql The SQL to execute.
     * @param columns the columns need be processed by sql.
     * @param modelOrRecordList model or record object list.
     * @return The number of rows updated per statement
     */
    public int[] batch(String sql, String columns, List modelOrRecordList) {
        Connection conn = null;
        Boolean autoCommit = null;
        try {
            conn = config.getConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("batch sql:"+sql.toString());
            }
            return batch(config, conn, sql, columns, modelOrRecordList);
        } catch (Exception e) {
            throw new ActiveRecordException(e);
        } finally {
            if (autoCommit != null)
                try {conn.setAutoCommit(autoCommit);} catch (Exception e) {e.printStackTrace();}
            config.close(conn);
        }
    }
	
	private int[] batch(AbstractConfig config, Connection conn, List<String> sqlList, int batchSize) throws SQLException {
		if (sqlList == null || sqlList.size() == 0)
			throw new IllegalArgumentException("The sqlList length must more than 0.");
		if (batchSize < 1)
			throw new IllegalArgumentException("The batchSize must more than 0.");
		int counter = 0;
		int pointer = 0;
		int size = sqlList.size();
		int[] result = new int[size];
		Statement st = conn.createStatement();
		for (int i=0; i<size; i++) {
			st.addBatch(sqlList.get(i));
			if (++counter >= batchSize) {
				counter = 0;
				int[] r = st.executeBatch();
				conn.commit();
				for (int k=0; k<r.length; k++)
					result[pointer++] = r[k];
			}
		}
		int[] r = st.executeBatch();
		conn.commit();
		for (int k=0; k<r.length; k++)
			result[pointer++] = r[k];
		DbKit.closeQuietly(st);
		return result;
	}
	
    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     * Example:
     * <pre>
     * int[] result = DbPro.use().batch("myConfig", sqlList, 500);
     * </pre>
	 * @param sqlList The SQL list to execute.
	 * @param batchSize batch size.
	 * @return The number of rows updated per statement
	 */
    public int[] batch(List<String> sqlList, int batchSize) {
		Connection conn = null;
		Boolean autoCommit = null;
		try {
			conn = config.getConnection();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			return batch(config, conn, sqlList, batchSize);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			if (autoCommit != null)
				try {conn.setAutoCommit(autoCommit);} catch (Exception e) {e.printStackTrace();}
			config.close(conn);
		}
    }
    
    private int[] batch(AbstractConfig config, Connection conn, List<String> sqlList) throws SQLException {
        if (sqlList == null || sqlList.size() == 0)
            throw new IllegalArgumentException("The sqlList length must more than 0.");
        int pointer = 0;
        int size = sqlList.size();
        int[] result = new int[size];
        Statement st = conn.createStatement();
        for (int i=0; i<size; i++) {
            st.addBatch(sqlList.get(i));
        }
        int[] r = st.executeBatch();
        for (int k=0; k<r.length; k++)
            result[pointer++] = r[k];
        DbKit.closeQuietly(st);
        return result;
    }
    
    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     * Example:
     * <pre>
     * int[] result = DbPro.use().batch("myConfig", sqlList, 500);
     * </pre>
     * @param sqlList The SQL list to execute.
     * @return The number of rows updated per statement
     */
    public int[] batch(List<String> sqlList) {
        Connection conn = null;
        Boolean autoCommit = null;
        try {
            conn = config.getConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("batch sql\n:" + "[ " + JsonKit.toJson(sqlList) + " ] ");
            }
            return batch(config, conn, sqlList);
        } catch (Exception e) {
            throw new ActiveRecordException(e);
        } finally {
            if (autoCommit != null)
                try {conn.setAutoCommit(autoCommit);} catch (Exception e) {e.printStackTrace();}
            config.close(conn);
        }
    }
}



