/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.jdbc;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a description of what this class does - Garey don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PreparedObject {

	String sql = new String();
	List<Object> objectList = new ArrayList();


	/**
	 * @return the sql
	 */
	public String getSql() {
		return this.sql;
	}
	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
	/**
	 * @return the objectList
	 */
	public List<Object> getObjectList() {
		return this.objectList;
	}
	/**
	 * @param objectList the objectList to set
	 */
	public void setObjectList(List<Object> objectList) {
		this.objectList = objectList;
	}

	public void append(PreparedObject po){
		if(sql == null) sql = new String();
		if(objectList == null) objectList = new ArrayList<Object>();

		sql += po.getSql();
		objectList.addAll(po.getObjectList());
	}

	public String toString(){
		return this.sql;
	}



}
