/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.dao.BusinessObjectDao;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.dbplatform.KualiDBPlatform;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.question.Question;
import org.kuali.rice.kns.util.OjbCollectionHelper;
import org.kuali.rice.kns.util.cache.MethodCacheInterceptor;
import org.kuali.rice.kns.util.spring.NamedOrderedListBean;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;
import org.kuali.rice.kns.workflow.service.WorkflowAttributePropertyResolutionService;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;

public class KNSServiceLocator<T extends Object> {

    public static final String VALIDATION_COMPLETION_UTILS = "validationCompletionUtils";

    public static Object getService(String serviceName) {
	return GlobalResourceLoader.getService(serviceName);
    }

    public static <T> T getNervousSystemContextBean(Class<T> type) {
	Collection<T> beansOfType = getBeansOfType(type).values();
	if (beansOfType.isEmpty()) {
	    throw new NoSuchBeanDefinitionException("No beans of this type in the KNS application context: "
		    + type.getName());
	}
	if (beansOfType.size() > 1) {
	    return getNervousSystemContextBean(type, type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1));
	}
	return beansOfType.iterator().next();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getNervousSystemContextBean(Class<T> type, String name) {
	return (T) RiceResourceLoaderFactory.getSpringResourceLoader().getContext().getBean(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
    	SpringResourceLoader springResourceLoader = RiceResourceLoaderFactory.getSpringResourceLoader();
    	if ( springResourceLoader != null ) {
    		return new HashMap((Map) springResourceLoader.getContext().getBeansOfType(type));
    	} else {
    		return new HashMap(0);
    	}
    }

    public static String[] getBeanNames() {
	return RiceResourceLoaderFactory.getSpringResourceLoader().getContext().getBeanDefinitionNames();
    }

    public static Set<String> getSingletonNames() {
	Set<String> singletonNames = new HashSet<String>();
	Collections.addAll(singletonNames, RiceResourceLoaderFactory.getSpringResourceLoader().getContext().getBeanFactory()
		.getSingletonNames());
	return singletonNames;
    }

    public static Set<Class> getSingletonTypes() {
	Set<Class> singletonTypes = new HashSet<Class>();
	for (String singletonName : getSingletonNames()) {
	    singletonTypes.add(RiceResourceLoaderFactory.getSpringResourceLoader().getContext().getBeanFactory().getType(
		    singletonName));
	}
	return singletonTypes;
    }

    public static boolean isSingleton( String beanName ) {
    	try {
    	    return RiceResourceLoaderFactory.getSpringResourceLoader().getContext().getBeanFactory().isSingleton(beanName);
    	} catch ( NoSuchBeanDefinitionException ex ) {
    	    // service is not in Spring so we can't assume
    	    return false;
    	}
    }
            
    public static List<NamedOrderedListBean> getNamedOrderedListBeans(String listName) {
	List<NamedOrderedListBean> namedOrderedListBeans = new ArrayList<NamedOrderedListBean>();
	for (Object namedOrderedListBean : RiceResourceLoaderFactory.getSpringResourceLoader().getContext().getBeansOfType(
		NamedOrderedListBean.class).values()) {
	    if (((NamedOrderedListBean) namedOrderedListBean).getName().equals(listName)) {
		namedOrderedListBeans.add((NamedOrderedListBean) namedOrderedListBean);
	    }
	}
	return namedOrderedListBeans;
    }

    public static final String ENCRYPTION_SERVICE = "encryptionService";

    public static final EncryptionService getEncryptionService() {
	return (EncryptionService) getService(ENCRYPTION_SERVICE);
    }
    
	public static final String EXCEPTION_INCIDENT_REPORT_SERVICE = "knsExceptionIncidentService";
    public static final KualiExceptionIncidentService getKualiExceptionIncidentService() {
    	return (KualiExceptionIncidentService) getService(EXCEPTION_INCIDENT_REPORT_SERVICE);
    }


    public static final String MAIL_SERVICE = "mailService";

    public static final MailService getMailService() {
	return (MailService) getService(MAIL_SERVICE);
    }

    public static final String METHOD_CACHE_INTERCEPTOR = "methodCacheInterceptor";

    public static MethodCacheInterceptor getMethodCacheInterceptor() {
	return (MethodCacheInterceptor) getService(METHOD_CACHE_INTERCEPTOR);
    }

    public static final String BUSINESS_OBJECT_AUTHORIZATION_SERVICE = "businessObjectAuthorizationService";

    public static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
	return (BusinessObjectAuthorizationService) getService(BUSINESS_OBJECT_AUTHORIZATION_SERVICE);
    }

    public static final String XML_OBJECT_SERIALIZER_SERVICE = "xmlObjectSerializerService";

    public static XmlObjectSerializerService getXmlObjectSerializerService() {
	return (XmlObjectSerializerService) getService(XML_OBJECT_SERIALIZER_SERVICE);
    }

    public static final String DOCUMENT_SERVICE = "documentService";

    public static DocumentService getDocumentService() {
	return (DocumentService) getService(DOCUMENT_SERVICE);
    }

    public static final String DOCUMENT_HEADER_SERVICE = "documentHeaderService";

    public static DocumentHeaderService getDocumentHeaderService() {
    return (DocumentHeaderService) getService(DOCUMENT_HEADER_SERVICE);
    }

    public static final String POST_PROCESSOR_SERVICE = "postProcessorService";

    public static PostProcessorService getPostProcessorService() {
	return (PostProcessorService) getService(POST_PROCESSOR_SERVICE);
    }

    public static final String DATETIME_SERVICE = "dateTimeService";

    public static DateTimeService getDateTimeService() {
	return (DateTimeService) getService(DATETIME_SERVICE);
    }

    public static final String LOOKUP_SERVICE = "lookupService";

    public static LookupService getLookupService() {
	return (LookupService) getService(LOOKUP_SERVICE);
    }

    public static final String LOOKUP_RESULTS_SERVICE = "lookupResultsService";

    public static LookupResultsService getLookupResultsService() {
	return (LookupResultsService) getService(LOOKUP_RESULTS_SERVICE);
    }

    public static final String KUALI_MODULE_SERVICE = "kualiModuleService";

    public static KualiModuleService getKualiModuleService() {
	return (KualiModuleService) getService(KUALI_MODULE_SERVICE);
    }

    public static final String WORKFLOW_DOCUMENT_SERVICE = "workflowDocumentService";

    public static WorkflowDocumentService getWorkflowDocumentService() {
	return (WorkflowDocumentService) getService(WORKFLOW_DOCUMENT_SERVICE);
    }

    public static final String WORKFLOW_INFO_SERVICE = "workflowInfoService";

    public static KualiWorkflowInfo getWorkflowInfoService() {
	return (KualiWorkflowInfo) getService(WORKFLOW_INFO_SERVICE);
    }

    public static final String KUALI_CONFIGURATION_SERVICE = "kualiConfigurationService";

    public static KualiConfigurationService getKualiConfigurationService() {
	return (KualiConfigurationService) getService(KUALI_CONFIGURATION_SERVICE);
    }
    
    public static final String PARAMETER_SERVICE = "parameterService";
    
    public static ParameterService getParameterService() {
    	return (ParameterService) getService(PARAMETER_SERVICE);
    }
    
    public static final String PARAMETER_SERVER_SERVICE = "parameterServerService";
    
    public static ParameterServerService getParameterServerService() {
    	return (ParameterServerService) getService(PARAMETER_SERVER_SERVICE);
    }

    public static final String BUSINESS_OBJECT_DICTIONARY_SERVICE = "businessObjectDictionaryService";

    public static BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
	return (BusinessObjectDictionaryService) getService(BUSINESS_OBJECT_DICTIONARY_SERVICE);
    }

    public static final String BUSINESS_OBJECT_METADATA_SERVICE = "businessObjectMetaDataService";

    public static BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
	return (BusinessObjectMetaDataService) getService(BUSINESS_OBJECT_METADATA_SERVICE);
    }

    public static final String TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE = "transactionalDocumentDictionaryService";

    public static TransactionalDocumentDictionaryService getTransactionalDocumentDictionaryService() {
	return (TransactionalDocumentDictionaryService) getService(TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE);
    }

    public static final String MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE = "maintenanceDocumentDictionaryService";

    public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
	return (MaintenanceDocumentDictionaryService) getService(MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE);
    }

    public static final String DATA_DICTIONARY_SERVICE = "dataDictionaryService";

    public static DataDictionaryService getDataDictionaryService() {
	return (DataDictionaryService) getService(DATA_DICTIONARY_SERVICE);
    }

    public static final String MAINTENANCE_DOCUMENT_SERVICE = "maintenanceDocumentService";

    public static MaintenanceDocumentService getMaintenanceDocumentService() {
	return (MaintenanceDocumentService) getService(MAINTENANCE_DOCUMENT_SERVICE);
    }

    public static final String NOTE_SERVICE = "noteService";

    public static NoteService getNoteService() {
	return (NoteService) getService(NOTE_SERVICE);
    }

    public static final String PERSISTENCE_SERVICE = "persistenceService";

    public static PersistenceService getPersistenceService() {
	return (PersistenceService) getService(PERSISTENCE_SERVICE);
    }

    public static final String PERSISTENCE_STRUCTURE_SERVICE = "persistenceStructureService";

    public static PersistenceStructureService getPersistenceStructureService() {
	return (PersistenceStructureService) getService(PERSISTENCE_STRUCTURE_SERVICE);
    }

    public static final String KUALI_RULE_SERVICE = "kualiRuleService";

    public static KualiRuleService getKualiRuleService() {
	return (KualiRuleService) getService(KUALI_RULE_SERVICE);
    }

    public static final String BUSINESS_OBJECT_SERVICE = "businessObjectService";

    public static BusinessObjectService getBusinessObjectService() {
	return (BusinessObjectService) getService(BUSINESS_OBJECT_SERVICE);
    }

    public static final String NAMESPACE_SERVICE = "namespaceService";

    public static NamespaceService getNamespaceService() {
	return (NamespaceService) getService(NAMESPACE_SERVICE);
    }
    
    // special ones for Inquirable and Lookupable
    public static final String KUALI_INQUIRABLE = "kualiInquirable";

    public static Inquirable getKualiInquirable() {
	return (Inquirable) getService(KUALI_INQUIRABLE);
    }

    public static final String KUALI_LOOKUPABLE = "kualiLookupable";

    public static Lookupable getKualiLookupable() {
	return (Lookupable) getService(KUALI_LOOKUPABLE);
    }

    public static Lookupable getLookupable(String lookupableName) {
	return (Lookupable) getService(lookupableName);
    }

    // special one for QuestionPrompt
    public static Question getQuestion(String questionName) {
	return (Question) getService(questionName);
    }

    // DictionaryValidationService
    public static final String DICTIONARY_VALIDATION_SERVICE = "dictionaryValidationService";

    public static DictionaryValidationService getDictionaryValidationService() {
	return (DictionaryValidationService) getService(DICTIONARY_VALIDATION_SERVICE);
    }

    // AttachmentService
    public static final String ATTACHMENT_SERVICE = "attachmentService";

    public static AttachmentService getAttachmentService() {
	return (AttachmentService) getService(ATTACHMENT_SERVICE);
    }

    // SequenceAccessorService
    public static final String SEQUENCE_ACCESSOR_SERVICE = "sequenceAccessorService";

    public static SequenceAccessorService getSequenceAccessorService() {
	return (SequenceAccessorService) getService(SEQUENCE_ACCESSOR_SERVICE);
    }

    // KeyValuesService
    public static final String KEY_VALUES_SERVICE = "keyValuesService";

    public static KeyValuesService getKeyValuesService() {
	return (KeyValuesService) getService(KEY_VALUES_SERVICE);
    }

    public static final String OJB_COLLECTION_HELPER = "ojbCollectionHelper";

    public static OjbCollectionHelper getOjbCollectionHelper() {
	return (OjbCollectionHelper) getService(OJB_COLLECTION_HELPER);
    }

    public static final String PERSISTENCE_CACHE_ADMINISTRATOR = "persistenceCacheAdministrator";

    public static final GeneralCacheAdministrator getPersistenceCacheAdministrator() {
	return (GeneralCacheAdministrator) getService(PERSISTENCE_CACHE_ADMINISTRATOR);
    }

    public static final String TRANSACTION_MANAGER = "transactionManager";

    public static PlatformTransactionManager getTransactionManager() {
	return (PlatformTransactionManager) getService(TRANSACTION_MANAGER);
    }

    public static final String TRANSACTION_TEMPLATE = "transactionTemplate";

    public static TransactionTemplate getTransactionTemplate() {
	return (TransactionTemplate) getService(TRANSACTION_TEMPLATE);
    }
        
    public static final String PESSIMISTIC_LOCK_SERVICE = "pessimisticLockService";
    
    public static PessimisticLockService getPessimisticLockService() {
        return (PessimisticLockService) getService(PESSIMISTIC_LOCK_SERVICE);
    }
    
    public static final String DOCUMENT_SERIALIZER_SERVICE = "documentSerializerService";
    
    public static DocumentSerializerService getDocumentSerializerService() {
        return (DocumentSerializerService) getService(DOCUMENT_SERIALIZER_SERVICE);
    }
    
    public static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";

    public static EntityManagerFactory getEntityManagerFactory() {
    	return (EntityManagerFactory) getService(ENTITY_MANAGER_FACTORY);
    }

    public static final String PERSISTENCE_SERVICE_OJB = "persistenceServiceOjb";
    
    public static PersistenceService getPersistenceServiceOjb() {
        return (PersistenceService) getService(PERSISTENCE_SERVICE_OJB);
    }
    
    public static final String SESSION_DOCUMENT_SERVICE = "sessionDocumentService";

    public static SessionDocumentService getSessionDocumentService() {
	return (SessionDocumentService) getService(SESSION_DOCUMENT_SERVICE);
    }

    public static final String DEFAULT_INACTIVATION_BLOCKING_DETECTION_SERVICE = "inactivationBlockingDetectionService";
    
    public static InactivationBlockingDetectionService getInactivationBlockingDetectionService(String serviceName) {
        return (InactivationBlockingDetectionService) getService(serviceName);
    }
    
    public static final String INACTIVATION_BLOCKING_DISPLAY_SERVICE = "inactivationBlockingDisplayService";
    
    public static InactivationBlockingDisplayService getInactivationBlockingDisplayService() {
    	return (InactivationBlockingDisplayService) getService(INACTIVATION_BLOCKING_DISPLAY_SERVICE);
    }
    
    public static final String SERIALIZER_SERVICE = "businessObjectSerializerService";
    
    public static BusinessObjectSerializerService getBusinessObjectSerializerService() {
        return (BusinessObjectSerializerService) getService(SERIALIZER_SERVICE);
    }
    
    public static final String COUNTRY_SERVICE = "countryService";

    public static CountryService getCountryService() {
	return (CountryService) getService(COUNTRY_SERVICE);
    }
    
    public static final String STATE_SERVICE = "stateService";

    public static StateService getStateService() {
	return (StateService) getService(STATE_SERVICE);
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
    
    public static KualiDBPlatform getKualiDbPlatform() {
        return (KualiDBPlatform) getService(DB_PLATFORM);
    }
    
    public static final String MAINTENANCE_DOCUMENT_AUTHORIZATION_SERVICE = "maintenanceDocumentAuthorizationService";
    
    public static BusinessObjectAuthorizationService getMaintenanceDocumentAuthorizationService() {
    	return (BusinessObjectAuthorizationService) getService(MAINTENANCE_DOCUMENT_AUTHORIZATION_SERVICE);
    }
    
    public static final String DOCUMENT_HELPER_SERVICE = "documentHelperService";
    
    public static DocumentHelperService getDocumentHelperService() {
        return (DocumentHelperService) getService(DOCUMENT_HELPER_SERVICE);
    }
    
    public static final String RICE_APPLICATION_CONFIGURATION_SERVICE = "riceApplicationConfigurationService";
    
    public static RiceApplicationConfigurationService getRiceApplicationConfigurationService() {
    	return (RiceApplicationConfigurationService) getService(RICE_APPLICATION_CONFIGURATION_SERVICE);
    }

    public static final String RICE_APPLICATION_CONFIGURATION_MEDIATION_SERVICE = "riceApplicationConfigurationMediationService";
    
    public static RiceApplicationConfigurationMediationService getRiceApplicationConfigurationMediationService() {
    	return (RiceApplicationConfigurationMediationService) getService(RICE_APPLICATION_CONFIGURATION_MEDIATION_SERVICE);
    }
    
    public static final String WORKFLOW_ATTRIBUTE_PROPERTY_RESOLUTION_SERVICE = "workflowAttributesPropertyResolutionService";
    
    public static WorkflowAttributePropertyResolutionService getWorkflowAttributePropertyResolutionService() {
    	return (WorkflowAttributePropertyResolutionService) getService(WORKFLOW_ATTRIBUTE_PROPERTY_RESOLUTION_SERVICE);
    }
    
}