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
package org.kuali.rice.kim.bo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KRIM_PND_ROLE_MT")
public class PersonDocumentRole extends KimDocumentBoBase {
	@Column(name="ROLE_ID")
	protected String roleId;
	protected String kimTypeId;
	protected String roleName;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	protected KimTypeImpl kimRoleType;
	protected List<? extends KimAttributes> attributes;
	protected transient AttributeDefinitionMap definitions;
	protected transient Map<String,Object> attributeEntry;
	protected List<KimDocumentRoleMember> rolePrncpls;
    protected KimDocumentRoleMember newRolePrncpl;
	protected List<RoleResponsibilityImpl> assignedResponsibilities = new TypedArrayList(RoleResponsibilityImpl.class);

	public PersonDocumentRole() {
		attributes = new ArrayList<KimAttributes>();	
		rolePrncpls = new ArrayList<KimDocumentRoleMember>();	
		attributeEntry = new HashMap<String,Object>();
	}
	
	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "roleId", roleId );
		return m;
	}

	public String getKimTypeId() {
		return this.kimTypeId;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<? extends KimAttributes> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<? extends KimAttributes> attributes) {
		this.attributes = attributes;
	}

	public KimTypeImpl getKimRoleType() {
		return this.kimRoleType;
	}

	public void setKimRoleType(KimTypeImpl kimRoleType) {
		this.kimRoleType = kimRoleType;
	}

	public AttributeDefinitionMap getDefinitions() {
		if (definitions == null || definitions.isEmpty()) {
			String serviceName = this.getKimRoleType().getKimTypeServiceName();
			if (StringUtils.isBlank(serviceName)) {
				serviceName = "kimTypeService";				
			}
	        KimTypeService kimTypeService = (KimTypeServiceBase)KIMServiceLocator.getService(serviceName);
			definitions = kimTypeService.getAttributeDefinitions(getKimTypeId());
		}
		return definitions;
	}

	public void setDefinitions(AttributeDefinitionMap definitions) {
		this.definitions = definitions;
	}

	public Map<String,Object> getAttributeEntry() {
		if (attributeEntry == null || attributeEntry.isEmpty()) {
			attributeEntry = KIMServiceLocator.getUiDocumentService().getAttributeEntries(getDefinitions());
		}
		
		return this.attributeEntry;
	}

	public void setAttributeEntry(Map<String,Object> attributeEntry) {
		this.attributeEntry = attributeEntry;
	}

	public List<KimDocumentRoleMember> getRolePrncpls() {
		return this.rolePrncpls;
	}

	public void setRolePrncpls(List<KimDocumentRoleMember> rolePrncpls) {
		this.rolePrncpls = rolePrncpls;
	}

	public KimDocumentRoleMember getNewRolePrncpl() {
		return this.newRolePrncpl;
	}

	public void setNewRolePrncpl(KimDocumentRoleMember newRolePrncpl) {
		this.newRolePrncpl = newRolePrncpl;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public List<RoleResponsibilityImpl> getAssignedResponsibilities() {
		return this.assignedResponsibilities;
	}

	public void setAssignedResponsibilities(
			List<RoleResponsibilityImpl> assignedResponsibilities) {
		this.assignedResponsibilities = assignedResponsibilities;
	}

}
