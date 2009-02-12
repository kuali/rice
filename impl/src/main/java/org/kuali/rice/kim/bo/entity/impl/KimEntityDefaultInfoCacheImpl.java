/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.rice.kim.bo.entity.dto.KimEntityAddressInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityAffiliationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmailInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmploymentInformationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEntityTypeDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityExternalIdentifierInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityPhoneInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Used to store a cache of person information to be used if the user's information disappears from KIM. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KRIM_PRSN_CACHE_T")
public class KimEntityDefaultInfoCacheImpl extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 1L;

	@Transient
	protected Long versionNumber; // prevent JPA from attempting to persist the version number attribute
	
	// principal data
	@Id
	@Column(name="PRNCPL_ID")
	protected String principalId;
	@Column(name="PRNCPL_NM")
	protected String principalName;
	@Column(name="ENTITY_ID")
	protected String entityId;
	@Column(name="ENTITY_TYP_CD")
	protected String entityTypeCode;

	// name data
	@Column(name="FIRST_NM")
	protected String firstName = "";
	@Column(name="MIDDLE_NM")
	protected String middleName = "";
	@Column(name="LAST_NM")
	protected String lastName = "";
	@Column(name="PRSN_NM")
	protected String name = "";
	
	@Column(name="CAMPUS_CD")
	protected String campusCode = "";

	// employment data
	@Column(name="PRMRY_DEPT_CD")
	protected String primaryDepartmentCode = "";
	@Column(name="EMP_ID")
	protected String employeeId = "";
	
	@Column(name="LAST_UPDT_TS")
	protected Timestamp lastUpdateTimestamp;

	/**
	 * 
	 */
	public KimEntityDefaultInfoCacheImpl() {
	}
	
	public KimEntityDefaultInfoCacheImpl( KimEntityDefaultInfo entity ) {
		entityId = entity.getEntityId();
		principalId = entity.getPrincipals().get(0).getPrincipalId();
		principalName = entity.getPrincipals().get(0).getPrincipalName();
		entityTypeCode = entity.getEntityTypes().get(0).getEntityTypeCode();
		firstName = entity.getDefaultName().getFirstName();
		middleName = entity.getDefaultName().getMiddleName();
		lastName = entity.getDefaultName().getLastName();
		name = entity.getDefaultName().getFormattedName();
		campusCode = entity.getDefaultAffiliation().getCampusCode();
		primaryDepartmentCode = entity.getPrimaryEmployment().getPrimaryDepartmentCode();
		employeeId = entity.getPrimaryEmployment().getEmployeeId();
	}

    @SuppressWarnings("unchecked")
	public KimEntityDefaultInfo convertCacheToEntityDefaultInfo() {
		KimEntityDefaultInfo info = new KimEntityDefaultInfo();
		
		// entity info
		info.setEntityId( this.getEntityId() );
		info.setActive( this.isActive() );

		// principal info
		KimPrincipalInfo principalInfo = new KimPrincipalInfo();
		principalInfo.setEntityId(this.getEntityId() );
		principalInfo.setPrincipalId(this.getPrincipalId());
		principalInfo.setPrincipalName(this.getPrincipalName());
		principalInfo.setActive(this.isActive());
		info.setPrincipals( new ArrayList<KimPrincipalInfo>( 1 ) );
		((ArrayList<KimPrincipalInfo>)info.getPrincipals()).add(principalInfo);

		// name info
		KimEntityNameInfo nameInfo = new KimEntityNameInfo();
		nameInfo.setFirstName( this.getFirstName() );
		nameInfo.setLastName( this.getLastName() );
		nameInfo.setMiddleName( this.getMiddleName() );
		info.setDefaultName(nameInfo);

		// entity type information
		ArrayList<KimEntityEntityTypeDefaultInfo> entityTypesInfo = new ArrayList<KimEntityEntityTypeDefaultInfo>( 1 );
		info.setEntityTypes( entityTypesInfo );
		KimEntityEntityTypeDefaultInfo entityTypeInfo = new KimEntityEntityTypeDefaultInfo();
		entityTypeInfo.setEntityTypeCode( this.getEntityTypeCode() );
		entityTypeInfo.setDefaultAddress( new KimEntityAddressInfo() );
		entityTypeInfo.setDefaultEmailAddress( new KimEntityEmailInfo() );
//		((KimEntityEmailInfo)entityTypeInfo.getDefaultEmailAddress()).setEmailAddress(entity.get)
		entityTypeInfo.setDefaultPhoneNumber( new KimEntityPhoneInfo() );
		entityTypesInfo.add(entityTypeInfo);
		info.setEntityTypes(entityTypesInfo);

		// affiliations
		ArrayList<KimEntityAffiliationInfo> affInfo = new ArrayList<KimEntityAffiliationInfo>( 1 );
		info.setAffiliations( affInfo );
		KimEntityAffiliationInfo aff = new KimEntityAffiliationInfo();
		aff.setCampusCode(this.getCampusCode());
		aff.setDefault(true);
		info.setDefaultAffiliation(aff);
		info.setAffiliations(affInfo);

		// employment information
		KimEntityEmploymentInformationInfo empInfo = new KimEntityEmploymentInformationInfo();
		empInfo.setEmployeeId( this.getEmployeeId() );
		empInfo.setPrimary(true);
		empInfo.setPrimaryDepartmentCode(this.getPrimaryDepartmentCode());
		info.setPrimaryEmployment( empInfo );
		
		// external identifiers
		info.setExternalIdentifiers( new ArrayList<KimEntityExternalIdentifierInfo>(0) );
		return info;
    	
    }
	
	
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap<String,Object> m = new LinkedHashMap<String,Object>( 2 );
		m.put( "principalId", principalId );
		m.put( "principalName", principalName );
		return m;
	}


	public String getPrincipalId() {
		return this.principalId;
	}


	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}


	public String getPrincipalName() {
		return this.principalName;
	}


	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}


	public String getEntityId() {
		return this.entityId;
	}


	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}


	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}


	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}


	public String getFirstName() {
		return this.firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getMiddleName() {
		return this.middleName;
	}


	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}


	public String getLastName() {
		return this.lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getName() {
		return this.name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCampusCode() {
		return this.campusCode;
	}


	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}


	public String getPrimaryDepartmentCode() {
		return this.primaryDepartmentCode;
	}


	public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
		this.primaryDepartmentCode = primaryDepartmentCode;
	}


	public String getEmployeeId() {
		return this.employeeId;
	}


	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}


	public boolean isActive() {
		return false;
	}

	public Timestamp getLastUpdateTimestamp() {
		return this.lastUpdateTimestamp;
	}

	// handle automatic updating of the timestamp
	
    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	super.beforeInsert( persistenceBroker );
        lastUpdateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	super.beforeUpdate( persistenceBroker );
        lastUpdateTimestamp = new Timestamp(System.currentTimeMillis());
    }
    
    @PrePersist
    public void beforeInsert() {
    	super.beforeInsert();
        lastUpdateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void beforeUpdate() {
    	super.beforeUpdate();
        lastUpdateTimestamp = new Timestamp(System.currentTimeMillis());
    }
	
}
