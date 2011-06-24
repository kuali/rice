package org.kuali.rice.kew.api;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.note.NoteService;

/**
 * A static service locator which aids in locating the various services that
 * form the Kuali Service Bus API.
 */
public class KewApiServiceLocator {

	public static final String WORKFLOW_DOCUMENT_ACTIONS_SERVICE = "rice.kew.workflowDocumentActionsService";
	public static final String WORKFLOW_DOCUMENT_SERVICE = "rice.kew.workflowDocumentService";
	public static final String DOCUMENT_TYPE_SERVICE = "rice.kew.documentTypeService";
	public static final String NOTE_SERVICE = "rice.kew.noteService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static WorkflowDocumentActionsService getWorkflowDocumentActionsService() {
        return getService(WORKFLOW_DOCUMENT_ACTIONS_SERVICE);
    }
    
    public static WorkflowDocumentService getWorkflowDocumentService() {
        return getService(WORKFLOW_DOCUMENT_SERVICE);
    }
    
    public static DocumentTypeService getDocumentTypeService() {
        return getService(DOCUMENT_TYPE_SERVICE);
    }
    
    public static NoteService getNoteService() {
    	return getService(NOTE_SERVICE);
    }
    
}
