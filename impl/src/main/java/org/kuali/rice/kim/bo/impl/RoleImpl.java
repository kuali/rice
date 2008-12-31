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

import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleImpl extends TransientBusinessObjectBase implements Role {

	private static final long serialVersionUID = 1L;
	protected String roleId;
	protected String roleName;
	protected String kimTypeId;
	protected String namespaceCode;
	protected String principalName;
	protected String groupNamespaceCode;
	protected String groupName;
	protected String permNamespaceCode;
	protected String permName;
	protected String permTmplNamespaceCode;
	protected String permTmplName;
	protected String respNamespaceCode;
	protected String respName;
	protected String respTmplNamespaceCode;
	protected String respTmplName;
	protected KimTypeImpl kimRoleType; 

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put("kimTypeId", getKimTypeId());
		m.put("roleName", getRoleName());
		m.put("principalName", getPrincipalName());
		return m;
	}

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

	public String getPrincipalName() {
		return this.principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getGroupNamespaceCode() {
		return this.groupNamespaceCode;
	}

	public void setGroupNamespaceCode(String groupNamespaceCode) {
		this.groupNamespaceCode = groupNamespaceCode;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getPermNamespaceCode() {
		return this.permNamespaceCode;
	}

	public void setPermNamespaceCode(String permNamespaceCode) {
		this.permNamespaceCode = permNamespaceCode;
	}

	public String getPermName() {
		return this.permName;
	}

	public void setPermName(String permName) {
		this.permName = permName;
	}

	public String getRespNamespaceCode() {
		return this.respNamespaceCode;
	}

	public void setRespNamespaceCode(String respNamespaceCode) {
		this.respNamespaceCode = respNamespaceCode;
	}

	public String getRespName() {
		return this.respName;
	}

	public void setRespName(String respName) {
		this.respName = respName;
	}

	public String getPermTmplNamespaceCode() {
		return this.permTmplNamespaceCode;
	}

	public void setPermTmplNamespaceCode(String permTmplNamespaceCode) {
		this.permTmplNamespaceCode = permTmplNamespaceCode;
	}

	public String getPermTmplName() {
		return this.permTmplName;
	}

	public void setPermTmplName(String permTmplName) {
		this.permTmplName = permTmplName;
	}

	public String getRespTmplNamespaceCode() {
		return this.respTmplNamespaceCode;
	}

	public void setRespTmplNamespaceCode(String respTmplNamespaceCode) {
		this.respTmplNamespaceCode = respTmplNamespaceCode;
	}

	public String getRespTmplName() {
		return this.respTmplName;
	}

	public void setRespTmplName(String respTmplName) {
		this.respTmplName = respTmplName;
	}

	public KimTypeImpl getKimRoleType() {
		if (kimRoleType == null) {
			Map pkMap = new HashMap();
			pkMap.put("kimTypeId", kimTypeId);
			setKimRoleType((KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, pkMap));			
		}
		return this.kimRoleType;
	}

	public void setKimRoleType(KimTypeImpl kimRoleType) {
		this.kimRoleType = kimRoleType;
	}

}
