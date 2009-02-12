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
package org.kuali.rice.kim.bo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.EntityAddress;
import org.kuali.rice.kim.bo.entity.EntityAffiliation;
import org.kuali.rice.kim.bo.entity.EntityEmail;
import org.kuali.rice.kim.bo.entity.EntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.EntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.EntityName;
import org.kuali.rice.kim.bo.entity.EntityPhone;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEntityTypeDefaultInfo;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonImpl extends TransientBusinessObjectBase implements Person {

	private static final long serialVersionUID = 1L;
	
	protected static PersonService<Person> personService;
	protected static IdentityManagementService identityManagementService;

	private String lookupRoleNamespaceCode;
	private String lookupRoleName;
	
	// principal data
	protected String principalId;
	protected String principalName;
	protected String entityId;
	protected String entityTypeCode;
	// name data
	protected String firstName = "";
	protected String middleName = "";
	protected String lastName = "";
	protected String name = "";
	// address data
	protected String addressLine1 = "";
	protected String addressLine2 = "";
	protected String addressLine3 = "";
	protected String addressCityName = "";
	protected String addressStateCode = "";
	protected String addressPostalCode = "";
	protected String addressCountryCode = "";
	// email data
	protected String emailAddress = "";
	// phone data
	protected String phoneNumber = "";
	// affiliation data
	protected List<? extends EntityAffiliation> affiliations;
	
	protected String campusCode = "";
	// external identifier data
	protected Map<String,String> externalIdentifiers = null;
	// employment data
	protected String employeeStatusCode = "";
	protected String employeeTypeCode = "";
	protected String primaryDepartmentCode = "";
	protected String employeeId = "";
	
	protected KualiDecimal baseSalaryAmount = KualiDecimal.ZERO;
	protected boolean active = true;
	
	public PersonImpl() {}
	
	public PersonImpl( KimPrincipal principal, String personEntityTypeCode ) {
		this( principal, null, personEntityTypeCode );
	}

	public PersonImpl( KimPrincipal principal, KimEntityDefaultInfo entity, String personEntityTypeCode ) {
		setPrincipal( principal, entity, personEntityTypeCode );
	}
	
	public PersonImpl( String principalId, String personEntityTypeCode ) {
		this( getIdentityManagementService().getPrincipal(principalId), personEntityTypeCode );
	}
	
	public PersonImpl( PersonCacheImpl p ) {
		entityId = p.getEntityId();
		principalId = p.getPrincipalId();
		principalName = p.getPrincipalName();
		entityTypeCode = p.getEntityTypeCode();
		firstName = p.getFirstName();
		middleName = p.getMiddleName();
		lastName = p.getLastName();
		name = p.getName();
		campusCode = p.getCampusCode();
		primaryDepartmentCode = p.getPrimaryDepartmentCode();
		employeeId = p.getEmployeeId();
		affiliations = new ArrayList<EntityAffiliation>( 0 );
		externalIdentifiers = new HashMap<String,String>( 0 );
	}

	/**
	 * Sets the principal object and populates the person object from that. 
	 */
	public void setPrincipal(KimPrincipal principal, KimEntityDefaultInfo entity, String personEntityTypeCode) {
		populatePrincipalInfo( principal );
		if ( entity == null ) {
			entity = getIdentityManagementService().getEntityDefaultInfo( principal.getEntityId() );
		}
		populateEntityInfo( entity, principal, personEntityTypeCode );
	}

	
	protected void populatePrincipalInfo( KimPrincipal principal ) {
		entityId = principal.getEntityId();
		principalId = principal.getPrincipalId();
		principalName = principal.getPrincipalName();
		active = principal.isActive();
	}
	
	protected void populateEntityInfo( KimEntityDefaultInfo entity, KimPrincipal principal, String personEntityTypeCode ) {
		KimEntityEntityTypeDefaultInfo entityEntityType = entity.getEntityType( personEntityTypeCode );  
		entityTypeCode = personEntityTypeCode;
		populateNameInfo( personEntityTypeCode, entity, principal );
		populateAddressInfo( entityEntityType );
		populateEmailInfo( entityEntityType );
		populatePhoneInfo( entityEntityType );
		populateAffiliationInfo( entity );
		populateEmploymentInfo( entity );
		populateExternalIdentifiers( entity );
	}
	
	protected void populateNameInfo( String entityTypeCode, KimEntityDefaultInfo entity, KimPrincipal principal ) {
		EntityName entityName = entity.getDefaultName();
		if ( entityName != null ) {
			firstName = unNullify( entityName.getFirstName() );
			middleName = unNullify( entityName.getMiddleName() );
			lastName = unNullify( entityName.getLastName() );
			if ( entityTypeCode.equals( "SYSTEM" ) ) {
				name = principal.getPrincipalName().toUpperCase();
			} else {
				name = unNullify( entityName.getFormattedName() );
			}
		} else {
			firstName = "";
			middleName = "";
			if ( entityTypeCode.equals( "SYSTEM" ) ) {
				name = principal.getPrincipalName().toUpperCase();
				lastName = principal.getPrincipalName().toUpperCase();
			} else {
				name = "";
				lastName = "";
			}
		}
	}
	
	protected void populateAddressInfo( KimEntityEntityTypeDefaultInfo entityEntityType ) {
		EntityAddress defaultAddress = entityEntityType.getDefaultAddress();
		if ( defaultAddress != null ) {			
			addressLine1 = unNullify( defaultAddress.getLine1() );
			addressLine2 = unNullify( defaultAddress.getLine2() );
			addressLine3 = unNullify( defaultAddress.getLine3() );
			addressCityName = unNullify( defaultAddress.getCityName() );
			addressStateCode = unNullify( defaultAddress.getStateCode() );
			addressPostalCode = unNullify( defaultAddress.getPostalCode() );
			addressCountryCode = unNullify( defaultAddress.getCountryCode() );
		} else {
			addressLine1 = "";
			addressLine2 = "";
			addressLine3 = "";
			addressCityName = "";
			addressStateCode = "";
			addressPostalCode = "";
			addressCountryCode = "";
		}
	}
	
	protected void populateEmailInfo( KimEntityEntityTypeDefaultInfo entityEntityType ) {
		EntityEmail entityEmail = entityEntityType.getDefaultEmailAddress();
		if ( entityEmail != null ) {
			emailAddress = unNullify( entityEmail.getEmailAddress() );
		} else {
			emailAddress = "";
		}
	}
	
	protected void populatePhoneInfo( KimEntityEntityTypeDefaultInfo entityEntityType ) {
		EntityPhone entityPhone = entityEntityType.getDefaultPhoneNumber();
		if ( entityPhone != null ) {
			phoneNumber = unNullify( entityPhone.getFormattedPhoneNumber() );
		} else {
			phoneNumber = "";
		}
	}
	
	protected void populateAffiliationInfo( KimEntityDefaultInfo entity ) {
		affiliations = entity.getAffiliations();
		EntityAffiliation defaultAffiliation = entity.getDefaultAffiliation();
		if ( defaultAffiliation != null  ) {
			campusCode = unNullify( defaultAffiliation.getCampusCode() );
		} else {
			campusCode = "";
		}
	}
	
	protected void populateEmploymentInfo( KimEntityDefaultInfo entity ) {
		EntityEmploymentInformation employmentInformation = entity.getPrimaryEmployment();
		if ( employmentInformation != null ) {
			employeeStatusCode = unNullify( employmentInformation.getEmployeeStatusCode() );
			employeeTypeCode = unNullify( employmentInformation.getEmployeeTypeCode() );
			primaryDepartmentCode = unNullify( employmentInformation.getPrimaryDepartmentCode() );
			employeeId = unNullify( employmentInformation.getEmployeeId() );
			if ( employmentInformation.getBaseSalaryAmount() != null ) {
				baseSalaryAmount = employmentInformation.getBaseSalaryAmount();
			} else {
				baseSalaryAmount = KualiDecimal.ZERO;
			}
		} else {
			employeeStatusCode = "";
			employeeTypeCode = "";
			primaryDepartmentCode = "";
			employeeId = "";
			baseSalaryAmount = KualiDecimal.ZERO;
		}
	}
	
	protected void populateExternalIdentifiers( KimEntityDefaultInfo entity ) {
		List<? extends EntityExternalIdentifier> externalIds = entity.getExternalIdentifiers();
		externalIdentifiers = new HashMap<String,String>( externalIds.size() );
		for ( EntityExternalIdentifier eei : externalIds ) {
			externalIdentifiers.put( eei.getExternalIdentifierTypeCode(), eei.getExternalId() );
		}
	}
	
	/** So users of this class don't need to program around nulls. */
	private String unNullify( String str ) {
		if ( str == null ) {
			return "";
		}
		return str;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getEntityId()
	 */
	public String getEntityId() {
		return entityId;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getPrincipalId()
	 */
	public String getPrincipalId() {
		return principalId;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.Person#getPrincipalName()
	 */
	public String getPrincipalName() {
		return principalName;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getFirstName()
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.Person#getMiddleName()
	 */
	public String getMiddleName() {
		return middleName;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getLastName()
	 */
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getPhoneNumber()
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.Person#getEmailAddress()
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public List<? extends EntityAffiliation> getAffiliations() {
		return affiliations;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.Person#hasAffiliationOfType(java.lang.String)
	 */
	public boolean hasAffiliationOfType(String affiliationTypeCode) {
		return getCampusCodesForAffiliationOfType(affiliationTypeCode).size() > 0;
	}
	
	
	public List<String> getCampusCodesForAffiliationOfType(String affiliationTypeCode) {
		ArrayList<String> campusCodes = new ArrayList<String>( 3 );
		if ( affiliationTypeCode == null ) {
			return campusCodes;
		}
		for ( EntityAffiliation a : getAffiliations() ) {
			if ( a.getAffiliationTypeCode().equals(affiliationTypeCode)  ) {
				campusCodes.add( a.getCampusCode() );
			}
		}
		return campusCodes;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getExternalId(java.lang.String)
	 */
	public String getExternalId(String externalIdentifierTypeCode) {
		return externalIdentifiers.get( externalIdentifierTypeCode );
	}
	
	/**
	 * Pulls the campus code from the default affiliation for the entity.
	 * Returns null if no default affiliation is set.
	 * @see org.kuali.rice.kim.bo.Person#getCampusCode()
	 */
	public String getCampusCode() {
		return campusCode;
	}
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put("principalId", getPrincipalId());
		m.put("principalName", getPrincipalName());
		m.put("name", getName());
		m.put("emailAddress", getEmailAddress());
		m.put("campusCode", getCampusCode());
		return m;
	}

	/**
	 * @return the personService
	 */
	public static PersonService<Person> getPersonService() {
		if ( personService == null ) {
			personService = KIMServiceLocator.getPersonService();
		}
		return personService;
	}

	/**
	 * @return the identityManagementService
	 */
	public static IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}

	/**
	 * @see org.kuali.rice.kim.bo.Person#getExternalIdentifiers()
	 */
	public Map<String,String> getExternalIdentifiers() {
		return externalIdentifiers;
	}

	public String getAddressLine1() {
		return this.addressLine1;
	}

	public String getAddressLine2() {
		return this.addressLine2;
	}

	public String getAddressLine3() {
		return this.addressLine3;
	}

	public String getAddressCityName() {
		return this.addressCityName;
	}

	public String getAddressStateCode() {
		return this.addressStateCode;
	}

	public String getAddressPostalCode() {
		return this.addressPostalCode;
	}

	public String getAddressCountryCode() {
		return this.addressCountryCode;
	}

	public String getEmployeeStatusCode() {
		return this.employeeStatusCode;
	}

	public String getEmployeeTypeCode() {
		return this.employeeTypeCode;
	}

	public KualiDecimal getBaseSalaryAmount() {
		return this.baseSalaryAmount;
	}

	public String getEmployeeId() {
		return this.employeeId;
	}

	public String getPrimaryDepartmentCode() {
		return this.primaryDepartmentCode;
	}

	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the lookupRoleNamespaceCode
	 */
	public String getLookupRoleNamespaceCode() {
		return this.lookupRoleNamespaceCode;
	}

	/**
	 * @param lookupRoleNamespaceCode the lookupRoleNamespaceCode to set
	 */
	public void setLookupRoleNamespaceCode(String lookupRoleNamespaceCode) {
		this.lookupRoleNamespaceCode = lookupRoleNamespaceCode;
	}

	/**
	 * @return the lookupRoleName
	 */
	public String getLookupRoleName() {
		return this.lookupRoleName;
	}

	/**
	 * @param lookupRoleName the lookupRoleName to set
	 */
	public void setLookupRoleName(String lookupRoleName) {
		this.lookupRoleName = lookupRoleName;
	}
	
}
