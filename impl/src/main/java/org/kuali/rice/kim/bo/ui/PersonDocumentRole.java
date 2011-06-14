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
package org.kuali.rice.kim.bo.ui;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.KIMServiceLocatorWeb;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */

@Entity
@IdClass(org.kuali.rice.kim.bo.ui.PersonDocumentRoleId.class)
@Table(name="KRIM_PND_ROLE_MT",uniqueConstraints=@UniqueConstraint(columnNames={"FDOC_NBR", "ROLE_ID"}))
public class PersonDocumentRole extends KimDocumentBoActivatableEditableBase {
    private static final Logger LOG = Logger.getLogger(PersonDocumentRole.class);
	private static final long serialVersionUID = 4908044213007222739L;
	@Id
	@Column(name="ROLE_ID")
	protected String roleId;
	@Column(name="KIM_TYP_ID")
	protected String kimTypeId;
	@Column(name="ROLE_NM")
	protected String roleName;
	@Transient
	protected RoleImpl roleImpl;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	@Transient
	protected KimTypeBo kimRoleType;
	@Transient
	protected List<? extends KimAttributes> attributes;
	@Transient
	protected transient AttributeDefinitionMap definitions;
	@Transient
	protected transient Map<String,Object> attributeEntry;
	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
    @Fetch(value = FetchMode.SELECT)
	@JoinColumns({
		@JoinColumn(name="ROLE_ID",insertable=false,updatable=false),
		@JoinColumn(name="FDOC_NBR", insertable=false, updatable=false)
	})
	protected List<KimDocumentRoleMember> rolePrncpls;
	@Transient
    protected KimDocumentRoleMember newRolePrncpl;
	//currently mapped as manyToMany even though it is technically a OneToMany
	//The reason for this is it is linked with a partial Composite-id, which technically can't 
	//guarantee uniqueness.  
	@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name="ROLE_ID",insertable=false,updatable=false)
    //@JoinColumns({
	//	@JoinColumn(name="ROLE_ID",insertable=false,updatable=false),
	//	@JoinColumn(name="FDOC_NBR", insertable=false, updatable=false, table="KRIM_PERSON_DOCUMENT_T")
	//})
	protected List<RoleResponsibilityImpl> assignedResponsibilities = new AutoPopulatingList(RoleResponsibilityImpl.class);

	@Transient
    protected boolean isEditable = true;
    
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

	public KimTypeBo getKimRoleType() {
		if ( kimRoleType == null ) {
			kimRoleType = KimTypeBo.from(KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId));
		}
		return kimRoleType;
	}

	public AttributeDefinitionMap getDefinitionsKeyedByAttributeName() {
		AttributeDefinitionMap definitionsKeyedBySortCode = getDefinitions();
		AttributeDefinitionMap returnValue = new AttributeDefinitionMap();
		if (definitionsKeyedBySortCode != null) {
			for (AttributeDefinition definition : definitionsKeyedBySortCode.values()) {
				returnValue.put(definition.getName(), definition);
			}
		}
		return returnValue;
	}

	public AttributeDefinitionMap getDefinitions() {
		if (definitions == null || definitions.isEmpty()) {
	        KimTypeService kimTypeService = KIMServiceLocatorWeb.getKimTypeService(KimTypeBo.to(this.getKimRoleType()));
	        //it is possible that the the roleTypeService is coming from a remote application 
	        // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
	        try {
    	        if ( kimTypeService != null ) {
    	        	definitions = kimTypeService.getAttributeDefinitions(getKimTypeId());
    	        } else {
    	        	definitions = new AttributeDefinitionMap();
    	        }
    		} catch (Exception ex) {
                LOG.warn("Not able to retrieve KimTypeService from remote system for KIM Role Type: " + this.getKimRoleType(), ex);
            }
		}
		
		return definitions;
	}

	public void setDefinitions(AttributeDefinitionMap definitions) {
		this.definitions = definitions;
	}

	public Map<String,Object> getAttributeEntry() {
		if (attributeEntry == null || attributeEntry.isEmpty()) {
			attributeEntry = KIMServiceLocatorInternal.getUiDocumentService().getAttributeEntries(getDefinitions());
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

	/**
	 * @return the roleImpl
	 */
	public RoleImpl getRoleImpl() {
		return this.roleImpl;
	}

	/**
	 * @param roleImpl the roleImpl to set
	 */
	public void setRoleImpl(RoleImpl roleImpl) {
		this.roleImpl = roleImpl;
	}

	/**
	 * @return the isEditable
	 */
	public boolean isEditable() {
		return this.isEditable;
	}

	/**
	 * @param isEditable the isEditable to set
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

}
