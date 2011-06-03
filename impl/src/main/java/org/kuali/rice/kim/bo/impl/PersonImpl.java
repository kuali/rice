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
package org.kuali.rice.kim.bo.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.kim.api.entity.address.EntityAddressContract;
import org.kuali.rice.kim.api.entity.email.EntityEmailContract;
import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.api.entity.phone.EntityPhoneContract;
import org.kuali.rice.kim.api.entity.type.EntityTypeDataDefault;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.impl.KimEntityDefaultInfoCacheImpl;
import org.kuali.rice.kim.bo.reference.impl.EmploymentStatusImpl;
import org.kuali.rice.kim.bo.reference.impl.EmploymentTypeImpl;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.kuali.rice.shareddata.api.campus.Campus;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PersonImpl extends TransientBusinessObjectBase implements Person {

	private static final long serialVersionUID = 1L;
	
	protected static PersonService personService;
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
	// privacy preferences data
	protected boolean suppressName = false;
	protected boolean suppressAddress = false;
	protected boolean suppressPhone = false;
	protected boolean suppressPersonal = false;
	protected boolean suppressEmail = false;
	// affiliation data
	protected List<? extends KimEntityAffiliation> affiliations;
	
	protected String campusCode = "";
	//protected Campus campus;
	// external identifier data
	protected Map<String,String> externalIdentifiers = null;
	// employment data
	protected String employeeStatusCode = "";
	protected EmploymentStatusImpl employeeStatus;
	protected String employeeTypeCode = "";
	protected EmploymentTypeImpl employeeType;
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
	
	public PersonImpl( KimEntityDefaultInfoCacheImpl p ) {
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
		affiliations = new ArrayList<KimEntityAffiliation>( 0 );
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
		if(entity!=null){
		    populatePrivacyInfo (entity );
			EntityTypeDataDefault entityTypeDataDefault = entity.getEntityType( personEntityTypeCode );
			entityTypeCode = personEntityTypeCode;
			populateNameInfo( personEntityTypeCode, entity, principal );
			populateAddressInfo( entityTypeDataDefault );
			populateEmailInfo( entityTypeDataDefault );
			populatePhoneInfo( entityTypeDataDefault );
			populateAffiliationInfo( entity );
			populateEmploymentInfo( entity );
			populateExternalIdentifiers( entity );
		}
	}
	
	protected void populateNameInfo( String entityTypeCode, KimEntityDefaultInfo entity, KimPrincipal principal ) {
		if(entity!=null){
			KimEntityName entityName = entity.getDefaultName();
			if ( entityName != null ) {
				firstName = unNullify( entityName.getFirstName());
				middleName = unNullify( entityName.getMiddleName() );
				lastName = unNullify( entityName.getLastName() );
				if ( entityTypeCode.equals( KimConstants.EntityTypes.SYSTEM ) ) {
					name = principal.getPrincipalName().toUpperCase();
				} else {
					name = unNullify( entityName.getFormattedName() );
					if(name.equals("") || name == null){
						name = lastName + ", " + firstName;					
					}
				}
			} else {
				firstName = "";
				middleName = "";
				if ( entityTypeCode.equals( KimConstants.EntityTypes.SYSTEM ) ) {
					name = principal.getPrincipalName().toUpperCase();
					lastName = principal.getPrincipalName().toUpperCase();
				} else {
					name = "";
					lastName = "";
				}
			}
		}
	}
	
	protected void populatePrivacyInfo (KimEntityDefaultInfo entity) {
	    if(entity!=null) {
    	    if (entity.getPrivacyPreferences() != null) {
        	    suppressName = entity.getPrivacyPreferences().isSuppressName();
        	    suppressAddress = entity.getPrivacyPreferences().isSuppressAddress();
        	    suppressPhone = entity.getPrivacyPreferences().isSuppressPhone();
        	    suppressPersonal = entity.getPrivacyPreferences().isSuppressPersonal();
        	    suppressEmail = entity.getPrivacyPreferences().isSuppressEmail();
    	    }
	    }
	}
	
	protected void populateAddressInfo( EntityTypeDataDefault entityTypeData ) {
		if(entityTypeData!=null){
			EntityAddressContract defaultAddress = entityTypeData.getDefaultAddress();
			if ( defaultAddress != null ) {			
				addressLine1 = unNullify( defaultAddress.getLine1Unmasked() );
				addressLine2 = unNullify( defaultAddress.getLine2Unmasked() );
				addressLine3 = unNullify( defaultAddress.getLine3Unmasked() );
				addressCityName = unNullify( defaultAddress.getCityNameUnmasked() );
				addressStateCode = unNullify( defaultAddress.getStateCodeUnmasked() );
				addressPostalCode = unNullify( defaultAddress.getPostalCodeUnmasked() );
				addressCountryCode = unNullify( defaultAddress.getCountryCodeUnmasked() );
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
	}
	
	protected void populateEmailInfo( EntityTypeDataDefault entityTypeData ) {
		if(entityTypeData!=null){
			EntityEmailContract entityEmail = entityTypeData.getDefaultEmailAddress();
			if ( entityEmail != null ) {
				emailAddress = unNullify( entityEmail.getEmailAddressUnmasked() );
			} else {
				emailAddress = "";
			}
		}
	}
	
	protected void populatePhoneInfo( EntityTypeDataDefault entityTypeData ) {
		if(entityTypeData!=null){
			EntityPhoneContract entityPhone = entityTypeData.getDefaultPhoneNumber();
			if ( entityPhone != null ) {
				phoneNumber = unNullify( entityPhone.getFormattedPhoneNumberUnmasked() );
			} else {
				phoneNumber = "";
			}
		}
	}
	
	protected void populateAffiliationInfo( KimEntityDefaultInfo entity ) {
		if(entity!=null){
			affiliations = entity.getAffiliations();
			KimEntityAffiliation defaultAffiliation = entity.getDefaultAffiliation();
			if ( defaultAffiliation != null  ) {
				campusCode = unNullify( defaultAffiliation.getCampusCode() );
			} else {
				campusCode = "";
			}
		}
	}
	
	protected void populateEmploymentInfo( KimEntityDefaultInfo entity ) {
		if(entity!=null){
			KimEntityEmploymentInformation employmentInformation = entity.getPrimaryEmployment();
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
	}
	
	protected void populateExternalIdentifiers( KimEntityDefaultInfo entity ) {
		if(entity!=null){
			List<? extends KimEntityExternalIdentifier> externalIds = entity.getExternalIdentifiers();
			externalIdentifiers = new HashMap<String,String>( externalIds.size() );
			for ( KimEntityExternalIdentifier eei : externalIds ) {
				externalIdentifiers.put( eei.getExternalIdentifierTypeCode(), eei.getExternalId() );
			}
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
	    if (KimCommonUtilsInternal.isSuppressName(getEntityId())){
	        return KimConstants.RESTRICTED_DATA_MASK;
	    }
		return firstName;
	}
	
	/**
     * @see org.kuali.rice.kim.bo.Person#getFirstNameUnmasked()
     */
    public String getFirstNameUnmasked() {
        return firstName;
    }

	/**
	 * @see org.kuali.rice.kim.bo.Person#getMiddleName()
	 */
	public String getMiddleName() {
	    if (KimCommonUtilsInternal.isSuppressName(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return middleName;
	}
	
	/**
     * @see org.kuali.rice.kim.bo.Person#getMiddleNameUnmasked()
     */
	public String getMiddleNameUnmasked() {
	    return middleName;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getLastName()
	 */
	public String getLastName() {
	    if (KimCommonUtilsInternal.isSuppressName(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return lastName;
	}
	
	/**
     * @see org.kuali.rice.kim.bo.Person#getLastNameUnmasked()
     */
    public String getLastNameUnmasked() {
        return lastName;
    }
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getName()
	 */
	public String getName() {
        if (StringUtils.isNotBlank(getEntityId()) && KimCommonUtilsInternal.isSuppressName(getEntityId())) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
        return name;
    }
	
	public String getNameUnmasked() {
	    return this.name;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.Person#getPhoneNumber()
	 */
	public String getPhoneNumber() {
	    if (KimCommonUtilsInternal.isSuppressPhone(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return phoneNumber;
	}
	
	   /**
     * @see org.kuali.rice.kim.bo.Person#getPhoneNumberUnmasked()
     */
    public String getPhoneNumberUnmasked() {
        return phoneNumber;
    }

	/**
	 * @see org.kuali.rice.kim.bo.Person#getEmailAddress()
	 */
	public String getEmailAddress() {
	    if (KimCommonUtilsInternal.isSuppressEmail(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return emailAddress;
	}
	
	public String getEmailAddressUnmasked() {
	    return emailAddress;
	}
	
	public List<? extends KimEntityAffiliation> getAffiliations() {
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
		for ( KimEntityAffiliation a : getAffiliations() ) {
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
	 * @return the personService
	 */
	@SuppressWarnings("unchecked")
	public static PersonService getPersonService() {
		if ( personService == null ) {
			personService = KimApiServiceLocator.getPersonService();
		}
		return personService;
	}

	/**
	 * @return the identityManagementService
	 */
	public static IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KimApiServiceLocator.getIdentityManagementService();
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
	    if (KimCommonUtilsInternal.isSuppressAddress(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.addressLine1;
	}
	
	public String getAddressLine1Unmasked() {
	    return this.addressLine1;
	}

	public String getAddressLine2() {
	    if (KimCommonUtilsInternal.isSuppressAddress(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.addressLine2;
	}
	
	public String getAddressLine2Unmasked() {
        return this.addressLine2;
    }

	public String getAddressLine3() {
	    if (KimCommonUtilsInternal.isSuppressAddress(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.addressLine3;
	}
	
	public String getAddressLine3Unmasked() {
        return this.addressLine3;
    }

	public String getAddressCityName() {
	    if (KimCommonUtilsInternal.isSuppressAddress(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.addressCityName;
	}
	
	public String getAddressCityNameUnmasked() {
        return this.addressCityName;
    }

	public String getAddressStateCode() {
	    if (KimCommonUtilsInternal.isSuppressAddress(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.addressStateCode;
	}
	
	public String getAddressStateCodeUnmasked() {
        return this.addressStateCode;
    }

	public String getAddressPostalCode() {
	    if (KimCommonUtilsInternal.isSuppressAddress(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.addressPostalCode;
	}
	
	public String getAddressPostalCodeUnmasked() {
        return this.addressPostalCode;
    }

	public String getAddressCountryCode() {
	    if (KimCommonUtilsInternal.isSuppressAddress(getEntityId())){
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.addressCountryCode;
	}
	
	public String getAddressCountryCodeUnmasked() {
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

	/**
	 * @param principalName the principalName to set
	 */
	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	//public Campus getCampus() {
	//	return this.campus;
	//}

	public EmploymentStatusImpl getEmployeeStatus() {
		return this.employeeStatus;
	}

	public EmploymentTypeImpl getEmployeeType() {
		return this.employeeType;
	}
}
