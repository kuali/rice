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
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.doctype.IllegalDocumentTypeException;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentUpdate;
import org.kuali.rice.kew.api.document.InvalidDocumentContentException;
import org.kuali.rice.kew.api.document.RouteNodeInstance;
import org.kuali.rice.kew.api.document.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.WorkflowAttributeValidationError;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;

/**
 * This service defines various operations which are used to perform actions against a workflow
 * {@link Document}. These actions include creation, routing, approval, acknowledgment, saving,
 * updating document data, etc.
 * 
 * <p>
 * It also includes operations that allow for loading of information about which actions a given
 * principal is permitted to execute against a given document ({@link ValidActions}), as well as
 * providing information about what actions a particular principal has been requested to execute
 * against a given document ({@link RequestedActions}).
 * 
 * <p>
 * This service can be used in conjunction with the {@link WorkflowDocumentService} which provides
 * additional operations that relate to documents (but not document actions).
 * 
 * <p>
 * Unless otherwise specified, all parameters to all methods must not be null. If the argument is a
 * string value it must also not be "blank" (either the empty string or a string of only
 * whitespace). In the cases where this is violated, a {@link RiceIllegalArgumentException} will be
 * thrown. Additionally, unless otherwise specified, all methods will return non-null return values.
 * In the case of collections, an empty collection will be returned in preference to a null
 * collection value. All collections which are returned from methods on this service will be
 * unmodifiable collections.
 * 
 * <p>
 * Many of the actions trigger processing by the workflow engine. Unless otherwise specified, any
 * method on this service which performs an action against the document will also submit the
 * document to the workflow engine after performing the action. This may trigger the workflow
 * document to transition to the next node in the workflow process or activate additional action
 * requests depending on what the current state of the active node instance(s) is on the document.
 * 
 * <p>
 * Workflow engine processing may happen either asynchronously or synchronously depending on
 * configuration and capabilities of the back end workflow engine implementation. However,
 * asynchronous operation is suggested for most use cases. This means that when operating in
 * asynchronous mode, after an action is submitted against the document there may be a delay in
 * state changes to the document. For example, if a principal submits an approve against a document
 * that triggers transition to the next node in the workflow process (generating new action requests
 * in the process) those new actions requests will not be represented in the information returned in
 * the {@link DocumentActionResult} result object. Though future invocations of
 * {@link #determineRequestedActions(String, String)} and similar methods may yield such
 * information, though it may do so after an non-deterministic amount of time since the workflow
 * engine makes no guarantees about how quickly it will complete processing. Additionally,
 * asynchronous workflow processing is scheduled in a work queue and therefore it may not be picked
 * up for processing immediately depending on system load and other factors.
 * 
 * <p>
 * If there is an error during asynchronous workflow processing then the document will be put into
 * exception routing (which can be executed in various ways depending on how the document type
 * defines it's exception policy). Regardless of the exact process that gets triggered during
 * exception routing, the end result is a set of {@link ActionRequestType#COMPLETE} requests to
 * those principals who are capable of resolving the exception scenario as well as the document's
 * status being transitioned to {@link DocumentStatus#EXCEPTION}. Once they have resolved any
 * barriers to successful processing of the document, they can take the
 * {@link #complete(DocumentActionParameters)} action against the document in order to satisfy the
 * outstanding exception request(s) and send the document back through the workflow engine.
 * 
 * <p>
 * In contrast, when operating the workflow engine in synchronous mode, processing will happen
 * immediately and control will not be returned to the caller until all outstanding processing has
 * completed. As a result, the information returned in the {@link DocumentActionResult} will always
 * be in a consistent state after each action is performed. When operating in synchronous mode, the
 * process of exception routing does not occur when failures are encountered during workflow engine
 * processing, rather any exceptions that are raised during processing will instead be thrown back
 * to the calling code.
 * 
 * <p>
 * Implementations of this service are required to be thread-safe and should be able to be invoked
 * either locally or remotely.
 * 
 * @see WorkflowDocumentService
 * @see WorkflowDocument
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@WebService(name = "workflowDocumentActionsServiceSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface WorkflowDocumentActionsService {

    /**
     * Creates a new document instance from the given document type. The initiator of the resulting
     * document will be the same as the initiator that is passed to this method. Optional
     * {@link DocumentUpdate} and {@link DocumentContentUpdate} parameters can be supplied in order
     * to create the document with these additional pieces of data already set.
     * 
     * <p>
     * By default, if neither the {@link DocumentUpdate} or {@link DocumentContentUpdate} is passed
     * to this method, the document that is created and returned from this operation will have the
     * following initial state:
     * 
     * <ul>
     * <ol>
     * {@code status} set to {@link DocumentStatus#INITIATED}
     * </ol>
     * <ol>
     * {@code createDate} and {@code dateLastModified} set to the current date and time
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
     * Additionally, the initial {@link RouteNodeInstance} for the workflow process on the document
     * will be created and linked to the document as it's initial node. Once the document is
     * created, the {@link #route(DocumentActionParameters)} operation must be invoked in order to
     * submit it to the workflow engine for initial processing.
     * 
     * <p>
     * In certain situations, the given principal may not be permitted to initiate documents of the
     * given type. In these cases an {@link InvalidActionTakenException} will be thrown.
     * 
     * @param documentTypeName the name of the document type from which to create this document
     * @param initiatorPrincipalId the id of the principal who is initiating this document
     * @param documentUpdate specifies additional document to set on the document upon creation,
     *        this is optional and if null is passed then the document will be created with the
     *        default document state
     * @param documentContentUpdate defines what the initial document content for the document
     *        should be, this is optional if null is passed then the document will be created with
     *        the default empty document content
     * 
     * @return the document that was created
     * 
     * @throws RiceIllegalArgumentException if {@code principalId} is null or blank
     * @throws RiceIllegalArgumentException if {@code principalId} does not identify a valid
     *         principal
     * @throws RiceIllegalArgumentException if {@code documentTypeName} is null or blank
     * @throws RiceIllegalArgumentException if {@code documentTypeName} does not identify an
     *         existing document type
     * @throws IllegalDocumentTypeException if the specified document type is not active
     * @throws IllegalDocumentTypeException if the specified document type does not support document
     *         creation (in other words, it's a document type that is only used as a parent)
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     */
    @WebMethod(operationName = "create")
    @WebResult(name = "document")
    @XmlElement(name = "document", required = true)
    Document create(
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "initiatorPrincipalId") String initiatorPrincipalId,
            @WebParam(name = "documentUpdate") DocumentUpdate documentUpdate,
            @WebParam(name = "documentContentUpdate") DocumentContentUpdate documentContentUpdate)
            throws RiceIllegalArgumentException, IllegalDocumentTypeException, InvalidActionTakenException;

    /**
     * Determines which actions against the document with the given id are valid for the principal
     * with the given id.
     * 
     * @param documentId the id of the document for which to determine valid actions
     * @param principalId the id of the principal for which to determine valid actions against the
     *        given document
     * 
     * @return a {@link ValidActions} object which contains the valid actions for the given
     *         principal against the given document
     * 
     * @throws RiceIllegalArgumentException if {@code documentId} is null or blank
     * @throws RiceIllegalArgumentException if document with the given {@code documentId} does not
     *         exist
     * @throws RiceIllegalArgumentException if {@code principalId} is null or blank
     * @throws RiceIllegalArgumentException if principal with the given {@code principalId} does not
     *         exist
     */
    @WebMethod(operationName = "determineValidActions")
    @WebResult(name = "validActions")
    @XmlElement(name = "validActions", required = true)
    ValidActions determineValidActions(
            @WebParam(name = "documentId") String documentId,
            @WebParam(name = "principalId") String principalId)
            throws RiceIllegalArgumentException;

    /**
     * Determines which actions are requested against the document with the given id for the
     * principal with the given id. These are generally derived based on action requests that are
     * currently pending against the document.
     * 
     * <p>
     * This method is distinguished from {@link #determineValidActions(String, String)} in that fact
     * that valid actions are the actions that the principal is permitted to take, while requested
     * actions are those that the principal is specifically being asked to take. Note that the
     * actions that are requested are used when assembling valid actions but are not necessarily the
     * only authoritative source for determination of valid actions for the principal, as
     * permissions and other configuration can also come into play.
     * 
     * @param documentId the id of the document for which to determine requested actions
     * @param principalId the id of the principal for which to determine requested actions against
     *        the given document
     * 
     * @return a {@link RequestedActions} object which contains the actions that are being requested
     *         from the given principal against the given document
     * 
     * @throws RiceIllegalArgumentException if {@code documentId} is null or blank
     * @throws RiceIllegalArgumentException if document with the given {@code documentId} does not
     *         exist
     * @throws RiceIllegalArgumentException if {@code principalId} is null or blank
     * @throws RiceIllegalArgumentException if principal with the given {@code principalId} does not
     *         exist
     */
    @WebMethod(operationName = "determineRequestedActions")
    @WebResult(name = "requestedActions")
    @XmlElement(name = "requestedActions", required = true)
    RequestedActions determineRequestedActions(
            @WebParam(name = "documentId") String documentId,
            @WebParam(name = "principalId") String principalId)
            throws RiceIllegalArgumentException;

    /**
     * Executes an {@link ActionType#ACKNOWLEDGE} action for the given principal and document
     * specified in the supplied parameters. When a principal acknowledges a document, any of the
     * principal's pending action requests at or below the acknowledge level (which includes fyi
     * requests as well) will be satisfied by the principal's action. The principal's action should
     * be recorded with the document as an {@link ActionTaken}.
     * 
     * <p>
     * Depending on document type policy, a pending action request at or below the acknowledge level
     * may have to exist on the document in order for the principal to take this action. Otherwise
     * an {@link InvalidActionTakenException} may be thrown. In order to determine if an acknowledge
     * action is valid, the {@link ValidActions} or {@link RequestedActions} for the document can be
     * checked.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     */
    @WebMethod(operationName = "acknowledge")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult acknowledge(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * Executes an {@link ActionType#APPROVE} action for the given principal and document specified
     * in the supplied parameters. When a principal approves a document, any of the principal's
     * pending action requests at or below the approve level (which includes complete, acknowledge,
     * and fyi requests as well) will be satisfied by the principal's action. The principal's action
     * should be recorded with the document as an {@link ActionTaken}.
     * 
     * <p>
     * Depending on document type policy, a pending action request at or below the approve level may
     * have to exist on the document in order for the principal to take this action. Otherwise an
     * {@link InvalidActionTakenException} may be thrown. In order to determine if an approve action
     * is valid, the {@link ValidActions} or {@link RequestedActions} for the document can be
     * checked.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     */
    @WebMethod(operationName = "approve")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult approve(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * Executes an {@link ActionType#ADHOC_REQUEST} action for the given principal and document
     * specified in the supplied parameters to create an ad hoc action request to the target
     * principal specified in the {@code AdHocToPrincipal}.
     * 
     * <p>
     * Operates as per {@link #adHocToGroup(DocumentActionParameters, AdHocToGroup)} with the
     * exception that this method is used to send an adhoc request to principal instead of a group.
     * 
     * <p>
     * Besides this difference, the same rules that are in play for sending ad hoc requests to group
     * apply for sending ad hoc requests to principals.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * @param adHocToPrincipal defines various pieces of information that informs what type of ad
     *        hoc request should be created
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if {@code adHocToPrincipal} is null
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     * @throws InvalidActionTakenException if the target principal is not permitted to receive ad
     *         hoc requests on documents of this type
     * @throws InvalidActionTakenException if the specified ad hoc request cannot be generated
     *         because the current state of the document would result in an illegal request being
     *         generated
     * 
     * @see #adHocToGroup(DocumentActionParameters, AdHocToGroup)
     */
    @WebMethod(operationName = "adHocToPrincipal")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult adHocToPrincipal(
            @WebParam(name = "parameters") DocumentActionParameters parameters,
            @WebParam(name = "adHocToPrincipal") AdHocToPrincipal adHocToPrincipal)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * Executes an {@link ActionType#ADHOC_REQUEST} action for the given group and document
     * specified in the supplied parameters to create an ad hoc action request to the target group
     * specified in the {@code AdHocToGroup}. The {@code AdHocToGroup} contains additional
     * information on how the ad hoc action request should be created, including the type of request
     * to generate, at which node it should generated, etc.
     * 
     * <p>
     * The policy for how ad hoc actions handle request generation and interact with the workflow
     * engine is different depending on the state of the document when the request to generate the
     * ad hoc is submitted. There are also different scenarios under which certain types of ad hoc
     * actions are allowed to be executed (throwing {@link InvalidActionTakenException} in
     * situations where the actions are not permitted). The rules are defined as follows:
     * 
     * <ol>
     * <li>If the status of the document is {@link DocumentStatus#INITIATED} then the action request
     * will be generated with a status of {@link ActionRequestStatus#INITIALIZED} and no processing
     * directives will be submitted to the workflow engine. When the document is routed, these ad
     * hoc requests will get activated</li>
     * <li>If the ad hoc request being created is an {@link ActionRequestType#COMPLETE} or
     * {@link ActionRequestType#APPROVE} and the document is in a "terminal" state (either
     * {@link DocumentStatus#CANCELED}, {@link DocumentStatus#DISAPPROVED},
     * {@link DocumentStatus#PROCESSED}, {@link DocumentStatus#FINAL}) or is in
     * {@link DocumentStatus#EXCEPTION} status, then an {@link InvalidActionTakenException} will be
     * thrown. This is because submitting such an action with a document in that state would result
     * in creation of an illegal action request.</li>
     * <li>If the document is in a "terminal" state (see above for definition) then the request will
     * be immediately (and synchronously) activated.</li>
     * <li>Otherwise, after creating the ad hoc request it will be in the
     * {@link ActionRequestStatus#INITIALIZED} status, and the document will be immediately
     * forwarded to the workflow engine for processing at which point the ad hoc request will
     * activated at the appropriate time.</li>
     * </ol>
     * 
     * <p>
     * Unlink other actions, ad hoc actions don't result in the recording of an {@link ActionTaken}
     * against the document. Instead, only the requested ad hoc {@link ActionRequest} is created.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * @param adHocToGroup defines various pieces of information that informs what type of ad hoc
     *        request should be created
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if {@code adHocToGroup} is null
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principals i is not allowed to execute
     *         this action
     * @throws InvalidActionTakenException if any of the principals in the target group are not
     *         permitted to receive ad hoc requests on documents of this type
     * @throws InvalidActionTakenException if the specified ad hoc request cannot be generated
     *         because the current state of the document would result in an illegal request being
     *         generated
     */
    @WebMethod(operationName = "adHocToGroup")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult adHocToGroup(
            @WebParam(name = "parameters") DocumentActionParameters parameters,
            @WebParam(name = "adHocToGroup") AdHocToGroup adHocToGroup)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * Executes an {@link ActionType#ADHOC_REQUEST_REVOKE} action for the given principal and
     * document specified in the supplied parameters against the action request with the given id.
     * The process of revoking an ad hoc request simply deactivates the request associating the
     * generated {@link ActionTaken} of the revoke action with the deactivated request (this allows
     * for it to be determined what caused the ad hoc request to be deactivated). As with other
     * actions, this action taken is then recorded with the document.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * @param actionRequestId the id of the action request to revoke
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if {@code actionRequestId} is null or blank
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     * @throws InvalidActionTakenException if a pending ad hoc request with the given
     *         {@code actionRequestId} does not exist on the specified document, this could mean
     *         that the action request id is invalid, or that the action request has already been
     *         deactivated and is no longer pending
     */
    @WebMethod(operationName = "revokeAdHocRequestById")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult revokeAdHocRequestById(
            @WebParam(name = "parameters") DocumentActionParameters parameters,
            @WebParam(name = "actionRequestId") String actionRequestId)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * Executes an {@link ActionType#ADHOC_REQUEST_REVOKE} action which revokes all pending ad hoc
     * action requests that match the supplied {@link AdHocRevoke} criteria for the given principal
     * and document specified in the supplied parameters. The process of revoking an ad hoc requests
     * simply deactivates all ad hoc requests that match the given {@code AdHocRevoke} criteria,
     * associating the generated {@link ActionTaken} of the revoke action with the deactivated
     * requests (this allows for it to be determined what caused the ad hoc request to be
     * deactivated). As with other actions, this action taken is then recorded with the document.
     * 
     * <p>
     * It's possible that the given ad hoc revoke command will match no action requests on the
     * document, in which case this method will complete successfully but no requests will be
     * deactivated.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * @param revoke the criteria for matching ad hoc action requests on the specified document that
     *        should be revoked
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if {@code revoke} is null
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     */
    @WebMethod(operationName = "revokeAdHocRequests")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult revokeAdHocRequests(
            @WebParam(name = "parameters") DocumentActionParameters parameters,
            @WebParam(name = "revoke") AdHocRevoke revoke)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * Executes an {@link ActionType#ADHOC_REQUEST_REVOKE} action which revokes all pending ad hoc
     * action requests for the given principal and document specified in the supplied parameters.
     * This process of revoking all ad hoc requests will simply deactivate all ad hoc requests on
     * the specified document, associating the generated {@link ActionTaken} of the revoke action
     * with the deactivated requests (this allows for it to be determined what caused the ad hoc
     * request to be deactivated). As with other actions, this action taken is then recorded with
     * the document.
     * 
     * <p>
     * It's possible that the specified document will have no pending adhoc requests, in which case
     * this method will complete successfully but no requests will be deactivated.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     */
    @WebMethod(operationName = "revokeAllAdHocRequests")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult revokeAllAdHocRequests(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * Executes a {@link ActionType#CANCEL} action for the given principal and document specified in
     * the supplied parameters. When a principal cancels a document, all pending action requests on
     * the document are deactivated and the the principal's action will be recorded on the document
     * as an {@link ActionTaken}. Additionally, the document will be (sychronously) transitioned to
     * the {@link DocumentStatus#CANCELED} status.
     * 
     * <p>
     * In order to cancel a document, the principal must have permission to cancel documents of the
     * appropriate type, and one of the following must hold true:
     * 
     * <ol>
     * <li>The document must have a status of {@link DocumentStatus#INITIATED} <strong>or</strong></li>
     * <li>The document must have a status of {@link DocumentStatus#SAVED} <strong>or</strong></li>
     * <li>The principal must have a pending "complete" or "approve" request on the document.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     */
    @WebMethod(operationName = "cancel")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult cancel(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * Executes an {@link ActionType#FYI} action for the given principal and document specified in
     * the supplied parameters. When a principal clears fyis on a document, any of the principal's
     * pending fyis will be satisfied by the principal's action. The principal's action should be
     * recorded with the document as an {@link ActionTaken}.
     * 
     * <p>
     * Depending on document type policy, a pending fyi request may have to exist on the document in
     * order for the principal to take this action. Otherwise an {@link InvalidActionTakenException}
     * may be thrown. In order to determine if an fyi action is valid, the {@link ValidActions} or
     * {@link RequestedActions} for the document can be checked.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     */
    @WebMethod(operationName = "clearFyi")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult clearFyi(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * Executes an {@link ActionType#COMPLETE} action for the given principal and document specified
     * in the supplied parameters. When a principal completes a document, any of the principal's
     * pending action requests at or below the complete level (which includes approve, acknowledge,
     * and fyi requests as well) will be satisfied by the principal's action. The principal's action
     * should be recorded with the document as an {@link ActionTaken}.
     * 
     * <p>
     * Depending on document type policy, a pending action request at or below the complete level
     * may have to exist on the document in order for the principal to take this action. Otherwise
     * an {@link InvalidActionTakenException} may be thrown. In order to determine if an complete
     * action is valid, the {@link ValidActions} or {@link RequestedActions} for the document can be
     * checked.
     * 
     * @param parameters the parameters which indicate which principal is executing the action
     *        against which document, as well as additional operations to take against the document,
     *        such as updating document data
     * 
     * @return the result of executing the action, including a view on the updated state of the
     *         document and related actions
     * 
     * @throws RiceIllegalArgumentException if {@code parameters} is null
     * @throws RiceIllegalArgumentException if no document with the {@code documentId} specified in
     *         {@code parameters} exists
     * @throws RiceIllegalArgumentException if no principal with the {@code principalId} specified
     *         in {@code parameters} exists
     * @throws InvalidDocumentContentException if the document content on the
     *         {@link DocumentContentUpdate} supplied with the {@code parameters} is invalid.
     * @throws InvalidActionTakenException if the supplied principal is not allowed to execute this
     *         action
     */
    @WebMethod(operationName = "complete")
    @WebResult(name = "documentActionResult")
    @XmlElement(name = "documentActionResult", required = true)
    DocumentActionResult complete(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    DocumentActionResult disapprove(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    DocumentActionResult route(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    DocumentActionResult blanketApprove(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    DocumentActionResult blanketApproveToNodes(DocumentActionParameters parameters, Set<String> nodeNames);

    DocumentActionResult returnToPreviousNode(DocumentActionParameters parameters, ReturnPoint returnPoint);

    DocumentActionResult move(DocumentActionParameters parameters, MovePoint movePoint);

    DocumentActionResult takeGroupAuthority(DocumentActionParameters parameters, String groupId);

    DocumentActionResult releaseGroupAuthority(DocumentActionParameters parameters, String groupId);

    DocumentActionResult save(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    /**
     * TODO - document the fact that passing an annotation to this will have no effect as it's not
     * actually recorded on the route log
     */
    DocumentActionResult saveDocumentData(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    void delete(String documentId, String principalId);

    void logAnnotation(String documentId, String principalId, String annotation);

    void initiateIndexing(String documentId);

    DocumentActionResult superUserBlanketApprove(DocumentActionParameters parameters,
            boolean executePostProcessor);

    DocumentActionResult superUserNodeApprove(DocumentActionParameters parameters, boolean executePostProcessor,
            String nodeName);

    DocumentActionResult superUserTakeRequestedAction(DocumentActionParameters parameters,
            boolean executePostProcessor, String actionRequestId);

    DocumentActionResult superUserDisapprove(DocumentActionParameters parameters, boolean executePostProcessor);

    DocumentActionResult superUserCancel(DocumentActionParameters parameters, boolean executePostProcessor);

    DocumentActionResult superUserReturnToPreviousNode(DocumentActionParameters parameters,
            boolean executePostProcessor, ReturnPoint returnPoint);

    DocumentActionResult placeInExceptionRouting(@WebParam(name = "parameters") DocumentActionParameters parameters)
            throws RiceIllegalArgumentException, InvalidDocumentContentException, InvalidActionTakenException;

    // TODO add the following methods to this service

    List<WorkflowAttributeValidationError> validateWorkflowAttributeDefinition(
            @WebParam(name = "definition") WorkflowAttributeDefinition definition);

    //	boolean isUserInRouteLog(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "principalId") String principalId,
    //			@WebParam(name = "lookFuture") boolean lookFuture)
    //			throws WorkflowException;
    //
    //	boolean isUserInRouteLogWithOptionalFlattening(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "principalId") String principalId,
    //			@WebParam(name = "lookFuture") boolean lookFuture,
    //			@WebParam(name = "flattenNodes") boolean flattenNodes)
    //			throws WorkflowException;
    //
    //	void reResolveRoleByDocTypeName(
    //			@WebParam(name = "documentTypeName") String documentTypeName,
    //			@WebParam(name = "roleName") String roleName,
    //			@WebParam(name = "qualifiedRoleNameLabel") String qualifiedRoleNameLabel)
    //			throws WorkflowException;
    //
    //	void reResolveRoleByDocumentId(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "roleName") String roleName,
    //			@WebParam(name = "qualifiedRoleNameLabel") String qualifiedRoleNameLabel)
    //			throws WorkflowException;
    //
    //	DocumentDetailDTO routingReport(
    //			@WebParam(name = "reportCriteria") ReportCriteriaDTO reportCriteria)
    //			throws WorkflowException;
    //
    //	boolean isFinalApprover(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "principalId") String principalId)
    //			throws WorkflowException;
    //
    //	boolean isLastApproverAtNode(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "principalId") String principalId,
    //			@WebParam(name = "nodeName") String nodeName)
    //			throws WorkflowException;
    //
    //	boolean routeNodeHasApproverActionRequest(
    //			@WebParam(name = "docType") String docType,
    //			@WebParam(name = "docContent") String docContent,
    //			@WebParam(name = "nodeName") String nodeName)
    //			throws WorkflowException;
    //	
    //	boolean documentWillHaveAtLeastOneActionRequest(
    //			@WebParam(name = "reportCriteriaDTO") ReportCriteriaDTO reportCriteriaDTO,
    //			@WebParam(name = "actionRequestedCodes") String[] actionRequestedCodes,
    //			@WebParam(name = "ignoreCurrentActionRequests") boolean ignoreCurrentActionRequests);
    //    	
    //	
    //	String[] getPrincipalIdsInRouteLog(
    //			@WebParam(name = "documentId") String documentId,
    //			@WebParam(name = "lookFuture") boolean lookFuture)
    //			throws WorkflowException;

}
