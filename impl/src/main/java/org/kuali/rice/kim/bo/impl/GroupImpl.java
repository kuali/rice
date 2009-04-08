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
 */
package org.kuali.rice.kim.bo.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupImpl extends TransientBusinessObjectBase implements Group {

	private static final long serialVersionUID = 1L;
	protected String groupId;
	protected String groupName;
	protected String kimTypeId;
	protected String namespaceCode;
	protected KimTypeImpl kimGroupType; 

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put("kimTypeId", getKimTypeId());
		m.put("groupName", getGroupName());
		m.put("namespaceCode", getNamespaceCode());
		return m;
	}

	public String getKimTypeId() {
		return this.kimTypeId;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public KimTypeImpl getKimGroupType() {
		if (kimGroupType == null) {
			Map pkMap = new HashMap();
			pkMap.put("kimTypeId", kimTypeId);
			setKimGroupType((KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, pkMap));			
		}
		return this.kimGroupType;
	}

	public void setKimGroupType(KimTypeImpl kimGroupType) {
		this.kimGroupType = kimGroupType;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return this.groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
