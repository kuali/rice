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
package org.kuali.rice.kim.dao.impl;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.EntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.impl.PersonCacheImpl;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.dao.PersonDao;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonDaoOjb<T extends PersonImpl> extends PlatformAwareDaoBaseOjb implements PersonDao<T> {

	protected static final String ENTITY_EXT_ID_PROPERTY_PREFIX = "externalIdentifiers.";
	protected static final String ENTITY_AFFILIATION_PROPERTY_PREFIX = "affiliations.";
	protected static final String ENTITY_TYPE_PROPERTY_PREFIX = "entityTypes.";
	protected static final String ENTITY_EMAIL_PROPERTY_PREFIX = "entityTypes.emailAddresses.";
	protected static final String ENTITY_PHONE_PROPERTY_PREFIX = "entityTypes.phoneNumbers.";
	protected static final String ENTITY_ADDRESS_PROPERTY_PREFIX = "entityTypes.addresses.";
	protected static final String ENTITY_NAME_PROPERTY_PREFIX = "names.";
	protected static final String PRINCIPAL_PROPERTY_PREFIX = "principals.";
	protected static final String ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX = "employmentInformation.";

	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersonDaoOjb.class);

    protected Class<? extends T> personImplementationClass;
	protected List<String> personEntityTypeCodes = new ArrayList<String>( 4 );
	// String that can be passed to the lookup framework to create an type = X OR type = Y criteria
	private String personEntityTypeLookupCriteria = null;
    
	protected Map<String,String> baseLookupCriteria = new HashMap<String,String>();
	protected Map<String,String> criteriaConversion = new HashMap<String,String>();
	protected ArrayList<String> personCachePropertyNames = new ArrayList<String>();
	{
		// init the criteria which will need to be applied to every lookup against
		// the entity data tables
		baseLookupCriteria.put( KIMPropertyConstants.Person.ACTIVE, "Y" );
		baseLookupCriteria.put( ENTITY_TYPE_PROPERTY_PREFIX + KNSPropertyConstants.ACTIVE, "Y" );
		
		// create the field mappings between the Person object and the KimEntity object
		criteriaConversion.put( KIMPropertyConstants.Person.ENTITY_ID, KIMPropertyConstants.Person.ENTITY_ID );
		criteriaConversion.put( KIMPropertyConstants.Person.ACTIVE, PRINCIPAL_PROPERTY_PREFIX + KNSPropertyConstants.ACTIVE );
		criteriaConversion.put( KIMPropertyConstants.Person.PRINCIPAL_ID, PRINCIPAL_PROPERTY_PREFIX + KIMPropertyConstants.Person.PRINCIPAL_ID );
		criteriaConversion.put( KIMPropertyConstants.Person.PRINCIPAL_NAME, PRINCIPAL_PROPERTY_PREFIX + KIMPropertyConstants.Person.PRINCIPAL_NAME );
		criteriaConversion.put( KIMPropertyConstants.Person.FIRST_NAME, "names.firstName" );
		criteriaConversion.put( KIMPropertyConstants.Person.LAST_NAME, "names.lastName" );
		criteriaConversion.put( KIMPropertyConstants.Person.MIDDLE_NAME, "names.middleName" );
		criteriaConversion.put( KIMPropertyConstants.Person.EMAIL_ADDRESS, "entityTypes.emailAddresses.emailAddress" );
		criteriaConversion.put( KIMPropertyConstants.Person.PHONE_NUMBER, "entityTypes.phoneNumbers.phoneNumber" );
		criteriaConversion.put( KIMPropertyConstants.Person.ADDRESS_LINE_1, "entityTypes.addresses.line1" );
		criteriaConversion.put( KIMPropertyConstants.Person.ADDRESS_LINE_2, "entityTypes.addresses.line2" );
		criteriaConversion.put( KIMPropertyConstants.Person.ADDRESS_LINE_3, "entityTypes.addresses.line3" );
		criteriaConversion.put( KIMPropertyConstants.Person.CITY_NAME, "entityTypes.addresses.cityName" );
		criteriaConversion.put( KIMPropertyConstants.Person.STATE_CODE, "entityTypes.addresses.stateCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.POSTAL_CODE, "entityTypes.addresses.postalCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.COUNTRY_CODE, "entityTypes.addresses.countryCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.CAMPUS_CODE, "affiliations.campusCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.AFFILIATION_TYPE_CODE, "affiliations.affiliationTypeCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.EXTERNAL_IDENTIFIER_TYPE_CODE, "externalIdentifiers.externalIdentifierTypeCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.EXTERNAL_ID, "externalIdentifiers.externalId" );		
		criteriaConversion.put( KIMPropertyConstants.Person.EMPLOYEE_TYPE_CODE, "employmentInformation.employeeTypeCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.EMPLOYEE_STATUS_CODE, "employmentInformation.employeeStatusCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.EMPLOYEE_ID, "employmentInformation.employeeId" );
		criteriaConversion.put( KIMPropertyConstants.Person.BASE_SALARY_AMOUNT, "employmentInformation.baseSalaryAmount" );
		
		personCachePropertyNames.add( KIMPropertyConstants.Person.PRINCIPAL_ID );
		personCachePropertyNames.add( KIMPropertyConstants.Person.PRINCIPAL_NAME );
		personCachePropertyNames.add( KIMPropertyConstants.Person.ENTITY_ID );
		personCachePropertyNames.add( KIMPropertyConstants.Person.FIRST_NAME );
		personCachePropertyNames.add( KIMPropertyConstants.Person.LAST_NAME );
		personCachePropertyNames.add( KIMPropertyConstants.Person.MIDDLE_NAME );
		personCachePropertyNames.add( KIMPropertyConstants.Person.CAMPUS_CODE );
		personCachePropertyNames.add( KIMPropertyConstants.Person.EMPLOYEE_ID );
		personCachePropertyNames.add( KIMPropertyConstants.Person.PRIMARY_DEPARTMENT_CODE );
	}

	public T convertEntityToPerson( KimEntity entity, KimPrincipal principal ) {
		try {
			T person = (T)getPersonImplementationClass().newInstance();
			// get the EntityEntityType for the EntityType corresponding to a Person
			for ( String entityTypeCode : personEntityTypeCodes ) {
				EntityEntityType entType = entity.getEntityType( entityTypeCode );
				// if no "person" entity type present for the given principal, skip to the next type in the list
				if ( entType == null ) {
					continue;
				}
				// attach the principal and entity objects
				// PersonImpl has logic to pull the needed elements from the KimEntity-related classes
				person.setPrincipal(principal, entity, entityTypeCode );
				return person;
			}
			return null;
		} catch ( Exception ex ) {
			// allow runtime exceptions to pass through
			if ( ex instanceof RuntimeException ) {
				throw (RuntimeException)ex;
			} else {
				throw new RuntimeException( "Problem building person object", ex );
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findPeople(Map<String,String> criteria, boolean unbounded ) {
		// convert the criteria to a form that can be used by the ORM layer
		Map<String,String> entityCriteria = convertPersonPropertiesToEntityProperties( criteria );

		List<T> people = new ArrayList<T>(); 

		
		LookupService lookupService = KNSServiceLocator.getLookupService();
		Collection<KimEntity> entities = lookupService.findCollectionBySearchHelper(KimEntityImpl.class, entityCriteria, unbounded);

		for ( KimEntity e : entities ) {
			// get to get all principals for the entity as well
			for ( KimPrincipal p : e.getPrincipals() ) {
				people.add( convertEntityToPerson( e, p ) );
			}
		}
		
		// QUESTION: Should this be done?  Or, is the cache only for lookups by principal ID?
			// if not looking for only active employees, check the cache table
			// however, must exclude matches from the main query
		
//		LookupDao dao = (LookupDao)KIMServiceLocator.getService( "lookupDao" );
//		dao.findCollectionBySearchHelper( example, formProps, true, false, dao )
//		people.addAll(results);
		if ( entities instanceof CollectionIncomplete ) {
			return new CollectionIncomplete( people, ((CollectionIncomplete)entities).getActualSizeIfTruncated() );
		}
		return people;
	}
	
	
	
	public Map<String,String> convertPersonPropertiesToEntityProperties( Map<String,String> criteria ) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "convertPersonPropertiesToEntityProperties: " + criteria );
		}
		boolean nameCriteria = false;
		boolean addressCriteria = false;
		boolean externalIdentifierCriteria = false;
		boolean affiliationCriteria = false;
		boolean affiliationDefaultOnlyCriteria = false;
		boolean phoneCriteria = false;
		boolean emailCriteria = false;
		boolean employeeIdCriteria = false;
		// add base lookups for all person lookups
		HashMap<String,String> newCriteria = new HashMap<String,String>();
		newCriteria.putAll( baseLookupCriteria );
		newCriteria.put( "entityTypes.entityTypeCode", personEntityTypeLookupCriteria );
		if ( criteria != null ) {
			for ( String key : criteria.keySet() ) {
				// if no value was passed, skip the entry in the Map
				if ( StringUtils.isEmpty( criteria.get(key) ) ) {
					continue;
				}
				// convert the property to the Entity data model
				String entityProperty = criteriaConversion.get( key );
				if ( entityProperty != null ) {
					newCriteria.put( entityProperty, criteria.get( key ) );
				} else {
					entityProperty = key;
					// just pass it through if no translation present
					newCriteria.put( key, criteria.get( key ) );
				}
				// check if additional criteria are needed based on the types of properties specified
				if ( isNameEntityCriteria( entityProperty ) ) {
					nameCriteria = true;
				}
				if ( isExternalIdentifierEntityCriteria( entityProperty ) ) {
					externalIdentifierCriteria = true;
				}
				if ( isAffiliationEntityCriteria( entityProperty ) ) {
					affiliationCriteria = true;
				}
				if ( isAddressEntityCriteria( entityProperty ) ) {
					addressCriteria = true;
				}
				if ( isPhoneEntityCriteria( entityProperty ) ) {
					phoneCriteria = true;
				}
				if ( isEmailEntityCriteria( entityProperty ) ) {
					emailCriteria = true;
				}
				if ( isEmployeeIdEntityCriteria( entityProperty ) ) {
					employeeIdCriteria = true;
				}				
				// special handling for the campus code, since that forces the query to look
				// at the default affiliation record only
				if ( key.equals( "campusCode" ) ) {
					affiliationDefaultOnlyCriteria = true;
				}
			}		
			if ( nameCriteria ) {
				newCriteria.put( ENTITY_NAME_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_NAME_PROPERTY_PREFIX + "dflt", "Y" );
			}
			if ( addressCriteria ) {
				newCriteria.put( ENTITY_ADDRESS_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_ADDRESS_PROPERTY_PREFIX + "dflt", "Y" );
			}
			if ( phoneCriteria ) {
				newCriteria.put( ENTITY_PHONE_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_PHONE_PROPERTY_PREFIX + "dflt", "Y" );
			}
			if ( emailCriteria ) {
				newCriteria.put( ENTITY_EMAIL_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_EMAIL_PROPERTY_PREFIX + "dflt", "Y" );
			}
			if ( employeeIdCriteria ) {
				newCriteria.put( ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX + "primary", "Y" );
			}
			if ( affiliationCriteria ) {
				newCriteria.put( ENTITY_AFFILIATION_PROPERTY_PREFIX + "active", "Y" );
			}
			if ( affiliationDefaultOnlyCriteria ) {
				newCriteria.put( ENTITY_AFFILIATION_PROPERTY_PREFIX + "dflt", "Y" );
			}
			if ( externalIdentifierCriteria ) {
				newCriteria.put( ENTITY_EXT_ID_PROPERTY_PREFIX + "active", "Y" );
			}
		}
		
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Converted: " + newCriteria );
		}
		return newCriteria;		
	}

	protected boolean isNameEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_NAME_PROPERTY_PREFIX );
	}
	protected boolean isAddressEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_ADDRESS_PROPERTY_PREFIX );
	}
	protected boolean isPhoneEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_PHONE_PROPERTY_PREFIX );
	}
	protected boolean isEmailEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_EMAIL_PROPERTY_PREFIX );
	}
	protected boolean isEmployeeIdEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX );
	}
	protected boolean isAffiliationEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_AFFILIATION_PROPERTY_PREFIX );
	}
	protected boolean isExternalIdentifierEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_EXT_ID_PROPERTY_PREFIX );
	}

	/**
	 * @return the personImplementationClass
	 */
	public Class<? extends T> getPersonImplementationClass() {
		return this.personImplementationClass;
	}

	/**
	 * @param personImplementationClass the personImplementationClass to set
	 */
	public void setPersonImplementationClass(
			Class<? extends T> personImplementationClass) {
		this.personImplementationClass = personImplementationClass;
	}

	/**
	 * Get the entityTypeCode that can be associated with a Person.  This will determine
	 * where EntityType-related data is pulled from within the KimEntity object.  The codes
	 * in the list will be examined in the order present.
	 */
	public List<String> getPersonEntityTypeCodes() {
		return this.personEntityTypeCodes;
	}

	public void setPersonEntityTypeCodes(List<String> personEntityTypeCodes) {
		this.personEntityTypeCodes = personEntityTypeCodes;
		personEntityTypeLookupCriteria = null;
		for ( String entityTypeCode : personEntityTypeCodes ) {
			if ( personEntityTypeLookupCriteria == null ) {
				personEntityTypeLookupCriteria = entityTypeCode;
			} else {
				personEntityTypeLookupCriteria = personEntityTypeLookupCriteria + "|" + entityTypeCode;
			}
		}
	}
	
	public void savePersonToCache( Person p ) {
		// create the cache version of the person
		PersonCacheImpl pc = new PersonCacheImpl( p );
		
		// save it
		getPersistenceBrokerTemplate().store( pc );
	}
	
	/**
	 * @see org.kuali.rice.kim.dao.PersonDao#getPersonFromCache(java.lang.String)
	 */
	public T getPersonFromCache(String principalId) {
		try {
			PersonCacheImpl pci = (PersonCacheImpl)getPersistenceBrokerTemplate().getObjectById( PersonCacheImpl.class, principalId );
			Constructor<? extends T> copyConstructor = getPersonImplementationClass().getConstructor( PersonCacheImpl.class );
			T person = copyConstructor.newInstance( pci );			
			return person;
		} catch ( ObjectRetrievalFailureException ex ) {
			// if not found, just return null
			return null;
		} catch ( Exception ex ) {
			// allow runtime exceptions to pass through
			if ( ex instanceof RuntimeException ) {
				throw (RuntimeException)ex;
			} else {
				throw new RuntimeException( "Problem building person object from DB cache", ex );
			}
		}
	}
}
