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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.KimResponsibilityInfo;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KR_KIM_RESP_T")
public class KimResponsibilityImpl extends PersistableBusinessObjectBase implements KimResponsibility {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="RESP_ID")
	protected String responsibilityId;
	@Column(name="NAME")
	protected String name;
	@Column(name="DESCRIPTION", length=400)
	protected String description;
	@Column(name="ACTV_IND")
	protected boolean active;

	@OneToMany(targetEntity=ResponsibilityAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="RESP_ID", insertable=false, updatable=false)
	protected List<ResponsibilityAttributeDataImpl> detailObjects;

	protected String templateId;
	protected KimResponsibilityTemplateImpl template;
	
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
		dto.setName( getName() );
		dto.setDescription( getDescription() );
		dto.setActive( isActive() );
		dto.setDetails( getDetails() );
		
		return dto;
	}
	
	public List<ResponsibilityAttributeDataImpl> getDetailObjects() {
		return this.detailObjects;
	}

	public void setDetails(List<ResponsibilityAttributeDataImpl> detailObjects) {
		this.detailObjects = detailObjects;
	}
	
	public boolean hasDetails() {
		return !detailObjects.isEmpty();
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.role.ResponsibilityDetails#getDetails()
	 */
	public Map<String,String> getDetails() {
		Map<String, String> map = new HashMap<String, String>();
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

	
}
