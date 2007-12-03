/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.clientapp;

import java.rmi.RemoteException;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.RiceConfigurer;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.workflow.config.KEWConfigurer;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
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
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.InvalidWorkgroupException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.server.WorkflowUtility;
import edu.iu.uis.eden.util.Utilities;

/**
 * Convenience class for client applications to query workflow.  This class is one of two
 * (Java) client interfaces to the KEW system (the other being {@link WorkflowDocument} class).
 *
 * <p>The first time an instance of this class is created, it will read the client configuration to
 * determine how to connect to KEW.  To use this API, simply create a new instance using the
 * empty constructor.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowInfo implements java.io.Serializable {

	private static final long serialVersionUID = 3231835171780770399L;

	/**
     * Retrieves the WorkflowUtility proxy from the locator.  The locator will cache this for us.
     */
    private WorkflowUtility getWorkflowUtility() throws WorkflowException {
    	initializeBus();
    	return (WorkflowUtility)GlobalResourceLoader.getService(KEWServiceLocator.WORKFLOW_UTILITY_SERVICE);
    }

    /**
     * Initializes the KSB configuration if it has not already been initialized by the application;
     * in that case only the KEW configurer is added.
     * @throws WorkflowException if there is an error starting the RiceConfigurer
     */
    private synchronized void initializeBus() throws WorkflowException {
    	if (!isLocal() && !GlobalResourceLoader.isInitialized()) {
    		RiceConfigurer configurer = new RiceConfigurer();
    		configurer.setMessageEntity(EdenConstants.KEW_MESSAGING_ENTITY);
    		configurer.getModules().add(new KEWConfigurer());
    		try {
    			configurer.start();
    		} catch (Exception e) {
    			if (e instanceof WorkflowException) {
    				throw (WorkflowException)e;
    			} else if (e instanceof RuntimeException) {
    				throw (RuntimeException)e;
    			}
    			throw new WorkflowException(e);
    		}
    	}
    }

    private boolean isLocal() {
	Config config = Core.getCurrentContextConfig();
	if (config != null) {
	    return config.getProperty(Config.CLIENT_PROTOCOL).equals(EdenConstants.LOCAL_CLIENT_PROTOCOL);
	}
	return false;
    }

    /**
     * Returns the RouteHeaderVO of the specified document for the specified user
     * @param userId userId as whom to obtain the route header VO
     * @param routeHeaderId the id of the document whose route header VO to obtain
     * @return the RouteHeaderVO of the specified document for the specified user
     * @throws WorkflowException if an error occurs obtaining the route header VO
     * @see WorkflowUtility#getRouteHeaderWithUser(UserIdVO, Long)
     */
    public RouteHeaderVO getRouteHeader(UserIdVO userId, Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getRouteHeaderWithUser(userId, routeHeaderId);
        } catch (Exception e) {
            throw handleException(e);
        }

    }

    /**
     * Returns the RouteHeaderVO of the specified document
     * @param routeHeaderId the id of the document whose route header VO to obtain
     * @return the RouteHeaderVO of the specified document
     * @throws WorkflowException if an error occurs obtaining the route header VO
     * @see WorkflowUtility#getRouteHeader(Long)
     */
    public RouteHeaderVO getRouteHeader(Long documentId) throws WorkflowException {
        try {
            return getWorkflowUtility().getRouteHeader(documentId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns the status of the document with the given ID.
     *
     * @since 0.9.1
     * @throws WorkflowException if document cannot be found for the given ID or
     * if the given document ID is null.
     */
    public String getDocumentStatus(Long documentId) throws WorkflowException {
	try {
	    return getWorkflowUtility().getDocumentStatus(documentId);
	} catch (Exception e) {
	    throw handleException(e);
	}
    }

    /**
     * Returns the WorkgroupVO given a workgroup id
     * @param workgroupId id of the workgroup to obtain
     * @return the WorkgroupVO given a workgroup id
     * @throws WorkflowException if an error occurs obtaining the workgroup
     * @see WorkflowUtility#getWorkgroup(WorkgroupIdVO)
     */
    public WorkgroupVO getWorkgroup(WorkgroupIdVO workgroupId) throws WorkflowException {
        try {
            return getWorkflowUtility().getWorkgroup(workgroupId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns the UserVO given a user id
     * @param userId id of the user to obtain
     * @return the UserVO given a user id
     * @throws WorkflowException if an error occurs obtaining the user
     * @see WorkflowUtility#getWorkflowUser(UserIdVO)
     */
    public UserVO getWorkflowUser(UserIdVO userId) throws WorkflowException {
        try {
            return getWorkflowUtility().getWorkflowUser(userId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns the DocumentType of the document with the specified id
     * @param documentTypeId the id of the document whose document type we should return
     * @return the DocumentTypeVO of the document with the specified id
     * @throws WorkflowException if an error occurs obtaining the document type
     * @see WorkflowUtility#getDocumentType(Long)
     */
    public DocumentTypeVO getDocType(Long documentTypeId) throws WorkflowException {
        try {
            return getWorkflowUtility().getDocumentType(documentTypeId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns the DocumentType of the document type with the specified name
     * @param documentTypeName the name of the document type whose DocumentType we should return
     * @return the DocumentTypeVO of the document type with the specified name
     * @throws WorkflowException if an error occurs obtaining the document type
     * @see WorkflowUtility#getDocumentTypeByName(String)
     */
    public DocumentTypeVO getDocType(String documentTypeName) throws WorkflowException {
        try {
            return getWorkflowUtility().getDocumentTypeByName(documentTypeName);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns a new unique id to be used as a responsibility id
     * @return a new unique id to be used as a responsibility id
     * @throws WorkflowException if an error occurs obtaining a new responsibility id
     * @see WorkflowUtility#getNewResponsibilityId()
     */
    public Long getNewResponsibilityId() throws WorkflowException {
        try {
            return getWorkflowUtility().getNewResponsibilityId();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns an array of workgroups the specified user is in.
     * @param userId the id of the user whose workgroups we are to return
     * @return an array of workgroups the specified user is in
     * @throws WorkflowException if an error occurs obtaining the user's workgroups
     * @see WorkflowUtility#getUserWorkgroups(UserIdVO)
     */
    public WorkgroupVO[] getUserWorkgroups(UserIdVO userId) throws WorkflowException {
        try {
            return getWorkflowUtility().getUserWorkgroups(userId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns the pending action requests of the document of the specified id
     * @param routeHeaderId the id of the document whose action requests will be retrieved
     * @return the pending action requests of the document of the specified id
     * @throws WorkflowException if an error occurs obtaining the documents action requests
     * @see WorkflowUtility#getActionRequests(Long)
     */
    public ActionRequestVO[] getActionRequests(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getActionRequests(routeHeaderId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

   /**
    * Returns the pending action requests of the document of the specified id for the specified
    * user and/or the specified node name.  If both user and node name are empty then will
    * return all pending action requests.
    * @param routeHeaderId the id of the document whose action requests will be retrieved
    * @param nodeName the node name of the requests to be retrieved
    * @param userId the user that the requests would be satisfied by
    * @return the pending action requests of the document of the specified id
    * @throws WorkflowException if an error occurs obtaining the documents action requests
    * @see WorkflowUtility#getActionRequests(Long)
    */
   public ActionRequestVO[] getActionRequests(Long routeHeaderId, String nodeName, UserIdVO userId) throws WorkflowException {
       try {
           return getWorkflowUtility().getActionRequests(routeHeaderId, nodeName, userId);
       } catch (Exception e) {
           throw handleException(e);
       }
   }

   /**


    /**
     * Returns the actions taken on the document of the specified id
     * @param routeHeaderId the id of the document whose actions taken will be retrieved
     * @return the actions taken on the document of the specified id
     * @throws WorkflowException if an error occurs obtaining the actions taken
     * @see WorkflowUtility#getActionsTaken(Long)
     */
    public ActionTakenVO[] getActionsTaken(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getActionsTaken(routeHeaderId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns whether the user is in the document's route log (whether an action request has been, or may be,
     * generated for the user)
     * @param routeHeaderId the id of the document to analyze
     * @param userId the id of the user to check
     * @param lookFuture whether to evaluate potential future requests
     * @return whether the user is in the document's route log
     * @throws WorkflowException if an error occurs determining whether the user is in the document's route log
     * @see WorkflowUtility#isUserInRouteLog(Long, UserIdVO, boolean)
     */
    public boolean isUserAuthenticatedByRouteLog(Long routeHeaderId, UserIdVO userId, boolean lookFuture) throws WorkflowException {
        try {
            return getWorkflowUtility().isUserInRouteLog(routeHeaderId, userId, lookFuture);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns whether the specified user is the final approver for the document
     * @param routeHeaderId the id of document to check
     * @param userId the id of the user to check
     * @return whether the specified user is the final approver for the document
     * @throws WorkflowException if an error occurs determining whether the user is the final approver on the document
     * @see WorkflowUtility#isFinalApprover(Long, UserIdVO)
     */
    public boolean isFinalApprover(Long routeHeaderId, UserIdVO userId) throws WorkflowException {
        try {
            return getWorkflowUtility().isFinalApprover(routeHeaderId, userId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Validate the WorkflowAttributeDefinition against it's attribute on the server.  This will validate
     * the inputs that will eventually become xml.
     *
     * Only applies to attributes implementing WorkflowAttributeXmlValidator.
     *
     * @param attributeDefinition the attribute definition to validate
     * @return WorkflowAttributeValidationErrorVO[] of error from the attribute
     * @throws WorkflowException when attribute doesn't implement WorkflowAttributeXmlValidator
     * @see WorkflowUtility#validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionVO)
     */
    public WorkflowAttributeValidationErrorVO[] validAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) throws WorkflowException {
        try {
            return getWorkflowUtility().validateWorkflowAttributeDefinitionVO(attributeDefinition);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Helper to prevent us from needlessly wrapping a WorkflowException in another WorkflowException.
     */
    private WorkflowException handleException(Exception e) {
    	if (e instanceof WorkflowException) {
    		return (WorkflowException)e;
    	}
    	return new WorkflowException(e);
    }

    // WORKFLOW 2.3: new methods

    /**
     * Runs a "rule report" give a rule report criteria.
     * @param ruleReportCriteria the criteria for the rule report
     * @return an array of RuleVO representing rules that will fire under the specified criteria
     * @see WorkflowUtility#ruleReport(RuleReportCriteriaVO)
     */
    public RuleVO[] ruleReport(RuleReportCriteriaVO ruleReportCriteria) throws WorkflowException {
        try {
            return getWorkflowUtility().ruleReport(ruleReportCriteria);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    // WORKFLOW 2.1: new methods

    /**
     * Returns a document detail VO representing the route header along with action requests, actions taken,
     * and route node instances.
     * @param documentId id of the document whose details should be returned
     * @return Returns a document detail VO representing the route header along with action requests, actions taken, and route node instances.
     * @throws WorkflowException
     * @see WorkflowUtility#getDocumentDetail(Long)
     */
    public DocumentDetailVO getDocumentDetail(Long documentId) throws WorkflowException {
        try {
            return getWorkflowUtility().getDocumentDetail(documentId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns a node instance of the specified note instance id
     * @param nodeInstanceId the id of the node instance to return
     * @return a node instance of the specified note instance id
     * @throws WorkflowException if an error occurs obtaining the node instance
     * @see WorkflowUtility#getNodeInstance(Long)
     */
    public RouteNodeInstanceVO getNodeInstance(Long nodeInstanceId) throws WorkflowException {
        try {
            return getWorkflowUtility().getNodeInstance(nodeInstanceId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns the route node instances that have been created so far during the life of the specified document.  This includes
     * all previous instances which have already been processed and are no longer active.
     * @param routeHeaderId the id of the document whose route node instances should be returned
     * @return the route node instances that have been created so far during the life of this document
     * @throws WorkflowException if there is an error getting the route node instances for the document
     * @see WorkflowUtility#getDocumentRouteNodeInstances(Long)
     */
    public RouteNodeInstanceVO[] getDocumentRouteNodeInstances(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getDocumentRouteNodeInstances(routeHeaderId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns all active node instances on the document.
     * @param routeHeaderId id of the document whose active node instances should be returned
     * @return all active node instances on the document
     * @throws WorkflowException if there is an error obtaining the currently active nodes on the document
     * @see WorkflowUtility#getActiveNodeInstances(Long)
     */
    public RouteNodeInstanceVO[] getActiveNodeInstances(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getActiveNodeInstances(routeHeaderId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns all node instances on the document which have no successor.
     * @param routeHeaderId id of the document whose terminal node instances should be returned
     * @return all terminal node instances of the document
     * @throws WorkflowException if there is an error obtaining the terminal node instances on the document
     * @see WorkflowUtility#getTerminalNodeInstances(Long)
     */
    public RouteNodeInstanceVO[] getTerminalNodeInstances(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getTerminalNodeInstances(routeHeaderId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Re-resolves the specified role on the document, and refreshes any pending action requests.
     * @param documentTypeName the type of the document for which to re-resolve roles
     * @param roleName the role name to re-resolve
     * @param qualifiedRoleNameLabel the qualified role name label
     * @throws WorkflowException if an error occurs re-resolving the role
     * @see WorkflowUtility#reResolveRole(String, String, String)
     */
    public void reResolveRole(String documentTypeName, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
        try {
            getWorkflowUtility().reResolveRole(documentTypeName, roleName, qualifiedRoleNameLabel);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Re-resolves the specified role on the document, and refreshes any pending action requests.
     * @param documentId the id of the document for which to re-resolve roles
     * @param roleName the role name to re-resolve
     * @param qualifiedRoleNameLabel the qualified role name label
     * @throws WorkflowException if an error occurs re-resolving the role
     * @see WorkflowUtility#reResolveRoleByDocumentId(Long, String, String)
     */
    public void reResolveRole(Long documentId, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
        try {
            getWorkflowUtility().reResolveRoleByDocumentId(documentId, roleName, qualifiedRoleNameLabel);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Runs a "routing report"
     * @param reportCriteria routing report criteria
     * @return DocumentDetailVO detailing the actionrequests that would be generated under the specified criteria
     * @see WorkflowUtility#routingReport(ReportCriteriaVO)
     */
    public DocumentDetailVO routingReport(ReportCriteriaVO reportCriteria) throws WorkflowException {
        try {
            return getWorkflowUtility().routingReport(reportCriteria);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns whether the specified user is the last approver at the specified node name
     * @param routeHeaderId the id of document to check
     * @param userId the id of the user to check
     * @param nodeName name of node to check
     * @return whether the specified user is the last approver at the specified node name
     * @throws WorkflowException if an error occurs determining whether the user is the last approver at the specified node
     * @see WorkflowUtility#isLastApproverAtNode(Long, UserIdVO, String)
     */
    protected boolean isLastApproverAtNode(Long routeHeaderId, UserIdVO userId, String nodeName) throws WorkflowException {
        try {
            return getWorkflowUtility().isLastApproverAtNode(routeHeaderId, userId, nodeName);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns whether the specified node on the specified document type would produce approve or complete requests
     * @param docType the type of the document to check
     * @param docContent the content to use
     * @param nodeName the node to check
     * @return whether the specified node on the specified document type would produce approve or complete requests
     * @throws WorkflowException if an error occurs
     * @see WorkflowUtility#routeNodeHasApproverActionRequest(String, String, String)
     */
    protected boolean routeNodeHasApproverActionRequest(String docType, String docContent, String nodeName) throws WorkflowException {
        try {
            return getWorkflowUtility().routeNodeHasApproverActionRequest(docType, docContent, nodeName);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    // DEPRECATED: as of Workflow 2.0

    /**
     * @deprecated use getWorkgroup(WorkgroupIdVO) instead
     */
    public WorkgroupVO getWorkgroup(String workgroupName) throws WorkflowException {
        if (Utilities.isEmpty(workgroupName)) {
            throw new InvalidWorkgroupException("Workgroup name cannot be empty");
        }
        return getWorkgroup(new WorkgroupNameIdVO(workgroupName));//getWorkflowUtility().getWorkgroup(new WorkgroupNameIdVO(workgroupName));
    }

    /**
     * @deprecated use getWorkgroup(WorkgroupIdVO) instead
     */
    public WorkgroupVO getWorkgroup(Long workgroupId) throws WorkflowException {
        if (workgroupId == null) {
            throw new InvalidWorkgroupException("Workgroup name cannot be empty");
        }
        return getWorkgroup(new WorkflowGroupIdVO(workgroupId));
    }

    /**
     * @deprecated use getDocType using the name
     */
    public RouteTemplateEntryVO[] getRoute(String documentTypeName) throws WorkflowException {
        try {
            return getWorkflowUtility().getDocRoute(documentTypeName);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns the document content VO of the specified document
     * @param routeHeaderId the id of the document whose content should be returned
     * @return the document content VO of the specified document
     * @throws WorkflowException if an error occurs obtaining the document content
     * @see WorkflowUtility#getDocumentContent(Long)
     */
    public DocumentContentVO getDocumentContent(Long routeHeaderId) throws WorkflowException {
    	try {
    		return getWorkflowUtility().getDocumentContent(routeHeaderId);
    	} catch (Exception e) {
    		throw handleException(e);
    	}
    }

    /**
     * Returns names of nodes already traversed
     * @param documentId id of the document to check
     * @return names of nodes already traversed
     * @throws RemoteException if an error occurs
     * @throws WorkflowException if an error occurs
     * @see WorkflowUtility#getPreviousRouteNodeNames(Long)
     * TODO: RemoteException not thrown
     */
    public String[] getPreviousRouteNodeNames(Long documentId) throws RemoteException, WorkflowException {
        try {
            return getWorkflowUtility().getPreviousRouteNodeNames(documentId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Checks whether a document would product at least one action request under the specified criteria
     * @param reportCriteriaVO criteria under which to perform the check
     * @param actionRequestedCodes the types of action requests to check for
     * @return whether a document would product at least one action request under the specified criteria
     * @throws WorkflowException if an error occurs
     * @see WorkflowUtility#documentWillHaveAtLeastOneActionRequest(ReportCriteriaVO, String[])
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaVO reportCriteriaVO, String[] actionRequestedCodes) throws WorkflowException {
        try {
            return getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, actionRequestedCodes);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    // DEPRECATED: as of Workflow 2.1

    /**
     * @deprecated use {@link #isLastApproverAtNode(Long, UserIdVO, String)} instead
     */
    protected boolean isLastApproverInRouteLevel(Long routeHeaderId, UserIdVO userId, Integer routeLevel) throws WorkflowException {
        try {
            return getWorkflowUtility().isLastApproverInRouteLevel(routeHeaderId, userId, routeLevel);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * @deprecated use {@link #routeNodeHasApproverActionRequest(String, String, String)}
     */
    protected boolean routeLevelHasApproverActionRequest(String docType, String docContent, Integer routeLevel) throws WorkflowException {
        try {
            return getWorkflowUtility().routeLevelHasApproverActionRequest(docType, docContent, routeLevel);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}