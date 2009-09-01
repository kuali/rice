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
package org.kuali.rice.kim.bo.entity.dto;

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import org.kuali.rice.kim.bo.entity.KimEntityEthnicity;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityEthnicityInfo extends KimInfoBase implements KimEntityEthnicity {

	private static final long serialVersionUID = -5660229079458643653L;

	protected String entityId = "";
	protected String ethnicityCode = "";
	protected String subEthnicityCode = "";
	
	protected boolean suppressPersonal;

	public KimEntityEthnicityInfo() {
		super();
	}

	public KimEntityEthnicityInfo(KimEntityEthnicity kimEntityEthnicity) {
		this();
		if ( kimEntityEthnicity != null ) {
			entityId = unNullify(kimEntityEthnicity.getEntityId());
			ethnicityCode = unNullify(kimEntityEthnicity.getEthnicityCode());
			subEthnicityCode = unNullify(kimEntityEthnicity.getSubEthnicityCode());
			suppressPersonal = kimEntityEthnicity.isSuppressPersonal();
		}
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getId()
	 */
	public String getEntityId() {
		return entityId;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getEthnicityCode()
	 */
	public String getEthnicityCode() {
		return ethnicityCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getSubEthnicityCode()
	 */
	public String getSubEthnicityCode() {
		return subEthnicityCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#isSuppressPersonal()
	 */
	public boolean isSuppressPersonal() {
		return suppressPersonal;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setEthnicityCode(String ethnicityCode) {
		this.ethnicityCode = ethnicityCode;
	}

	public void setSubEthnicityCode(String subEthnicityCode) {
		this.subEthnicityCode = subEthnicityCode;
	}

	public void setSuppressPersonal(boolean suppressPersonal) {
		this.suppressPersonal = suppressPersonal;
	}

}
