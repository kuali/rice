/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.service;

import org.kuali.rice.core.api.namespace.NamespaceService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.krad.dao.BusinessObjectDao;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.util.OjbCollectionHelper;
import org.kuali.rice.krad.workflow.service.WorkflowAttributePropertyResolutionService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class KRADServiceLocatorInternal {

    public static final String VALIDATION_COMPLETION_UTILS = "validationCompletionUtils";

    public static <T extends Object> T getService(String serviceName) {
    	return GlobalResourceLoader.<T>getService(serviceName);
    }


    public static final String MAIL_SERVICE = "mailService";

    public static final MailService getMailService() {
	return (MailService) getService(MAIL_SERVICE);
    }

    public static final String POST_PROCESSOR_SERVICE = "postProcessorService";

    public static PostProcessorService getPostProcessorService() {
	return (PostProcessorService) getService(POST_PROCESSOR_SERVICE);
    }

    public static final String NAMESPACE_SERVICE = "namespaceService";

    public static NamespaceService getNamespaceService() {
	return (NamespaceService) getService(NAMESPACE_SERVICE);
    }

    public static final String OJB_COLLECTION_HELPER = "ojbCollectionHelper";

    public static OjbCollectionHelper getOjbCollectionHelper() {
	return (OjbCollectionHelper) getService(OJB_COLLECTION_HELPER);
    }

    public static final String TRANSACTION_MANAGER = "transactionManager";

    public static PlatformTransactionManager getTransactionManager() {
	return (PlatformTransactionManager) getService(TRANSACTION_MANAGER);
    }

    public static final String TRANSACTION_TEMPLATE = "transactionTemplate";

    public static TransactionTemplate getTransactionTemplate() {
	return (TransactionTemplate) getService(TRANSACTION_TEMPLATE);
    }

    public static final String INACTIVATION_BLOCKING_DISPLAY_SERVICE = "inactivationBlockingDisplayService";
    
    public static InactivationBlockingDisplayService getInactivationBlockingDisplayService() {
    	return (InactivationBlockingDisplayService) getService(INACTIVATION_BLOCKING_DISPLAY_SERVICE);
    }

    public static final String DATA_DICTIONARY_COMPONENT_PUBLISHER_SERVICE = "dataDictionaryComponentPublisherService";

    public static DataDictionaryComponentPublisherService getDataDictionaryComponentPublisherService() {
        return getService(DATA_DICTIONARY_COMPONENT_PUBLISHER_SERVICE);
    }

    
    public static final String DOCUMENT_DAO = "documentDao";
    
    public static DocumentDao getDocumentDao() {
        return (DocumentDao) getService(DOCUMENT_DAO);
    }
    
    public static final String BUSINESS_OBJECT_DAO = "businessObjectDao";
    
    public static BusinessObjectDao getBusinessObjectDao() {
        return (BusinessObjectDao) getService(BUSINESS_OBJECT_DAO);
    }
    

   public static final String DB_PLATFORM = "dbPlatform";
    
    public static DatabasePlatform getDatabasePlatform() {
        return (DatabasePlatform) getService(DB_PLATFORM);
    }
    
    public static final String MAINTENANCE_DOCUMENT_AUTHORIZATION_SERVICE = "maintenanceDocumentAuthorizationService";
    
    public static BusinessObjectAuthorizationService getMaintenanceDocumentAuthorizationService() {
    	return (BusinessObjectAuthorizationService) getService(MAINTENANCE_DOCUMENT_AUTHORIZATION_SERVICE);
    }

    public static final String WORKFLOW_ATTRIBUTE_PROPERTY_RESOLUTION_SERVICE = "workflowAttributesPropertyResolutionService";
    
    public static WorkflowAttributePropertyResolutionService getWorkflowAttributePropertyResolutionService() {
    	return (WorkflowAttributePropertyResolutionService) getService(WORKFLOW_ATTRIBUTE_PROPERTY_RESOLUTION_SERVICE);
    }
    
    public static final String INACTIVATEABLE_FROM_TO_SERVICE = "inactivateableFromToService";
    
    public static InactivateableFromToService getInactivateableFromToService() {
    	return (InactivateableFromToService) getService(INACTIVATEABLE_FROM_TO_SERVICE);
    }
    
}
