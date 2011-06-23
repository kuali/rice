package org.kuali.rice.kew.api.action;

import java.util.List;
import java.util.Set;

import javax.jws.WebParam;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.doctype.DocumentTypeNotFoundException;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentCreationException;
import org.kuali.rice.kew.api.document.DocumentUpdate;
import org.kuali.rice.kew.api.document.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.WorkflowAttributeValidationError;

// TODO still need to add JAX-WS annotations to this class

public interface WorkflowDocumentActionsService {

	public Document create(String documentTypeName, String initiatorPrincipalId, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate)
		throws RiceIllegalArgumentException, DocumentTypeNotFoundException, DocumentCreationException;
	
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
     * TODO - document the fact that passing an annotation to this will have no effect as it's not actually recorded on the route log
     */
    public DocumentActionResult saveDocumentData(DocumentActionParameters parameters);
    
    public void delete(String documentId, String principalId);
    
    public void logAnnotation(String documentId, String principalId, String annotation);
        
    public void initiateIndexing(String documentId);
    
    public DocumentActionResult superUserBlanketApprove(DocumentActionParameters parameters, boolean executePostProcessor);
    
    public DocumentActionResult superUserNodeApprove(DocumentActionParameters parameters, boolean executePostProcessor, String nodeName);
    
    public DocumentActionResult superUserTakeRequestedAction(DocumentActionParameters parameters, boolean executePostProcessor, String actionRequestId);
    
    public DocumentActionResult superUserDisapprove(DocumentActionParameters parameters, boolean executePostProcessor);
    
    public DocumentActionResult superUserCancel(DocumentActionParameters parameters, boolean executePostProcessor);
    
    public DocumentActionResult superUserReturnToPreviousNode(DocumentActionParameters parameters, boolean executePostProcessor, ReturnPoint returnPoint);
    
    public void placeInExceptionRouting(String documentId, String principalId);
    
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
//	public boolean isSuperUserForDocumentType(
//			@WebParam(name = "principalId") String principalId,
//			@WebParam(name = "documentTypeId") Long documentTypeId)
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
