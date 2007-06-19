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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.authorization.AuthorizationStore;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.AuthorizationDefinition;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.service.AuthorizationService;
import org.kuali.core.service.DataDictionaryService;


/**
 * Most frequently, isAuthorized(group,action,targetType) will be called from isAuthorized(user,action,target) from inside the loop,
 * so it'd be a good idea to optimize getting an answer for a given group...
 */
public class AuthorizationServiceImpl implements AuthorizationService {
    // logger
    private static Log LOG = LogFactory.getLog(AuthorizationServiceImpl.class);

    private AuthorizationStore authorizationStore;
    private DataDictionaryService dataDictionaryService;

    private boolean disabled;

    /**
     * Constructs an empty AuthorizationServiceImpl instance
     */
    public AuthorizationServiceImpl() {
        disabled = false;
        authorizationStore = new AuthorizationStore();
    }

//    /**
//     * Creates and initializes the authorizationStore which will be used by the authorizationService
//     */
//    public void completeInitialization( DataDictionary dataDictionary ) {
//        LOG.info("loading authorization data");
//
//        Map documentEntries = dataDictionary.getDocumentEntries();
//        for (Iterator i = documentEntries.entrySet().iterator(); i.hasNext();) {
//            DocumentEntry documentEntry = (DocumentEntry) ((Map.Entry) i.next()).getValue();
//
//            String documentType = documentEntry.getDocumentTypeName();
//            Map authorizationDefinitions = documentEntry.getAuthorizationDefinitions();
//            for (Iterator j = authorizationDefinitions.entrySet().iterator(); j.hasNext();) {
//                AuthorizationDefinition auth = (AuthorizationDefinition) ((Map.Entry) j.next()).getValue();
//
//                String authorizedAction = auth.getAction();
//                Set authorizedGroups = auth.getGroupNames();
//                for (Iterator k = authorizedGroups.iterator(); k.hasNext();) {
//                    String authorizedGroup = (String) k.next();
//
//                    authorizationStore.addAuthorization(authorizedGroup, authorizedAction, documentType);
//                }
//            }
//        }
//        LOG.info("completed loading authorization data");
//    }
    
    public void setupAuthorizations(DocumentEntry documentEntry) {

        String documentType = documentEntry.getDocumentTypeName();
        Map authorizationDefinitions = documentEntry.getAuthorizationDefinitions();
        for (Iterator j = authorizationDefinitions.entrySet().iterator(); j.hasNext();) {
            AuthorizationDefinition auth = (AuthorizationDefinition) ((Map.Entry) j.next()).getValue();

            String authorizedAction = auth.getAction();
            Set authorizedGroups = auth.getGroupNames();
            for (Iterator k = authorizedGroups.iterator(); k.hasNext();) {
                String authorizedGroup = (String) k.next();

                authorizationStore.addAuthorization(authorizedGroup, authorizedAction, documentType);
            }
        }
    }

    /**
     * @see org.kuali.core.service.AuthorizationService#isAuthorized(org.kuali.core.bo.user.KualiUser, java.lang.String,
     *      java.lang.String)
     */
    public boolean isAuthorized(UniversalUser user, String action, String targetType) {
        return disabled || authorizationStore.isAuthorized(user, action, targetType);
    }
    

    /**
     * @see org.kuali.core.service.AuthorizationService#getAuthorizedWorkgroups(java.lang.String, java.lang.String)
     */
    public Set getAuthorizedWorkgroups(String action, String targetType) {
        Map authorizedActions = authorizationStore.authorizedActions(targetType);
        return authorizationStore.authorizedGroups(authorizedActions, action);
    }

    /**
     * @see org.kuali.core.service.AuthorizationService#isAuthorizedToViewAttribute(org.kuali.core.bo.user.KualiUser,
     *      java.lang.String, java.lang.String)
     */
    public boolean isAuthorizedToViewAttribute(UniversalUser user, String entryName, String attributeName) {
        boolean authorized = true;

        String displayWorkgroupName = this.dataDictionaryService.getAttributeDisplayWorkgroup(entryName, attributeName);
        if (StringUtils.isNotBlank(displayWorkgroupName)) {
            if (!user.isMember( displayWorkgroupName )) {
                authorized = false;
            }
        }

        return authorized;
    }

    /**
     * If disable is true, isAuthorized will thenceforth always return true regardless of the contents of the authorzationStore; if
     * false, isAuthorized will return results based on the contents of the authorizationStore.
     * 
     * @param hackedValue
     */
    public void disableAuthorization(boolean disable) {
        this.disabled = disable;
    }


    /* spring-injected services */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }
}
