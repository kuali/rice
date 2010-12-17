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
package org.kuali.rice.kns.bo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="KRNS_NMSPC_T")
@AttributeOverrides({@AttributeOverride(name="code", column=@Column(name="NMSPC_CD")), @AttributeOverride(name="name", column=@Column(name="NM"))})
public class Namespace extends KualiCodeBase {
	@Column(name="APPL_NMSPC_CD")
	private String applicationNamespaceCode;
	public String getNamespaceCode() {
		return code;
	}
	
	public void setNamespaceCode(String namespaceCode) {
		code = namespaceCode;
	}
	
	public String getNamespaceName() {
		return name;
	}
	
	public void setNamespaceName(String namespaceName) {
		name = namespaceName;
	}

	/**
	 * @return the applicationNamespaceCode
	 */
	public String getApplicationNamespaceCode() {
		return this.applicationNamespaceCode;
	}

	/**
	 * @param applicationNamespaceCode the applicationNamespaceCode to set
	 */
	public void setApplicationNamespaceCode(String applicationNamespaceCode) {
		this.applicationNamespaceCode = applicationNamespaceCode;
	}
	
	

}

