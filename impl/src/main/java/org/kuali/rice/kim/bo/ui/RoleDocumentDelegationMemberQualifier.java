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
package org.kuali.rice.kim.bo.ui;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;


/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KRIM_PND_DLGN_MBR_ATTR_DATA_T")
public class RoleDocumentDelegationMemberQualifier extends KimDocumentBoBase {
	@Id
	@Column(name="ATTR_DATA_ID")
	private String attrDataId;
	@Column(name="DLGN_MBR_ID")
	private String delegationMemberId;
	@Column(name="KIM_TYP_ID")
	private String kimTypId;
	@Column(name="KIM_ATTR_DEFN_ID")
	private String kimAttrDefnId;
	@Column(name="ATTR_VAL")
	private String attrVal;
	private KimAttributeImpl kimAttribute;

	// a temporary for display that matches definition key
	@Transient
	private String qualifierKey;

	public String getAttrDataId() {
		return attrDataId;
	}

	public void setAttrDataId(String attrDataId) {
		this.attrDataId = attrDataId;
	}

	public String getDelegationMemberId() {
		return delegationMemberId;
	}

	public void setDelegationMemberId(String delegationMemberId) {
		this.delegationMemberId = delegationMemberId;
	}

	public String getKimTypId() {
		return kimTypId;
	}

	public void setKimTypId(String kimTypId) {
		this.kimTypId = kimTypId;
	}

	public String getKimAttrDefnId() {
		return kimAttrDefnId;
	}

	public void setKimAttrDefnId(String kimAttrDefnId) {
		this.kimAttrDefnId = kimAttrDefnId;
	}

	public String getAttrVal() {
		return attrVal;
	}

	public void setAttrVal(String attrVal) {
		this.attrVal = attrVal;
	}


	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap hashMap = new LinkedHashMap();
		hashMap.put("attrDataId", getAttrDataId());
		hashMap.put("delegationMemberId", getDelegationMemberId());
		hashMap.put("kimTypId", getKimTypId());
		hashMap.put("kimAttrDefnId", getKimAttrDefnId());
		hashMap.put("attrVal", getAttrVal());
		return hashMap;
	}

	public String getQualifierKey() {
		return this.qualifierKey;
	}

	public void setQualifierKey(String qualifierKey) {
		this.qualifierKey = qualifierKey;
	}

	/**
	 * @return the kimAttribute
	 */
	public KimAttributeImpl getKimAttribute() {
		return this.kimAttribute;
	}

	/**
	 * @param kimAttribute the kimAttribute to set
	 */
	public void setKimAttribute(KimAttributeImpl kimAttribute) {
		this.kimAttribute = kimAttribute;
	}

}
