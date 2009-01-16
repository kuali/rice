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
 * See the License for the specific language governing responsibilitys and
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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KRIM_RSP_T")
public class KimResponsibilityImpl extends PersistableBusinessObjectBase implements KimResponsibility {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="RSP_ID")
	protected String responsibilityId;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	@Column(name="NM")
	protected String name;
	@Column(name="DESC_TXT", length=400)
	protected String description;
	@Column(name="ACTV_IND")
	protected boolean active;

	@OneToMany(targetEntity=ResponsibilityAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="RSP_ID", insertable=false, updatable=false)
	protected List<ResponsibilityAttributeDataImpl> detailObjects = new TypedArrayList(ResponsibilityAttributeDataImpl.class);

	@OneToMany(targetEntity=RolePermissionImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="RSP_ID", insertable=false, updatable=false)
	protected List<RoleResponsibilityImpl> assignedToRoles = new TypedArrayList(RoleResponsibilityImpl.class);

	@OneToMany(targetEntity=KimResponsibilityRequiredAttributeImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="RSP_ID", insertable=false, updatable=false)
	protected List<KimResponsibilityRequiredAttributeImpl> requiredRoleQualifierAttributes = new TypedArrayList(KimResponsibilityRequiredAttributeImpl.class);
	
	
	protected String templateId;
	protected KimResponsibilityTemplateImpl template;

	protected KimRoleImpl assignedToRole;
	protected KimGroupImpl assignedToGroup;
	protected KimPrincipalImpl assignedToPrincipal;
	
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
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getResponsibilityId()
	 */
	public String getResponsibilityId() {
		return responsibilityId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getName()
	 */
	public String getName() {
		return name;
	}

	public void setDescription(String responsibilityDescription) {
		this.description = responsibilityDescription;
	}

	public void setName(String responsibilityName) {
		this.name = responsibilityName;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "responsibilityId", responsibilityId );
		m.put( "name", name );
		m.put( "details", getDetails() );
		return m;
	}

	public KimResponsibilityInfo toSimpleInfo() {
		KimResponsibilityInfo dto = new KimResponsibilityInfo();
		
		dto.setResponsibilityId( getResponsibilityId() );
		dto.setNamespaceCode( getNamespaceCode() );
		dto.setName( getName() );
		dto.setDescription( getDescription() );
		dto.setActive( isActive() );
		dto.setDetails( getDetails() );
		
		return dto;
	}
	
	public List<ResponsibilityAttributeDataImpl> getDetailObjects() {
		return this.detailObjects;
	}

	public void setDetailObjectss(List<ResponsibilityAttributeDataImpl> detailObjects) {
		this.detailObjects = detailObjects;
	}
	
	public boolean hasDetails() {
		return !detailObjects.isEmpty();
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.role.ResponsibilityDetails#getDetails()
	 */
	public AttributeSet getDetails() {
		AttributeSet map = new AttributeSet();
		for (ResponsibilityAttributeDataImpl data : detailObjects) {
			map.put(data.getKimAttribute().getAttributeName(), data.getAttributeValue());
		}
		return map;
	}

	public KimResponsibilityTemplateImpl getTemplate() {
		return this.template;
	}

	public void setTemplate(KimResponsibilityTemplateImpl template) {
		this.template = template;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
	}

	public void setDetailObjects(List<ResponsibilityAttributeDataImpl> detailObjects) {
		this.detailObjects = detailObjects;
	}

	public List<KimResponsibilityRequiredAttributeImpl> getRequiredRoleQualifierAttributes() {
		return this.requiredRoleQualifierAttributes;
	}

	public void setRequiredRoleQualifierAttributes(
			List<KimResponsibilityRequiredAttributeImpl> requiredRoleQualifierAttributes) {
		this.requiredRoleQualifierAttributes = requiredRoleQualifierAttributes;
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


	/**
	 * @return the assignedToGroup
	 */
	public KimGroupImpl getAssignedToGroup() {
		return this.assignedToGroup;
	}

	/**
	 * @param assignedToGroup the assignedToGroup to set
	 */
	public void setAssignedToGroup(KimGroupImpl assignedToGroup) {
		this.assignedToGroup = assignedToGroup;
	}

	/**
	 * @return the assignedToPrincipal
	 */
	public KimPrincipalImpl getAssignedToPrincipal() {
		return this.assignedToPrincipal;
	}

	/**
	 * @param assignedToPrincipal the assignedToPrincipal to set
	 */
	public void setAssignedToPrincipal(KimPrincipalImpl assignedToPrincipal) {
		this.assignedToPrincipal = assignedToPrincipal;
	}

	/**
	 * @return the assignedToRole
	 */
	public KimRoleImpl getAssignedToRole() {
		return this.assignedToRole;
	}

	/**
	 * @param assignedToRole the assignedToRole to set
	 */
	public void setAssignedToRole(KimRoleImpl assignedToRole) {
		this.assignedToRole = assignedToRole;
	}

	/**
	 * @return the assignedToRoles
	 */
	public List<RoleResponsibilityImpl> getAssignedToRoles() {
		return this.assignedToRoles;
	}

	/**
	 * @param assignedToRoles the assignedToRoles to set
	 */
	public void setAssignedToRoles(List<RoleResponsibilityImpl> assignedToRoles) {
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

	public String getDetailObjectsValues(){
		StringBuffer detailObjectsToDisplay = new StringBuffer();
		for(ResponsibilityAttributeDataImpl responsibilityAttributeData: detailObjects){
			detailObjectsToDisplay.append(responsibilityAttributeData.getAttributeValue()+KimConstants.COMMA_SEPARATOR);
		}
        if(detailObjectsToDisplay.toString().endsWith(KimConstants.COMMA_SEPARATOR))
        	detailObjectsToDisplay.delete(detailObjectsToDisplay.length()-KimConstants.COMMA_SEPARATOR.length(), detailObjectsToDisplay.length());

		return detailObjectsToDisplay.toString();
	}
	
	public String getDetailObjectsToDisplay() {
		StringBuffer detailObjectsToDisplay = new StringBuffer();
		for(ResponsibilityAttributeDataImpl responsibilityAttributeData: detailObjects){
			detailObjectsToDisplay.append(getAttributeDetailToDisplay(responsibilityAttributeData));
		}
        if(detailObjectsToDisplay.toString().endsWith(KimConstants.COMMA_SEPARATOR))
        	detailObjectsToDisplay.delete(detailObjectsToDisplay.length()-KimConstants.COMMA_SEPARATOR.length(), detailObjectsToDisplay.length());

		return detailObjectsToDisplay.toString();
	}

	public String getAttributeDetailToDisplay(ResponsibilityAttributeDataImpl responsibilityAttributeData){
		return responsibilityAttributeData.getKimAttribute().getAttributeName()+KimConstants.NAME_VALUE_SEPARATOR+
		responsibilityAttributeData.getAttributeValue()+KimConstants.COMMA_SEPARATOR;
	}

	public String getRequiredRoleQualifierAttributesToDisplay() {
		StringBuffer requiredRoleQualifierAttributesToDisplay = new StringBuffer();
		for(KimResponsibilityRequiredAttributeImpl responsibilityRequiredAttribute: requiredRoleQualifierAttributes){
			requiredRoleQualifierAttributesToDisplay.append(getRequiredRoleQualifierAttributeToDisplay(responsibilityRequiredAttribute));
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
	public String getRequiredRoleQualifierAttributeToDisplay(KimResponsibilityRequiredAttributeImpl responsibilityRequiredAttribute){
		String value = getKimAttributeLabelFromDD(responsibilityRequiredAttribute.getKimAttribute().getAttributeName());
		return StringUtils.isEmpty(value)?value:value+KimConstants.COMMA_SEPARATOR;
	}

	/**
	 * @return the assignedToRoles
	 */
	public String getAssignedToRolesToDisplay() {
		StringBuffer assignedToRolesToDisplay = new StringBuffer();
		for(RoleResponsibilityImpl roleResponsibilityImpl: assignedToRoles){
			assignedToRolesToDisplay.append(getRoleDetailsToDisplay(roleResponsibilityImpl));
		}
        if(assignedToRolesToDisplay.toString().endsWith(KimConstants.COMMA_SEPARATOR))
        	assignedToRolesToDisplay.delete(
        			assignedToRolesToDisplay.length()-KimConstants.COMMA_SEPARATOR.length(), assignedToRolesToDisplay.length());

		return assignedToRolesToDisplay.toString();
	}

	public String getRoleDetailsToDisplay(RoleResponsibilityImpl roleResponsibilityImpl){
		return roleResponsibilityImpl.getKimRole().getKimRoleType().getName()+KimConstants.NAME_VALUE_SEPARATOR+
		roleResponsibilityImpl.getKimRole().getNamespaceCode()+KimConstants.NAME_VALUE_SEPARATOR+
		roleResponsibilityImpl.getKimRole().getRoleName()+KimConstants.COMMA_SEPARATOR;
	}

}