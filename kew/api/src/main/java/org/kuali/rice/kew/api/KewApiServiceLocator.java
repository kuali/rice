package org.kuali.rice.kew.api;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;

/**
 * A static service locator which aids in locating the various services that
 * form the Kuali Service Bus API.
 */
public class KewApiServiceLocator {

	public static final String WORKFLOW_DOCUMENT_ACTIONS_SERVICE = "rice.kew.workflowDocumentActionsService";
	public static final String WORKFLOW_DOCUMENT_SERVICE = "rice.kew.workflowDocumentService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static WorkflowDocumentActionsService getWorkflowDocumentActionsService() {
        return getService(WORKFLOW_DOCUMENT_ACTIONS_SERVICE);
    }
    
    public static WorkflowDocumentService getWorkflowDocumentService() {
        return getService(WORKFLOW_DOCUMENT_SERVICE);
    }
    
}
