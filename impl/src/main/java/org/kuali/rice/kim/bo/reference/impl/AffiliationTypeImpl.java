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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.reference.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.reference.AffiliationType;
import org.kuali.rice.kim.bo.reference.dto.AffiliationTypeInfo;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_AFLTN_TYP_T")
@AttributeOverrides({
	@AttributeOverride(name="code",column=@Column(name="AFLTN_TYP_CD")),
	@AttributeOverride(name="name",column=@Column(name="NM"))
})
public class AffiliationTypeImpl extends KimCodeBase implements AffiliationType {

	private static final long serialVersionUID = 1L;
    @Type(type="yes_no")
    @Column(name="EMP_AFLTN_TYP_IND")
    protected boolean employmentAffiliationType;

    @Transient
    private String affiliationTypeCode;
	/**
	 * @see org.kuali.rice.kim.bo.reference.AffiliationType#getAffiliationTypeCode()
	 */
	public String getAffiliationTypeCode() {
		return getCode();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.AffiliationType#getAffiliationTypeName()
	 */
	public String getAffiliationTypeName() {
		return getName();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.AffiliationType#setAffiliationTypeCode(java.lang.String)
	 */
	public void setAffiliationTypeCode(String affiliationTypeCode) {
		setCode(affiliationTypeCode);
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.AffiliationType#setAffiliationTypeName(java.lang.String)
	 */
	public void setAffiliationTypeName(String affiliationTypeName) {
		setName(affiliationTypeName);
	}

	public boolean isEmploymentAffiliationType() {
		if(ObjectUtils.isNull(this.employmentAffiliationType))
			return false;
		return this.employmentAffiliationType;
	}

	public void setEmploymentAffiliationType(boolean employmentAffiliationType) {
		this.employmentAffiliationType = employmentAffiliationType;
	}
	
	public AffiliationTypeInfo toInfo() {
		AffiliationTypeInfo info = new AffiliationTypeInfo();
		info.setCode(code);
		info.setName(name);
		info.setDisplaySortCode(displaySortCode);
		info.setActive(active);
		return info;
	}
	
}
