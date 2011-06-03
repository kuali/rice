/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import org.kuali.rice.kim.bo.entity.KimEntityEthnicity;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityEthnicityInfo extends KimInfoBase implements KimEntityEthnicity {

	private static final long serialVersionUID = -5660229079458643653L;

	protected String id;
	protected String entityId;
	protected String ethnicityCode;
	protected String ethnicityCodeUnmasked;
	protected String subEthnicityCode;
	protected String subEthnicityCodeUnmasked;
	
	protected boolean suppressPersonal;

	public KimEntityEthnicityInfo() {
		super();
	}

	public KimEntityEthnicityInfo(KimEntityEthnicity kimEntityEthnicity) {
		this();
		if ( kimEntityEthnicity != null ) {
			id = kimEntityEthnicity.getId();
			entityId = kimEntityEthnicity.getEntityId();
			ethnicityCode = kimEntityEthnicity.getEthnicityCode();
			ethnicityCodeUnmasked = kimEntityEthnicity.getEthnicityCodeUnmasked();
			subEthnicityCode = kimEntityEthnicity.getSubEthnicityCode();
			subEthnicityCodeUnmasked = kimEntityEthnicity.getSubEthnicityCodeUnmasked();
			suppressPersonal = kimEntityEthnicity.isSuppressPersonal();
		}
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getEntityId()
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getEthnicityCode()
	 */
	public String getEthnicityCode() {
		return ethnicityCode;
	}

	/**
	 * @param ethnicityCode the ethnicityCode to set
	 */
	public void setEthnicityCode(String ethnicityCode) {
		this.ethnicityCode = ethnicityCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getEthnicityCodeUnmasked()
	 */
	public String getEthnicityCodeUnmasked() {
		return ethnicityCodeUnmasked;
	}

	/**
	 * @param ethnicityCodeUnmasked the ethnicityCodeUnmasked to set
	 */
	public void setEthnicityCodeUnmasked(String ethnicityCodeUnmasked) {
		this.ethnicityCodeUnmasked = ethnicityCodeUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getSubEthnicityCode()
	 */
	public String getSubEthnicityCode() {
		return subEthnicityCode;
	}

	/**
	 * @param subEthnicityCode the subEthnicityCode to set
	 */
	public void setSubEthnicityCode(String subEthnicityCode) {
		this.subEthnicityCode = subEthnicityCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getSubEthnicityCodeUnmasked()
	 */
	public String getSubEthnicityCodeUnmasked() {
		return subEthnicityCodeUnmasked;
	}

	/**
	 * @param subEthnicityCodeUnmasked the subEthnicityCodeUnmasked to set
	 */
	public void setSubEthnicityCodeUnmasked(String subEthnicityCodeUnmasked) {
		this.subEthnicityCodeUnmasked = subEthnicityCodeUnmasked;
	}

	/**
	 * @return the suppressPersonal
	 */
	public boolean isSuppressPersonal() {
		return this.suppressPersonal;
	}

	/**
	 * @param suppressPersonal the suppressPersonal to set
	 */
	public void setSuppressPersonal(boolean suppressPersonal) {
		this.suppressPersonal = suppressPersonal;
	}

}
