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
package org.kuali.rice.kim.service.impl;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.resourceloader.ContextClassLoaderBinder;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.dao.PersonDao;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.BusinessObjectRelationship;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonServiceImpl implements PersonService<PersonImpl> {

	private static Logger LOG = Logger.getLogger( PersonServiceImpl.class );
	
	protected IdentityManagementService identityManagementService;	
	protected PersonDao<PersonImpl> personDao;
	protected BusinessObjectMetaDataService businessObjectMetaDataService;
	protected MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

	// Max age defined in seconds
	protected int personCacheMaxSize = 200;
	protected int personCacheMaxAge = 30;

	protected HashMap<String,MaxAgeSoftReference<PersonImpl>> personByPrincipalNameCache = new HashMap<String,MaxAgeSoftReference<PersonImpl>>( personCacheMaxSize );
	protected HashMap<String,MaxAgeSoftReference<PersonImpl>> personByPrincipalIdCache = new HashMap<String,MaxAgeSoftReference<PersonImpl>>( personCacheMaxSize );
	protected HashMap<String,MaxAgeSoftReference<PersonImpl>> personByEmployeeIdCache = new HashMap<String,MaxAgeSoftReference<PersonImpl>>( personCacheMaxSize );
	// PERSON/ENTITY RELATED METHODS
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#getPerson(java.lang.String)
	 */
	public PersonImpl getPerson(String principalId) {
		if ( StringUtils.isBlank(principalId) ) {
			return null;
		}
		PersonImpl person = null;
		// check the cache		
		person = getPersonImplFromPrincipalIdCache( principalId );
		if ( person != null ) {
			return person;
		}
		KimEntity entity = null;
		// get the corresponding principal
		KimPrincipal principal = identityManagementService.getPrincipal( principalId );
		// get the entity
		if ( principal != null ) {
			entity = identityManagementService.getEntity( principal.getEntityId() );
		} else { // attempt to load from the cache and create the Person object
			person = personDao.getPersonFromCache( principalId );
		}
		// convert the principal and entity to a Person
		// skip if the person was created from the DB cache
		if ( person == null && entity != null ) {
			person = (PersonImpl)personDao.convertEntityToPerson( entity, principal );
			addPersonImplToCache( person );
		}
		return person;
	}

	protected PersonImpl getPersonImplFromPrincipalNameCache( String principalName ) {
		SoftReference<PersonImpl> personRef = personByPrincipalNameCache.get( principalName );
		if ( personRef != null ) {
			return personRef.get();
		}
		return null;
	}

	protected PersonImpl getPersonImplFromPrincipalIdCache( String principalId ) {
		SoftReference<PersonImpl> personRef = personByPrincipalIdCache.get( principalId );
		if ( personRef != null ) {
			return personRef.get();
		}
		return null;
	}
	
	protected PersonImpl getPersonImplFromEmployeeIdCache( String principalId ) {
		SoftReference<PersonImpl> personRef = personByEmployeeIdCache.get( principalId );
		if ( personRef != null ) {
			return personRef.get();
		}
		return null;
	}
	
	protected void addPersonImplToCache( PersonImpl person ) {
		if ( person != null ) {
			personByPrincipalNameCache.put( person.getPrincipalName(), new MaxAgeSoftReference<PersonImpl>( personCacheMaxAge, person ) );
			personByPrincipalIdCache.put( person.getPrincipalId(), new MaxAgeSoftReference<PersonImpl>( personCacheMaxAge, person ) );
			personByEmployeeIdCache.put( person.getEmployeeId(), new MaxAgeSoftReference<PersonImpl>( personCacheMaxAge, person ) );
			// store the person to the database cache
			
			// but do this an alternate thread to prevent transaction issues since this service is non-transactional
			
			KSBServiceLocator.getThreadPool().execute( new SavePersonToCacheRunnable( personDao, person ) );
		}
	}
	
	private static class SavePersonToCacheRunnable implements Runnable {
		PersonDao personDao;
		PersonImpl person;
		/**
		 * 
		 */
		public SavePersonToCacheRunnable( PersonDao personDao, PersonImpl person ) {
			this.personDao = personDao;
			this.person = person;
		}
		
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				PlatformTransactionManager transactionManager = KNSServiceLocator.getTransactionManager();
				TransactionTemplate template = new TransactionTemplate(transactionManager);
				template.execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						personDao.savePersonToCache( person );
						return null;
					}
				});
			} catch (Throwable t) {
				LOG.error("Failed to load transaction manager.", t);
			}
		}
	}
	
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#getPersonByPrincipalName(java.lang.String)
	 */
	public PersonImpl getPersonByPrincipalName(String principalName) {
		if ( StringUtils.isBlank(principalName) ) {
			return null;
		}
		PersonImpl person = null;
		// check the cache		
		person = getPersonImplFromPrincipalNameCache( principalName );
		if ( person != null ) {
			return person;
		}
		KimEntity entity = null;
		// get the corresponding principal
		KimPrincipal principal = identityManagementService.getPrincipalByPrincipalName( principalName );
		// get the entity
		if ( principal != null ) {
			entity = identityManagementService.getEntity( principal.getEntityId() );
		}
		// convert the principal and entity to a Person
		if ( entity != null ) {
			person = (PersonImpl)personDao.convertEntityToPerson( entity, principal );
		}
		addPersonImplToCache( person );
		return person;
	}

	public PersonImpl getPersonByEmployeeId(String employeeId) {
		if ( StringUtils.isBlank( employeeId  ) ) {
			return null;
		}
		
		PersonImpl person = getPersonImplFromEmployeeIdCache( employeeId );
		if ( person != null ) {
			return person;
		}
		
		Map<String,String> criteria = new HashMap<String,String>( 1 );
		criteria.put( "employeeId", employeeId );
		List<PersonImpl> people = findPeople( criteria ); 
		if ( !people.isEmpty() ) {
			person = people.get(0);
			addPersonImplToCache( person );
		}
		return person;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#findPeople(Map)
	 */
	@SuppressWarnings("unchecked")
	public List<PersonImpl> findPeople(Map<String, String> criteria) {
		return findPeople(criteria, true);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#findPeople(java.util.Map, boolean)
	 */
	public List<PersonImpl> findPeople(Map<String, String> criteria, boolean unbounded) {
		List<PersonImpl> people = personDao.findPeople(criteria, unbounded); 
		// QUESTION: Do we want to do this?
		for ( PersonImpl p : people ) {
			// check whether the item is in the cache first - otherwise would add
			// to cache on every lookup
			if ( getPersonImplFromPrincipalIdCache( p.getPrincipalId() ) == null ) {
				addPersonImplToCache( p );
			}
		}
		return people;
	}

	/**
	 * @see org.kuali.rice.kim.service.PersonService#getPersonByExternalIdentifier(java.lang.String, java.lang.String)
	 */
	public List<PersonImpl> getPersonByExternalIdentifier(String externalIdentifierTypeCode, String externalId) {
		if (StringUtils.isBlank( externalIdentifierTypeCode ) || StringUtils.isBlank( externalId ) ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put( "externalIdentifierTypeCode", externalIdentifierTypeCode );
		criteria.put( "externalId", externalId );
		return findPeople( criteria );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#updatePersonIfNecessary(java.lang.String, org.kuali.rice.kim.bo.Person)
	 */
    public Person updatePersonIfNecessary(String sourcePrincipalId, Person currentPerson ) {
        if (currentPerson  == null // no person set
                || !StringUtils.equals(sourcePrincipalId, currentPerson.getPrincipalId() ) // principal ID mismatch
                || currentPerson.getEntityId() == null ) { // syntheticially created Person object
            Person person = getPerson( sourcePrincipalId );
            if ( person == null && currentPerson == null ) {
            	try {
            		return getPersonImplementationClass().newInstance();
            	} catch ( Exception ex ) {
            		LOG.error( "unable to instantiate an object of type: " + getPersonImplementationClass() + " - returning null", ex );
            		return null;
            	}
            }
            return person;
        }
        // otherwise, no need to change the given object
        return currentPerson;
    }

    /**
     * Builds a map containing entries from the passed in Map that do NOT represent properties on an embedded
     * Person object.
     */
    private Map<String,String> getNonPersonSearchCriteria( BusinessObject bo, Map<String,String> fieldValues) {
        Map<String,String> nonUniversalUserSearchCriteria = new HashMap<String,String>();
        for ( String propertyName : fieldValues.keySet() ) {
            if (!isPersonProperty(bo, propertyName)) {
                nonUniversalUserSearchCriteria.put(propertyName, fieldValues.get(propertyName));
            }
        }
        return nonUniversalUserSearchCriteria;
    }


    private boolean isPersonProperty(BusinessObject bo, String propertyName) {
        try {
            return ObjectUtils.isNestedAttribute( propertyName ) // is a nested property
            		&& !StringUtils.contains(propertyName, "add.") // exclude add line properties (due to path parsing problems in PropertyUtils.getPropertyType)
            		// property type indicates a Person object
            		&& Person.class.isAssignableFrom( PropertyUtils.getPropertyType(bo, ObjectUtils.getNestedAttributePrefix( propertyName )));
        }
        catch (Exception ex) {
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug("Unable to determine if property belongs to a person object" + propertyName, ex );
        	}
        }
        return false;
    }
    
    /**
     * @see org.kuali.rice.kim.service.PersonService#hasPersonProperty(java.lang.Class, java.util.Map)
     */
    public boolean hasPersonProperty(Class<? extends BusinessObject> businessObjectClass, Map<String,String> fieldValues) {
    	if ( businessObjectClass == null || fieldValues == null ) {
    		return false;
    	}
    	try {
	    	BusinessObject bo = businessObjectClass.newInstance();
	        for ( String propertyName : fieldValues.keySet() ) {
	            if (isPersonProperty(bo, propertyName)) {
	            	return true;
	            }
	        }
    	} catch (Exception ex) {
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Error instantiating business object class passed into hasPersonProperty", ex );
    		}
			// do nothing
		}
        return false;
    }    

    /**
     * @see org.kuali.rice.kim.service.PersonService#resolvePrincipalNamesToPrincipalIds(org.kuali.rice.kns.bo.BusinessObject, java.util.Map)
     */
    @SuppressWarnings("unchecked")
	public Map<String,String> resolvePrincipalNamesToPrincipalIds(BusinessObject businessObject, Map<String,String> fieldValues) {
    	if ( fieldValues == null ) {
    		return null;
    	}
    	if ( businessObject == null ) {
    		return fieldValues;
    	}
    	StringBuffer resolvedPrincipalIdPropertyName = new StringBuffer();
    	// save off all criteria which are not references to Person properties
    	// leave person properties out so they can be resolved and replaced by this method
        Map<String,String> processedFieldValues = getNonPersonSearchCriteria(businessObject, fieldValues);
        for ( String propertyName : fieldValues.keySet() ) {        	
            if (	!StringUtils.isBlank(fieldValues.get(propertyName))  // property has a value
            		&& isPersonProperty(businessObject, propertyName) // is a property on a Person object
            		) {
            	// strip off the prefix on the property
                String personPropertyName = ObjectUtils.getNestedAttributePrimitive( propertyName );
                // special case - the user ID 
                if ( StringUtils.equals( KimConstants.PropertyNames.PRINCIPAL_NAME, personPropertyName) ) {
                    Class targetBusinessObjectClass = null;
                    BusinessObject targetBusinessObject = null;
                    resolvedPrincipalIdPropertyName.setLength( 0 ); // clear the buffer without requiring a new object allocation on each iteration
                	// get the property name up until the ".principalName"
                	// this should be a reference to the Person object attached to the BusinessObject                	
                	String personReferenceObjectPropertyName = ObjectUtils.getNestedAttributePrefix( propertyName );
                	// check if the person was nested within another BO under the master BO.  If so, go up one more level
                	// otherwise, use the passed in BO class as the target class
                    if ( ObjectUtils.isNestedAttribute( personReferenceObjectPropertyName ) ) {
                        String targetBusinessObjectPropertyName = ObjectUtils.getNestedAttributePrefix( personReferenceObjectPropertyName );
                        targetBusinessObject = (BusinessObject)ObjectUtils.getPropertyValue( businessObject, targetBusinessObjectPropertyName );
                        if (targetBusinessObject != null) {
                            targetBusinessObjectClass = targetBusinessObject.getClass();
                            resolvedPrincipalIdPropertyName.append(targetBusinessObjectPropertyName).append(".");
                        } else {
                            LOG.error("Could not find target property '"+propertyName+"' in class "+businessObject.getClass().getName()+". Property value was null.");
                        }
                    } else { // not a nested Person property
                        targetBusinessObjectClass = businessObject.getClass();
                        targetBusinessObject = businessObject;
                    }
                    
                    if (targetBusinessObjectClass != null) {
                    	// use the relationship metadata in the KNS to determine the property on the
                    	// host business object to put back into the map now that the principal ID
                    	// (the value stored in application tables) has been resolved
                        String propName = ObjectUtils.getNestedAttributePrimitive( personReferenceObjectPropertyName );
                        BusinessObjectRelationship rel = getBusinessObjectMetaDataService().getBusinessObjectRelationship( targetBusinessObject, propName );
                        if ( rel != null ) {
                            String sourcePrimitivePropertyName = rel.getParentAttributeForChildAttribute(KimConstants.PropertyNames.PRINCIPAL_ID);
                            resolvedPrincipalIdPropertyName.append(sourcePrimitivePropertyName);
                        	// get the principal - for translation of the principalName to principalId
                        	KimPrincipal principal = identityManagementService.getPrincipalByPrincipalName( fieldValues.get( propertyName ) );
                            if (principal != null ) {
                                processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), principal.getPrincipalId());
                            } else {
                                processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), null);
                            }
                        } else {
                        	LOG.error( "Missing relationship for " + propName + " on " + targetBusinessObjectClass.getName() );
                        }
                    } else { // no target BO class - the code below probably will not work
                        processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), null);
                    }
                }
            // if the property does not seem to match the definition of a Person property but it
            // does end in principalName then...
            // this is to handle the case where the user ID is on an ADD line - a case excluded from isPersonProperty()
            } else if (propertyName.endsWith("." + KimConstants.PropertyNames.PRINCIPAL_NAME)){
                // if we're adding to a collection and we've got the principalName; let's populate universalUser
                String principalName = fieldValues.get(propertyName);
                if ( StringUtils.isNotEmpty( principalName ) ) {
                    String containerPropertyName = propertyName;
                    if (containerPropertyName.startsWith(KNSConstants.MAINTENANCE_ADD_PREFIX)) {
                        containerPropertyName = StringUtils.substringAfter( propertyName, KNSConstants.MAINTENANCE_ADD_PREFIX );
                    }
                    // get the class of the object that is referenced by the property name
                    // if this is not true then there's a principalName collection or primitive attribute 
                    // directly on the BO on the add line, so we just ignore that since something is wrong here
                    if ( ObjectUtils.isNestedAttribute( containerPropertyName ) ) {
                    	// the first part of the property is the collection name
                        String collectionName = StringUtils.substringBefore( containerPropertyName, "." );
                        // what is the class held by that collection?
                        // JHK: I don't like this.  This assumes that this method is only used by the maintenance
                        // document service.  If that will always be the case, this method should be moved over there.
                        Class<? extends BusinessObject> collectionBusinessObjectClass = getMaintenanceDocumentDictionaryService()
                        		.getCollectionBusinessObjectClass(
                        				getMaintenanceDocumentDictionaryService()
                        						.getDocumentTypeName(businessObject.getClass()), collectionName);
                        if (collectionBusinessObjectClass != null) {
                            // we are adding to a collection; get the relationships for that object; 
                        	// is there one for personUniversalIdentifier?
                            List<BusinessObjectRelationship> relationships = 
                            		getBusinessObjectMetaDataService().getBusinessObjectRelationships( collectionBusinessObjectClass );
                            // JHK: this seems like a hack - looking at all relationships for a BO does not guarantee that we get the right one
                            // JHK: why not inspect the objects like above?  Is it the property path problems because of the .add. portion?
                            for ( BusinessObjectRelationship rel : relationships ) {
                            	String parentAttribute = rel.getParentAttributeForChildAttribute( KimConstants.PropertyNames.PRINCIPAL_ID );
                            	if ( parentAttribute == null ) {
                            		continue;
                            	}
                                // there is a relationship for personUserIdentifier; use that to find the universal user
                            	processedFieldValues.remove( propertyName );
                        		String fieldPrefix = StringUtils.substringBeforeLast( StringUtils.substringBeforeLast( propertyName, "." + KimConstants.PropertyNames.PRINCIPAL_NAME ), "." );
                                String relatedPrincipalIdPropertyName = fieldPrefix + "." + parentAttribute;
                                String currRelatedPersonPrincipalId = processedFieldValues.get(relatedPrincipalIdPropertyName);
                                if ( StringUtils.isBlank( currRelatedPersonPrincipalId ) ) {
                                	KimPrincipal principal = identityManagementService.getPrincipalByPrincipalName( principalName );
                                	if ( principal != null ) {
                                		processedFieldValues.put(relatedPrincipalIdPropertyName, principal.getPrincipalId());
                                	} else {
                                		processedFieldValues.put(relatedPrincipalIdPropertyName, null);
                                	}
                                }
                            } // relationship loop
                        } else {
                        	if ( LOG.isDebugEnabled() ) {
                        		LOG.debug( "Unable to determine class for collection referenced as part of property: " + containerPropertyName + " on " + businessObject.getClass().getName() );
                        	}
                        }
                    } else {
                    	if ( LOG.isDebugEnabled() ) {
                    		LOG.debug( "Non-nested property ending with 'principalName': " + containerPropertyName + " on " + businessObject.getClass().getName() );
                    	}
                    }
                }
            }
        }
        return processedFieldValues;
    }
	
	// OTHER METHODS

	protected IdentityManagementService getIdentityManagementService() {
		return this.identityManagementService;
	}

	public void setIdentityManagementService(IdentityManagementService identityManagementService) {
		this.identityManagementService = identityManagementService;
	}

	public Class<? extends Person> getPersonImplementationClass() {
		return personDao.getPersonImplementationClass();
	}
	
	/**
	 * @return the personDao
	 */
	public PersonDao<PersonImpl> getPersonDao() {
		return this.personDao;
	}

	/**
	 * @param personDao the personDao to set
	 */
	public void setPersonDao(PersonDao<PersonImpl> personDao) {
		this.personDao = personDao;
	}

	protected BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
		if ( businessObjectMetaDataService == null ) {
			businessObjectMetaDataService = KNSServiceLocator.getBusinessObjectMetaDataService();
		}
		return businessObjectMetaDataService;
	}

	protected MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if ( maintenanceDocumentDictionaryService == null ) {
			maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}

	public void setPersonCacheMaxSize(int personCacheMaxSize) {
		this.personCacheMaxSize = personCacheMaxSize;
	}

	public void setPersonCacheMaxAge(int personCacheMaxAge) {
		this.personCacheMaxAge = personCacheMaxAge;
	}
	
}
