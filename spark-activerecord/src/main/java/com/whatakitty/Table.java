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

import com.whatakitty.utils.StrKit;

import java.util.Map;
import java.util.Set;


/**
 * Table save the table meta info like column name and column type.
 */
public class Table {
	
	private String name;
	private String primaryKey;
	private String secondaryKey = null;
	private Map<String, Class<?>> columnTypeMap;	// config.containerFactory.getAttrsMap();
	private Map<String, Boolean> columnRequiredMap;
	
	private Class<? extends Model<?>> modelClass;
	
	public Table(String name, Class<? extends Model<?>> modelClass) {
		if (StrKit.isBlank(name))
			throw new IllegalArgumentException("Table name can not be blank.");
		if (modelClass == null)
			throw new IllegalArgumentException("Model class can not be null.");
		
		this.name = name.trim();
		this.modelClass = modelClass;
	}
	
	public Table(String name, String primaryKey, Class<? extends Model<?>> modelClass) {
		if (StrKit.isBlank(name))
			throw new IllegalArgumentException("Table name can not be blank.");
		if (StrKit.isBlank(primaryKey))
			throw new IllegalArgumentException("Primary key can not be blank.");
		if (modelClass == null)
			throw new IllegalArgumentException("Model class can not be null.");
		
		this.name = name.trim();
		setPrimaryKey(primaryKey.trim());	// this.primaryKey = primaryKey.trim();
		this.modelClass = modelClass;
	}
	
	void setPrimaryKey(String primaryKey) {
		String[] keyArr = primaryKey.split(",");
		if (keyArr.length > 1) {
			if (StrKit.isBlank(keyArr[0]) || StrKit.isBlank(keyArr[1]))
				throw new IllegalArgumentException("The composite primary key can not be blank.");
			this.primaryKey = keyArr[0].trim();
			this.secondaryKey = keyArr[1].trim();
		}
		else {
			this.primaryKey = primaryKey;
		}
	}
	
	void setColumnTypeMap(Map<String, Class<?>> columnTypeMap) {
		if (columnTypeMap == null)
			throw new IllegalArgumentException("columnTypeMap can not be null");
		
		this.columnTypeMap = columnTypeMap;
	}
	
	void setColumnRequiredMap(Map<String, Boolean> columnRequiredMap) {
		if (columnRequiredMap == null)
			throw new IllegalArgumentException("columnRequiredMap can not be null");
		
		this.columnRequiredMap = columnRequiredMap;
	}
	
	public String getName() {
		return name;
	}
	
	public void setColumnType(String columnLabel, Class<?> columnType) {
		columnTypeMap.put(columnLabel, columnType);
	}
	
	public Class<?> getColumnType(String columnLabel) {
		return columnTypeMap.get(columnLabel);
	}
	
	public void setColumnRequired(String columnLabel, Boolean required) {
		columnRequiredMap.put(columnLabel, required);
	}
	
	public Boolean getColumnRequired(String columnLabel) {
		return columnRequiredMap.get(columnLabel);
	}
	
	/**
	 * Model.save() need know what columns belongs to himself that he can saving to db.
	 * Think about auto saving the related table's column in the future.
	 */
	public boolean hasColumnLabel(String columnLabel) {
		return columnTypeMap.containsKey(columnLabel);
	}
	
	/**
	 * Get columns from this table.
	 * @return
	 */
	public Set<String> getColumns() {
	    return columnTypeMap.keySet();
	}
	
	/**
	 * update() and delete() need this method.
	 */
	public String getPrimaryKey() {
		return primaryKey;
	}
	
	public String getSecondaryKey() {
		return secondaryKey;
	}
	
	public Class<? extends Model<?>> getModelClass() {
		return modelClass;
	}
}






