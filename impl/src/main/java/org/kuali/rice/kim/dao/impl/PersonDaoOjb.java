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
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonDaoOjb<T extends PersonImpl> extends PlatformAwareDaoBaseOjb implements PersonDao<T> {

	protected static final String ENTITY_EXT_ID_PROPERTY_PREFIX = "externalIdentifiers.";
	protected static final String ENTITY_AFFILIATION_PROPERTY_PREFIX = "affiliations.";
	protected static final String ENTITY_EMAIL_PROPERTY_PREFIX = "entityTypes.emailAddresses.";
	protected static final String ENTITY_PHONE_PROPERTY_PREFIX = "entityTypes.phoneNumbers.";
	protected static final String ENTITY_ADDRESS_PROPERTY_PREFIX = "entityTypes.addresses.";
	protected static final String ENTITY_NAME_PROPERTY_PREFIX = "names.";
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
		baseLookupCriteria.put( "active", "Y" );
		baseLookupCriteria.put( "entityTypes.active", "Y" );
		
		// create the field mappings between the Person object and the KimEntity object
		criteriaConversion.put( "entityId", "entityId" );
		criteriaConversion.put( "active", "principals.active" );
		criteriaConversion.put( "principalId", "principals.principalId" );
		criteriaConversion.put( "principalName", "principals.principalName" );
		criteriaConversion.put( "firstName", "names.firstName" );
		criteriaConversion.put( "lastName", "names.lastName" );
		criteriaConversion.put( "middleName", "names.middleName" );
		criteriaConversion.put( "emailAddress", "entityTypes.emailAddresses.emailAddress" );
		criteriaConversion.put( "phoneNumber", "entityTypes.phoneNumbers.phoneNumber" );
		criteriaConversion.put( "line1", "entityTypes.addresses.line1" );
		criteriaConversion.put( "line2", "entityTypes.addresses.line2" );
		criteriaConversion.put( "line3", "entityTypes.addresses.line3" );
		criteriaConversion.put( "cityName", "entityTypes.addresses.cityName" );
		criteriaConversion.put( "stateCode", "entityTypes.addresses.stateCode" );
		criteriaConversion.put( "postalCode", "entityTypes.addresses.postalCode" );
		criteriaConversion.put( "countryCode", "entityTypes.addresses.countryCode" );
		criteriaConversion.put( "campusCode", "affiliations.campusCode" );
		criteriaConversion.put( "affiliationTypeCode", "affiliations.affiliationTypeCode" );
		criteriaConversion.put( "externalIdentifierTypeCode", "externalIdentifiers.externalIdentifierTypeCode" );
		criteriaConversion.put( "externalId", "externalIdentifiers.externalId" );		
		criteriaConversion.put( "employeeTypeCode", "employmentInformation.employeeTypeCode" );
		criteriaConversion.put( "employeeStatusCode", "employmentInformation.employeeStatusCode" );
		criteriaConversion.put( "employeeId", "employmentInformation.employeeId" );
		criteriaConversion.put( "baseSalaryAmount", "employmentInformation.baseSalaryAmount" );
		criteriaConversion.put( "primaryDepartmentCode", "employmentInformation.primaryDepartmentCode" );
		
		personCachePropertyNames.add( "principalId" );
		personCachePropertyNames.add( "principalName" );
		personCachePropertyNames.add( "entityId" );
		personCachePropertyNames.add( "firstName" );
		personCachePropertyNames.add( "lastName" );
		personCachePropertyNames.add( "middleName" );
		personCachePropertyNames.add( "campusCode" );
		personCachePropertyNames.add( "employeeId" );
		personCachePropertyNames.add( "primaryDepartmentCode" );
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
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.dao.PersonDao#findPeople(java.util.Map)
	 */
	public List<T> findPeople(Map<String,String> entityCriteria) {
		return findPeople(entityCriteria, true);
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
	 * @param personEntityTypeCode the personEntityTypeCode to set
	 */
	@Deprecated
	public void setPersonEntityTypeCode(String personEntityTypeCode) {
		personEntityTypeCodes.add( personEntityTypeCode );
		personEntityTypeLookupCriteria = null;
		for ( String entityTypeCode : personEntityTypeCodes ) {
			if ( personEntityTypeLookupCriteria == null ) {
				personEntityTypeLookupCriteria = entityTypeCode;
			} else {
				personEntityTypeLookupCriteria = personEntityTypeLookupCriteria + "|" + entityTypeCode;
			}
		}
	}

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
// commented out to prevent transaction error until can be worked to run in own transaction
// in own thread		
//		getPersistenceBrokerTemplate().store( pc );
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
