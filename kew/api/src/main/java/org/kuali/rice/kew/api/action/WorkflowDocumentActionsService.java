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
	
	public DocumentActionResult acknowledge(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
	
	public DocumentActionResult approve(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
			
    public DocumentActionResult adHocToPrincipal(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate, AdHocToPrincipal adHocCommand);
    
    public DocumentActionResult adHocToGroup(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate, AdHocToGroup adHocCommand);
    
    public DocumentActionResult revokeAdHocRequestById(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate, String actionRequestId);
    
    public DocumentActionResult revokeAdHocRequests(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate, AdHocRevoke revoke);
    
    public DocumentActionResult revokeAllAdHocRequests(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
        
    public DocumentActionResult cancel(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
    
    public DocumentActionResult clearFyi(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
    
    public DocumentActionResult complete(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
    
    public DocumentActionResult disapprove(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);

    public DocumentActionResult route(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
    
    public DocumentActionResult blanketApprove(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
    
    public DocumentActionResult blanketApproveToNodes(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate, Set<String> nodeNames);
    
    public DocumentActionResult returnToPreviousNode(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate, ReturnPoint returnPoint);

    public void move(String documentId, String principalId, MovePoint movePoint, String annotation);
    
    public void takeGroupAuthority(String documentId, String principalId, String groupId, String annotation);
    
    public void releaseGroupAuthority(String documentId, String principalId, String groupId, String annotation);
    
    public DocumentActionResult save(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
    
    public DocumentActionResult saveDocumentData(String documentId, String principalId, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
    
    public void delete(String documentId);
    
    public void logAnnotation(String documentId, String principalId, String annotation);
        
    public void initiateIndexing(String documentId);
    
    public void superUserFullApprove(String documentId, String principalId, boolean executePostProcessor, String annotation);
    
    public void superUserNodeApprove(String documentId, String principalId, String nodeName, boolean executePostProcessor, String annotation);
    
    public void superUserTakeRequestedAction(String documentId, String principalId, String actionRequestId, boolean executePostProcessor, String annotation);
    
    public void superUserDisapprove(String documentId, String principalId, boolean executePostProcessor, String annotation);
    
    public void superUserCancel(String documentId, String principalId, boolean executePostProcessor, String annotation);
    
    public void superUserReturnToPreviousNode(String documentId, String principalId, ReturnPoint returnPoint, boolean executePostProcessor, String annotation);
    
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
