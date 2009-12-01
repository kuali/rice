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

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import org.kuali.rice.kim.bo.entity.KimEntityEthnicity;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityEthnicityInfo extends KimInfoBase implements KimEntityEthnicity {

	private static final long serialVersionUID = -5660229079458643653L;

	protected String id = "";
	protected String entityId = "";
	protected String ethnicityCode = "";
	protected String ethnicityCodeUnmasked = "";
	protected String subEthnicityCode = "";
	protected String subEthnicityCodeUnmasked = "";
	
	protected boolean suppressPersonal;

	public KimEntityEthnicityInfo() {
		super();
	}

	public KimEntityEthnicityInfo(KimEntityEthnicity kimEntityEthnicity) {
		this();
		if ( kimEntityEthnicity != null ) {
			id = unNullify(kimEntityEthnicity.getId());
			entityId = unNullify(kimEntityEthnicity.getEntityId());
			ethnicityCode = unNullify(kimEntityEthnicity.getEthnicityCode());
			ethnicityCodeUnmasked = unNullify(kimEntityEthnicity.getEthnicityCodeUnmasked());
			subEthnicityCode = unNullify(kimEntityEthnicity.getSubEthnicityCode());
			subEthnicityCodeUnmasked = unNullify(kimEntityEthnicity.getSubEthnicityCodeUnmasked());
			suppressPersonal = kimEntityEthnicity.isSuppressPersonal();
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return unNullify(this.id);
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return unNullify(this.entityId);
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the ethnicityCode
	 */
	public String getEthnicityCode() {
		return unNullify(this.ethnicityCode);
	}

	/**
	 * @param ethnicityCode the ethnicityCode to set
	 */
	public void setEthnicityCode(String ethnicityCode) {
		this.ethnicityCode = ethnicityCode;
	}

	/**
	 * @return the ethnicityCodeUnmasked
	 */
	public String getEthnicityCodeUnmasked() {
		return unNullify(this.ethnicityCodeUnmasked);
	}

	/**
	 * @param ethnicityCodeUnmasked the ethnicityCodeUnmasked to set
	 */
	public void setEthnicityCodeUnmasked(String ethnicityCodeUnmasked) {
		this.ethnicityCodeUnmasked = ethnicityCodeUnmasked;
	}

	/**
	 * @return the subEthnicityCode
	 */
	public String getSubEthnicityCode() {
		return unNullify(this.subEthnicityCode);
	}

	/**
	 * @param subEthnicityCode the subEthnicityCode to set
	 */
	public void setSubEthnicityCode(String subEthnicityCode) {
		this.subEthnicityCode = subEthnicityCode;
	}

	/**
	 * @return the subEthnicityCodeUnmasked
	 */
	public String getSubEthnicityCodeUnmasked() {
		return unNullify(this.subEthnicityCodeUnmasked);
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
