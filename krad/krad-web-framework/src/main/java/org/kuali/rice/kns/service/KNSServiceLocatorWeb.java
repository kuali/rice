package org.kuali.rice.kns.service;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.question.Question;
import org.kuali.rice.kns.uif.service.ExpressionEvaluatorService;
import org.kuali.rice.kns.uif.service.ViewDictionaryService;
import org.kuali.rice.kns.uif.service.ViewService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

public class KNSServiceLocatorWeb {

    public static final String SESSION_DOCUMENT_SERVICE = "sessionDocumentService";
    public static final String BUSINESS_OBJECT_AUTHORIZATION_SERVICE = "businessObjectAuthorizationService";
    public static final String MAINTENANCE_DOCUMENT_SERVICE = "maintenanceDocumentService";
    public static final String WORKFLOW_DOCUMENT_SERVICE = "workflowDocumentService";
    public static final String EXCEPTION_INCIDENT_REPORT_SERVICE = "knsExceptionIncidentService";
    public static final String MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE = "maintenanceDocumentDictionaryService";
    public static final String DATA_DICTIONARY_SERVICE = "dataDictionaryService";
    public static final String KUALI_INQUIRABLE = "kualiInquirable";
    public static final String BUSINESS_OBJECT_METADATA_SERVICE = "businessObjectMetaDataService";
    public static final String BUSINESS_OBJECT_DICTIONARY_SERVICE = "businessObjectDictionaryService";
    public static final String DOCUMENT_HEADER_SERVICE = "documentHeaderService";
    public static final String PESSIMISTIC_LOCK_SERVICE = "pessimisticLockService";
    public static final String KUALI_LOOKUPABLE = "kualiLookupable";
    public static final String PERSISTENCE_SERVICE_OJB = "persistenceServiceOjb";
    public static final String KUALI_MODULE_SERVICE = "kualiModuleService";
    public static final String DOCUMENT_HELPER_SERVICE = "documentHelperService";
    public static final String KUALI_RULE_SERVICE = "kualiRuleService";
    public static final String DOCUMENT_SERVICE = "documentService";
    public static final String WORKFLOW_INFO_SERVICE = "workflowInfoService";
    public static final String DOCUMENT_SERIALIZER_SERVICE = "documentSerializerService";
    public static final String LOOKUP_SERVICE = "lookupService";
    public static final String LOOKUP_RESULTS_SERVICE = "lookupResultsService";
    public static final String DICTIONARY_VALIDATION_SERVICE = "dictionaryValidationService";
    public static final String DEFAULT_INACTIVATION_BLOCKING_DETECTION_SERVICE = "inactivationBlockingDetectionService";
    public static final String TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE = "transactionalDocumentDictionaryService";
    public static final String RICE_APPLICATION_CONFIGURATION_MEDIATION_SERVICE = "riceApplicationConfigurationMediationService";
    public static final String DATA_OBJECT_METADATA_SERVICE = "dataObjectMetaDataService";
    public static final String EXPRESSION_EVALUATOR_SERVICE = "expressionEvaluatorService";
    public static final String VIEW_SERVICE = "viewService";
    public static final String VIEW_DICTIONARY_SERVICE = "viewDictionaryService";

    public static <T extends Object> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static SessionDocumentService getSessionDocumentService() {
        return  getService(SESSION_DOCUMENT_SERVICE);
    }

    public static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        return  KNSServiceLocator.getService(BUSINESS_OBJECT_AUTHORIZATION_SERVICE);
    }

    public static MaintenanceDocumentService getMaintenanceDocumentService() {
        return getService(MAINTENANCE_DOCUMENT_SERVICE);
    }

    public static WorkflowDocumentService getWorkflowDocumentService() {
        return getService(WORKFLOW_DOCUMENT_SERVICE);
    }

    public static final KualiExceptionIncidentService getKualiExceptionIncidentService() {
        return getService(EXCEPTION_INCIDENT_REPORT_SERVICE);
    }

    public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        return getService(MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE);
    }

    public static DataDictionaryService getDataDictionaryService() {
        return getService(DATA_DICTIONARY_SERVICE);
    }

    public static Inquirable getKualiInquirable() {
        return getService(KUALI_INQUIRABLE);
    }

    public static BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return getService(BUSINESS_OBJECT_METADATA_SERVICE);
    }

    public static BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return getService(BUSINESS_OBJECT_DICTIONARY_SERVICE);
    }

    public static DocumentHeaderService getDocumentHeaderService() {
        return getService(DOCUMENT_HEADER_SERVICE);
    }

    public static PessimisticLockService getPessimisticLockService() {
        return getService(PESSIMISTIC_LOCK_SERVICE);
    }

    public static Lookupable getLookupable(String lookupableName) {
        return getService(lookupableName);
    }

    public static PersistenceService getPersistenceServiceOjb() {
        return getService(PERSISTENCE_SERVICE_OJB);
    }

    public static KualiModuleService getKualiModuleService() {
        return getService(KUALI_MODULE_SERVICE);
    }

    public static DocumentHelperService getDocumentHelperService() {
        return getService(DOCUMENT_HELPER_SERVICE);
    }

    public static KualiRuleService getKualiRuleService() {
        return getService(KUALI_RULE_SERVICE);
    }

    public static DocumentService getDocumentService() {
        return getService(DOCUMENT_SERVICE);
    }

    public static KualiWorkflowInfo getWorkflowInfoService() {
	return (KualiWorkflowInfo) getService(WORKFLOW_INFO_SERVICE);
    }

    public static DocumentSerializerService getDocumentSerializerService() {
        return (DocumentSerializerService) getService(DOCUMENT_SERIALIZER_SERVICE);
    }

    public static LookupService getLookupService() {
	return (LookupService) getService(LOOKUP_SERVICE);
    }

    public static LookupResultsService getLookupResultsService() {
	return (LookupResultsService) getService(LOOKUP_RESULTS_SERVICE);
    }

    public static DictionaryValidationService getDictionaryValidationService() {
	return (DictionaryValidationService) getService(DICTIONARY_VALIDATION_SERVICE);
    }

    public static InactivationBlockingDetectionService getInactivationBlockingDetectionService(String serviceName) {
        return (InactivationBlockingDetectionService) getService(serviceName);
    }

    public static TransactionalDocumentDictionaryService getTransactionalDocumentDictionaryService() {
	return (TransactionalDocumentDictionaryService) getService(TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE);
    }

    public static RiceApplicationConfigurationMediationService getRiceApplicationConfigurationMediationService() {
    	return (RiceApplicationConfigurationMediationService) getService(RICE_APPLICATION_CONFIGURATION_MEDIATION_SERVICE);
    }

	public static Question getQuestion(String questionName) {
		return (Question) getService(questionName);
	}

	public static DataObjectMetaDataService getDataObjectMetaDataService() {
		return (DataObjectMetaDataService) getService(DATA_OBJECT_METADATA_SERVICE);
	}

	public static ExpressionEvaluatorService getExpressionEvaluatorService() {
		return (ExpressionEvaluatorService) getService(EXPRESSION_EVALUATOR_SERVICE);
	}

	public static ViewService getViewService() {
		return (ViewService) getService(VIEW_SERVICE);
	}

	public static ViewDictionaryService getViewDictionaryService() {
		return (ViewDictionaryService) getService(VIEW_DICTIONARY_SERVICE);
	}
}
