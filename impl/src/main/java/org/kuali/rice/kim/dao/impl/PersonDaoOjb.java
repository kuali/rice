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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.EntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
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

	protected static final String ENTITY_NAME_PROPERTY_PREFIX = "entityTypes.names.";

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersonDaoOjb.class);

    protected Class<? extends T> personImplementationClass;
	protected String personEntityTypeCode;
    

	public T convertEntityToPerson( KimEntity entity, KimPrincipal principal ) {
		try {
			T person = (T)getPersonImplementationClass().newInstance();
			// get the EntityEntityType for the EntityType corresponding to a Person
			EntityEntityType entType = entity.getEntityType( getPersonEntityTypeCode() );
			// if no "person" entity type present for the given principal, return null
			if ( entType == null ) {
				return null;
			}
			// attach the principal and entity objects
			// PersonImpl has lazy-loading logic to pull the needed elements from the KimEntity as needed
			person.setPrincipal(principal, entity, personEntityTypeCode );
			return person;
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
	
	public List<T> findPeople(Map<String,String> criteria, boolean unbounded ) {
		// convert the criteria to a form that can be used by the ORM layer
		Map<String,String> entityCriteria = convertPersonPropertiesToEntityProperties( criteria );

		List<T> people = new ArrayList<T>(); 

		
		LookupService lookupService = KNSServiceLocator.getLookupService();
		Collection<KimEntity> entities = lookupService.findCollectionBySearchHelper(KimEntityImpl.class, entityCriteria, unbounded);

//		// use the normal BO service to get the entity objects
//		Collection<KimEntity> entities = KNSServiceLocator.getBusinessObjectService().findMatching( KimEntityImpl.class, entityCriteria );

		for ( KimEntity e : entities ) {
			// get to get all principals for the entity as well
			for ( KimPrincipal p : e.getPrincipals() ) {
				people.add( convertEntityToPerson( e, p ) );
			}
		}
		
//		LookupDao dao = (LookupDao)KIMServiceLocator.getService( "lookupDao" );
//		dao.findCollectionBySearchHelper( example, formProps, true, false, dao )
//		people.addAll(results);
		if ( entities instanceof CollectionIncomplete ) {
			return new CollectionIncomplete( people, ((CollectionIncomplete)entities).getActualSizeIfTruncated() );
		}
		return people;
	}
	
	
	protected Map<String,String> baseLookupCriteria = new HashMap<String,String>();
	protected Map<String,String> criteriaConversion = new HashMap<String,String>();
	{
		// init the criteria which will need to be applied to every lookup against
		// the entity data tables
		baseLookupCriteria.put( "active", "Y" );
		baseLookupCriteria.put( "principals.active", "Y" );
		baseLookupCriteria.put( "entityTypes.active", "Y" );
		
		// create the field mappings between the Person object and the KimEntity object
		criteriaConversion.put( "entityId", "entityId" );
		criteriaConversion.put( "principalId", "principals.principalId" );
		criteriaConversion.put( "principalName", "principals.principalName" );
		criteriaConversion.put( "firstName", "entityTypes.names.firstName" );
		criteriaConversion.put( "lastName", "entityTypes.names.lastName" );
		criteriaConversion.put( "middleName", "entityTypes.names.middleName" );
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
		// add base lookups for all person lookups
		HashMap<String,String> newCriteria = new HashMap<String,String>();
		newCriteria.putAll( baseLookupCriteria );
		newCriteria.put( "entityTypes.entityTypeCode", personEntityTypeCode );
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
	 * @return the personEntityTypeCode
	 */
	public String getPersonEntityTypeCode() {
		return this.personEntityTypeCode;
	}

	/**
	 * @param personEntityTypeCode the personEntityTypeCode to set
	 */
	public void setPersonEntityTypeCode(String personEntityTypeCode) {
		this.personEntityTypeCode = personEntityTypeCode;
	}
	
}
