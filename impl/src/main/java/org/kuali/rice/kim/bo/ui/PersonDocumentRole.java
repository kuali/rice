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

import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KRIM_PND_ROLE_MT")
public class PersonDocumentRole extends PersonDocumentBoBase {
	@Column(name="ROLE_ID")
	protected String roleId;
	protected String kimTypeId;
	protected String roleName;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	protected KimTypeImpl kimRoleType;
	protected List<KimAttributeImpl> attributes;
	protected AttributeDefinitionMap definitions;
	protected Map attributeEntry;
	protected List<PersonDocumentRolePrncpl> rolePrncpls;
    protected PersonDocumentRolePrncpl newRolePrncpl;
	
	public PersonDocumentRole() {
		attributes = new ArrayList<KimAttributeImpl>();	
		rolePrncpls = new ArrayList<PersonDocumentRolePrncpl>();	
		// set following for testing
//		KimAttributeImpl attrDefn = new KimAttributeImpl();
//		KimAttributeImpl attrDefn1 = new KimAttributeImpl();
//		attrDefn.setAttributeName("campusCode");
//		attrDefn.setComponentName("org.kuali.rice.kns.bo.Campus");
//		attributes.add(attrDefn);
		attributeEntry = new HashMap();
//		attrDefn.setAttributeName("chartOfAccountsCode");
//		attrDefn.setComponentName("org.kuali.kfs.coa.businessobject.Chart");
//		attributes.add(attrDefn);
//		
//		attrDefn1.setAttributeName("organizationCode");
//		attrDefn1.setComponentName("org.kuali.kfs.coa.businessobject.Org");
//		attributes.add(attrDefn1);
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

	public List<KimAttributeImpl> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<KimAttributeImpl> attributes) {
		this.attributes = attributes;
	}

	public KimTypeImpl getKimRoleType() {
		return this.kimRoleType;
	}

	public void setKimRoleType(KimTypeImpl kimRoleType) {
		this.kimRoleType = kimRoleType;
	}

	public AttributeDefinitionMap getDefinitions() {
		return this.definitions;
	}

	public void setDefinitions(AttributeDefinitionMap definitions) {
		this.definitions = definitions;
	}

	public Map getAttributeEntry() {
		return this.attributeEntry;
	}

	public void setAttributeEntry(Map attributeEntry) {
		this.attributeEntry = attributeEntry;
	}

	public List<PersonDocumentRolePrncpl> getRolePrncpls() {
		return this.rolePrncpls;
	}

	public void setRolePrncpls(List<PersonDocumentRolePrncpl> rolePrncpls) {
		this.rolePrncpls = rolePrncpls;
	}

	public PersonDocumentRolePrncpl getNewRolePrncpl() {
		return this.newRolePrncpl;
	}

	public void setNewRolePrncpl(PersonDocumentRolePrncpl newRolePrncpl) {
		this.newRolePrncpl = newRolePrncpl;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

}
