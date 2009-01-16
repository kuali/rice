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
package org.kuali.rice.kim.bo.role.impl;

import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KRIM_PERM_T")
public class KimPermissionImpl extends PersistableBusinessObjectBase implements KimPermission {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="PERM_ID")
	protected String permissionId;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	@Column(name="NM")
	protected String name;
	@Column(name="DESC_TXT", length=400)
	protected String description;
	@Column(name="ACTV_IND")
	protected boolean active;
	
	@OneToMany(targetEntity=PermissionAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="PERM_ID", insertable=false, updatable=false)
	protected List<PermissionAttributeDataImpl> detailObjects = new TypedArrayList(PermissionAttributeDataImpl.class);

	@OneToMany(targetEntity=KimPermissionRequiredAttributeImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="PERM_ID", insertable=false, updatable=false)
	protected List<KimPermissionRequiredAttributeImpl> requiredRoleQualifierAttributes = new TypedArrayList(KimPermissionRequiredAttributeImpl.class);
	
	protected String templateId;
	protected KimPermissionTemplateImpl template;

	@OneToMany(targetEntity=RolePermissionImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="PERM_ID", insertable=false, updatable=false)
	protected List<RolePermissionImpl> assignedToRoles = new TypedArrayList(RolePermissionImpl.class);
	
	protected String assignedToRoleNamespaceForLookup;
	protected String assignedToRoleNameForLookup;
	protected String assignedToPrincipalNameForLookup;
	protected String assignedToGroupNamespaceForLookup;
	protected String assignedToGroupNameForLookup;
	protected String attributeValue;
	
	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getPermissionId()
	 */
	public String getPermissionId() {
		return permissionId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getName()
	 */
	public String getName() {
		return name;
	}

	public void setDescription(String permissionDescription) {
		this.description = permissionDescription;
	}

	public void setName(String permissionName) {
		this.name = permissionName;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "permissionId", permissionId );
		m.put( "name", name );
		m.put( "details", getDetails() );
		return m;
	}

	public KimPermissionInfo toSimpleInfo() {
		KimPermissionInfo dto = new KimPermissionInfo();
		
		dto.setPermissionId( getPermissionId() );
		dto.setNamespaceCode( getNamespaceCode() );
		dto.setName( getName() );
		dto.setDescription( getDescription() );
		dto.setActive( isActive() );
		dto.setDetails( getDetails() );
		dto.setTemplate( getTemplate().toSimpleInfo() );
		
		return dto;
	}

	public String getDetailObjectsValues(){
		StringBuffer detailObjectsToDisplay = new StringBuffer();
		for(PermissionAttributeDataImpl permissionAttributeData: detailObjects){
			detailObjectsToDisplay.append(permissionAttributeData.getAttributeValue()+KimConstants.COMMA_SEPARATOR);
		}
        if(detailObjectsToDisplay.toString().endsWith(KimConstants.COMMA_SEPARATOR))
        	detailObjectsToDisplay.delete(detailObjectsToDisplay.length()-KimConstants.COMMA_SEPARATOR.length(), detailObjectsToDisplay.length());

		return detailObjectsToDisplay.toString();
	}

	public String getDetailObjectsToDisplay() {
		StringBuffer detailObjectsToDisplay = new StringBuffer();
		for(PermissionAttributeDataImpl permissionAttributeData: detailObjects){
			detailObjectsToDisplay.append(getAttributeDetailToDisplay(permissionAttributeData));
		}
        if(detailObjectsToDisplay.toString().endsWith(KimConstants.COMMA_SEPARATOR))
        	detailObjectsToDisplay.delete(detailObjectsToDisplay.length()-KimConstants.COMMA_SEPARATOR.length(), detailObjectsToDisplay.length());

		return detailObjectsToDisplay.toString();
	}

	public String getAttributeDetailToDisplay(PermissionAttributeDataImpl permissionAttributeData){
		return permissionAttributeData.getKimAttribute().getAttributeName()+KimConstants.NAME_VALUE_SEPARATOR+
				permissionAttributeData.getAttributeValue()+KimConstants.COMMA_SEPARATOR;
	}
	
	public List<PermissionAttributeDataImpl> getDetailObjects() {
		return this.detailObjects;
	}

	public void setDetails(List<PermissionAttributeDataImpl> detailObjects) {
		this.detailObjects = detailObjects;
	}
	
	public KimPermissionTemplateImpl getTemplate() {
		return this.template;
	}

	public void setTemplate(KimPermissionTemplateImpl template) {
		this.template = template;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public AttributeSet getDetails() {
		AttributeSet m = new AttributeSet();
		for ( PermissionAttributeDataImpl data : getDetailObjects() ) {
			m.put( data.getKimAttribute().getAttributeName(), data.getAttributeValue() );
		}
		return m;
	}
	
	public boolean hasDetails() {
		return !getDetailObjects().isEmpty();
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}

	public void setDetailObjects(List<PermissionAttributeDataImpl> detailObjects) {
		this.detailObjects = detailObjects;
	}

	public String getRequiredRoleQualifierAttributesToDisplay() {
		StringBuffer requiredRoleQualifierAttributesToDisplay = new StringBuffer();
		for(KimPermissionRequiredAttributeImpl permissionRequiredAttribute: requiredRoleQualifierAttributes){
			requiredRoleQualifierAttributesToDisplay.append(getRequiredRoleQualifierAttributeToDisplay(permissionRequiredAttribute));
		}
        if(requiredRoleQualifierAttributesToDisplay.toString().endsWith(KimConstants.COMMA_SEPARATOR))
        	requiredRoleQualifierAttributesToDisplay.delete(requiredRoleQualifierAttributesToDisplay.length()-KimConstants.COMMA_SEPARATOR.length(), requiredRoleQualifierAttributesToDisplay.length());

		return requiredRoleQualifierAttributesToDisplay.toString();
	}

	//TODO: remove this and find a better way to do this. Should be done by next week with role doc task
	protected String getKimAttributeLabelFromDD(String attributeName){
    	return KNSServiceLocator.getDataDictionaryService().getAttributeLabel(KimAttributes.class, attributeName);
    }

	//TODO: remove this and find a better way to do this. Should be done by next week with role doc task
	public String getRequiredRoleQualifierAttributeToDisplay(KimPermissionRequiredAttributeImpl permissionRequiredAttribute){
		String value = getKimAttributeLabelFromDD(permissionRequiredAttribute.getKimAttribute().getAttributeName());
		return StringUtils.isEmpty(value)?value:value+KimConstants.COMMA_SEPARATOR;
	}
	
	public List<KimPermissionRequiredAttributeImpl> getRequiredRoleQualifierAttributes() {
		return this.requiredRoleQualifierAttributes;
	}

	public void setRequiredRoleQualifierAttributes(
			List<KimPermissionRequiredAttributeImpl> requiredRoleQualifierAttributes) {
		this.requiredRoleQualifierAttributes = requiredRoleQualifierAttributes;
	}

	/**
	 * @return the assignedToRoles
	 */
	public String getAssignedToRolesToDisplay() {
		StringBuffer assignedToRolesToDisplay = new StringBuffer();
		for(RolePermissionImpl rolePermissionImpl: assignedToRoles){
			assignedToRolesToDisplay.append(getRoleDetailsToDisplay(rolePermissionImpl));
		}
        if(assignedToRolesToDisplay.toString().endsWith(KimConstants.COMMA_SEPARATOR))
        	assignedToRolesToDisplay.delete(
        			assignedToRolesToDisplay.length()-KimConstants.COMMA_SEPARATOR.length(), assignedToRolesToDisplay.length());

		return assignedToRolesToDisplay.toString();
	}

	public String getRoleDetailsToDisplay(RolePermissionImpl rolePermissionImpl){
		return rolePermissionImpl.getKimRole().getKimRoleType().getName()+KimConstants.NAME_VALUE_SEPARATOR+
		rolePermissionImpl.getKimRole().getNamespaceCode()+KimConstants.NAME_VALUE_SEPARATOR+
		rolePermissionImpl.getKimRole().getRoleName()+KimConstants.COMMA_SEPARATOR;
	}
	
	/**
	 * @return the assignedToRoles
	 */
	public List<RolePermissionImpl> getAssignedToRoles() {
		return this.assignedToRoles;
	}

	/**
	 * @param assignedToRoles the assignedToRoles to set
	 */
	public void setAssignedToRoles(List<RolePermissionImpl> assignedToRoles) {
		this.assignedToRoles = assignedToRoles;
	}

	/**
	 * @return the assignedToGroupNameForLookup
	 */
	public String getAssignedToGroupNameForLookup() {
		return this.assignedToGroupNameForLookup;
	}

	/**
	 * @param assignedToGroupNameForLookup the assignedToGroupNameForLookup to set
	 */
	public void setAssignedToGroupNameForLookup(String assignedToGroupNameForLookup) {
		this.assignedToGroupNameForLookup = assignedToGroupNameForLookup;
	}

	/**
	 * @return the assignedToGroupNamespaceForLookup
	 */
	public String getAssignedToGroupNamespaceForLookup() {
		return this.assignedToGroupNamespaceForLookup;
	}

	/**
	 * @param assignedToGroupNamespaceForLookup the assignedToGroupNamespaceForLookup to set
	 */
	public void setAssignedToGroupNamespaceForLookup(
			String assignedToGroupNamespaceForLookup) {
		this.assignedToGroupNamespaceForLookup = assignedToGroupNamespaceForLookup;
	}

	/**
	 * @return the assignedToPrincipalNameForLookup
	 */
	public String getAssignedToPrincipalNameForLookup() {
		return this.assignedToPrincipalNameForLookup;
	}

	/**
	 * @param assignedToPrincipalNameForLookup the assignedToPrincipalNameForLookup to set
	 */
	public void setAssignedToPrincipalNameForLookup(
			String assignedToPrincipalNameForLookup) {
		this.assignedToPrincipalNameForLookup = assignedToPrincipalNameForLookup;
	}

	/**
	 * @return the assignedToRoleNameForLookup
	 */
	public String getAssignedToRoleNameForLookup() {
		return this.assignedToRoleNameForLookup;
	}

	/**
	 * @param assignedToRoleNameForLookup the assignedToRoleNameForLookup to set
	 */
	public void setAssignedToRoleNameForLookup(String assignedToRoleNameForLookup) {
		this.assignedToRoleNameForLookup = assignedToRoleNameForLookup;
	}

	/**
	 * @return the assignedToRoleNamespaceForLookup
	 */
	public String getAssignedToRoleNamespaceForLookup() {
		return this.assignedToRoleNamespaceForLookup;
	}

	/**
	 * @param assignedToRoleNamespaceForLookup the assignedToRoleNamespaceForLookup to set
	 */
	public void setAssignedToRoleNamespaceForLookup(
			String assignedToRoleNamespaceForLookup) {
		this.assignedToRoleNamespaceForLookup = assignedToRoleNamespaceForLookup;
	}

	/**
	 * @return the attributeValue
	 */
	public String getAttributeValue() {
		return this.attributeValue;
	}

	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	
}
