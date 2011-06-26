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
package org.kuali.rice.kew.api.action;

import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.bind.annotation.XmlElement;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.doctype.IllegalDocumentTypeException;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentUpdate;
import org.kuali.rice.kew.api.document.RouteNodeInstance;
import org.kuali.rice.kew.api.document.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.WorkflowAttributeValidationError;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;

/**
 * This service defines various operations which are used to perform actions against a
 * workflow document. These actions include creation, routing, approving, acknowledging,
 * saving, updating document data, etc.
 * 
 * <p>
 * It also includes operations that allow for loading information about actions that for a
 * given principal to execute against the document, as well as providing information about
 * what actions a particular principal has been requested to execute against a document.
 * 
 * <p>
 * This service can be used in conjunction with the {@link WorkflowDocumentService} which
 * provides additional operations that relate to documents (but not document actions).
 * 
 * <p>
 * Implementations of this service are required to be thread-safe and should be able to be
 * invoked either locally or remotely.
 * 
 * @see WorkflowDocumentService
 * @see WorkflowDocument
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public interface WorkflowDocumentActionsService {

    /**
     * Creates a new document instance from the given document type. The initiator of the
     * resulting document will be the same as the initiator that is passed to this method.
     * Optional {@link DocumentUpdate} and {@link DocumentContentUpdate} parameters can be
     * supplied in order to create the document with these additional pieces of data
     * already set.
     * 
     * <p>
     * By default, if neither the {@link DocumentUpdate} or {@link DocumentContentUpdate}
     * is passed to this method, the document that is created and returned from this
     * operation will have the following initial state:
     * 
     * <ul>
     * <ol>
     * {@code status} set to {@link DocumentStatus#INITIATED}
     * </ol>
     * <ol>
     * {@code createDate} set to the current date and time
     * </ol>
     * <ol>
     * {@code current} set to 'true'
     * </ol>
     * <ol>
     * {@code documentContent} set to the default and empty content
     * </ol>
     * </ul>
     * 
     * <p>
     * Additionally, the initial {@link RouteNodeInstance} for the workflow process on the
     * document will be created and linked to the document as it's initial node. Once the
     * document is created, the {@link #route(DocumentActionParameters)} operation must be
     * invoked in order to submit it to the workflow engine for initial processing.
     * 
     * <p>
     * In certain situations, the given principal may not be permitted to initiate
     * documents of the given type. In these cases an {@link InvalidActionTakenException}
     * will be thrown.
     * 
     * @param documentTypeName ...
     * @param initiatorPrincipalId ...
     * @param documentUpdate ...
     * @param documentContentUpdate ...
     * 
     * @return ...
     * 
     * @throws RiceIllegalArgumentException if principalId is null or blank
     * @throws RiceIllegalArgumentException if principalId does not identify a valid
     * principal
     * @throws RiceIllegalArgumentException if documentTypeName is null or blank
     * @throws RiceIllegalArgumentException if documentTypeName does not identify an
     * existing document type
     * @throws IllegalDocumentTypeException if the given document type is not active
     * @throws IllegalDocumentTypeException if the document type does not support document
     * creation (in other words, it's a document type that is only used as a parent)
     * @throws InvalidActionTakenException if the caller is not allowed to execute this
     * action
     */
    @WebMethod(operationName = "create")
    @WebResult(name = "document")
    @XmlElement(name = "document", required = true)
    public Document create(
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "initiatorPrincipalId") String initiatorPrincipalId,
            @WebParam(name = "documentUpdate") DocumentUpdate documentUpdate,
            @WebParam(name = "documentContentUpdate") DocumentContentUpdate documentContentUpdate)
            throws RiceIllegalArgumentException, IllegalDocumentTypeException, InvalidActionTakenException;

    public ValidActions determineValidActions(String documentId, String principalId);

    public RequestedActions determineRequestedActions(String documentId, String principalId);

    public DocumentActionResult acknowledge(DocumentActionParameters parameters);

    public DocumentActionResult approve(DocumentActionParameters parameters);

    public DocumentActionResult adHocToPrincipal(DocumentActionParameters parameters, AdHocToPrincipal adHocCommand);

    public DocumentActionResult adHocToGroup(DocumentActionParameters parameters, AdHocToGroup adHocCommand);

    public DocumentActionResult revokeAdHocRequestById(DocumentActionParameters parameters, String actionRequestId);

    public DocumentActionResult revokeAdHocRequests(DocumentActionParameters parameters, AdHocRevoke revoke);

    public DocumentActionResult revokeAllAdHocRequests(DocumentActionParameters parameters);

    public DocumentActionResult cancel(DocumentActionParameters parameters);

    public DocumentActionResult clearFyi(DocumentActionParameters parameters);

    public DocumentActionResult complete(DocumentActionParameters parameters);

    public DocumentActionResult disapprove(DocumentActionParameters parameters);

    public DocumentActionResult route(DocumentActionParameters parameters);

    public DocumentActionResult blanketApprove(DocumentActionParameters parameters);

    public DocumentActionResult blanketApproveToNodes(DocumentActionParameters parameters, Set<String> nodeNames);

    public DocumentActionResult returnToPreviousNode(DocumentActionParameters parameters, ReturnPoint returnPoint);

    public DocumentActionResult move(DocumentActionParameters parameters, MovePoint movePoint);

    public DocumentActionResult takeGroupAuthority(DocumentActionParameters parameters, String groupId);

    public DocumentActionResult releaseGroupAuthority(DocumentActionParameters parameters, String groupId);

    public DocumentActionResult save(DocumentActionParameters parameters);

    /**
     * TODO - document the fact that passing an annotation to this will have no effect as
     * it's not actually recorded on the route log
     */
    public DocumentActionResult saveDocumentData(DocumentActionParameters parameters);

    public void delete(String documentId, String principalId);

    public void logAnnotation(String documentId, String principalId, String annotation);

    public void initiateIndexing(String documentId);

    public DocumentActionResult superUserBlanketApprove(DocumentActionParameters parameters,
            boolean executePostProcessor);

    public DocumentActionResult superUserNodeApprove(DocumentActionParameters parameters, boolean executePostProcessor,
            String nodeName);

    public DocumentActionResult superUserTakeRequestedAction(DocumentActionParameters parameters,
            boolean executePostProcessor, String actionRequestId);

    public DocumentActionResult superUserDisapprove(DocumentActionParameters parameters, boolean executePostProcessor);

    public DocumentActionResult superUserCancel(DocumentActionParameters parameters, boolean executePostProcessor);

    public DocumentActionResult superUserReturnToPreviousNode(DocumentActionParameters parameters,
            boolean executePostProcessor, ReturnPoint returnPoint);

    public DocumentActionResult placeInExceptionRouting(DocumentActionParameters parameters);

    // TODO add the following methods to this service

    public List<WorkflowAttributeValidationError> validateWorkflowAttributeDefinition(
            @WebParam(name = "definition") WorkflowAttributeDefinition definition);

    //	public boolean isUserInRouteLog(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "principalId") String principalId,
    //			@WebParam(name = "lookFuture") boolean lookFuture)
    //			throws WorkflowException;
    //
    //	public boolean isUserInRouteLogWithOptionalFlattening(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "principalId") String principalId,
    //			@WebParam(name = "lookFuture") boolean lookFuture,
    //			@WebParam(name = "flattenNodes") boolean flattenNodes)
    //			throws WorkflowException;
    //
    //	public void reResolveRoleByDocTypeName(
    //			@WebParam(name = "documentTypeName") String documentTypeName,
    //			@WebParam(name = "roleName") String roleName,
    //			@WebParam(name = "qualifiedRoleNameLabel") String qualifiedRoleNameLabel)
    //			throws WorkflowException;
    //
    //	public void reResolveRoleByDocumentId(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "roleName") String roleName,
    //			@WebParam(name = "qualifiedRoleNameLabel") String qualifiedRoleNameLabel)
    //			throws WorkflowException;
    //
    //	public DocumentDetailDTO routingReport(
    //			@WebParam(name = "reportCriteria") ReportCriteriaDTO reportCriteria)
    //			throws WorkflowException;
    //
    //	public boolean isFinalApprover(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "principalId") String principalId)
    //			throws WorkflowException;
    //
    //	public boolean isLastApproverAtNode(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "principalId") String principalId,
    //			@WebParam(name = "nodeName") String nodeName)
    //			throws WorkflowException;
    //
    //	public boolean routeNodeHasApproverActionRequest(
    //			@WebParam(name = "docType") String docType,
    //			@WebParam(name = "docContent") String docContent,
    //			@WebParam(name = "nodeName") String nodeName)
    //			throws WorkflowException;
    //	
    //	public boolean documentWillHaveAtLeastOneActionRequest(
    //			@WebParam(name = "reportCriteriaDTO") ReportCriteriaDTO reportCriteriaDTO,
    //			@WebParam(name = "actionRequestedCodes") String[] actionRequestedCodes,
    //			@WebParam(name = "ignoreCurrentActionRequests") boolean ignoreCurrentActionRequests);
    //    	
    //	
    //	public String[] getPrincipalIdsInRouteLog(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "lookFuture") boolean lookFuture)
    //			throws WorkflowException;

}
