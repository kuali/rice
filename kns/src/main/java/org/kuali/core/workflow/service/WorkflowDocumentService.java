/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.workflow.service;

import java.util.List;

import org.kuali.core.bo.user.UniversalUser;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This interface defines the contract that must be implemented by the workflow engine.
 * 
 * 
 */
public interface WorkflowDocumentService {
    /**
     * @param documentHeaderId
     * @return true if a workflowDocument exists for the given documentHeaderId
     */
    public boolean workflowDocumentExists(String documentHeaderId);


    /**
     * Given a documentTypeId and workflowUser, returns a new FlexDoc from the workflow server.
     * 
     * @param documentTypeId
     * @param workflowUser
     * @return newly-created FlexDoc instance
     * @throws IllegalArgumentException if the given documentTypeId is blank
     * @throws IllegalArgumentException if the given workflowUser is null or contains no id
     * @throws ResourceUnavailableException
     * @throws EdenUserNotFoundException
     * @throws EdenException
     */
    public KualiWorkflowDocument createWorkflowDocument(String documentTypeId, UniversalUser workflowUser) throws WorkflowException;

    /**
     * Given a documentHeaderId and workflowUser, retrieves the FlexDoc associated with that documentHeaderId from the workflow
     * server.
     * 
     * @param documentHeaderId
     * @param workflowUser
     * @return existing workflowDoc
     * @throws IllegalArgumentException if the given documentHeaderId is null
     * @throws IllegalArgumentException if the given workflowUser is null or contains no id
     * @throws EdenException
     */
    public KualiWorkflowDocument createWorkflowDocument(Long documentHeaderId, UniversalUser workflowUser) throws WorkflowException;

    /**
     * save the document to workflows action list optionally providing an annotation which will show up in the route log for this
     * document corresponding to this action taken and additionally providing a list of ad hoc recipients
     * 
     * @param flexDoc
     * @param annotation
     * @param adHocRecipients
     * @throws EdenException
     */
    public void save(KualiWorkflowDocument workflowDocument, String annotation, List adHocRecipients) throws WorkflowException;

    /**
     * route this flexDoc optionally providing an annotation for this action taken which will show up in the route log for this
     * document corresponding to this action taken, and additionally optionally providing a list of ad hoc recipients for the
     * document
     * 
     * @param flexDoc
     * @param annotation
     * @param adHocRecipients
     * @throws EdenException
     */
    public void route(KualiWorkflowDocument workflowDocument, String annotation, List adHocRecipients) throws WorkflowException;

    /**
     * approve this flexdoc optionally providing an annotation for this action taken which will show up in the route log for this
     * document corresponding to this action taken, and additionally optionally providing a list of ad hoc recipients for the
     * document
     * 
     * @param flexDoc
     * @param annotation
     * @param adHocRecipients
     * @throws EdenException
     */
    public void approve(KualiWorkflowDocument workflowDocument, String annotation, List adHocRecipients) throws WorkflowException;

    /**
     * super user approve this flexdoc optionally providing an annotation for this action taken which will show up in the route log
     * for this document corresponding to this action taken
     * 
     * @param flexDoc
     * @param annotation
     * @param adHocRecipients
     * @throws EdenException
     */
    public void superUserApprove(KualiWorkflowDocument workflowDocument, String annotation) throws WorkflowException;

    /**
     * disapprove this flexDoc optionally providing an annotation for this action taken which will show up in the route log for this
     * document corresponding to this action taken
     * 
     * @param flexDoc
     * @param annotation
     * @throws EdenException
     */
    public void disapprove(KualiWorkflowDocument workflowDocument, String annotation) throws WorkflowException;

    /**
     * cancel this flexDoc optionally providing an annotation for this action taken which will show up in the route log for this
     * document corresponding to this action taken
     * 
     * @param flexDoc
     * @param annotation
     * @throws EdenException
     */
    public void cancel(KualiWorkflowDocument workflowDocument, String annotation) throws WorkflowException;

    /**
     * acknowledge this flexDoc optionally providing an annotation for this action taken which will show up in the route log for
     * this document corresponding to this action taken, additionally optionally providing a list of ad hoc recipients for this
     * document which should be restricted to actions requested of acknowledge or fyi as all other action request types will be
     * discarded
     * 
     * @param flexDoc
     * @param annotation
     * @param adHocRecipients
     * @throws EdenException
     */
    public void acknowledge(KualiWorkflowDocument workflowDocument, String annotation, List adHocRecipients) throws WorkflowException;

    /**
     * blanket approve this document optionally providing an annotation for this action taken which will show up in the route log
     * for this document corresponding to this action taken, and additionally optionally providing a list of ad hoc recipients for
     * this document which should be restricted to actions requested of acknowledge or fyi as all other action request types will be
     * discarded.
     * 
     * @param flexDoc
     * @param annotation
     * @param adHocRecipients
     * @throws EdenException
     */
    public void blanketApprove(KualiWorkflowDocument workflowDocument, String annotation, List adHocRecipients) throws WorkflowException;

    /**
     * clear the fyi request for this document, optinoally providing a list of ad hoc recipients for this document which should be
     * restricted to actions requested of fyi as all other action request types will be discarded
     * 
     * @param flexDoc
     * @param adHocRecipients
     * @throws EdenException
     */
    public void clearFyi(KualiWorkflowDocument workflowDocument, List adHocRecipients) throws WorkflowException;
}