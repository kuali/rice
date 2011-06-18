package org.kuali.rice.kew.api.action;

import java.util.List;

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

	public void acknowledge(String documentId, String principalId, String annotation);
	
	public void approve(String documentId, String principalId, String annotation);
			
    public void adHocToPrincipal(String documentId, String principalId, AdHocToPrincipal adHocCommand, String annotation);
    
    public void adHocToGroup(String documentId, String principalId, AdHocToGroup adHocCommand, String annotation);
    
    public void revokeAdHocRequestsFromPrincipal(String documentId, String principalId, AdHocRevokeFromPrincipal revoke, String annotation);
    
    public void revokeAdHocRequestsFromGroup(String documentId, String principalId, AdHocRevokeFromGroup revoke, String annotation);
    
    public void cancel(String documentId, String principalId, String annotation);
    
    public void clearFyi(String documentId, String principalId, String annotation);
    
    public void complete(String documentId, String principalId, String annotation);
    
    public void disapprove(String documentId, String principalId, String annotation);

    public void route(String documentId, String principalId, String annotation, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate);
    
    public void blanketApproveToNodes(String documentId, String principalId, List<String> nodeNames, String annotation);
    
    public void returnToPreviousNode(String documentId, String principalId, ReturnPoint returnPoint, String annotation);

    public void move(String documentId, String principalId, MovePoint movePoint, String annotation);
    
    public void takeGroupAuthority(String documentId, String principalId, String groupId, String annotation);
    
    public void releaseGroupAuthority(String documentId, String principalId, String groupId, String annotation);
    
    public void save(String documentId, String principalId, String annotation);
    
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
