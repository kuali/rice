/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
