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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KRIM_ATTR_DEFN_T")
public class KimAttributeImpl extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="KIM_ATTR_DEFN_ID")
	protected String kimAttributeId;
	@Column(name="NM")
	protected String attributeName;
	@Column(name="LBL")
	protected String attributeLabel;
	@Column(name="ACTV_IND")
	protected boolean active;
	
	public String getKimAttributeId() {
		return kimAttributeId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
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
		m.put( "attributeId", kimAttributeId );
		m.put( "attributeName", attributeName );
		return m;
	}

	public String getAttributeLabel() {
		return this.attributeLabel;
	}

	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}

	public void setKimAttributeId(String kimAttributeId) {
		this.kimAttributeId = kimAttributeId;
	}

}
