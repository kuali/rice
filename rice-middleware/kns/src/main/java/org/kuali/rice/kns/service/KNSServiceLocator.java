/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.service;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.question.Question;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataObjectMetaDataService;
import org.kuali.rice.krad.service.DocumentHeaderService;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.service.LegacyAppFrameworkAdapterService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.service.util.OjbCollectionHelper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Service locator for the KRAD Web module
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated As of release 2.0
 */
@Deprecated
public class KNSServiceLocator {

    public static final String BUSINESS_OBJECT_AUTHORIZATION_SERVICE = "businessObjectAuthorizationService";
    public static final String BUSINESS_OBJECT_METADATA_SERVICE = "businessObjectMetaDataService";
    public static final String BUSINESS_OBJECT_DICTIONARY_SERVICE = "businessObjectDictionaryService";
    public static final String DATA_DICTIONARY_SERVICE = "dataDictionaryService";
    public static final String DICTIONARY_VALIDATION_SERVICE = "knsDictionaryValidationService";
    public static final String DOCUMENT_HELPER_SERVICE = "documentHelperService";
    public static final String LOOKUP_RESULTS_SERVICE = "lookupResultsService";
    public static final String KUALI_INQUIRABLE = "kualiInquirable";
    public static final String KUALI_LOOKUPABLE = "kualiLookupable";
    public static final String MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE = "maintenanceDocumentDictionaryService";
    public static final String TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE = "transactionalDocumentDictionaryService";
    public static final String KNS_SESSION_DOCUMENT_SERVICE = "knsSessionDocumentService";
    public static final String WORKFLOW_ATTRIBUTE_PROPERTY_RESOLUTION_SERVICE = "workflowAttributesPropertyResolutionService";
    public static final String TRANSACTION_MANAGER = "transactionManager";
    public static final String TRANSACTION_TEMPLATE = "transactionTemplate";
    public static final String MAINTENANCE_DOCUMENT_AUTHORIZATION_SERVICE = "maintenanceDocumentAuthorizationService";
    public static final String BUSINESS_OBJECT_SERVICE = "businessObjectService";
    public static final String DATA_OBJECT_METADATA_SERVICE = "dataObjectMetaDataService";
    public static final String KEY_VALUES_SERVICE = "keyValuesService";
    public static final String SESSION_DOCUMENT_SERVICE = "knsSessionDocumentService";
    public static final String OJB_COLLECTION_HELPER = "ojbCollectionHelper";
    public static final String PERSISTENCE_SERVICE = "persistenceService";
    public static final String PERSISTENCE_STRUCTURE_SERVICE = "persistenceStructureService";
    public static final String SEQUENCE_ACCESSOR_SERVICE = "sequenceAccessorService";
    public static final String DOCUMENT_HEADER_SERVICE = "documentHeaderService";
    public static final String LEGACY_DATA_ADAPTER_FRAMEWORK = "legacyAppFrameworkAdapter";

    @Deprecated
    public static <T extends Object> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    @Deprecated
    public static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        return getService(BUSINESS_OBJECT_AUTHORIZATION_SERVICE);
    }

    @Deprecated
    public static BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return getService(BUSINESS_OBJECT_METADATA_SERVICE);
    }

    @Deprecated
    public static DictionaryValidationService getKNSDictionaryValidationService() {
	    return (DictionaryValidationService) getService(DICTIONARY_VALIDATION_SERVICE);
    }

    @Deprecated
    public static LookupResultsService getLookupResultsService() {
        return (LookupResultsService) getService(LOOKUP_RESULTS_SERVICE);
    }

    @Deprecated
    public static Inquirable getKualiInquirable() {
        return getService(KUALI_INQUIRABLE);
    }

    @Deprecated
    public static Lookupable getKualiLookupable() {
        return getService(KUALI_LOOKUPABLE);
    }

    @Deprecated
    public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        return getService(MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE);
    }

    @Deprecated
    public static TransactionalDocumentDictionaryService getTransactionalDocumentDictionaryService() {
        return (TransactionalDocumentDictionaryService) getService(TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE);
    }

    @Deprecated
    public static org.kuali.rice.kns.service.SessionDocumentService getKNSSessionDocumentService() {
        return  getService(KNS_SESSION_DOCUMENT_SERVICE);
    }

    @Deprecated
    public static Lookupable getLookupable(String lookupableName) {
        return getService(lookupableName);
    }

    @Deprecated
    public static DataDictionaryService getDataDictionaryService() {
        return getService(DATA_DICTIONARY_SERVICE);
    }

    @Deprecated
    public static BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return getService(BUSINESS_OBJECT_DICTIONARY_SERVICE);
    }

    @Deprecated
    public static DocumentHelperService getDocumentHelperService() {
        return getService(DOCUMENT_HELPER_SERVICE);
    }

    @Deprecated
    public static Question getQuestion(String questionName) {
        return (Question) getService(questionName);
    }

    @Deprecated
    public static WorkflowAttributePropertyResolutionService getWorkflowAttributePropertyResolutionService() {
    	return (WorkflowAttributePropertyResolutionService) getService(WORKFLOW_ATTRIBUTE_PROPERTY_RESOLUTION_SERVICE);
    }

    @Deprecated
    public static PlatformTransactionManager getTransactionManager() {
	    return (PlatformTransactionManager) getService(TRANSACTION_MANAGER);
    }

    @Deprecated
    public static TransactionTemplate getTransactionTemplate() {
	    return (TransactionTemplate) getService(TRANSACTION_TEMPLATE);
    }

    @Deprecated
    public static BusinessObjectAuthorizationService getMaintenanceDocumentAuthorizationService() {
    	return (BusinessObjectAuthorizationService) getService(MAINTENANCE_DOCUMENT_AUTHORIZATION_SERVICE);
    }

    @Deprecated
    public static BusinessObjectService getBusinessObjectService(){
        return (BusinessObjectService) getService(BUSINESS_OBJECT_SERVICE);
    }

    @Deprecated
    public static DataObjectMetaDataService getDataObjectMetaDataService() {
        return (DataObjectMetaDataService) getService(DATA_OBJECT_METADATA_SERVICE);
    }

    @Deprecated
    public static KeyValuesService getKeyValuesService() {
        return getService(KEY_VALUES_SERVICE);
    }

    @Deprecated
    public static SessionDocumentService getSessionDocumentService() {
        return getService(SESSION_DOCUMENT_SERVICE);
    }

    @Deprecated
    public static OjbCollectionHelper getOjbCollectionHelper() {
        return (OjbCollectionHelper) getService(OJB_COLLECTION_HELPER);
    }
    @Deprecated
    public static DocumentHeaderService getDocumentHeaderService() {
        return getService(DOCUMENT_HEADER_SERVICE);
    }


    @Deprecated
    public static PersistenceService getPersistenceService() {
        return getService(PERSISTENCE_SERVICE);
    }

    @Deprecated
    public static PersistenceStructureService getPersistenceStructureService() {
        return getService(PERSISTENCE_STRUCTURE_SERVICE);
    }

    @Deprecated
    public static SequenceAccessorService getSequenceAccessorService() {
        return getService(SEQUENCE_ACCESSOR_SERVICE);
    }

	public static LegacyAppFrameworkAdapterService getLegacyAppFrameworkAdapterService() {
		return (LegacyAppFrameworkAdapterService) getService(LEGACY_DATA_ADAPTER_FRAMEWORK);
	}
}
