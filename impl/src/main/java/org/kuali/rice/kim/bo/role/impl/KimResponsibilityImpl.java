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
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.role.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
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
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	protected boolean active;

	@OneToMany(targetEntity=ResponsibilityAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.EAGER,mappedBy="responsibilityId")
	@Fetch(value = FetchMode.SELECT)
	//@JoinColumn(name="RSP_ID", insertable = false, updatable = false)
	protected List<ResponsibilityAttributeDataImpl> detailObjects = new AutoPopulatingList(ResponsibilityAttributeDataImpl.class);
	
	@Column(name="RSP_TMPL_ID")
	protected String templateId;
	@OneToOne(cascade={},fetch=FetchType.EAGER)
    @JoinColumn(name="RSP_TMPL_ID", insertable=false, updatable=false)
	protected KimResponsibilityTemplateImpl template = new KimResponsibilityTemplateImpl();
	
	@OneToMany(targetEntity=RoleResponsibilityImpl.class,cascade={CascadeType.ALL},fetch=FetchType.EAGER,mappedBy="responsibilityId")
	@Fetch(value = FetchMode.SELECT)
	//@JoinColumn(name="RSP_ID", insertable = false, updatable = false)
	protected List<RoleResponsibilityImpl> roleResponsibilities = new AutoPopulatingList(RoleResponsibilityImpl.class);
	
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

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getName()
	 */
	public String getNameToDisplay() {
		if(template!=null && StringUtils.equals(template.getName(), name)){
			return name;
		} else{
			return template.getName()+" "+name;
		}
	}

	public void setDescription(String responsibilityDescription) {
		this.description = responsibilityDescription;
	}

	public void setName(String responsibilityName) {
		this.name = responsibilityName;
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
	
	protected transient AttributeSet detailsAsAttributeSet = null;
	
	public AttributeSet getDetails() {
		if ( detailsAsAttributeSet == null ) {
			KimType kimType = getTypeInfoService().getKimType( getTemplate().getKimTypeId() );
			AttributeSet m = new AttributeSet();
			for ( ResponsibilityAttributeDataImpl data : getDetailObjects() ) {
				KimTypeAttribute attribute = null;
				if ( kimType != null ) {
					attribute = kimType.getAttributeDefinitionById( data.getKimAttributeId() );
				}
				if ( attribute != null ) {
					m.put( attribute.getKimAttribute().getAttributeName(), data.getAttributeValue() );
				} else {
					m.put( data.getKimAttribute().getAttributeName(), data.getAttributeValue() );
				}
			}
			detailsAsAttributeSet = m;
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
		Iterator<ResponsibilityAttributeDataImpl> respIter = getDetailObjects().iterator();
		while ( respIter.hasNext() ) {
			ResponsibilityAttributeDataImpl responsibilityAttributeData = respIter.next();
			detailObjectsToDisplay.append( responsibilityAttributeData.getAttributeValue() );
			if ( respIter.hasNext() ) {
				detailObjectsToDisplay.append( KimConstants.KimUIConstants.COMMA_SEPARATOR );
			}
		}
		return detailObjectsToDisplay.toString();
	}

	public String getDetailObjectsToDisplay() {
		KimType kimType = getTypeInfoService().getKimType( getTemplate().getKimTypeId() );
		StringBuffer detailObjectsToDisplay = new StringBuffer();
		Iterator<ResponsibilityAttributeDataImpl> respIter = getDetailObjects().iterator();
		while ( respIter.hasNext() ) {
			ResponsibilityAttributeDataImpl responsibilityAttributeData = respIter.next();
			detailObjectsToDisplay.append( getKimAttributeLabelFromDD(kimType.getAttributeDefinitionById(responsibilityAttributeData.getKimAttributeId())));
			detailObjectsToDisplay.append( KimConstants.KimUIConstants.NAME_VALUE_SEPARATOR );
			detailObjectsToDisplay.append( responsibilityAttributeData.getAttributeValue() );
			if ( respIter.hasNext() ) {
				detailObjectsToDisplay.append( KimConstants.KimUIConstants.COMMA_SEPARATOR );
			}
		}
		return detailObjectsToDisplay.toString();
	}
	
	protected String getKimAttributeLabelFromDD( KimTypeAttribute attribute ){
    	return getDataDictionaryService().getAttributeLabel(attribute.getKimAttribute().getComponentName(), attribute.getKimAttribute().getAttributeName() );
    }

	transient private DataDictionaryService dataDictionaryService;
	public DataDictionaryService getDataDictionaryService() {
		if(dataDictionaryService == null){
			dataDictionaryService = KNSServiceLocatorWeb.getDataDictionaryService();
		}
		return dataDictionaryService;
	}

	private transient static KimTypeInfoService kimTypeInfoService;
	protected KimTypeInfoService getTypeInfoService() {
		if(kimTypeInfoService == null){
			kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
		}
		return kimTypeInfoService;
	}
}
