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
package edu.iu.uis.eden.routeheader;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.DefaultCustomActionListAttribute;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.docsearch.SearchableAttributeValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.CompatUtils;
import edu.iu.uis.eden.engine.node.Branch;
import edu.iu.uis.eden.engine.node.BranchState;
import edu.iu.uis.eden.engine.node.KeyValuePair;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.mail.CustomEmailAttributeImpl;
import edu.iu.uis.eden.notes.CustomNoteAttribute;
import edu.iu.uis.eden.notes.CustomNoteAttributeImpl;
import edu.iu.uis.eden.notes.Note;
import edu.iu.uis.eden.plugin.attributes.CustomActionListAttribute;
import edu.iu.uis.eden.plugin.attributes.CustomEmailAttribute;
import edu.iu.uis.eden.server.BeanConverter;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.util.Utilities;

/**
 * A document within KEW.  A document effectively represents a process that moves through
 * the workflow engine.  It is created from a particular {@link DocumentType} and follows
 * the route path defined by that DocumentType.
 *
 * <p>During a document's lifecycle it progresses through a series of statuses, starting
 * with INITIATED and moving to one of the terminal states (such as FINAL, CANCELED, etc).
 * The list of status on a document are defined in the {@link EdenConstants} class and
 * include the constants starting with "ROUTE_HEADER_" and ending with "_CD".
 *
 * <p>Associated with the document is the document content.  The document content is XML
 * which represents the content of that document.  This XML content is typically used
 * to make routing decisions for the document.
 *
 * <p>A document has associated with it a set of {@link ActionRequestValue} object and
 * {@link ActionTakenValue} objects.  Action Requests represent requests for user
 * action (such as Approve, Acknowledge, etc).  Action Takens represent action that
 * users have performed on the document, such as approvals or cancelling of the document.
 *
 * <p>The instantiated route path of a document is defined by it's graph of
 * {@link RouteNodeInstance} objects.  The path starts at the initial node of the document
 * and progresses from there following the next nodes of each node instance.  The current
 * active nodes on the document are defined by the "active" flag on the node instance
 * where are not marked as "complete".
 *
 * @see DocumentType
 * @see ActionRequestValue
 * @see ActionItem
 * @see ActionTakenValue
 * @see RouteNodeInstance
 * @see EdenConstants
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentRouteHeaderValue implements WorkflowPersistable {
    private static final long serialVersionUID = -4700736340527913220L;
    private static final Logger LOG = Logger.getLogger(DocumentRouteHeaderValue.class);

    public static final String CURRENT_ROUTE_NODE_NAME_DELIMITER = ", ";

    private java.lang.Long documentTypeId;
    private java.lang.String docRouteStatus;
    private java.lang.Integer docRouteLevel;
    private java.sql.Timestamp statusModDate;
    private java.sql.Timestamp createDate;
    private java.sql.Timestamp approvedDate;
    private java.sql.Timestamp finalizedDate;
    private DocumentRouteHeaderValueContent documentContent;
    private java.lang.String docTitle;
    private java.lang.String appDocId;
    private java.lang.String overrideInd;
    private java.lang.Integer docVersion = new Integer(EdenConstants.DOCUMENT_VERSION_NODAL);
    private java.lang.Integer jrfVerNbr;
    private java.lang.String initiatorWorkflowId;
    private java.lang.String routedByUserWorkflowId;
    private java.sql.Timestamp routeStatusDate;
    private java.sql.Timestamp routeLevelDate;
    private java.lang.Long routeHeaderId;
    private String lockCode;

    private List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();

//      apache lazy list commented out due to not being serializable
//    	ListUtils.lazyList(new ArrayList(),
//            new Factory() {
//		public Object create() {
//			return new ActionRequestFactory().createBlankActionRequest();
//		}
//		});
    private List<ActionTakenValue> actionsTaken = new ArrayList<ActionTakenValue>();
    private List<ActionItem> actionItems = new ArrayList<ActionItem>();
    private List<Note> notes = new ArrayList<Note>();
    private List<SearchableAttributeValue> searchableAttributeValues = new ArrayList<SearchableAttributeValue>();
    private Collection queueItems = new ArrayList();
    private boolean routingReport = false;

    private static final boolean FINAL_STATE = true;
    protected static final HashMap<String,String> legalActions;
    protected static final HashMap<String,String> stateTransitionMap;

    /* New Workflow 2.1 Field */
    private List<RouteNodeInstance> initialRouteNodeInstances = new ArrayList<RouteNodeInstance>();

    static {
        stateTransitionMap = new HashMap<String,String>();
        stateTransitionMap.put(EdenConstants.ROUTE_HEADER_INITIATED_CD, EdenConstants.ROUTE_HEADER_SAVED_CD + EdenConstants.ROUTE_HEADER_ENROUTE_CD + EdenConstants.ROUTE_HEADER_CANCEL_CD);

        stateTransitionMap.put(EdenConstants.ROUTE_HEADER_SAVED_CD, EdenConstants.ROUTE_HEADER_SAVED_CD + EdenConstants.ROUTE_HEADER_ENROUTE_CD + EdenConstants.ROUTE_HEADER_CANCEL_CD + EdenConstants.ROUTE_HEADER_APPROVED_CD);

        stateTransitionMap.put(EdenConstants.ROUTE_HEADER_ENROUTE_CD, EdenConstants.ROUTE_HEADER_DISAPPROVED_CD +
                EdenConstants.ROUTE_HEADER_CANCEL_CD + EdenConstants.ROUTE_HEADER_APPROVED_CD + EdenConstants.ROUTE_HEADER_EXCEPTION_CD + EdenConstants.ROUTE_HEADER_SAVED_CD);
        stateTransitionMap.put(EdenConstants.ROUTE_HEADER_DISAPPROVED_CD, "");
        stateTransitionMap.put(EdenConstants.ROUTE_HEADER_CANCEL_CD, "");
        stateTransitionMap.put(EdenConstants.ROUTE_HEADER_FINAL_CD, "");
        stateTransitionMap.put(EdenConstants.ROUTE_HEADER_EXCEPTION_CD, EdenConstants.ROUTE_HEADER_EXCEPTION_CD + EdenConstants.ROUTE_HEADER_ENROUTE_CD + EdenConstants.ROUTE_HEADER_CANCEL_CD + EdenConstants.ROUTE_HEADER_APPROVED_CD + EdenConstants.ROUTE_HEADER_DISAPPROVED_CD + EdenConstants.ROUTE_HEADER_SAVED_CD);
        stateTransitionMap.put(EdenConstants.ROUTE_HEADER_APPROVED_CD, EdenConstants.ROUTE_HEADER_APPROVED_CD + EdenConstants.ROUTE_HEADER_PROCESSED_CD + EdenConstants.ROUTE_HEADER_EXCEPTION_CD);
        stateTransitionMap.put(EdenConstants.ROUTE_HEADER_PROCESSED_CD, EdenConstants.ROUTE_HEADER_FINAL_CD + EdenConstants.ROUTE_HEADER_PROCESSED_CD);

        legalActions = new HashMap<String,String>();
        legalActions.put(EdenConstants.ROUTE_HEADER_INITIATED_CD, EdenConstants.ACTION_TAKEN_SAVED_CD + EdenConstants.ACTION_TAKEN_COMPLETED_CD + EdenConstants.ACTION_TAKEN_ROUTED_CD + EdenConstants.ACTION_TAKEN_CANCELED_CD + EdenConstants.ACTION_TAKEN_ADHOC_CD + EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD + EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD + EdenConstants.ACTION_TAKEN_MOVE_CD);
        legalActions.put(EdenConstants.ROUTE_HEADER_SAVED_CD, EdenConstants.ACTION_TAKEN_SAVED_CD + EdenConstants.ACTION_TAKEN_COMPLETED_CD + EdenConstants.ACTION_TAKEN_ROUTED_CD + EdenConstants.ACTION_TAKEN_APPROVED_CD + EdenConstants.ACTION_TAKEN_CANCELED_CD + EdenConstants.ACTION_TAKEN_ADHOC_CD + EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD + EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD + EdenConstants.ACTION_TAKEN_MOVE_CD);
        /* ACTION_TAKEN_ROUTED_CD not included in enroute state
         * ACTION_TAKEN_SAVED_CD removed as of version 2.4
         */
        legalActions.put(EdenConstants.ROUTE_HEADER_ENROUTE_CD, /*EdenConstants.ACTION_TAKEN_SAVED_CD + EdenConstants.ACTION_TAKEN_ROUTED_CD + */EdenConstants.ACTION_TAKEN_APPROVED_CD + EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD + EdenConstants.ACTION_TAKEN_FYI_CD + EdenConstants.ACTION_TAKEN_ADHOC_CD + EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD + EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD + EdenConstants.ACTION_TAKEN_CANCELED_CD + EdenConstants.ACTION_TAKEN_COMPLETED_CD + EdenConstants.ACTION_TAKEN_DENIED_CD + EdenConstants.ACTION_TAKEN_SU_APPROVED_CD + EdenConstants.ACTION_TAKEN_SU_CANCELED_CD + EdenConstants.ACTION_TAKEN_SU_DISAPPROVED_CD + EdenConstants.ACTION_TAKEN_SU_ROUTE_LEVEL_APPROVED_CD + EdenConstants.ACTION_TAKEN_RETURNED_TO_PREVIOUS_CD + EdenConstants.ACTION_TAKEN_SU_RETURNED_TO_PREVIOUS_CD + EdenConstants.ACTION_TAKEN_MOVE_CD);
        /* ACTION_TAKEN_ROUTED_CD not included in exception state
         * ACTION_TAKEN_SAVED_CD removed as of version 2.4.2
         */
        legalActions.put(EdenConstants.ROUTE_HEADER_EXCEPTION_CD, /*EdenConstants.ACTION_TAKEN_SAVED_CD + */EdenConstants.ACTION_TAKEN_FYI_CD + EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD + EdenConstants.ACTION_TAKEN_ADHOC_CD + EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD + EdenConstants.ACTION_TAKEN_APPROVED_CD + EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD + EdenConstants.ACTION_TAKEN_CANCELED_CD + EdenConstants.ACTION_TAKEN_COMPLETED_CD + EdenConstants.ACTION_TAKEN_DENIED_CD + EdenConstants.ACTION_TAKEN_SU_APPROVED_CD + EdenConstants.ACTION_TAKEN_SU_CANCELED_CD + EdenConstants.ACTION_TAKEN_SU_DISAPPROVED_CD + EdenConstants.ACTION_TAKEN_SU_ROUTE_LEVEL_APPROVED_CD + EdenConstants.ACTION_TAKEN_RETURNED_TO_PREVIOUS_CD + EdenConstants.ACTION_TAKEN_SU_RETURNED_TO_PREVIOUS_CD + EdenConstants.ACTION_TAKEN_MOVE_CD);
        legalActions.put(EdenConstants.ROUTE_HEADER_FINAL_CD, EdenConstants.ACTION_TAKEN_FYI_CD + EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD + EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD);
        legalActions.put(EdenConstants.ROUTE_HEADER_CANCEL_CD, EdenConstants.ACTION_TAKEN_FYI_CD + EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD + EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD);
        legalActions.put(EdenConstants.ROUTE_HEADER_DISAPPROVED_CD, EdenConstants.ACTION_TAKEN_FYI_CD + EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD + EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD);
        legalActions.put(EdenConstants.ROUTE_HEADER_APPROVED_CD, EdenConstants.ACTION_TAKEN_FYI_CD + EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD + EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD);
        legalActions.put(EdenConstants.ROUTE_HEADER_PROCESSED_CD, EdenConstants.ACTION_TAKEN_FYI_CD + EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD + EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD);
    }

    public DocumentRouteHeaderValue() {
    }

    public WorkflowUser getInitiatorUser() throws EdenUserNotFoundException {
    	// if we are running a simulation, there will be no initiator
    	if (getInitiatorWorkflowId() == null) {
    		return null;
    	}
        return getUser(getInitiatorWorkflowId());
    }

    public WorkflowUser getRoutedByUser() throws EdenUserNotFoundException {
        // if we are running a simulation, there will be no initiator
        if (getRoutedByUserWorkflowId() == null) {
            return null;
        }
        return getUser(getRoutedByUserWorkflowId());
    }

    private WorkflowUser getUser(java.lang.String workflowId) throws EdenUserNotFoundException {
        UserService userSrv = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
        WorkflowUser user = null;
        user = userSrv.getWorkflowUser(new WorkflowUserId(workflowId));
        if (user == null ) {
            LOG.fatal("we are toasted...user" + workflowId + " rhid " + this.routeHeaderId);
            return null;
        } else {
            return user;
        }
    }

    public String getDocRouteStatusLabel() {
        return CodeTranslator.getRouteStatusLabel(getDocRouteStatus());
    }

    public String getCurrentRouteLevelName() {
        String name = "Not Found";
        // TODO the isRouteLevelDocument junk can be ripped out
    	if(routingReport){
    		name = "Routing Report";
    	} else if (CompatUtils.isRouteLevelDocument(this)) {
            int routeLevelInt = getDocRouteLevel().intValue();
            LOG.info("Getting current route level name for a Route level document: " + routeLevelInt+CURRENT_ROUTE_NODE_NAME_DELIMITER+routeHeaderId);
            List routeLevelNodes = CompatUtils.getRouteLevelCompatibleNodeList(getDocumentType());
            LOG.info("Route level compatible node list has " + routeLevelNodes.size() + " nodes");
            if (routeLevelInt < routeLevelNodes.size()) {
                name = ((RouteNode)routeLevelNodes.get(routeLevelInt)).getRouteNodeName();
            }
        } else {
            Collection nodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(getRouteHeaderId());
            name = "";
            if (nodeInstances.isEmpty()) {
                nodeInstances = KEWServiceLocator.getRouteNodeService().getTerminalNodeInstances(getRouteHeaderId());
            }
            for (Iterator iterator = nodeInstances.iterator(); iterator.hasNext();) {
                RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
                name += nodeInstance.getRouteNode().getRouteNodeName() + (iterator.hasNext() ? CURRENT_ROUTE_NODE_NAME_DELIMITER : "");
            }
        }
        return name;
    }

    public String getRouteStatusLabel() {
        return CodeTranslator.getRouteStatusLabel(getDocRouteStatus());
    }

    public Collection getQueueItems() {
        return queueItems;
    }

    public void setQueueItems(Collection queueItems) {
        this.queueItems = queueItems;
    }

    public List<ActionItem> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<ActionItem> actionItems) {
        this.actionItems = actionItems;
    }

    public List<ActionTakenValue> getActionsTaken() {
        return actionsTaken;
    }

    public void setActionsTaken(List<ActionTakenValue> actionsTaken) {
        this.actionsTaken = actionsTaken;
    }

    public List<ActionRequestValue> getActionRequests() {
        return actionRequests;
    }

    public void setActionRequests(List<ActionRequestValue> actionRequests) {
        this.actionRequests = actionRequests;
    }

    public DocumentType getDocumentType() {
    	return KEWServiceLocator.getDocumentTypeService().findById(getDocumentTypeId());
    }

    public java.lang.String getAppDocId() {
        return appDocId;
    }

    public void setAppDocId(java.lang.String appDocId) {
        this.appDocId = appDocId;
    }

    public java.sql.Timestamp getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(java.sql.Timestamp approvedDate) {
        this.approvedDate = approvedDate;
    }

    public java.sql.Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(java.sql.Timestamp createDate) {
        this.createDate = createDate;
    }

    public java.lang.String getDocContent() {
    	return getDocumentContent().getDocumentContent();
    }

    public void setDocContent(java.lang.String docContent) {
    	DocumentRouteHeaderValueContent content = getDocumentContent();
    	content.setDocumentContent(docContent);
    }

    public java.lang.Integer getDocRouteLevel() {
        return docRouteLevel;
    }

    public void setDocRouteLevel(java.lang.Integer docRouteLevel) {
        this.docRouteLevel = docRouteLevel;
    }

    public java.lang.String getDocRouteStatus() {
        return docRouteStatus;
    }

    public void setDocRouteStatus(java.lang.String docRouteStatus) {
        this.docRouteStatus = docRouteStatus;
    }

    public java.lang.String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(java.lang.String docTitle) {
        this.docTitle = docTitle;
    }

    public java.lang.Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(java.lang.Long docTypeId) {
        this.documentTypeId = docTypeId;
    }

    public java.lang.Integer getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(java.lang.Integer docVersion) {
        this.docVersion = docVersion;
    }

    public java.sql.Timestamp getFinalizedDate() {
        return finalizedDate;
    }

    public void setFinalizedDate(java.sql.Timestamp finalizedDate) {
        this.finalizedDate = finalizedDate;
    }

    public java.lang.String getInitiatorWorkflowId() {
        return initiatorWorkflowId;
    }

    public void setInitiatorWorkflowId(java.lang.String initiatorWorkflowId) {
        this.initiatorWorkflowId = initiatorWorkflowId;
    }

    public java.lang.String getRoutedByUserWorkflowId() {
        if ( (isEnroute()) && (StringUtils.isBlank(routedByUserWorkflowId)) ) {
            return initiatorWorkflowId;
        }
        return routedByUserWorkflowId;
    }

    public void setRoutedByUserWorkflowId(java.lang.String routedByUserWorkflowId) {
        this.routedByUserWorkflowId = routedByUserWorkflowId;
    }

    public java.lang.Integer getJrfVerNbr() {
        return jrfVerNbr;
    }

    public void setJrfVerNbr(java.lang.Integer jrfVerNbr) {
        this.jrfVerNbr = jrfVerNbr;
    }

    public java.lang.String getOverrideInd() {
        return overrideInd;
    }

    public void setOverrideInd(java.lang.String overrideInd) {
        this.overrideInd = overrideInd;
    }

    public java.lang.Long getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setRouteHeaderId(java.lang.Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public java.sql.Timestamp getRouteLevelDate() {
        return routeLevelDate;
    }

    public void setRouteLevelDate(java.sql.Timestamp routeLevelDate) {
        this.routeLevelDate = routeLevelDate;
    }

    public java.sql.Timestamp getRouteStatusDate() {
        return routeStatusDate;
    }

    public void setRouteStatusDate(java.sql.Timestamp routeStatusDate) {
        this.routeStatusDate = routeStatusDate;
    }

    public java.sql.Timestamp getStatusModDate() {
        return statusModDate;
    }

    public void setStatusModDate(java.sql.Timestamp statusModDate) {
        this.statusModDate = statusModDate;
    }

    public Object copy(boolean preserveKeys) {
        throw new UnsupportedOperationException("The copy method is deprecated and unimplemented!");
    }

    /**
     * @return True if the document is in the state of Initiated
     */
    public boolean isStateInitiated() {
        return EdenConstants.ROUTE_HEADER_INITIATED_CD.equals(docRouteStatus);
    }

    /**
     * @return True if the document is in the state of Saved
     */
    public boolean isStateSaved() {
        return EdenConstants.ROUTE_HEADER_SAVED_CD.equals(docRouteStatus);
    }

    /**
     * @return true if the document has ever been inte enroute state
     */
    public boolean isRouted() {
        return !(isStateInitiated() || isStateSaved());
    }

    public boolean isInException() {
        return EdenConstants.ROUTE_HEADER_EXCEPTION_CD.equals(docRouteStatus);
    }

    public boolean isDisaproved() {
        return EdenConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(docRouteStatus);
    }

    public boolean isCanceled() {
        return EdenConstants.ROUTE_HEADER_CANCEL_CD.equals(docRouteStatus);
    }

    public boolean isFinal() {
        return EdenConstants.ROUTE_HEADER_FINAL_CD.equals(docRouteStatus);
    }

    public boolean isEnroute() {
    	return EdenConstants.ROUTE_HEADER_ENROUTE_CD.equals(docRouteStatus);
    }

    /**
     * @return true if the document is in the processed state
     */
    public boolean isProcessed() {
        return EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(docRouteStatus);
    }

    /**
     * @return true if the document is in the approved state
     */
    public boolean isApproved() {
        return EdenConstants.ROUTE_HEADER_APPROVED_CD.equals(docRouteStatus);
    }

    public boolean isRoutable() {
        return EdenConstants.ROUTE_HEADER_ENROUTE_CD.equals(docRouteStatus) ||
        		//EdenConstants.ROUTE_HEADER_EXCEPTION_CD.equals(docRouteStatus) ||
        		EdenConstants.ROUTE_HEADER_SAVED_CD.equals(docRouteStatus) ||
        		EdenConstants.ROUTE_HEADER_APPROVED_CD.equals(docRouteStatus) ||
        		EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(docRouteStatus);
    }

    /**
     * Return true if the given action code is valid for this document's current state.
     *
     * @param actionCd
     *            The action code to be tested.
     * @return True if the action code is valid for the document's status.
     */
    public boolean isValidActionToTake(String actionCd) {
        String actions = (String) legalActions.get(docRouteStatus);
        if (actions.indexOf(actionCd) == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidStatusChange(String newStatus) {
    	return ((String) stateTransitionMap.get(getDocRouteStatus())).indexOf(newStatus) >= 0;
    }

    public void setRouteStatus(String newStatus, boolean finalState) throws InvalidActionTakenException {
        if (newStatus != getDocRouteStatus()) {
            // only modify the status mod date if the status actually changed
            setRouteStatusDate(new Timestamp(System.currentTimeMillis()));
        }
        if (((String) stateTransitionMap.get(getDocRouteStatus())).indexOf(newStatus) >= 0) {
            LOG.debug("changing status");
            setDocRouteStatus(newStatus);
        } else {
            LOG.debug("unable to change status");
            throw new InvalidActionTakenException("Document status " + CodeTranslator.getRouteStatusLabel(getDocRouteStatus()) + " cannot transition to status " + CodeTranslator.getRouteStatusLabel(newStatus));
        }
        setStatusModDate(new Timestamp(System.currentTimeMillis()));
        if (finalState) {
            LOG.debug("setting final timeStamp");
            setFinalizedDate(new Timestamp(System.currentTimeMillis()));
        }
    }

    public boolean isLocked() {
        return getLockCode() != EdenConstants.DOC_UNLOCKED;
    }

    public void unlock() {
        setLockCode(EdenConstants.DOC_UNLOCKED);
    }

    public String getLockCode() {
        return lockCode;
    }

    public void setLockCode(String lockReason) {
        this.lockCode = lockReason;
    }

    /**
     * Mark the document as being processed.
     *
     * @throws ResourceUnavailableException
     * @throws InvalidActionTakenException
     */
    public void markDocumentProcessed() throws InvalidActionTakenException {
        LOG.debug(this + " marked processed");
        setRouteStatus(EdenConstants.ROUTE_HEADER_PROCESSED_CD, !FINAL_STATE);
    }

    /**
     * Mark document cancled.
     *
     * @throws ResourceUnavailableException
     * @throws InvalidActionTakenException
     */
    public void markDocumentCanceled() throws InvalidActionTakenException {
        LOG.debug(this + " marked canceled");
        setRouteStatus(EdenConstants.ROUTE_HEADER_CANCEL_CD, FINAL_STATE);
    }

    /**
     * Mark document disapproved
     *
     * @throws ResourceUnavailableException
     * @throws InvalidActionTakenException
     */
    public void markDocumentDisapproved() throws InvalidActionTakenException {
        LOG.debug(this + " marked disapproved");
        setRouteStatus(EdenConstants.ROUTE_HEADER_DISAPPROVED_CD, FINAL_STATE);
    }

    /**
     * Mark document saved
     *
     * @throws ResourceUnavailableException
     * @throws InvalidActionTakenException
     */
    public void markDocumentSaved() throws InvalidActionTakenException {
        LOG.debug(this + " marked saved");
        setRouteStatus(EdenConstants.ROUTE_HEADER_SAVED_CD, !FINAL_STATE);
    }

    /**
     * Mark the document as being approved.
     *
     * @throws ResourceUnavailableException
     * @throws InvalidActionTakenException
     */
    public void markDocumentApproved() throws InvalidActionTakenException {
        LOG.debug(this + " marked approved");
        setApprovedDate(new Timestamp(System.currentTimeMillis()));
        setRouteStatus(EdenConstants.ROUTE_HEADER_APPROVED_CD, !FINAL_STATE);
    }

    /**
     * Mark the document as being in the exception state.
     *
     * @throws ResourceUnavailableException
     * @throws InvalidActionTakenException
     */
    public void markDocumentInException() throws InvalidActionTakenException {
        LOG.debug(this + " marked in exception");
        setRouteStatus(EdenConstants.ROUTE_HEADER_EXCEPTION_CD, !FINAL_STATE);
    }

    /**
     * Mark the document as being actively routed.
     *
     * @throws ResourceUnavailableException
     * @throws InvalidActionTakenException
     */
    public void markDocumentEnroute() throws InvalidActionTakenException {
        LOG.debug(this + " marked enroute");
        setRouteStatus(EdenConstants.ROUTE_HEADER_ENROUTE_CD, !FINAL_STATE);
    }

    /**
     * Mark document finalized.
     *
     * @throws ResourceUnavailableException
     * @throws InvalidActionTakenException
     */
    public void markDocumentFinalized() throws InvalidActionTakenException {
        LOG.debug(this + " marked finalized");
        setRouteStatus(EdenConstants.ROUTE_HEADER_FINAL_CD, FINAL_STATE);
    }

    /**
     * This method takes data from a VO and sets it on this route header
     * @param routeHeaderVO
     * @throws WorkflowException
     */
    public void setRouteHeaderData(RouteHeaderVO routeHeaderVO) throws WorkflowException {
//    	String updatedDocumentContent = BeanConverter.buildUpdatedDocumentContent(routeHeaderVO);
//    	// updatedDocumentContent will be null if the content has not changed, only update if its changed
//    	if (updatedDocumentContent != null) {
//    		setDocContent(updatedDocumentContent);
//    	}
        if (!Utilities.equals(getDocTitle(), routeHeaderVO.getDocTitle())) {
        	KEWServiceLocator.getActionListService().updateActionItemsForTitleChange(getRouteHeaderId(), routeHeaderVO.getDocTitle());
        }
        setDocTitle(routeHeaderVO.getDocTitle());
        setAppDocId(routeHeaderVO.getAppDocId());
        setOverrideInd(routeHeaderVO.getOverrideInd());
        setStatusModDate(new Timestamp(System.currentTimeMillis()));

        /* set the variables from the routeHeaderVO */
        List variables = routeHeaderVO.getVariables();
        Iterator it = variables.iterator();
        while (it.hasNext()) {
            KeyValuePair kvp = (KeyValuePair) it.next();
            setVariable(kvp.getKey(), kvp.getValue());
        }
    }

    /**
     * Convenience method that returns the branch of the first (and presumably only?) initial node
     * @return the branch of the first (and presumably only?) initial node
     */
    private Branch getRootBranch() {
        // FIXME: assuming there is always a single initial route node instance
        return ((RouteNodeInstance) getInitialRouteNodeInstance(0)).getBranch();
    }

    /**
     * Looks up a variable (embodied in a "BranchState" key/value pair) in the
     * branch state table.
     */
    private BranchState findVariable(String name) {
        Branch rootBranch = getRootBranch();
        List branchState = rootBranch.getBranchState();
        Iterator it = branchState.iterator();
        while (it.hasNext()) {
            BranchState state = (BranchState) it.next();
            if (Utilities.equals(state.getKey(), BranchState.VARIABLE_PREFIX + name)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Gets a variable
     * @param name variable name
     * @return variable value, or null if variable is not defined
     */
    public String getVariable(String name) {
        BranchState state = findVariable(name);
        if (state == null) {
            LOG.debug("Variable not found: '" + name + "'");
            return null;
        }
        return state.getValue();
    }

    /**
     * Sets a variable
     * @param name variable name
     * @param value variable value, or null if variable should be removed
     */
    public void setVariable(String name, String value) {
        BranchState state = findVariable(name);
        Branch rootBranch = getRootBranch();
        List branchState = rootBranch.getBranchState();
        if (state == null) {
            if (value == null) {
                LOG.debug("set non existent variable '" + name + "' to null value");
                return;
            }
            LOG.debug("Adding branch state: '" + name + "'='" + value + "'");
            state = new BranchState();
            state.setBranch(rootBranch);
            state.setKey(BranchState.VARIABLE_PREFIX + name);
            state.setValue(value);
            rootBranch.addBranchState(state);
        } else {
            if (value == null) {
                LOG.debug("Removing value: " + state.getKey() + "=" + state.getValue());
                branchState.remove(state);
            } else {
                LOG.debug("Setting value of variable '" + name + "' to '" + value + "'");
                state.setValue(value);
            }
        }
    }

    public CustomActionListAttribute getCustomActionListAttribute() throws WorkflowException, EdenUserNotFoundException {
        CustomActionListAttribute customActionListAttribute = null;
        if (this.getDocumentType() != null) {
        	customActionListAttribute = this.getDocumentType().getCustomActionListAttribute();
        	if (customActionListAttribute != null) {
        		return customActionListAttribute;
        	}
        }
        customActionListAttribute = new DefaultCustomActionListAttribute();
        return customActionListAttribute;
    }

    public CustomEmailAttribute getCustomEmailAttribute() throws WorkflowException, EdenUserNotFoundException {
        CustomEmailAttribute customEmailAttribute = null;
        try {
            if (this.getDocumentType() != null) {
                customEmailAttribute = this.getDocumentType().getCustomEmailAttribute();
                if (customEmailAttribute != null) {
                    customEmailAttribute.setRouteHeaderVO(BeanConverter.convertRouteHeader(this, null));
                    return customEmailAttribute;
                }
            }
        } catch (Exception e) {
            LOG.debug("Error in retrieving custom email attribute", e);
        }
        customEmailAttribute = new CustomEmailAttributeImpl();
        customEmailAttribute.setRouteHeaderVO(BeanConverter.convertRouteHeader(this, null));
        return customEmailAttribute;
    }

    public CustomNoteAttribute getCustomNoteAttribute() throws WorkflowException, EdenUserNotFoundException {
        CustomNoteAttribute customNoteAttribute = null;
        try {
            if (this.getDocumentType() != null) {
                customNoteAttribute = this.getDocumentType().getCustomNoteAttribute();
                if (customNoteAttribute != null) {
                    customNoteAttribute.setRouteHeaderVO(BeanConverter.convertRouteHeader(this, null));
                    return customNoteAttribute;
                }
            }
        } catch (Exception e) {
            LOG.debug("Error in retrieving custom note attribute", e);
        }
        customNoteAttribute = new CustomNoteAttributeImpl();
        customNoteAttribute.setRouteHeaderVO(BeanConverter.convertRouteHeader(this, null));
        return customNoteAttribute;
    }

    public ActionRequestValue getDocActionRequest(int index) {
        while (actionRequests.size() <= index) {
        	ActionRequestValue actionRequest = new ActionRequestFactory(this).createBlankActionRequest();
        	actionRequest.setNodeInstance(new RouteNodeInstance());
            actionRequests.add(actionRequest);
        }
        return (ActionRequestValue) actionRequests.get(index);
    }

    public ActionTakenValue getDocActionTaken(int index) {
        while (actionsTaken.size() <= index) {
            actionsTaken.add(new ActionTakenValue());
        }
        return (ActionTakenValue) actionsTaken.get(index);
    }

    public ActionItem getDocActionItem(int index) {
        while (actionItems.size() <= index) {
            actionItems.add(new ActionItem());
        }
        return (ActionItem) actionItems.get(index);
    }

    public RouteNodeInstance getInitialRouteNodeInstance(int index) {
    	while (initialRouteNodeInstances.size() <= index) {
    		initialRouteNodeInstances.add(new RouteNodeInstance());
    	}
    	return (RouteNodeInstance) initialRouteNodeInstances.get(index);
    }

	/**
	 * @param searchableAttributeValues The searchableAttributeValues to set.
	 */
	public void setSearchableAttributeValues(List<SearchableAttributeValue> searchableAttributeValues) {
		this.searchableAttributeValues = searchableAttributeValues;
	}

	/**
	 * @return Returns the searchableAttributeValues.
	 */
	public List<SearchableAttributeValue> getSearchableAttributeValues() {
		return searchableAttributeValues;
	}

	public boolean isRoutingReport() {
		return routingReport;
	}

	public void setRoutingReport(boolean routingReport) {
		this.routingReport = routingReport;
	}

    public List<RouteNodeInstance> getInitialRouteNodeInstances() {
        return initialRouteNodeInstances;
    }

    public void setInitialRouteNodeInstances(List<RouteNodeInstance> initialRouteNodeInstances) {
        this.initialRouteNodeInstances = initialRouteNodeInstances;
    }

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public DocumentRouteHeaderValueContent getDocumentContent() {
		if (documentContent == null) {
			documentContent = KEWServiceLocator.getRouteHeaderService().getContent(getRouteHeaderId());
		}
		return documentContent;
	}

	public void setDocumentContent(DocumentRouteHeaderValueContent documentContent) {
		this.documentContent = documentContent;
	}

}