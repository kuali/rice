/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.document;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.ValidActions;
import org.kuali.rice.krad.uif.view.RequestAuthorizationCache;

/**
 * Request authorization cache object which adds caching on workflow document calls
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentRequestAuthorizationCache extends RequestAuthorizationCache {

    private static final long serialVersionUID = -3965168125219051628L;

    private WorkflowDocumentInfo workflowDocumentInfo;

    public void createWorkflowDocumentInfo(WorkflowDocument workflowDocument) {
        if (this.workflowDocumentInfo == null) {
            this.workflowDocumentInfo = new WorkflowDocumentInfo(workflowDocument);
        }
    }

    public WorkflowDocumentInfo getWorkflowDocumentInfo() {
        return workflowDocumentInfo;
    }

    public static class WorkflowDocumentInfo {

        private Boolean isCompletionRequested;
        private Boolean isApprovalRequested;
        private Boolean isAcknowledgeRequested;
        private Boolean isFYIRequested;
        private Boolean isInitiated;
        private Boolean isSaved;
        private Boolean isEnroute;
        private Boolean isException;
        private Boolean isCanceled;
        private Boolean isRecalled;
        private Boolean isDisapproved;
        private Boolean isApproved;
        private Boolean isProcessed;
        private Boolean isFinal;

        private ValidActions validActions;

        private WorkflowDocument workflowDocument;

        public WorkflowDocumentInfo(WorkflowDocument workflowDocument) {
            this.workflowDocument = workflowDocument;
        }

        public boolean isCompletionRequested() {
            if (isCompletionRequested == null) {
                isCompletionRequested = Boolean.valueOf(workflowDocument.isCompletionRequested());
            }

            return isCompletionRequested.booleanValue();
        }

        public boolean isApprovalRequested() {
            if (isApprovalRequested == null) {
                isApprovalRequested = Boolean.valueOf(workflowDocument.isApprovalRequested());
            }

            return isApprovalRequested.booleanValue();
        }

        public boolean isAcknowledgeRequested() {
            if (isAcknowledgeRequested == null) {
                isAcknowledgeRequested = Boolean.valueOf(workflowDocument.isAcknowledgeRequested());
            }

            return isAcknowledgeRequested.booleanValue();
        }

        public boolean isFYIRequested() {
            if (isFYIRequested == null) {
                isFYIRequested = Boolean.valueOf(workflowDocument.isFYIRequested());
            }

            return isFYIRequested.booleanValue();
        }

        public boolean isInitiated() {
            if (isInitiated == null) {
                isInitiated = Boolean.valueOf(workflowDocument.isInitiated());
            }

            return isInitiated.booleanValue();
        }

        public boolean isSaved() {
            if (isSaved == null) {
                isSaved = Boolean.valueOf(workflowDocument.isSaved());
            }

            return isSaved.booleanValue();
        }

        public boolean isEnroute() {
            if (isEnroute == null) {
                isEnroute = Boolean.valueOf(workflowDocument.isEnroute());
            }

            return isEnroute.booleanValue();
        }

        public boolean isException() {
            if (isException == null) {
                isException = Boolean.valueOf(workflowDocument.isException());
            }

            return isException.booleanValue();
        }

        public boolean isCanceled() {
            if (isCanceled == null) {
                isCanceled = Boolean.valueOf(workflowDocument.isCanceled());
            }

            return isCanceled.booleanValue();
        }

        public boolean isRecalled() {
            if (isRecalled == null) {
                isRecalled = Boolean.valueOf(workflowDocument.isRecalled());
            }

            return isRecalled.booleanValue();
        }

        public boolean isDisapproved() {
            if (isDisapproved == null) {
                isDisapproved = Boolean.valueOf(workflowDocument.isDisapproved());
            }

            return isDisapproved.booleanValue();
        }

        public boolean isApproved() {
            if (isApproved == null) {
                isApproved = Boolean.valueOf(workflowDocument.isApproved());
            }

            return isApproved.booleanValue();
        }

        public boolean isProcessed() {
            if (isProcessed == null) {
                isProcessed = Boolean.valueOf(workflowDocument.isProcessed());
            }

            return isProcessed.booleanValue();
        }

        public boolean isFinal() {
            if (isFinal == null) {
                isFinal = Boolean.valueOf(workflowDocument.isFinal());
            }

            return isFinal.booleanValue();
        }

        public ValidActions getValidActions() {
            if (validActions == null) {
                validActions = workflowDocument.getValidActions();
            }

            return validActions;
        }

        public boolean isValidAction(ActionType actionType) {
            if (actionType == null) {
                throw new IllegalArgumentException("actionType was null");
            }

            return getValidActions().getValidActions().contains(actionType);
        }

        public WorkflowDocument getWorkflowDocument() {
            return workflowDocument;
        }
    }
}
