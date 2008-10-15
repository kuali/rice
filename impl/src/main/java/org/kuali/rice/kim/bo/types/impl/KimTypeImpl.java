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
package org.kuali.rice.kim.bo.types.impl;

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

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KR_KIM_TYPE_T")
public class KimTypeImpl extends PersistableBusinessObjectBase {

	@Id
	@Column(name="KIM_TYPE_ID")
	protected String kimTypeId;
	@Column(name="TYPE_NM")
	protected String name;
	@Column(name="SRVC_NM")
	protected String kimTypeServiceName;
	@Column(name="ACTV_IND")
	protected boolean active; 
	
	@OneToMany(targetEntity=KimTypeAttributeImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="KIM_TYPE_ID", insertable=false, updatable=false)
	protected List<KimTypeAttributeImpl> attributeDefinitions;
	
	public List<KimTypeAttributeImpl> getAttributeDefinitions() {
		return attributeDefinitions;
	}

	public String getKimTypeId() {
		return kimTypeId;
	}

	public String getKimTypeServiceName() {
		return kimTypeServiceName;
	}

	public String getName() {
		return name;
	}

	public void setAttributeDefinitions(List<KimTypeAttributeImpl> attributeDefinitions) {
		this.attributeDefinitions = attributeDefinitions;
	}

	public void setKimTypeServiceName(String kimTypeServiceName) {
		this.kimTypeServiceName = kimTypeServiceName;
	}

	public void setName(String name) {
		this.name = name;
	}

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
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "kimTypeId", kimTypeId );
		m.put( "name", name );
		m.put( "kimTypeServiceName", kimTypeServiceName );
		m.put( "active", active );
		return m;
	}
	
}
