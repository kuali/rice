/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.datadictionary.ValidationCompletionUtils;
import org.kuali.core.inquiry.Inquirable;
import org.kuali.core.lookup.LookupResultsService;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.lookup.ModuleLookupableHelperServiceImpl;
import org.kuali.core.question.Question;
import org.kuali.core.service.AttachmentService;
import org.kuali.core.service.AuthorizationService;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.BusinessObjectMetaDataService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.service.KualiModuleUserPropertyService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.LookupService;
import org.kuali.core.service.MailService;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.core.service.MaintenanceDocumentService;
import org.kuali.core.service.NoteService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.service.PostProcessorService;
import org.kuali.core.service.SequenceAccessorService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.service.WebAuthenticationService;
import org.kuali.core.service.XmlObjectSerializerService;
import org.kuali.core.util.OjbCollectionHelper;
import org.kuali.core.util.cache.MethodCacheInterceptor;
import org.kuali.core.util.spring.NamedOrderedListBean;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.core.workflow.service.WorkflowGroupService;
import org.kuali.rice.kns.config.KNSResourceLoaderFactory;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;

public class KNSServiceLocator {

	public static final String VALIDATION_COMPLETION_UTILS = "validationCompletionUtils";

	public static <T> T getService(String serviceName) {
		return (T)GlobalResourceLoader.getService(serviceName);
	}

	public static List<NamedOrderedListBean> getNamedOrderedListBeans(String listName) {
		return getNamedOrderedListBeans(listName, KNSResourceLoaderFactory.getSpringResourceLoader().getContext());
	}
	
	protected static List<NamedOrderedListBean> getNamedOrderedListBeans(String listName, ApplicationContext applicationContext) {
		List<NamedOrderedListBean> namedOrderedListBeans = new ArrayList<NamedOrderedListBean>();
		for (Object namedOrderedListBean : applicationContext.getBeansOfType(NamedOrderedListBean.class).values()) {
			if (((NamedOrderedListBean) namedOrderedListBean).getName().equals(listName)) {
				namedOrderedListBeans.add((NamedOrderedListBean) namedOrderedListBean);
			}
		}
		return namedOrderedListBeans;
	}

	public static final ValidationCompletionUtils getValidationCompletionUtils() {
		return (ValidationCompletionUtils) getService(VALIDATION_COMPLETION_UTILS);
	}

	public static final String ENCRYPTION_SERVICE = "encryptionService";

	public static final EncryptionService getEncryptionService() {
		return (EncryptionService) getService(ENCRYPTION_SERVICE);
	}

	public static final String MAIL_SERVICE = "mailService";

	public static final MailService getMailService() {
		return (MailService) getService(MAIL_SERVICE);
	}

	public static final String METHOD_CACHE_INTERCEPTOR = "methodCacheInterceptor";

	public static MethodCacheInterceptor getMethodCacheInterceptor() {
		return (MethodCacheInterceptor) getService(METHOD_CACHE_INTERCEPTOR);
	}

	public static final String XML_OBJECT_SERIALIZER_SERVICE = "xmlObjectSerializerService";

	public static XmlObjectSerializerService getXmlObjectSerializerService() {
		return (XmlObjectSerializerService) getService(XML_OBJECT_SERIALIZER_SERVICE);
	}

	public static final String DOCUMENT_SERVICE = "documentService";

	public static DocumentService getDocumentService() {
		return (DocumentService) getService(DOCUMENT_SERVICE);
	}

	public static final String POST_PROCESSOR_SERVICE = "postProcessorService";

	public static PostProcessorService getPostProcessorService() {
		return (PostProcessorService) getService(POST_PROCESSOR_SERVICE);
	}

	public static final String WEB_AUTHENTICATION_SERVICE = "webAuthenticationService";

	public static WebAuthenticationService getWebAuthenticationService() {
		return (WebAuthenticationService) getService(WEB_AUTHENTICATION_SERVICE);
	}

	public static final String DATETIME_SERVICE = "dateTimeService";

	public static DateTimeService getDateTimeService() {
		return (DateTimeService) getService(DATETIME_SERVICE);
	}

	public static final String WORKFLOW_GROUP_SERVICE = "workflowGroupService";

	public static WorkflowGroupService getWorkflowGroupService() {
		return (WorkflowGroupService) getService(WORKFLOW_GROUP_SERVICE);
	}

	public static final String DOCUMENT_TYPE_SERVICE = "documentTypeService";

	public static DocumentTypeService getDocumentTypeService() {
		return (DocumentTypeService) getService(DOCUMENT_TYPE_SERVICE);
	}

	public static final String LOOKUP_SERVICE = "lookupService";

	public static LookupService getLookupService() {
		return (LookupService) getService(LOOKUP_SERVICE);
	}

	public static final String LOOKUP_RESULTS_SERVICE = "lookupResultsService";

	public static LookupResultsService getLookupResultsService() {
		return (LookupResultsService) getService(LOOKUP_RESULTS_SERVICE);
	}

	public static final String UNIVERSAL_USER_SERVICE = "universalUserService";

	public static UniversalUserService getUniversalUserService() {
		return (UniversalUserService) getService(UNIVERSAL_USER_SERVICE);
	}

	public static final String KUALI_MODULE_SERVICE = "kualiModuleService";

	public static KualiModuleService getKualiModuleService() {
		return (KualiModuleService) getService(KUALI_MODULE_SERVICE);
	}
	
	public static final String MODULE_LOOKUPABLE_HELPER_SERVICE = "moduleLookupableHelperService";

	public static ModuleLookupableHelperServiceImpl getModuleLookupableHelperService() {
		return (ModuleLookupableHelperServiceImpl) getService(MODULE_LOOKUPABLE_HELPER_SERVICE);
	}
	
	public static final String MODULE_USER_PROPERTY_SERVICE = "kualiModuleUserPropertyService";

	public static KualiModuleUserPropertyService getKualiModuleUserPropertyService() {
		return (KualiModuleUserPropertyService) getService(MODULE_USER_PROPERTY_SERVICE);
	}

	public static final String KUALI_GROUP_SERVICE = "kualiGroupService";

	public static KualiGroupService getKualiGroupService() {
		return (KualiGroupService) getService(KUALI_GROUP_SERVICE);
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

	// special ones for Inquirable and Lookupable
	public static final String KUALI_INQUIRABLE = "kualiInquirable";

	public static Inquirable getKualiInquirable() {
		return (Inquirable) getService(KUALI_INQUIRABLE);
	}

	public static final String KUALI_LOOKUPABLE = "kualiLookupable";

	public static Lookupable getKualiLookupable() {
		return (Lookupable) getService(KUALI_LOOKUPABLE);
	}

	public static final String GL_LOOKUPABLE = "glLookupable";

	public static Lookupable getGLLookupable() {
		return (Lookupable) getService(GL_LOOKUPABLE);
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

	// AuthorizationService
	public static final String AUTHORIZATION_SERVICE = "authorizationService";

	public static AuthorizationService getAuthorizationService() {
		return (AuthorizationService) getService(AUTHORIZATION_SERVICE);
	}

	// AttachmentService
	public static final String ATTACHMENT_SERVICE = "attachmentService";

	public static AttachmentService getAttachmentService() {
		return (AttachmentService) getService(ATTACHMENT_SERVICE);
	}

	// DocumentAuthorizationService
	public static final String DOCUMENT_AUTHORIZATION_SERVICE = "documentAuthorizationService";

	public static DocumentAuthorizationService getDocumentAuthorizationService() {
		return (DocumentAuthorizationService) getService(DOCUMENT_AUTHORIZATION_SERVICE);
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

}
