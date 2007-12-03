/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.docsearch.dao;

public class SearchableAttributeSql {

	private String selectSql = "";
	private String fromSql = "";
	private String whereSql = "";
	
	private int searchableAttributeCount = 0;

	/**
	 * @return Returns the tables.
	 */
	public String getFromSql() {
		return fromSql;
	}

	/**
	 * @return Returns the searchableAttributeCount.
	 */
	public int getSearchableAttributeCount() {
		return searchableAttributeCount;
	}

	/**
	 * @return Returns the sql.
	 */
	public String getSelectSql() {
		return selectSql;
	}

	/**
	 * @return Returns the tempSql.
	 */
	public String getWhereSql() {
		return whereSql;
	}

	/**
	 * @param tables The tables to set.
	 */
	public void setFromSql(String tables) {
		this.fromSql = tables;
	}

	/**
	 * @param searchableAttributeCount The searchableAttributeCount to set.
	 */
	public void setSearchableAttributeCount(int searchableAttributeCount) {
		this.searchableAttributeCount = searchableAttributeCount;
	}

	/**
	 * @param sql The sql to set.
	 */
	public void setSelectSql(String sql) {
		this.selectSql = sql;
	}

	/**
	 * @param tempSql The tempSql to set.
	 */
	public void setWhereSql(String tempSql) {
		this.whereSql = tempSql;
	}
	
}
