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
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DataDictionaryService;
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

	@OneToMany(targetEntity=KimResponsibilityRequiredAttributeImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="RSP_ID", insertable=false, updatable=false)
	protected List<KimResponsibilityRequiredAttributeImpl> requiredRoleQualifierAttributes = new TypedArrayList(KimResponsibilityRequiredAttributeImpl.class);
	
	
	protected String templateId;
	protected KimResponsibilityTemplateImpl template;
	
	protected List<RoleResponsibilityImpl> roleResponsibilities = new TypedArrayList(RoleResponsibilityImpl.class);
	
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

	public void setDetailObjects(List<ResponsibilityAttributeDataImpl> detailObjects) {
		this.detailObjects = detailObjects;
		detailsAsAttributeSet = null;
	}
	
	public boolean hasDetails() {
		return !detailObjects.isEmpty();
	}
	
	protected AttributeSet detailsAsAttributeSet = null;
	
	/**
	 * @see org.kuali.rice.kim.bo.role.ResponsibilityDetails#getDetails()
	 */
	public AttributeSet getDetails() {
		if ( detailsAsAttributeSet == null ) {
			detailsAsAttributeSet = new AttributeSet();
			for (ResponsibilityAttributeDataImpl data : detailObjects) {
				detailsAsAttributeSet.put(data.getKimAttribute().getAttributeName(), data.getAttributeValue());
			}
		}
		return detailsAsAttributeSet;
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

	public List<KimResponsibilityRequiredAttributeImpl> getRequiredRoleQualifierAttributes() {
		return this.requiredRoleQualifierAttributes;
	}

	public void setRequiredRoleQualifierAttributes(
			List<KimResponsibilityRequiredAttributeImpl> requiredRoleQualifierAttributes) {
		this.requiredRoleQualifierAttributes = requiredRoleQualifierAttributes;
	}

	/**
	 * @return the roleResponsibilities
	 */
	public List<RoleResponsibilityImpl> getRoleResponsibilities() {
		return this.roleResponsibilities;
	}

	/**
	 * @param roleResponsibilities the roleResponsibilities to set
	 */
	public void setRoleResponsibilities(
			List<RoleResponsibilityImpl> roleResponsibilities) {
		this.roleResponsibilities = roleResponsibilities;
	}


	public String getDetailObjectsValues(){
		StringBuffer detailObjectsToDisplay = new StringBuffer();
		for(ResponsibilityAttributeDataImpl responsibilityAttributeData: detailObjects){
			detailObjectsToDisplay.append(responsibilityAttributeData.getAttributeValue()+KimConstants.KimUIConstants.COMMA_SEPARATOR);
		}
        if(detailObjectsToDisplay.toString().endsWith(KimConstants.KimUIConstants.COMMA_SEPARATOR))
        	detailObjectsToDisplay.delete(detailObjectsToDisplay.length()-KimConstants.KimUIConstants.COMMA_SEPARATOR.length(), detailObjectsToDisplay.length());

		return detailObjectsToDisplay.toString();
	}

	public String getDetailObjectsToDisplay() {
		StringBuffer detailObjectsToDisplay = new StringBuffer();
		for(ResponsibilityAttributeDataImpl responsibilityAttributeData: detailObjects){
			detailObjectsToDisplay.append(getAttributeDetailToDisplay(responsibilityAttributeData));
		}
        if(detailObjectsToDisplay.toString().endsWith(KimConstants.KimUIConstants.COMMA_SEPARATOR))
        	detailObjectsToDisplay.delete(detailObjectsToDisplay.length()-KimConstants.KimUIConstants.COMMA_SEPARATOR.length(), detailObjectsToDisplay.length());

		return detailObjectsToDisplay.toString();
	}

	public String getAttributeDetailToDisplay(ResponsibilityAttributeDataImpl responsibilityAttributeData){
		return getKimAttributeLabelFromDD(responsibilityAttributeData.getKimAttribute().getAttributeName())+
				KimConstants.KimUIConstants.NAME_VALUE_SEPARATOR+
				responsibilityAttributeData.getAttributeValue()+KimConstants.KimUIConstants.COMMA_SEPARATOR;
	}
	
	public String getRequiredRoleQualifierAttributesToDisplay() {
		StringBuffer requiredRoleQualifierAttributesToDisplay = new StringBuffer();
		for(KimResponsibilityRequiredAttributeImpl responsibilityRequiredAttribute: requiredRoleQualifierAttributes){
			requiredRoleQualifierAttributesToDisplay.append(getRequiredRoleQualifierAttributeToDisplay(responsibilityRequiredAttribute));
		}
        if(requiredRoleQualifierAttributesToDisplay.toString().endsWith(KimConstants.KimUIConstants.COMMA_SEPARATOR))
        	requiredRoleQualifierAttributesToDisplay.delete(requiredRoleQualifierAttributesToDisplay.length()-KimConstants.KimUIConstants.COMMA_SEPARATOR.length(), requiredRoleQualifierAttributesToDisplay.length());

		return requiredRoleQualifierAttributesToDisplay.toString();
	}

	//TODO: remove this and find a better way to do this. Should be done by next week with role doc task
	protected String getKimAttributeLabelFromDD(String attributeName){
    	return getDataDictionaryService().getAttributeLabel(KimAttributes.class, attributeName);
    }

	transient private DataDictionaryService dataDictionaryService;
	public DataDictionaryService getDataDictionaryService() {
		if(dataDictionaryService == null){
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		}
		return dataDictionaryService;
	}
	
	//TODO: remove this and find a better way to do this. Should be done by next week with role doc task
	public String getRequiredRoleQualifierAttributeToDisplay(KimResponsibilityRequiredAttributeImpl responsibilityRequiredAttribute){
		String value = getKimAttributeLabelFromDD(responsibilityRequiredAttribute.getKimAttribute().getAttributeName());
		return StringUtils.isEmpty(value)?value:value+KimConstants.KimUIConstants.COMMA_SEPARATOR;
	}

}
