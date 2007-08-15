/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.RiceConstants;
import org.kuali.core.KualiModule;
import org.kuali.core.bo.BusinessObjectRelationship;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.KualiModuleUserProperty;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.bo.user.UuId;
import org.kuali.core.dao.LookupDao;
import org.kuali.core.dao.UniversalUserDao;
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectMetaDataService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.service.KualiModuleUserPropertyService;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.clientapp.vo.EmplIdVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UuIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.BaseUserService;
import edu.iu.uis.eden.user.BaseWorkflowUser;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;

/**
 * This class is the service implementation for the KualiUser structure. This is the default implementation, that is delivered with
 * Kuali. This implementation retrieves the user based on a relational data structure that we have defined withing the KUL schema.
 */
@Transactional
public class UniversalUserServiceImpl extends BaseUserService implements UniversalUserService {
    private static final Logger LOG = Logger.getLogger(UniversalUserServiceImpl.class);
    private static final String IS_UNIVERSAL_USER_PROPERTY_METHOD_ERROR = "UniversalUserServiceImpl encountered an exception while attempting to determine whether a property name references UniversalUser - propertyName: ";
    private static final String RESOLVE_USER_IDENTIFIERS_TO_UNIVERSAL_IDENTIFIERS_METHOD_ERROR = "UniversalUserServiceImpl encountered an exception while attempting to result user identifier to universal identifier - propertyName: ";

    private UniversalUserDao universalUserDao;
    private KualiModuleUserPropertyService moduleUserPropertyService;
    private KualiGroupService kualiGroupService;
    private BusinessObjectMetaDataService businessObjectMetaDataService;
    private DataDictionaryService dataDictionaryService;
    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    private LookupDao lookupDao;
    private KualiModuleService kualiModuleService;
//    private KualiConfigurationService kualiConfigurationService;
    private DateTimeService dateTimeService;
    
    private String supervisorWorkgroup;
    private String workflowExceptionWorkgroup;

    /**
     * @see org.kuali.core.service.UniversalUserService#getUniversalUser(org.kuali.core.bo.user.UserId)
     */
    public UniversalUser getUniversalUser(UserId userId) throws UserNotFoundException {
        UniversalUser user = universalUserDao.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("unable to find universaluser for userId '" + userId + "'");
        }
        return user;
    }

    public UniversalUser getUniversalUserByAuthenticationUserId(String authenticationUserId ) throws UserNotFoundException {
        return getUniversalUser(new AuthenticationUserId(authenticationUserId));
    }
    
    public UniversalUser getUniversalUser(String personUniversalIdentifier) throws UserNotFoundException {
        return getUniversalUser(new UuId(personUniversalIdentifier));
    }

    public UniversalUser updateUniversalUserIfNecessary(String sourcePersonUniversalIdentifier, UniversalUser currentSourceUniversalUser) {
        if (currentSourceUniversalUser == null 
                || (sourcePersonUniversalIdentifier != null && !sourcePersonUniversalIdentifier.equals(currentSourceUniversalUser.getPersonUniversalIdentifier()))
                || currentSourceUniversalUser.getVersionNumber() == null ) {
            try {
                return getUniversalUser(new UuId(sourcePersonUniversalIdentifier));
            }
            catch (UserNotFoundException unfe) {
            	if ( currentSourceUniversalUser == null ) {
            		return new UniversalUser();
            	}
            }
        }
        return currentSourceUniversalUser;
    }

    /**
     * @see org.kuali.core.service.UniversalUserService#findUniversalUsers(java.util.Map)
     */
    public Collection findUniversalUsers(Map formFields) {
        String moduleCode = (String)formFields.get( "activeModuleCodeString" );
        if ( StringUtils.isNotBlank( moduleCode ) ) {
            KualiModule module = kualiModuleService.getModuleByCode( moduleCode );
            if ( module != null ) {
                return lookupDao.findCollectionBySearchHelper(UniversalUser.class, formFields, false, KNSServiceLocator.getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(UniversalUser.class, formFields), module.getModuleUserService().getUserActiveCriteria());
            }
        }
        return lookupDao.findCollectionBySearchHelper(UniversalUser.class, formFields, false, KNSServiceLocator.getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(UniversalUser.class, formFields));
    }

    // TODO WARNING: this does not support nested joins, because i don't have a test case
    public Collection findWithUniversalUserJoin(Class businessObjectClass, Map fieldValues, boolean unbounded) {
        return lookupDao.findCollectionBySearchHelperWithUniversalUserJoin(businessObjectClass, 
                getNonUniversalUserSearchCriteria(businessObjectClass, fieldValues), 
                getUniversalUserSearchCriteria(businessObjectClass, fieldValues), unbounded, 
                KNSServiceLocator.getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(businessObjectClass, fieldValues));
    }

    public boolean hasUniversalUserProperty(Class businessObjectClass, Map fieldValues) {
        boolean hasUniversalUserProperty = false;
        Iterator propertyNameItr = fieldValues.keySet().iterator();
        while (propertyNameItr.hasNext() && !hasUniversalUserProperty) {
            hasUniversalUserProperty = isUniversalUserProperty(businessObjectClass, (String) propertyNameItr.next());
        }
        return hasUniversalUserProperty;
    }

    /**
     * @see org.kuali.core.service.UniversalUserService#resolveUserIdentifiersToUniversalIdentifiers(PersistableBusinessObject, java.util.Map)
     */
    public Map resolveUserIdentifiersToUniversalIdentifiers(PersistableBusinessObject businessObject, Map fieldValues) {
        Map processedFieldValues = getNonUniversalUserSearchCriteria(businessObject.getClass(), fieldValues);
        Iterator propertyNameItr = fieldValues.keySet().iterator();
        while (propertyNameItr.hasNext()) {
            String propertyName = (String) propertyNameItr.next();
            if (isUniversalUserProperty(businessObject.getClass(), propertyName) && !StringUtils.isBlank((String) fieldValues.get(propertyName))) {
                String universalUserPropertyName = propertyName.substring(propertyName.lastIndexOf(".") + 1);
                if ("personUserIdentifier".equals(universalUserPropertyName)) {
                    Map searchCriteria = new HashMap();
                    searchCriteria.put("personUserIdentifier", fieldValues.get(propertyName).toString().toUpperCase());
                    // since personUserIdentifier is not the PK of UnivUser, then we are not using the PK of the UU BO to search (hence, the second false parameter)
                    Collection universalUsers = lookupDao.findCollectionBySearchHelper(UniversalUser.class, searchCriteria, false, false);
                    String universalUserReferenceObjectPropertyName = StringUtils.substringBeforeLast(propertyName, ".");
                    Class targetBusinessObjectClass = null;
                    try {
                        StringBuffer resolvedPersonUniversalIdentifierPropertyName = new StringBuffer();
                        if (universalUserReferenceObjectPropertyName.indexOf(".") > 0) {
                            String targetBusinessObjectPropertyName = StringUtils.substringBeforeLast(universalUserReferenceObjectPropertyName, ".");
                            Object targetProperty = PropertyUtils.getProperty(businessObject, targetBusinessObjectPropertyName);
                            if (targetProperty != null) {
                                targetBusinessObjectClass = targetProperty.getClass();
                                resolvedPersonUniversalIdentifierPropertyName.append(targetBusinessObjectPropertyName).append(".");
                            } else {
                                LOG.info("Could not find target property for "+propertyName+" in class of "+businessObject.getClass().getName());
                            }
                        }
                        else {
                            targetBusinessObjectClass = businessObject.getClass();
                        }
                        if (targetBusinessObjectClass != null) {
                            String propName = universalUserReferenceObjectPropertyName.substring(universalUserReferenceObjectPropertyName.lastIndexOf(".") + 1);
                            String institutionalIdSourcePrimitivePropertyName = (String) dataDictionaryService.getRelationshipAttributeMap(targetBusinessObjectClass.getName(), propName).get("personUniversalIdentifier");
                            resolvedPersonUniversalIdentifierPropertyName.append(institutionalIdSourcePrimitivePropertyName);
                            if (universalUsers != null && universalUsers.size() == 1) {
                                processedFieldValues.put(resolvedPersonUniversalIdentifierPropertyName.toString(), ((UniversalUser) ((List) universalUsers).get(0)).getPersonUniversalIdentifier());
                            }
                            else {
                                processedFieldValues.put(resolvedPersonUniversalIdentifierPropertyName.toString(), null);
                            }
                        } else {
                            processedFieldValues.put(resolvedPersonUniversalIdentifierPropertyName.toString(), null);
                        }
                    }
                    catch (IllegalAccessException e) {
                        throw new RuntimeException(RESOLVE_USER_IDENTIFIERS_TO_UNIVERSAL_IDENTIFIERS_METHOD_ERROR + propertyName, e);
                    }
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(RESOLVE_USER_IDENTIFIERS_TO_UNIVERSAL_IDENTIFIERS_METHOD_ERROR + propertyName, e);
                    }
                    catch (NoSuchMethodException e) {
                        throw new RuntimeException(RESOLVE_USER_IDENTIFIERS_TO_UNIVERSAL_IDENTIFIERS_METHOD_ERROR + propertyName, e);
                    }
                    catch (NumberFormatException e) {
                        throw new RuntimeException(RESOLVE_USER_IDENTIFIERS_TO_UNIVERSAL_IDENTIFIERS_METHOD_ERROR + propertyName, e);
                    }
                }
            } else if (propertyName.endsWith("personUserIdentifier")){
                // if we're adding to a collection and we've got the personUserIdentifier; let's populate universalUser
                Object personUserIdentifier = fieldValues.get(propertyName);
                if (personUserIdentifier != null) {
                    String containerPropertyName = propertyName;
                    if (containerPropertyName.startsWith(RiceConstants.MAINTENANCE_ADD_PREFIX)) {
                        containerPropertyName = propertyName.substring(RiceConstants.MAINTENANCE_ADD_PREFIX.length());
                    }
                    // get the class of the object that is referenced by the property name
                    if (containerPropertyName.indexOf(".") > 0) {
                        String collectionName = containerPropertyName.substring(0, containerPropertyName.indexOf("."));
                        // what is the class held by that collection?
                        Class collectionBusinessObjectClass = maintenanceDocumentDictionaryService.getCollectionBusinessObjectClass(maintenanceDocumentDictionaryService.getDocumentTypeName(businessObject.getClass()), collectionName);
                        if (collectionBusinessObjectClass != null) {
                            // we are adding to a collection; get the relationships for that object; is there one for personUniversalIdentifier?
                            try {
                                List<BusinessObjectRelationship> relationships = businessObjectMetaDataService.getBusinessObjectRelationships( (PersistableBusinessObject)collectionBusinessObjectClass.newInstance() );
                                for ( BusinessObjectRelationship rel : relationships ) {
                                    for ( Map.Entry<String,String> entry : rel.getParentToChildReferences().entrySet() ) {
                                        if ( entry.getValue().equals( "personUniversalIdentifier" ) ) {
                                            // there is a relationship for personUserIdentifier; use that to find the universal user
                                            String fieldPrefix = propertyName.substring(0, propertyName.substring(0, propertyName.lastIndexOf(".personUserIdentifier")).lastIndexOf("."));
                                            String relatedUniversalUserPropertyName = fieldPrefix + "." + entry.getKey();
                                            Object currRelatedUniversalUser = processedFieldValues.get(relatedUniversalUserPropertyName);
                                            if (currRelatedUniversalUser == null || (currRelatedUniversalUser instanceof String && ((String)currRelatedUniversalUser).length() == 0)) {
                                                try {
                                                    UniversalUser universalUser = this.getUniversalUserByAuthenticationUserId(personUserIdentifier.toString().toUpperCase());
                                                        processedFieldValues.put(relatedUniversalUserPropertyName, universalUser.getPersonUniversalIdentifier());
                                                } catch (UserNotFoundException unfe) {
                                                    LOG.info("User "+personUserIdentifier.toString()+" was not found.");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            catch (InstantiationException e) {
                                throw new RuntimeException(RESOLVE_USER_IDENTIFIERS_TO_UNIVERSAL_IDENTIFIERS_METHOD_ERROR + propertyName, e);
                            }
                            catch (IllegalAccessException e) {
                                throw new RuntimeException(RESOLVE_USER_IDENTIFIERS_TO_UNIVERSAL_IDENTIFIERS_METHOD_ERROR + propertyName, e);
                            }
                        }
                    }
                }
            }
        }
        return processedFieldValues;
    }

    private Map getUniversalUserSearchCriteria(Class businessObjectClass, Map fieldValues) {
        Map allUniversalUserSearchCriteria = new HashMap();
        Iterator propertyNameItr = fieldValues.keySet().iterator();
        while (propertyNameItr.hasNext()) {
            String propertyName = (String) propertyNameItr.next();
            if (isUniversalUserProperty(businessObjectClass, propertyName) && !StringUtils.isBlank((String) fieldValues.get(propertyName))) {
                ControlDefinition controlDefinition = dataDictionaryService.getAttributeControlDefinition(businessObjectClass, propertyName);
                if ((controlDefinition != null) && !controlDefinition.isHidden()) {
                    String universalUserReferenceObjectPropertyName = propertyName.substring(0, propertyName.lastIndexOf("."));
                    String universalUserSearchFieldName = propertyName.substring(propertyName.lastIndexOf(".") + 1);
                    String institutionalIdSourcePrimitivePropertyName = (String) dataDictionaryService.getRelationshipAttributeMap(businessObjectClass.getName(), universalUserReferenceObjectPropertyName).get("personUniversalIdentifier");
                    if (allUniversalUserSearchCriteria.containsKey(institutionalIdSourcePrimitivePropertyName)) {
                        ((Map) allUniversalUserSearchCriteria.get(institutionalIdSourcePrimitivePropertyName)).put(universalUserSearchFieldName, fieldValues.get(propertyName));
                    }
                    else {
                        Map universalUserReferenceObjectSearchCriteria = new HashMap();
                        universalUserReferenceObjectSearchCriteria.put(universalUserSearchFieldName, fieldValues.get(propertyName));
                        allUniversalUserSearchCriteria.put(institutionalIdSourcePrimitivePropertyName, universalUserReferenceObjectSearchCriteria);
                    }
                }
            }
        }
        return allUniversalUserSearchCriteria;
    }

    private Map getNonUniversalUserSearchCriteria(Class businessObjectClass, Map fieldValues) {
        Map nonUniversalUserSearchCriteria = new HashMap();
        Iterator propertyNameItr = fieldValues.keySet().iterator();
        while (propertyNameItr.hasNext()) {
            String propertyName = (String) propertyNameItr.next();
            if (!isUniversalUserProperty(businessObjectClass, propertyName)) {
                nonUniversalUserSearchCriteria.put(propertyName, fieldValues.get(propertyName));
            }
        }
        return nonUniversalUserSearchCriteria;
    }

    private boolean isUniversalUserProperty(Class businessObjectClass, String propertyName) {
        try {
            return (propertyName.indexOf(".") > 0) && !(StringUtils.contains(propertyName, "add.")) && UniversalUser.class.equals(PropertyUtils.getPropertyType(businessObjectClass.newInstance(), propertyName.substring(0, propertyName.lastIndexOf("."))));
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(IS_UNIVERSAL_USER_PROPERTY_METHOD_ERROR + propertyName, e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(IS_UNIVERSAL_USER_PROPERTY_METHOD_ERROR + propertyName, e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(IS_UNIVERSAL_USER_PROPERTY_METHOD_ERROR + propertyName, e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(IS_UNIVERSAL_USER_PROPERTY_METHOD_ERROR + propertyName, e);
        }
    }

    public void setUniversalUserDao(UniversalUserDao userDao) {
        this.universalUserDao = userDao;
    }

    /**
     * setter for spring injected group service
     * 
     * @param kualiGroupService The kualiGroupService to set.
     */
    public void setKualiGroupService(KualiGroupService kualiGroupService) {
        this.kualiGroupService = kualiGroupService;
    }


    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    // workflow user service methods
    public void save(WorkflowUser user) {
        BaseWorkflowUser simpleUser = (BaseWorkflowUser) user;
        Timestamp currentTimestamp = dateTimeService.getCurrentTimestamp();
        if (user.getWorkflowId() == null || simpleUser.getCreateDate() == null) {
            simpleUser.setCreateDate(currentTimestamp);
        }
        else {
            removeFromCache(user.getWorkflowUserId());
        }
        simpleUser.setLastUpdateDate(currentTimestamp);
        universalUserDao.save(simpleUser);
    }

    public WorkflowUser getWorkflowUser(UserIdVO userId) throws EdenUserNotFoundException {
        return universalUserDao.getWorkflowUser(getWorkflowUserId(userId));
    }

    public WorkflowUser getWorkflowUser(edu.iu.uis.eden.user.UserId userId) throws EdenUserNotFoundException {
        WorkflowUser user = getFromCache(userId);
        if (user == null) {
            user = (WorkflowUser) universalUserDao.getWorkflowUser(userId);
            if (user == null) {
                throw new EdenUserNotFoundException("User is invalid. userId " + userId.toString());
            }
            else {
                addToCache(user);
            }
        }
        return user;
    }

    public List search(WorkflowUser user, boolean useWildcards) {
        return universalUserDao.search(user, useWildcards);
    }

    private edu.iu.uis.eden.user.UserId getWorkflowUserId(UserIdVO userId) throws EdenUserNotFoundException {
        edu.iu.uis.eden.user.UserId userIdInterface = null;
        if (userId instanceof EmplIdVO) {
            userIdInterface = new edu.iu.uis.eden.user.EmplId(((EmplIdVO) userId).getEmplId());
        }
        else if (userId instanceof NetworkIdVO) {
            userIdInterface = new edu.iu.uis.eden.user.AuthenticationUserId(((NetworkIdVO) userId).getNetworkId());
        }
        else if (userId instanceof UuIdVO) {
            userIdInterface = new edu.iu.uis.eden.user.UuId(((UuIdVO) userId).getUuId());
        }
        else if (userId instanceof WorkflowIdVO) {
            userIdInterface = new WorkflowUserId(((WorkflowIdVO) userId).getWorkflowId());
        }
        else {
            throw new EdenUserNotFoundException("Attempting to fetch user with unknown id type");
        }
        return userIdInterface;
    }

    public WorkflowUser getBlankUser() {
        return new org.kuali.core.workflow.bo.WorkflowUser();
    }

    public void loadXmlFile(File xmlFile) {
        LOG.warn("KualiUserService cannot import XML");
    }

    public void loadXml(InputStream arg0, WorkflowUser arg1) {
        LOG.warn("KualiUserService cannot import XML");
    }

    public Map<String, KualiModuleUser> getModuleUsers(UniversalUser user) {
        return kualiModuleService.getModuleUsers(user);
    }
        
    /**
     * boolean to indicate if the user is a member of a kuali group
     * 
     * @param groupName
     * @return true if the user is a member of the group passed in
     */
    public boolean isMember(UniversalUser user, String groupName) {
        if (groupName != null && !StringUtils.isEmpty(groupName)) {
            groupName = groupName.toUpperCase().trim();
            if (isSupervisorUser(user)) { // TODO why was there a check for inquiry role?
                return true;
            }
            for (KualiGroup group : user.getGroups()) {
                if (groupName.toUpperCase().equals(group.getGroupName().toUpperCase().trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isMember(UniversalUser user, KualiGroup kualiGroup) {
        return isMember(user, kualiGroup.getGroupName());
    }

    public List<KualiGroup> getUsersGroups(UniversalUser user) {
        return kualiGroupService.getUsersGroups(user);
    }

    /**
     * check if the user is a supervisor user (belongs to all groups)
     * 
     * @return
     */
    public boolean isSupervisorUser(UniversalUser user) {
        for (KualiGroup group : user.getGroups()) {
            if (getSupervisorWorkgroup().equals(group.getGroupName().toUpperCase().trim())) {
                return true;
            }
        }
        return false;
    }

    private String getSupervisorWorkgroup() {
        if (supervisorWorkgroup == null) {
            supervisorWorkgroup = KNSServiceLocator.getKualiConfigurationService().getApplicationParameterValue(RiceConstants.CoreApcParms.GROUP_CORE_MAINT_EDOCS, RiceConstants.CoreApcParms.SUPERVISOR_WORKGROUP);
        }
        return supervisorWorkgroup;
    }

    private String getWorkflowExceptionWorkgroup() {
        if (workflowExceptionWorkgroup == null) {
            workflowExceptionWorkgroup = KNSServiceLocator.getKualiConfigurationService().getApplicationParameterValue(RiceConstants.CoreApcParms.GROUP_CORE_MAINT_EDOCS, RiceConstants.CoreApcParms.WORKFLOW_EXCEPTION_WORKGROUP);
        }
        return workflowExceptionWorkgroup;
    }

    /**
     * check if the user is an exception user (has the Exception role, belongs to the Exception workgroup, whatever)
     * 
     * @return
     */
    public boolean isWorkflowExceptionUser(UniversalUser user) {
        return isMember(user, getWorkflowExceptionWorkgroup());
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

//    public KualiConfigurationService getKualiConfigurationService() {
//        return kualiConfigurationService;
//    }
//
//    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
//        this.kualiConfigurationService = kualiConfigurationService;
//    }

    

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public Map<String,Map<String,String>> loadModuleUserProperties( UniversalUser user ) {
        // load the property objects from the DAO
        Collection<KualiModuleUserProperty> props = moduleUserPropertyService.getPropertiesForUser( user );
        
        Map<String,Map<String,String>> moduleProps = new HashMap<String,Map<String,String>>();
        // iterate over the DB objects and build the map
        for ( KualiModuleUserProperty prop : props ) {
            if ( moduleProps.get( prop.getModuleId() ) == null ) {
                moduleProps.put( prop.getModuleId(), new HashMap<String,String>() );
            }
            moduleProps.get( prop.getModuleId() ).put( prop.getName(), prop.getValue() );
        }
        return moduleProps;
    }

    public KualiModuleUserPropertyService getModuleUserPropertyService() {
        return moduleUserPropertyService;
    }

    public void setModuleUserPropertyService(KualiModuleUserPropertyService moduleUserPropertyService) {
        this.moduleUserPropertyService = moduleUserPropertyService;
    }
    
    public MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        return this.maintenanceDocumentDictionaryService;
    }
    
    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService mdds) {
        this.maintenanceDocumentDictionaryService = mdds;
    }
    
    public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return this.businessObjectMetaDataService;
    }
    
    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService boms) {
        this.businessObjectMetaDataService = boms;
    }

}