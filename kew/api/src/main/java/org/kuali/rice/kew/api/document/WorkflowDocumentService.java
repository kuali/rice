package org.kuali.rice.kew.api.document;

import java.util.List;

import org.kuali.rice.kew.api.document.actions.AdHocRevokeFromGroup;
import org.kuali.rice.kew.api.document.actions.AdHocRevokeFromPrincipal;
import org.kuali.rice.kew.api.document.actions.AdHocToGroup;
import org.kuali.rice.kew.api.document.actions.AdHocToPrincipal;
import org.kuali.rice.kew.api.document.actions.MovePoint;
import org.kuali.rice.kew.api.document.actions.ReturnPoint;
import org.kuali.rice.kew.api.document.actions.ValidActions;

// TODO still need to add JAX-WS annotations to this class

public interface WorkflowDocumentService {

	public Document createDocument(String documentTypeName, String initiatorPrincipalId, String title, DocumentContentUpdate documentContent);
	
	public Document retrieveDocument(String documentId);
	
	public DocumentContent retrieveDocumentContent(String documentId);
	
	public void updateDocumentTitle(String documentId, String documentTitle);
	
	public DocumentContent updateDocumentContent(String documentId, DocumentContentUpdate documentContent);
	
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

    public void submit(String documentId, String principalId, String annotation);
    
    public void blanketApproveToNodes(String documentId, String principalId, List<String> nodeNames, String annotation);
    
    public void returnToPreviousNode(String documentId, String principalId, ReturnPoint returnPoint, String annotation);

    public void moveDocument(String documentId, String principalId, MovePoint movePoint, String annotation);
    
    public void takeGroupAuthority(String documentId, String principalId, String groupId, String annotation);
    
    public void releaseGroupAuthority(String documentId, String principalId, String groupId, String annotation);
    
    public void save(String documentId, String principalId, String annotation);
    
    public void delete(String documentId);
    
    public void logAnnotation(String documentId, String principalId, String annotation);
        
    public void initiateDocumentIndexing(String documentId);
    
    public void superUserFullApprove(String documentId, String principalId, boolean executePostProcessor, String annotation);
    
    public void superUserNodeApprove(String documentId, String principalId, String nodeName, boolean executePostProcessor, String annotation);
    
    public void superUserTakeRequestedAction(String documentId, String principalId, String actionRequestId, boolean executePostProcessor, String annotation);
    
    public void superUserDisapprove(String documentId, String principalId, boolean executePostProcessor, String annotation);
    
    public void superUserCancel(String documentId, String principalId, boolean executePostProcessor, String annotation);
    
    public void superUserReturnToPreviousNode(String documentId, String principalId, ReturnPoint returnPoint, boolean executePostProcessor, String annotation);
    
    public void placeInExceptionRouting(String documentId, String principalId);
    
    //public List<ActionRequest> getActionRequests(String documentId);
	
}
