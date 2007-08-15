/*
 * Copyright 2007 The Kuali Foundation
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
 *//**
 * 
 */
package edu.sampleu.kim.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

public class User extends BusinessObjectBase {

	private static final long serialVersionUID = -1207463934478758540L;
	private Long id;
	private String username;
	private String password;
	private List<UserAttribute> userAttributes;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<UserAttribute> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(List<UserAttribute> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("username", getUsername());
        propMap.put("password", getPassword());
        propMap.put("userAttributes", getUserAttributes());
        return propMap;
	}

	public void refresh() {
		// not doing this unless we need it
	}
}
