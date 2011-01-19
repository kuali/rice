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
package org.kuali.rice.kim.bo.entity.impl;

import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.reference.AffiliationType;
import org.kuali.rice.kim.bo.reference.impl.AffiliationTypeImpl;

import javax.persistence.*;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_AFLTN_T")
public class KimEntityAffiliationImpl extends KimDefaultableEntityDataBase implements KimEntityAffiliation {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_AFLTN_ID")
	protected String entityAffiliationId;

	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Column(name = "AFLTN_TYP_CD")
	protected String affiliationTypeCode;

	@Column(name = "CAMPUS_CD")
	protected String campusCode;

	@ManyToOne(targetEntity=AffiliationTypeImpl.class, fetch=FetchType.EAGER, cascade = {})
	@JoinColumn(name = "AFLTN_TYP_CD", insertable = false, updatable = false)
	protected AffiliationType affiliationType;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ENTITY_ID",insertable=false, updatable=false)
	protected KimEntityImpl kimEntity;

	// Waiting until we pull in from KFS
	// protected Campus campus;


	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#getAffiliationTypeCode()
	 */
	public String getAffiliationTypeCode() {
		return affiliationTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#getCampusCode()
	 */
	public String getCampusCode() {
		return campusCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#getEntityAffiliationId()
	 */
	public String getEntityAffiliationId() {
		return entityAffiliationId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#setAffiliationTypeCode(java.lang.String)
	 */
	public void setAffiliationTypeCode(String affiliationTypeCode) {
		this.affiliationTypeCode = affiliationTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#setCampusCode(java.lang.String)
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public AffiliationType getAffiliationType() {
		return this.affiliationType;
	}

	public void setAffiliationType(AffiliationType affiliationType) {
		this.affiliationType = affiliationType;
	}

	public void setEntityAffiliationId(String entityAffiliationId) {
		this.entityAffiliationId = entityAffiliationId;
	}

	public KimEntityImpl getKimEntity() {
		return this.kimEntity;
	}

	public void setKimEntity(KimEntityImpl kimEntity) {
		this.kimEntity = kimEntity;
	}
}
