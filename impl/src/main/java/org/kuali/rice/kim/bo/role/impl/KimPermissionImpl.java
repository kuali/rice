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
	protected List<KimPermissionRequiredAttributeImpl> requiredRoleQualifierAttributes;
	
	protected String templateId;
	protected KimPermissionTemplateImpl template;

	protected List<RolePermissionImpl> rolePermissions = new TypedArrayList(RolePermissionImpl.class);

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

	public List<KimPermissionRequiredAttributeImpl> getRequiredRoleQualifierAttributes() {
		return this.requiredRoleQualifierAttributes;
	}

	public void setRequiredRoleQualifierAttributes(
			List<KimPermissionRequiredAttributeImpl> requiredRoleQualifierAttributes) {
		this.requiredRoleQualifierAttributes = requiredRoleQualifierAttributes;
	}

	/**
	 * @return the rolePermissions
	 */
	public List<RolePermissionImpl> getRolePermissions() {
		return this.rolePermissions;
	}

	/**
	 * @param rolePermissions the rolePermissions to set
	 */
	public void setRolePermissions(List<RolePermissionImpl> rolePermissions) {
		this.rolePermissions = rolePermissions;
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

	public String getNameToDisplay(){
		return (StringUtils.isBlank(getName()) && getTemplate()!=null)?getTemplate().getName():getName();
	}
}
