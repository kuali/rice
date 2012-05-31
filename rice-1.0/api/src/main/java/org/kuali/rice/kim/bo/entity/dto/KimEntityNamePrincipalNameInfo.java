/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import org.kuali.rice.kim.bo.entity.KimEntityNamePrincipalName;

/**
 * DTO to be used for caching default EntityNames with the PrincipalId
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimEntityNamePrincipalNameInfo implements KimEntityNamePrincipalName {

	protected KimEntityNameInfo defaultEntityName;
	protected String principalName;
	
	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityNamePrincipalName#getDefaultEntityName()
	 */
	public KimEntityNameInfo getDefaultEntityName() {
		return defaultEntityName;
	}
	
	/**
	 * @param defaultEntityName the defaultEntityName to set
	 */
	public void setDefaultEntityName(KimEntityNameInfo defaultEntityName) {
		this.defaultEntityName = defaultEntityName;
	}
	
	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityNamePrincipalName#getPrincipalName()
	 */
	public String getPrincipalName() {
		return principalName;
	}
	
	/**
	 * @param principalName the principalName to set
	 */
	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}
}
