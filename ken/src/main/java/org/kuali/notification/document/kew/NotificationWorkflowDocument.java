/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.notification.document.kew;

import org.kuali.notification.util.NotificationConstants;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class extends the KEW WorkflowDocument object and becomes our gateway for 
 * get a handle on KEW documents in workflow.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationWorkflowDocument extends WorkflowDocument {
    private static final long serialVersionUID = 6125662798733898964L;
    
    /**
     * Constructs a NotificationWorkflowDocument instance - this essentially creates a new routable document in KEW 
     * for the given user.
     * @param user
     * @throws WorkflowException
     */
    public NotificationWorkflowDocument(UserIdVO user) throws WorkflowException {
    	super(user, NotificationConstants.KEW_CONSTANTS.NOTIFICATION_DOC_TYPE);
    }

    /**
     * Constructs a NotificationWorkflowDocument instance - this essentially creates a new routable document in KEW 
     * for the given user and document type name.
     * @param user
     * @param documentTypeName
     * @throws WorkflowException
     */
    public NotificationWorkflowDocument(UserIdVO user, String documentTypeName) throws WorkflowException {
    	super(user, documentTypeName);
    }
    
    /**
     * Constructs a NotificationWorkflowDocument instance - this one is used to get a handle on a workflow 
     * document that was already created in the system.
     * @param user
     * @param documentId
     * @throws WorkflowException
     */
    public NotificationWorkflowDocument(UserIdVO user, Long documentId) throws WorkflowException {
    	super(user, documentId);
    }
}
