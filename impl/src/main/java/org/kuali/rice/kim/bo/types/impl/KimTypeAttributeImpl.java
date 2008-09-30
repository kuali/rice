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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.types.KimAttribute;
import org.kuali.rice.kim.bo.types.KimTypeAttribute;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KR_KIM_TYPE_ATTRIBUTE_T")
public class KimTypeAttributeImpl extends PersistableBusinessObjectBase implements KimTypeAttribute {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="KIM_TYPE_ATTRIB_ID")
	protected String kimTypeAttributeId;
	@Column(name="KIM_TYPE_ID")
	protected String kimTypeId; 	
	@Column(name="KIM_ATTRIB_ID")
	protected String kimAttributeId;
	@Column(name="ACTV_IND")
	protected boolean active; 	 
	
	@OneToOne(targetEntity=KimAttributeImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "KIM_ATTRIB_ID", insertable = false, updatable = false)
	protected KimAttribute kimAttribute;
	
	/**
	 * @see org.kuali.rice.kim.bo.types.KimTypeAttribute#getKimAttribute()
	 */
	public KimAttribute getKimAttribute() {
		return kimAttribute;
	}

	/**
	 * @see org.kuali.rice.kim.bo.types.KimTypeAttribute#getKimAttributeId()
	 */
	public String getKimAttributeId() {
		return kimAttributeId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.types.KimTypeAttribute#getKimTypeAttributeId()
	 */
	public String getKimTypeAttributeId() {
		return kimTypeAttributeId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.types.KimTypeAttribute#getKimTypeId()
	 */
	public String getKimTypeId() {
		return kimTypeId;
	}

	public void setKimAttributeId(String kimAttributeId) {
		this.kimAttributeId = kimAttributeId;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
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
		m.put( "kimTypeAttributeId", kimTypeAttributeId );
		m.put( "kimTypeId", kimTypeId );
		m.put( "kimAttributeId", kimAttributeId );
		m.put( "active", active );
		return m;
	}

}
