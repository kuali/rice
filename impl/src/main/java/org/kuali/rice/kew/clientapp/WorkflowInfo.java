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
package org.kuali.rice.kew.clientapp;

import java.rmi.RemoteException;

import org.kuali.rice.core.Core;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.RiceConfigurer;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.dto.ActionItemDTO;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.DocumentDetailDTO;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.dto.RouteTemplateEntryDTO;
import org.kuali.rice.kew.dto.RuleDTO;
import org.kuali.rice.kew.dto.RuleReportCriteriaDTO;
import org.kuali.rice.kew.dto.UserDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeValidationErrorDTO;
import org.kuali.rice.kew.dto.WorkflowGroupIdDTO;
import org.kuali.rice.kew.dto.WorkgroupDTO;
import org.kuali.rice.kew.dto.WorkgroupIdDTO;
import org.kuali.rice.kew.dto.WorkgroupNameIdDTO;
import org.kuali.rice.kew.exception.InvalidWorkgroupException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowUtility;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.workflow.config.KEWConfigurer;


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
        WorkflowUtility workflowUtility = (WorkflowUtility)GlobalResourceLoader.getService(KEWConstants.WORKFLOW_UTILITY_SERVICE);
    	if (workflowUtility == null) {
    		throw new WorkflowException("Could not locate the WorkflowUtility service.  Please ensure that KEW client is configured properly!");
    	}
    	return workflowUtility;

    }

    /**
     * Initializes the KSB configuration if it has not already been initialized by the application;
     * in that case only the KEW configurer is added.
     * @throws WorkflowException if there is an error starting the RiceConfigurer
     */
    private synchronized void initializeBus() throws WorkflowException {
    	if (!isLocal() && !GlobalResourceLoader.isInitialized()) {
    		RiceConfigurer configurer = new RiceConfigurer();
    		configurer.setMessageEntity(KEWConstants.KEW_MESSAGING_ENTITY);
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
	    return config.getProperty(Config.CLIENT_PROTOCOL).equals(KEWConstants.LOCAL_CLIENT_PROTOCOL);
	}
	return false;
    }

    /**
     * Returns the RouteHeaderVO of the specified document for the specified user
     * @param userId userId as whom to obtain the route header VO
     * @param routeHeaderId the id of the document whose route header VO to obtain
     * @return the RouteHeaderVO of the specified document for the specified user
     * @throws WorkflowException if an error occurs obtaining the route header VO
     * @see WorkflowUtility#getRouteHeaderWithUser(UserIdDTO, Long)
     */
    public RouteHeaderDTO getRouteHeader(UserIdDTO userId, Long routeHeaderId) throws WorkflowException {
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
    public RouteHeaderDTO getRouteHeader(Long documentId) throws WorkflowException {
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
     * @see WorkflowUtility#getWorkgroup(WorkgroupIdDTO)
     */
    public WorkgroupDTO getWorkgroup(WorkgroupIdDTO workgroupId) throws WorkflowException {
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
     * @see WorkflowUtility#getWorkflowUser(UserIdDTO)
     */
    public UserDTO getWorkflowUser(UserIdDTO userId) throws WorkflowException {
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
    public DocumentTypeDTO getDocType(Long documentTypeId) throws WorkflowException {
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
    public DocumentTypeDTO getDocType(String documentTypeName) throws WorkflowException {
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
     * @see WorkflowUtility#getUserWorkgroups(UserIdDTO)
     */
    public WorkgroupDTO[] getUserWorkgroups(UserIdDTO userId) throws WorkflowException {
        try {
            return getWorkflowUtility().getUserWorkgroups(userId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    
    public Integer getUserActionItemCount(UserIdDTO userId) throws WorkflowException {
        try {
            return getWorkflowUtility().getUserActionItemCount(userId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    
    public ActionItemDTO[] getActionItems(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getActionItems(routeHeaderId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    public ActionItemDTO[] getActionItems(Long routeHeaderId, String[] actionRequestedCodes) throws WorkflowException {
        try {
            return getWorkflowUtility().getActionItems(routeHeaderId, actionRequestedCodes);
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
    public ActionRequestDTO[] getActionRequests(Long routeHeaderId) throws WorkflowException {
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
   public ActionRequestDTO[] getActionRequests(Long routeHeaderId, String nodeName, UserIdDTO userId) throws WorkflowException {
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
    public ActionTakenDTO[] getActionsTaken(Long routeHeaderId) throws WorkflowException {
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
     * @see WorkflowUtility#isUserInRouteLog(Long, UserIdDTO, boolean)
     */
    public boolean isUserAuthenticatedByRouteLog(Long routeHeaderId, UserIdDTO userId, boolean lookFuture) throws WorkflowException {
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
     * @see WorkflowUtility#isFinalApprover(Long, UserIdDTO)
     */
    public boolean isFinalApprover(Long routeHeaderId, UserIdDTO userId) throws WorkflowException {
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
     * @see WorkflowUtility#validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionDTO)
     */
    public WorkflowAttributeValidationErrorDTO[] validAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) throws WorkflowException {
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
     * @see WorkflowUtility#ruleReport(RuleReportCriteriaDTO)
     */
    public RuleDTO[] ruleReport(RuleReportCriteriaDTO ruleReportCriteria) throws WorkflowException {
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
    public DocumentDetailDTO getDocumentDetail(Long documentId) throws WorkflowException {
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
    public RouteNodeInstanceDTO getNodeInstance(Long nodeInstanceId) throws WorkflowException {
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
    public RouteNodeInstanceDTO[] getDocumentRouteNodeInstances(Long routeHeaderId) throws WorkflowException {
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
    public RouteNodeInstanceDTO[] getActiveNodeInstances(Long routeHeaderId) throws WorkflowException {
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
    public RouteNodeInstanceDTO[] getTerminalNodeInstances(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getTerminalNodeInstances(routeHeaderId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    
    /**
     * Returns the current node instances on the document.  If the document has active nodes, those will
     * be returned.  Otherwise, all terminal nodes will be returned.
     * @param routeHeaderId id of the document whose current node instances should be returned
     * @return all current node instances of the document
     * @throws WorkflowException if there is an error obtaining the current node instances on the document
     * @see WorkflowUtility#getCurrentNodeInstances(Long)
     */
    public RouteNodeInstanceDTO[] getCurrentNodeInstances(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getCurrentNodeInstances(routeHeaderId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    
    /**
     * Returns names of all current nodes the document is currently at.  If the document has active nodes, those
     * will be returned.  Otherwise, the document's terminal nodes will be returned.
     *
     * @return names of all current nodes the document is currently at.
     * @throws WorkflowException if there is an error obtaining the current nodes on the document
     * @see WorkflowUtility#getCurrentNodeInstances(Long)
     */
    public String[] getCurrentNodeNames(Long documentId) throws WorkflowException {
        try {
            RouteNodeInstanceDTO[] currentNodeInstances = getWorkflowUtility().getCurrentNodeInstances(documentId);
            String[] nodeNames = new String[(currentNodeInstances == null ? 0 : currentNodeInstances.length)];
            for (int index = 0; index < currentNodeInstances.length; index++) {
                nodeNames[index] = currentNodeInstances[index].getName();
            }
            return nodeNames;
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
     * @see WorkflowUtility#routingReport(ReportCriteriaDTO)
     */
    public DocumentDetailDTO routingReport(ReportCriteriaDTO reportCriteria) throws WorkflowException {
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
     * @see WorkflowUtility#isLastApproverAtNode(Long, UserIdDTO, String)
     */
    protected boolean isLastApproverAtNode(Long routeHeaderId, UserIdDTO userId, String nodeName) throws WorkflowException {
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

    /**
     * This method allows a document search to be executed just as would occur from the User Interface using the given user as
     * the searching user
     * 
     * @param userId - user to use when executing the search (for security filtering purposes)
     * @param criteriaVO - criteria to use for the search
     * @return a {@link DocumentSearchResultDTO} object containing a list of search result columns and data rows
     * @throws RemoteException
     * @throws WorkflowException
     */
    public DocumentSearchResultDTO performDocumentSearch(UserIdDTO userId, DocumentSearchCriteriaDTO criteriaVO) throws RemoteException, WorkflowException {
        try {
            return getWorkflowUtility().performDocumentSearch(userId, criteriaVO);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * This method allows a document search to be executed just as would occur from the User Interface
     * 
     * @param criteriaVO - criteria to use for the search
     * @return a {@link DocumentSearchResultDTO} object containing a list of search result columns and data rows
     * @throws RemoteException
     * @throws WorkflowException
     */
    public DocumentSearchResultDTO performDocumentSearch(DocumentSearchCriteriaDTO criteriaVO) throws RemoteException, WorkflowException {
        try {
            return getWorkflowUtility().performDocumentSearch(criteriaVO);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    // DEPRECATED: as of Workflow 2.0

    /**
     * @deprecated use getWorkgroup(WorkgroupIdVO) instead
     */
    public WorkgroupDTO getWorkgroup(String workgroupName) throws WorkflowException {
        if (Utilities.isEmpty(workgroupName)) {
            throw new InvalidWorkgroupException("Workgroup name cannot be empty");
        }
        return getWorkgroup(new WorkgroupNameIdDTO(workgroupName));//getWorkflowUtility().getWorkgroup(new WorkgroupNameIdVO(workgroupName));
    }

    /**
     * @deprecated use getWorkgroup(WorkgroupIdVO) instead
     */
    public WorkgroupDTO getWorkgroup(Long workgroupId) throws WorkflowException {
        if (workgroupId == null) {
            throw new InvalidWorkgroupException("Workgroup name cannot be empty");
        }
        return getWorkgroup(new WorkflowGroupIdDTO(workgroupId));
    }

    /**
     * @deprecated use getDocType using the name
     */
    public RouteTemplateEntryDTO[] getRoute(String documentTypeName) throws WorkflowException {
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
    public DocumentContentDTO getDocumentContent(Long routeHeaderId) throws WorkflowException {
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
     * @param ignoreCurrentActionRequests determines if method should look only at simulation generated requests 
     *        or both simulation generated requests and requests that are currently active on the document
     * @return whether a document would product at least one action request under the specified criteria
     * @throws WorkflowException if an error occurs
     * @see WorkflowUtility#documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO, String[], boolean)
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaVO, String[] actionRequestedCodes, boolean ignoreCurrentActionRequests) throws WorkflowException {
        try {
            return getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, actionRequestedCodes, ignoreCurrentActionRequests);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * @deprecated use {@link #documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO, String[], boolean)} instead
     * 
     * This method assumes both existing and generated requests should be taken into account
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaVO, String[] actionRequestedCodes) throws WorkflowException {
        try {
            return getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, actionRequestedCodes);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    // DEPRECATED: as of Workflow 2.1

    /**
     * @deprecated use {@link #isLastApproverAtNode(Long, UserIdDTO, String)} instead
     */
    protected boolean isLastApproverInRouteLevel(Long routeHeaderId, UserIdDTO userId, Integer routeLevel) throws WorkflowException {
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