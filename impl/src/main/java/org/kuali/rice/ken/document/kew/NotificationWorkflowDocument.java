/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ken.document.kew;

import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowDocument;


/**
 * This class extends the KEW WorkflowDocument object and becomes our gateway for 
 * get a handle on KEW documents in workflow.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotificationWorkflowDocument extends WorkflowDocument {
    private static final long serialVersionUID = 6125662798733898964L;
    
    /**
     * Constructs a NotificationWorkflowDocument instance - this essentially creates a new routable document in KEW 
     * for the given user.
     * @param user
     * @throws WorkflowException
     */
    /*@Deprecated
    public NotificationWorkflowDocument(UserIdDTO user) throws WorkflowException {
    	super(user, NotificationConstants.KEW_CONSTANTS.NOTIFICATION_DOC_TYPE);
    }*/

    /**
     * Constructs a NotificationWorkflowDocument instance - this essentially creates a new routable document in KEW
     * for the given user.
     * @param principalId
     * @throws WorkflowException
     */
    public NotificationWorkflowDocument(String principalId) throws WorkflowException {
    	super(principalId, NotificationConstants.KEW_CONSTANTS.NOTIFICATION_DOC_TYPE);
    }

    /**
     * Constructs a NotificationWorkflowDocument instance - this essentially creates a new routable document in KEW
     * for the given user and document type name.
     * @param user
     * @param documentTypeName
     * @throws WorkflowException
     */
    /*@Deprecated
    public NotificationWorkflowDocument(UserIdDTO user, String documentTypeName) throws WorkflowException {
    	super(user, documentTypeName);
    }*/

    /**
     * Constructs a NotificationWorkflowDocument instance - this essentially creates a new routable document in KEW
     * for the given user and document type name.
     * @param principalId
     * @param documentTypeName
     * @throws WorkflowException
     */

    public NotificationWorkflowDocument(String principalId, String documentTypeName) throws WorkflowException {
    	super(principalId, documentTypeName);
    }
    
    /**
     * Constructs a NotificationWorkflowDocument instance - this one is used to get a handle on a workflow 
     * document that was already created in the system.
     * @param user
     * @param documentId
     * @throws WorkflowException
     */
    /*@Deprecated
    public NotificationWorkflowDocument(UserIdDTO user, Long documentId) throws WorkflowException {
    	super(user, documentId);
    }*/

    /**
     * Constructs a NotificationWorkflowDocument instance - this one is used to get a handle on a workflow
     * document that was already created in the system.
     * @param princpipalId
     * @param documentId
     * @throws WorkflowException
     */
    public NotificationWorkflowDocument(String princpipalId, Long documentId) throws WorkflowException {
    	super(princpipalId, documentId);
    }
}
