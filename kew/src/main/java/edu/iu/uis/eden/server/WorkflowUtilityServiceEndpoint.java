/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.server;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.SpringLoader;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.clientapp.vo.DocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.RouteNodeInstanceVO;
import edu.iu.uis.eden.clientapp.vo.RouteTemplateEntryVO;
import edu.iu.uis.eden.clientapp.vo.RuleReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RuleVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeValidationErrorVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * @workflow.webservice name="WorkflowUtility"
 */
public class WorkflowUtilityServiceEndpoint implements WorkflowUtility {

	private WorkflowUtility utility;

    public void init(Object context) {
    	// fetch the service directly from the SpringLoader to gaurantee we are fetching the local, in-memory service
        this.utility = (WorkflowUtility)SpringLoader.getInstance().getService(new QName(KEWServiceLocator.WORKFLOW_UTILITY_SERVICE));
//    	this.utility = ;
    }

    public void destroy() {}

	public ActionRequestVO[] getActionRequests(Long routeHeaderId) throws RemoteException, WorkflowException {
		return utility.getActionRequests(routeHeaderId);
	}

	    public ActionRequestVO[] getActionRequests(Long routeHeaderId, String nodeName, UserIdVO userId) throws RemoteException, WorkflowException {
	        return utility.getActionRequests(routeHeaderId, nodeName, userId);
	    }


	public WorkflowAttributeValidationErrorVO[] validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionVO definition) throws RemoteException, WorkflowException {
        return utility.validateWorkflowAttributeDefinitionVO(definition);
    }

    public ActionTakenVO[] getActionsTaken(Long routeHeaderId) throws RemoteException, WorkflowException {
		return utility.getActionsTaken(routeHeaderId);
	}

	public RouteTemplateEntryVO[] getDocRoute(String docName) throws RemoteException, WorkflowException {
		return utility.getDocRoute(docName);
	}

	public DocumentTypeVO getDocumentType(Long documentTypeId) throws RemoteException, WorkflowException {
		return utility.getDocumentType(documentTypeId);
	}

	public DocumentTypeVO getDocumentTypeByName(String documentTypeName) throws RemoteException, WorkflowException {
		return utility.getDocumentTypeByName(documentTypeName);
	}

	public Long getNewResponsibilityId() throws RemoteException, WorkflowException {
		return utility.getNewResponsibilityId();
	}

	public RouteHeaderVO getRouteHeader(Long routeHeaderId) throws RemoteException, WorkflowException {
		return utility.getRouteHeader(routeHeaderId);
	}

	public RouteHeaderVO getRouteHeaderWithUser(UserIdVO userId, Long routeHeaderId) throws RemoteException, WorkflowException {
		return utility.getRouteHeaderWithUser(userId, routeHeaderId);
	}

	public WorkgroupVO[] getUserWorkgroups(UserIdVO userId) throws RemoteException, WorkflowException {
		return utility.getUserWorkgroups(userId);
	}

	public UserVO getWorkflowUser(UserIdVO userId) throws RemoteException, WorkflowException {
		return utility.getWorkflowUser(userId);
	}

	public WorkgroupVO getWorkgroup(WorkgroupIdVO workgroupId) throws RemoteException, WorkflowException {
		return utility.getWorkgroup(workgroupId);
	}

	public boolean isFinalApprover(Long routeHeaderId, UserIdVO userId) throws RemoteException, WorkflowException {
		return utility.isFinalApprover(routeHeaderId, userId);
	}

	public boolean isLastApproverInRouteLevel(Long routeHeaderId, UserIdVO userId, Integer routeLevel) throws RemoteException, WorkflowException {
		return utility.isLastApproverInRouteLevel(routeHeaderId, userId, routeLevel);
	}

	public boolean isLastApproverAtNode(Long routeHeaderId, UserIdVO userId, String nodeName) throws RemoteException, WorkflowException {
        return utility.isLastApproverAtNode(routeHeaderId, userId, nodeName);
    }

    public boolean isUserInRouteLog(Long routeHeaderId, UserIdVO userId, boolean lookFuture) throws RemoteException, WorkflowException {
		return utility.isUserInRouteLog(routeHeaderId, userId, lookFuture);
	}

	public void reResolveRoleByDocumentId(Long documentId, String roleName, String qualifiedRoleNameLabel) throws RemoteException, WorkflowException {
		utility.reResolveRoleByDocumentId(documentId, roleName, qualifiedRoleNameLabel);
	}

	public void reResolveRole(String documentTypeName, String roleName, String qualifiedRoleNameLabel) throws RemoteException, WorkflowException {
		utility.reResolveRole(documentTypeName, roleName, qualifiedRoleNameLabel);
	}

	public boolean routeLevelHasApproverActionRequest(String docType, String docContent, Integer routeLevel) throws RemoteException, WorkflowException {
		return utility.routeLevelHasApproverActionRequest(docType, docContent, routeLevel);
	}

    public boolean routeNodeHasApproverActionRequest(String docType, String docContent, String nodeName) throws RemoteException, WorkflowException {
        return utility.routeNodeHasApproverActionRequest(docType, docContent, nodeName);
    }

	public DocumentDetailVO routingReport(ReportCriteriaVO reportCriteria) throws RemoteException, WorkflowException {
		return utility.routingReport(reportCriteria);
	}

    public RouteNodeInstanceVO[] getDocumentRouteNodeInstances(Long routeHeaderId) throws RemoteException, WorkflowException {
        return utility.getDocumentRouteNodeInstances(routeHeaderId);
    }

    public DocumentDetailVO getDocumentDetail(Long documentId) throws RemoteException, WorkflowException {
        return utility.getDocumentDetail(documentId);
    }

    public RouteNodeInstanceVO getNodeInstance(Long nodeInstanceId) throws RemoteException, WorkflowException {
        return utility.getNodeInstance(nodeInstanceId);
    }

    public RouteNodeInstanceVO[] getActiveNodeInstances(Long documentId) throws RemoteException, WorkflowException {
        return utility.getActiveNodeInstances(documentId);
    }

    public RouteNodeInstanceVO[] getTerminalNodeInstances(Long documentId) throws RemoteException, WorkflowException {
        return utility.getTerminalNodeInstances(documentId);
    }

	public DocumentContentVO getDocumentContent(Long routeHeaderId) throws RemoteException, WorkflowException {
		return utility.getDocumentContent(routeHeaderId);
	}

	public boolean isSuperUserForDocumentType(UserIdVO userId, Long documentTypeId) throws RemoteException, WorkflowException {
		return utility.isSuperUserForDocumentType(userId, documentTypeId);
	}

	public String[] getPreviousRouteNodeNames(Long documentId) throws RemoteException, WorkflowException {
		return utility.getPreviousRouteNodeNames(documentId);
	}

    public RuleVO[] ruleReport(RuleReportCriteriaVO ruleReportCriteria) throws RemoteException, WorkflowException {
        return utility.ruleReport(ruleReportCriteria);
    }

    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaVO reportCriteriaVO, String[] actionRequestedCodes) throws RemoteException {
        return utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, actionRequestedCodes);
    }
}
