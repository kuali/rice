/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObjectRelationship;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.BusinessObjectDao;
import org.kuali.core.exceptions.ObjectNotABusinessObjectRuntimeException;
import org.kuali.core.exceptions.ReferenceAttributeDoesntExistException;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectMetaDataService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the BusinessObjectService structure. This is the default implementation, that is
 * delivered with Kuali.
 */
@Transactional
public class BusinessObjectServiceImpl implements BusinessObjectService {

    private PersistenceService persistenceService;
    private PersistenceStructureService persistenceStructureService;
    private BusinessObjectDao businessObjectDao;
    private UniversalUserService universalUserService;
    private BusinessObjectMetaDataService businessObjectMetaDataService;

    /**
     * @see org.kuali.core.service.BusinessObjectService#save(org.kuali.bo.BusinessObject)
     */
    public void save(PersistableBusinessObject bo) {
        if (!(bo instanceof PersistableBusinessObject)) {
            throw new IllegalArgumentException("Object passed in is not a BusinessObject class or subclass.");
        }
        businessObjectDao.save(bo);
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#save(java.util.List)
     */
    public void save(List businessObjects) {
        int index = 0;
        for (Iterator i = businessObjects.iterator(); i.hasNext(); index++) {
            Object current = i.next();
            if (!(current instanceof PersistableBusinessObject)) {
                throw new IllegalArgumentException("item '" + index + "' on the given list is not a BusinessObject");
            }
        }
        businessObjectDao.save(businessObjects);
    }

    /**
     * 
     * @see org.kuali.core.service.BusinessObjectService#linkAndSave(org.kuali.core.bo.BusinessObject)
     */
    public void linkAndSave(PersistableBusinessObject bo) {
        if (!(bo instanceof PersistableBusinessObject)) {
            throw new IllegalArgumentException("Object passed in is not a BusinessObject class or subclass.");
        }
        persistenceService.linkObjects(bo);
        businessObjectDao.save(bo);
    }

    /**
     * 
     * @see org.kuali.core.service.BusinessObjectService#linkAndSave(java.util.List)
     */
    public void linkAndSave(List<PersistableBusinessObject> businessObjects) {
        for (PersistableBusinessObject bo : businessObjects) {
            if (!(bo instanceof PersistableBusinessObject)) {
                throw new IllegalArgumentException("One of the items in the list passed in is not " + "a BusinessObject descendent: [" + bo.getClass().getName() + "] " + bo.toString());
            }
            persistenceService.linkObjects(bo);
        }
        businessObjectDao.save(businessObjects);
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#findByPrimaryKey(java.lang.Class, java.util.Map)
     */
    public PersistableBusinessObject findByPrimaryKey(Class clazz, Map primaryKeys) {
        return businessObjectDao.findByPrimaryKey(clazz, primaryKeys);
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#retrieve(java.lang.Object)
     */
    public PersistableBusinessObject retrieve(PersistableBusinessObject object) {
        return businessObjectDao.retrieve(object);
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#findAll(java.lang.Class)
     */
    public Collection findAll(Class clazz) {
        Collection coll = businessObjectDao.findAll(clazz);
        return new ArrayList(coll);
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#findMatching(java.lang.Class, java.util.Map)
     */
    public Collection findMatching(Class clazz, Map fieldValues) {
        return new ArrayList(businessObjectDao.findMatching(clazz, fieldValues));
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#countMatching(java.lang.Class, java.util.Map)
     */
    public int countMatching(Class clazz, Map fieldValues) {
        return businessObjectDao.countMatching(clazz, fieldValues);
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#countMatching(java.lang.Class, java.util.Map, java.util.Map)
     */
    public int countMatching(Class clazz, Map positiveFieldValues, Map negativeFieldValues) {
        return businessObjectDao.countMatching(clazz, positiveFieldValues, negativeFieldValues);
    }
    
    /**
     * @see org.kuali.core.service.BusinessObjectService#findMatchingOrderBy(java.lang.Class, java.util.Map)
     */
    public Collection findMatchingOrderBy(Class clazz, Map fieldValues, String sortField, boolean sortAscending) {
        return new ArrayList(businessObjectDao.findMatchingOrderBy(clazz, fieldValues, sortField, sortAscending));
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#delete(org.kuali.bo.BusinessObject)
     */
    public void delete(PersistableBusinessObject bo) {
        businessObjectDao.delete(bo);
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#delete(java.util.List)
     */
    public void delete(List<PersistableBusinessObject> boList) {
        businessObjectDao.delete(boList);
    }


    /**
     * @see org.kuali.core.service.BusinessObjectService#deleteMatching(java.lang.Class, java.util.Map)
     */
    public void deleteMatching(Class clazz, Map fieldValues) {
        businessObjectDao.deleteMatching(clazz, fieldValues);
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#getReferenceIfExists(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    public PersistableBusinessObject getReferenceIfExists(PersistableBusinessObject bo, String referenceName) {

        PersistableBusinessObject referenceBo = null;
        boolean allFkeysHaveValues = true;

        // if either argument is null, then we have nothing to do, complain and abort
        if (ObjectUtils.isNull(bo)) {
            throw new IllegalArgumentException("Passed in BusinessObject was null.  No processing can be done.");
        }
        if (StringUtils.isEmpty(referenceName)) {
            throw new IllegalArgumentException("Passed in referenceName was empty or null.  No processing can be done.");
        }

        // make sure the attribute exists at all, throw exception if not
        PropertyDescriptor propertyDescriptor;
        try {
            propertyDescriptor = PropertyUtils.getPropertyDescriptor(bo, referenceName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (propertyDescriptor == null) {
            throw new ReferenceAttributeDoesntExistException("Requested attribute: '" + referenceName + "' does not exist " + "on class: '" + bo.getClass().getName() + "'. GFK");
        }

        // get the class of the attribute name
        Class referenceClass = propertyDescriptor.getPropertyType();

        /*
         * check for UniversalUser references in which case we can just get the reference through propertyutils
         */
        if (UniversalUser.class.isAssignableFrom(referenceClass)) {
            try {
                return (PersistableBusinessObject) PropertyUtils.getProperty(bo, referenceName);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e.getMessage());
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // make sure the class of the attribute descends from BusinessObject,
        // otherwise throw an exception
        if (!PersistableBusinessObject.class.isAssignableFrom(referenceClass)) {
            throw new ObjectNotABusinessObjectRuntimeException("Attribute requested (" + referenceName + ") is of class: " + "'" + referenceClass.getName() + "' and is not a " + "descendent of BusinessObject.  Only descendents of BusinessObject " + "can be used.");
        }

        // get the list of foreign-keys for this reference. if the reference
        // does not exist, or is not a reference-descriptor, an exception will
        // be thrown here.
        Map fkMap = persistenceStructureService.getForeignKeysForReference(bo.getClass(), referenceName);

        // walk through the foreign keys, testing each one to see if it has a value
        Map pkMap = new HashMap();
        for (Iterator iter = fkMap.keySet().iterator(); iter.hasNext();) {
            String fkFieldName = (String) iter.next();
            String pkFieldName = (String) fkMap.get(fkFieldName);

            // attempt to retrieve the value for the given field
            Object fkFieldValue;
            try {
                fkFieldValue = PropertyUtils.getSimpleProperty(bo, fkFieldName);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

            // determine if there is a value for the field
            if (ObjectUtils.isNull(fkFieldValue)) {
                allFkeysHaveValues = false;
                break; // no reason to continue processing the fkeys
            }
            else if (String.class.isAssignableFrom(fkFieldValue.getClass())) {
                if (StringUtils.isEmpty((String) fkFieldValue)) {
                    allFkeysHaveValues = false;
                    break;
                }
                else {
                    pkMap.put(pkFieldName, fkFieldValue);
                }
            }

            // if there is a value, grab it
            else {
                pkMap.put(pkFieldName, fkFieldValue);
            }
        }

        // only do the retrieval if all Foreign Keys have values
        if (allFkeysHaveValues) {
            referenceBo = findByPrimaryKey(referenceClass, pkMap);
        }

        // return what we have, it'll be null if it was never retrieved
        return referenceBo;
    }

    /**
     * 
     * @see org.kuali.core.service.BusinessObjectService#linkUserFields(org.kuali.core.bo.BusinessObject)
     */
    public void linkUserFields(PersistableBusinessObject bo) {
        if (bo == null) {
            throw new IllegalArgumentException("bo passed in was null");
        }

        bo.linkEditableUserFields();
       
        List bos = new ArrayList();
        bos.add(bo);
        linkUserFields(bos);
    }

    /**
     * 
     * @see org.kuali.core.service.BusinessObjectService#linkUserFields(java.util.List)
     */
    public void linkUserFields(List<PersistableBusinessObject> bos) {

        // do nothing if there's nothing to process
        if (bos == null) {
            throw new IllegalArgumentException("List of bos passed in was null");
        }
        else if (bos.isEmpty()) {
            return;
        }

        UniversalUser universalUser = null;

        for (PersistableBusinessObject bo : bos) {
            // get a list of the reference objects on the BO
            List<BusinessObjectRelationship> relationships = businessObjectMetaDataService.getBusinessObjectRelationships( bo );
            for ( BusinessObjectRelationship rel : relationships ) {
                if ( UniversalUser.class.equals( rel.getRelatedClass() ) ) {
                    universalUser = (UniversalUser) ObjectUtils.getPropertyValue(bo, rel.getParentAttributeName() );
                    if (universalUser != null) {
                        // find the universal user ID relationship and link the field
                        for ( Map.Entry<String,String> entry : rel.getParentToChildReferences().entrySet() ) {
                            if ( entry.getValue().equals( "personUniversalIdentifier" ) ) {
                                linkUserReference(bo, universalUser, rel.getParentAttributeName(), entry.getKey() );
                                break;
                            }
                        }
                    }                    
                }
            }
            
            Map<String, Class> references = persistenceStructureService.listReferenceObjectFields(bo);

            // walk through the ref objects, only doing work if they are KualiUser or UniversalUser
            for (Iterator<String> iter = references.keySet().iterator(); iter.hasNext();) {
                String refField = "";
                Class refClass = null;
                refField = iter.next();
                refClass = references.get(refField);
                if (UniversalUser.class.equals(refClass)) {
                    String fkFieldName = persistenceStructureService.getForeignKeyFieldName(bo.getClass(), refField, "personUniversalIdentifier");
                    universalUser = (UniversalUser) ObjectUtils.getPropertyValue(bo, refField);
                    if (universalUser != null) {
                        linkUserReference(bo, universalUser, refField, fkFieldName);
                    }
                }
            }
        }
    }

    /**
     * 
     * This method links a single UniveralUser back to the parent BO based on the authoritative personUserIdentifier.
     * 
     * @param bo
     * @param referenceFieldName
     * @param referenceClass
     */
    private void linkUserReference(PersistableBusinessObject bo, UniversalUser user, String refFieldName, String fkFieldName) {

        // if the UserId field is blank, there's nothing we can do, so quit
        if (StringUtils.isBlank(user.getPersonUserIdentifier())) {
            return;
        }

        // attempt to load the user from the user-name, exit quietly if the user isnt found
        UniversalUser userFromDb = getUniversalUserFromUserName(user.getPersonUserIdentifier().toUpperCase());
        if (userFromDb == null) {
            return;
        }

        // attempt to set the universalId on the parent BO
        setBoField(bo, fkFieldName, userFromDb.getPersonUniversalIdentifier());

        // setup a minimally populated user object ... this is not getting fully populated as this
        // seems to cause errors in XStream, due to the system not really expecting all the sub-objects
        // to be fully populated
        UniversalUser newUserObject = new UniversalUser();
        newUserObject.setPersonUniversalIdentifier(userFromDb.getPersonUniversalIdentifier());
        newUserObject.setPersonUserIdentifier(userFromDb.getPersonUserIdentifier());

        // attempt to set the minimally populated user object to the parent BO
        setBoField(bo, refFieldName, newUserObject);
    }

    private void setBoField(PersistableBusinessObject bo, String fieldName, Object fieldValue) {
        try {
            ObjectUtils.setObjectProperty(bo, fieldName, fieldValue.getClass(), fieldValue);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not set field [" + fieldName + "] on BO to value: " + fieldValue.toString() + " (see nested exception for details).", e);
        }
    }

    /**
     * 
     * This method obtains a populated UniversalUser instance, if one exists by the userName, and returns it. Null instance is
     * returned if an account cannot be found by userName.
     * 
     * @param userName String containing the userName.
     * @return Returns a populated UniversalUser for the given userName, or Null if a record cannot be found for this userName
     * 
     */
    private UniversalUser getUniversalUserFromUserName(String userName) {
        UniversalUser user = null;
        try {
            user = universalUserService.getUniversalUser(new AuthenticationUserId(userName));
        }
        catch (UserNotFoundException e) {
            // do nothing, return a null object
        }
        return user;
    }

    /**
     * Gets the businessObjectDao attribute.
     * 
     * @return Returns the businessObjectDao.
     */
    public BusinessObjectDao getBusinessObjectDao() {
        return businessObjectDao;
    }

    /**
     * Sets the businessObjectDao attribute value.
     * 
     * @param businessObjectDao The businessObjectDao to set.
     */
    public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * 
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Sets the kualiUserService attribute value.
     * 
     * @param kualiUserService The kualiUserService to set.
     */
    public final void setUniversalUserService(UniversalUserService kualiUserService) {
        this.universalUserService = kualiUserService;
    }

    /**
     * Sets the persistenceService attribute value.
     * 
     * @param persistenceService The persistenceService to set.
     */
    public final void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return businessObjectMetaDataService;
    }

    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService boMetadataService) {
        this.businessObjectMetaDataService = boMetadataService;
    }

}