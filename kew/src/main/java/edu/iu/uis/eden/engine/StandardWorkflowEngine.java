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
package edu.iu.uis.eden.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.MDC;

import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.applicationconstants.ApplicationConstant;
import edu.iu.uis.eden.engine.node.Branch;
import edu.iu.uis.eden.engine.node.BranchState;
import edu.iu.uis.eden.engine.node.Process;
import edu.iu.uis.eden.engine.node.ProcessResult;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.engine.node.RouteNodeService;
import edu.iu.uis.eden.engine.transition.Transition;
import edu.iu.uis.eden.engine.transition.TransitionEngine;
import edu.iu.uis.eden.engine.transition.TransitionEngineFactory;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.RouteManagerException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.util.PerformanceLogger;
import edu.iu.uis.eden.util.Utilities;

/**
 * The standard and supported implementation of the WorkflowEngine.  Runs a processing loop against a given
 * Document, processing nodes on the document until the document is completed or a node halts the
 * processing.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StandardWorkflowEngine implements WorkflowEngine {

	protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

	protected RouteHelper helper = new RouteHelper();

	public void process(Long documentId, Long nodeInstanceId) throws Exception {
		if (documentId == null) {
			throw new IllegalArgumentException("Cannot process a null document id.");
		}
		MDC.put("docID", documentId);
		boolean success = true;
		RouteContext context = RouteContext.getCurrentRouteContext();
		try {
			LOG.debug("Aquiring lock on document " + documentId);
			KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
			LOG.debug("Aquired lock on document " + documentId);
			LOG.info("Processing document: " + documentId + " : " + nodeInstanceId);
			DocumentRouteHeaderValue document = getRouteHeaderService().getRouteHeader(documentId);
			if (!document.isRoutable()) {
				LOG.debug("Document not routable so returning with doing no action");
				return;
			}
			List nodeInstancesToProcess = new LinkedList();
			if (nodeInstanceId == null) {
				nodeInstancesToProcess.addAll(getRouteNodeService().getActiveNodeInstances(documentId));
			} else {
				RouteNodeInstance instanceNode = getRouteNodeService().findRouteNodeInstanceById(nodeInstanceId);
				if (instanceNode == null) {
					throw new IllegalArgumentException("Invalid node instance id: " + nodeInstanceId);
				}
				nodeInstancesToProcess.add(instanceNode);
			}

			context.setDocument(document);

			context.setEngineState(new EngineState());
			ProcessContext processContext = new ProcessContext(true, nodeInstancesToProcess);
			try {
				while (!nodeInstancesToProcess.isEmpty()) {
					context.setNodeInstance((RouteNodeInstance) nodeInstancesToProcess.remove(0));
					processContext = processNodeInstance(context, helper);
					if (processContext.isComplete() && !processContext.getNextNodeInstances().isEmpty()) {
						nodeInstancesToProcess.addAll(processContext.getNextNodeInstances());
					}
				}
				context.setDocument(nodePostProcess(context));
			} catch (Exception e) {
				success = false;
				// TODO throw a new 'RoutingException' which holds the
				// RoutingState
				throw new RouteManagerException(e, context);
			}
		} finally {
			LOG.info((success ? "Successfully processed" : "Failed to process") + " document: " + documentId + " : " + nodeInstanceId);
			RouteContext.clearCurrentRouteContext();
			MDC.remove("docID");
		}
	}

	protected ProcessContext processNodeInstance(RouteContext context, RouteHelper helper) throws Exception {
		RouteNodeInstance nodeInstance = context.getNodeInstance();
		LOG.debug("Processing node instance: " + nodeInstance.getRouteNode().getRouteNodeName());
		if (checkAssertions(context)) {
			// returning an empty context causes the outer loop to terminate
			return new ProcessContext();
		}
		TransitionEngine transitionEngine = TransitionEngineFactory.createTransitionEngine(nodeInstance);
		ProcessResult processResult = transitionEngine.isComplete(context);
		nodeInstance.setInitial(false);

		// if this nodeInstance already has next node instance we don't need to
		// go to the TE
		if (processResult.isComplete()) {
			LOG.debug("Routing node has completed: " + nodeInstance.getRouteNode().getRouteNodeName());

			context.getEngineState().getCompleteNodeInstances().add(nodeInstance.getRouteNodeInstanceId());
			List nextNodeCandidates = invokeTransition(context, context.getNodeInstance(), processResult, transitionEngine);

			// iterate over the next node candidates sending them through the
			// transition engine's transitionTo method
			// one at a time for a potential switch. Place the transition
			// engines result back in the 'actual' next node
			// list which we put in the next node before doing work.
			List<RouteNodeInstance> nodesToActivate = new ArrayList<RouteNodeInstance>();
			if (!nextNodeCandidates.isEmpty()) {
				nodeInstance.setNextNodeInstances(new ArrayList());
				for (Iterator nextIt = nextNodeCandidates.iterator(); nextIt.hasNext();) {
					RouteNodeInstance nextNodeInstance = (RouteNodeInstance) nextIt.next();
					transitionEngine = TransitionEngineFactory.createTransitionEngine(nextNodeInstance);
					RouteNodeInstance currentNextNodeInstance = nextNodeInstance;
					nextNodeInstance = transitionEngine.transitionTo(nextNodeInstance, context);
					// if the next node has changed, we need to remove our
					// current node as a next node of the original node
					if (!currentNextNodeInstance.equals(nextNodeInstance)) {
						currentNextNodeInstance.getPreviousNodeInstances().remove(nodeInstance);
					}
					// before adding next node instance, be sure that it's not
					// already linked via previous node instances
					// this is to prevent the engine from setting up references
					// on nodes that already reference each other.
					// the primary case being when we are walking over an
					// already constructed graph of nodes returned from a
					// dynamic node - probably a more sensible approach would be
					// to check for the existence of the link and moving on
					// if it's been established.
					nextNodeInstance.getPreviousNodeInstances().remove(nodeInstance);
					nodeInstance.addNextNodeInstance(nextNodeInstance);
					handleBackwardCompatibility(context, nextNodeInstance);
					// call the post processor
					notifyNodeChange(context, nextNodeInstance);
					nodesToActivate.add(nextNodeInstance);
					// TODO update document content on context?
				}
			}
			// deactive the current active node
			nodeInstance.setComplete(true);
			nodeInstance.setActive(false);
			// active the nodes we're transitioning into
			for (RouteNodeInstance nodeToActivate : nodesToActivate) {
				nodeToActivate.setActive(true);
			}
		} else {
		    nodeInstance.setComplete(false);
        }

		saveNode(context, nodeInstance);
		return new ProcessContext(nodeInstance.isComplete(), nodeInstance.getNextNodeInstances());
	}

	/**
	 * Checks various assertions regarding the processing of the current node.
	 * If this method returns true, then the node will not be processed.
	 *
	 * This method will throw an exception if it deems that the processing is in
	 * a illegal state.
	 */
	private boolean checkAssertions(RouteContext context) throws Exception {
		if (context.getNodeInstance().isComplete()) {
			LOG.debug("The node has already been completed: " + context.getNodeInstance().getRouteNode().getRouteNodeName());
			return true;
		}
		if (isRunawayProcessDetected(context.getEngineState())) {
//			 TODO more info in message
			throw new WorkflowException("Detected runaway process.");
		}
		return false;
	}

	/**
	 * Invokes the transition and returns the next node instances to transition
	 * to from the current node instance on the route context.
	 *
	 * This is a 3-step process:
	 *
	 * <pre>
	 *  1) If the node instance already has next nodes, return those,
	 *  2) otherwise, invoke the transition engine for the node, if the resulting node instances are not empty, return those,
	 *  3) lastly, if our node is in a process and no next nodes were returned from it's transition engine, invoke the
	 *     transition engine of the process node and return the resulting node instances.
	 * </pre>
	 */
	/*
	 * private List invokeTransition(RouteContext context, RouteNodeInstance
	 * nodeInstance, ProcessResult processResult, TransitionEngine
	 * transitionEngine) throws Exception { List nextNodeInstances =
	 * nodeInstance.getNextNodeInstances(); if (nextNodeInstances.isEmpty()) {
	 * Transition result = transitionEngine.transitionFrom(context,
	 * processResult); nextNodeInstances = result.getNextNodeInstances(); if
	 * (nextNodeInstances.isEmpty() && nodeInstance.isInProcess()) {
	 * transitionEngine =
	 * TransitionEngineFactory.createTransitionEngine(nodeInstance.getProcess());
	 * nextNodeInstances = invokeTransition(context, nodeInstance.getProcess(),
	 * processResult, transitionEngine); } } return nextNodeInstances; }
	 */

	private List invokeTransition(RouteContext context, RouteNodeInstance nodeInstance, ProcessResult processResult, TransitionEngine transitionEngine) throws Exception {
		List nextNodeInstances = nodeInstance.getNextNodeInstances();
		if (nextNodeInstances.isEmpty()) {
			Transition result = transitionEngine.transitionFrom(context, processResult);
			nextNodeInstances = result.getNextNodeInstances();
			if (nextNodeInstances.isEmpty() && nodeInstance.isInProcess()) {
				transitionEngine = TransitionEngineFactory.createTransitionEngine(nodeInstance.getProcess());
				context.setNodeInstance(nodeInstance);
				nextNodeInstances = invokeTransition(context, nodeInstance.getProcess(), processResult, transitionEngine);
			}
		}
		return nextNodeInstances;
	}

	/*
	 * private List invokeTransition(RouteContext context, RouteNodeInstance
	 * process, ProcessResult processResult) throws Exception {
	 * RouteNodeInstance nodeInstance = (context.getNodeInstance() ; List
	 * nextNodeInstances = nodeInstance.getNextNodeInstances(); if
	 * (nextNodeInstances.isEmpty()) { TransitionEngine transitionEngine =
	 * TransitionEngineFactory.createTransitionEngine(nodeInstance); Transition
	 * result = transitionEngine.transitionFrom(context, processResult);
	 * nextNodeInstances = result.getNextNodeInstances(); if
	 * (nextNodeInstances.isEmpty() && nodeInstance.isInProcess()) {
	 * transitionEngine =
	 * TransitionEngineFactory.createTransitionEngine(nodeInstance.getProcess());
	 * nextNodeInstances = invokeTransition(context, nodeInstance.getProcess(),
	 * processResult, transitionEngine); } } return nextNodeInstances; }
	 *
	 */private void notifyNodeChange(RouteContext context, RouteNodeInstance nextNodeInstance) {
		if (!context.isSimulation()) {
			RouteNodeInstance nodeInstance = context.getNodeInstance();
			DocumentRouteLevelChange event = new DocumentRouteLevelChange(context.getDocument().getRouteHeaderId(), context.getDocument().getAppDocId(), CompatUtils.getLevelForNode(context.getDocument().getDocumentType(), context.getNodeInstance()
					.getRouteNode().getRouteNodeName()), CompatUtils.getLevelForNode(context.getDocument().getDocumentType(), nextNodeInstance.getRouteNode().getRouteNodeName()), nodeInstance.getRouteNode().getRouteNodeName(), nextNodeInstance
					.getRouteNode().getRouteNodeName(), nodeInstance.getRouteNodeInstanceId(), nextNodeInstance.getRouteNodeInstanceId());
			context.setDocument(notifyPostProcessor(context.getDocument(), nodeInstance, event));
		}
	}

	private void handleBackwardCompatibility(RouteContext context, RouteNodeInstance nextNodeInstance) {
		context.getDocument().setDocRouteLevel(new Integer(context.getDocument().getDocRouteLevel().intValue() + 1)); // preserve
																														// route
																														// level
																														// concept
																														// if
																														// possible
		saveDocument(context);
	}

	private void saveDocument(RouteContext context) {
		if (!context.isSimulation()) {
			getRouteHeaderService().saveRouteHeader(context.getDocument());
		}
	}

	private void saveBranch(RouteContext context, Branch branch) {
		if (!context.isSimulation()) {
			KEWServiceLocator.getRouteNodeService().save(branch);
		}
	}

	protected void saveNode(RouteContext context, RouteNodeInstance nodeInstance) {
		if (!context.isSimulation()) {
			getRouteNodeService().save(nodeInstance);
		} else {
			// if we are in simulation mode, lets go ahead and assign some id
			// values to our beans
			for (Iterator iterator = nodeInstance.getNextNodeInstances().iterator(); iterator.hasNext();) {
				RouteNodeInstance routeNodeInstance = (RouteNodeInstance) iterator.next();
				if (routeNodeInstance.getRouteNodeInstanceId() == null) {
					routeNodeInstance.setRouteNodeInstanceId(context.getEngineState().getNextSimulationId());
				}
			}
			if (nodeInstance.getProcess() != null && nodeInstance.getProcess().getRouteNodeInstanceId() == null) {
				nodeInstance.getProcess().setRouteNodeInstanceId(context.getEngineState().getNextSimulationId());
			}
			if (nodeInstance.getBranch() != null && nodeInstance.getBranch().getBranchId() == null) {
				nodeInstance.getBranch().setBranchId(context.getEngineState().getNextSimulationId());
			}
		}
	}

	// TODO extract this into some sort of component which handles transitioning
	// document state
	protected DocumentRouteHeaderValue nodePostProcess(RouteContext context) throws InvalidActionTakenException {
		DocumentRouteHeaderValue document = context.getDocument();
		Collection activeNodes = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
		boolean moreNodes = false;
		for (Iterator iterator = activeNodes.iterator(); iterator.hasNext();) {
			RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
			moreNodes = moreNodes || !nodeInstance.isComplete();
		}
		List pendingRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
		boolean activeApproveRequests = false;
		boolean activeAckRequests = false;
		for (Iterator iterator = pendingRequests.iterator(); iterator.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iterator.next();
			activeApproveRequests = request.isApproveOrCompleteRequest() || activeApproveRequests;
			activeAckRequests = request.isAcknowledgeRequest() || activeAckRequests;
		}
		// TODO is the logic for going processed still going to be valid?
		if (!document.isProcessed() && (!moreNodes || !activeApproveRequests)) {
			LOG.debug("No more nodes for this document " + document.getRouteHeaderId());
			// TODO perhaps the policies could also be factored out?
			checkDefaultApprovalPolicy(document);
			LOG.debug("Marking document approved");
			// TODO factor out this magical post processing
			DocumentRouteStatusChange event = new DocumentRouteStatusChange(document.getRouteHeaderId(), document.getAppDocId(), document.getDocRouteStatus(), EdenConstants.ROUTE_HEADER_APPROVED_CD);
			document.markDocumentApproved();
			// saveDocument(context);
			notifyPostProcessor(context, event);

			LOG.debug("Marking document processed");
			event = new DocumentRouteStatusChange(document.getRouteHeaderId(), document.getAppDocId(), document.getDocRouteStatus(), EdenConstants.ROUTE_HEADER_PROCESSED_CD);
			document.markDocumentProcessed();
			// saveDocument(context);
			notifyPostProcessor(context, event);
		}

		// if document is processed and no pending action requests put the
		// document into the finalized state.
		if (document.isProcessed()) {
			DocumentRouteStatusChange event = new DocumentRouteStatusChange(document.getRouteHeaderId(), document.getAppDocId(), document.getDocRouteStatus(), EdenConstants.ROUTE_HEADER_FINAL_CD);
			List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
			if (actionRequests.isEmpty()) {
				document.markDocumentFinalized();
				// saveDocument(context);
				notifyPostProcessor(context, event);
			} else {
				boolean markFinalized = true;
				for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
					ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
					if (EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(actionRequest.getActionRequested())) {
						markFinalized = false;
					}
				}
				if (markFinalized) {
					document.markDocumentFinalized();
					// saveDocument(context);
					this.notifyPostProcessor(context, event);
				}
			}
		}
		saveDocument(context);
		return document;
	}

	/**
	 * Check the default approval policy for the document. If the default
	 * approval policy is no and no approval action requests have been created
	 * then throw an execption so that the document will get thrown into
	 * exception routing.
	 *
	 * @param rh
	 *            route header to be checked
	 * @param docType
	 *            docType of the routeHeader to be checked.
	 * @throws RouteManagerException
	 */
	private void checkDefaultApprovalPolicy(DocumentRouteHeaderValue document) throws RouteManagerException {
		if (!document.getDocumentType().getDefaultApprovePolicy().getPolicyValue().booleanValue()) {
			LOG.debug("Checking if any requests have been generated for the document");
			List requests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(document.getRouteHeaderId());
			boolean approved = false;
			for (Iterator iter = requests.iterator(); iter.hasNext();) {
				ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
				if (actionRequest.isApproveOrCompleteRequest() && actionRequest.isDone()) { // &&
																							// !(actionRequest.getRouteMethodName().equals(EdenConstants.ADHOC_ROUTE_MODULE_NAME)
																							// &&
																							// actionRequest.isReviewerUser()
																							// &&
																							// document.getInitiatorWorkflowId().equals(actionRequest.getWorkflowId())))
																							// {
					LOG.debug("Found at least one processed approve request so document can be approved");
					approved = true;
					break;
				}
			}
			if (!approved) {
				LOG.debug("Document requires at least one request and none are present");
				// TODO what route method name to pass to this?
				throw new RouteManagerException("Document should have generated at least one approval request.");
			}
		}
	}

	private DocumentRouteHeaderValue notifyPostProcessor(RouteContext context, DocumentRouteStatusChange event) {
		DocumentRouteHeaderValue document = context.getDocument();
		if (context.isSimulation()) {
			return document;
		}
		if (hasContactedPostProcessor(context, event)) {
			return document;
		}
		Long routeHeaderId = event.getRouteHeaderId();
		PerformanceLogger performanceLogger = new PerformanceLogger(routeHeaderId);
		ProcessDocReport processReport = null;
		PostProcessor postProc = null;
		try {
			postProc = document.getDocumentType().getPostProcessor();// SpringServiceLocator.getExtensionService().getPostProcessor(document.getDocumentType().getPostProcessorName());
		} catch (Exception e) {
			LOG.error("Error retrieving PostProcessor for document " + document.getRouteHeaderId(), e);
			throw new RouteManagerException("Error retrieving PostProcessor for document " + document.getRouteHeaderId(), e);
		}
		try {
			processReport = postProc.doRouteStatusChange(event);
		} catch (Exception e) {
			LOG.error("Error notifying post processor", e);
			throw new RouteManagerException(EdenConstants.POST_PROCESSOR_FAILURE_MESSAGE, e);
		} finally {
			performanceLogger.log("Time to notifyPostProcessor of event " + event.getDocumentEventCode() + ".");
		}

		if (!processReport.isSuccess()) {
			LOG.warn("PostProcessor failed to process document: " + processReport.getMessage());
			throw new RouteManagerException(EdenConstants.POST_PROCESSOR_FAILURE_MESSAGE + processReport.getMessage());
		}
		return document;
	}

	/**
	 * Returns true if the post processor has already been contacted about a
	 * PROCESSED or FINAL post processor change. If the post processor has not
	 * been contacted, this method will record on the document that it has been.
	 *
	 * This is because, in certain cases, a document could end up in exception
	 * routing after it has already gone PROCESSED or FINAL (i.e. on Mass Action
	 * processing) and we don't want to re-contact the post processor in these
	 * cases.
	 */
	private boolean hasContactedPostProcessor(RouteContext context, DocumentRouteStatusChange event) {
		// get the initial node instance, the root branch is where we will store
		// the state
		RouteNodeInstance initialInstance = (RouteNodeInstance) context.getDocument().getInitialRouteNodeInstance(0);
		Branch rootBranch = initialInstance.getBranch();
		String key = null;
		if (EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(event.getNewRouteStatus())) {
			key = EdenConstants.POST_PROCESSOR_PROCESSED_KEY;
		} else if (EdenConstants.ROUTE_HEADER_FINAL_CD.equals(event.getNewRouteStatus())) {
			key = EdenConstants.POST_PROCESSOR_FINAL_KEY;
		} else {
			return false;
		}
		BranchState branchState = rootBranch.getBranchState(key);
		if (branchState == null) {
			branchState = new BranchState(key, "true");
			rootBranch.addBranchState(branchState);
			saveBranch(context, rootBranch);
			return false;
		}
		return "true".equals(branchState.getValue());
	}

	/**
	 * TODO in some cases, someone may modify the route header in the post
	 * processor, if we don't save before and reload after we will get an
	 * optimistic lock exception, we need to work on a better solution for this!
	 * TODO get the routeContext in this method - it should be a better object
	 * than the nodeInstance
	 */
	private DocumentRouteHeaderValue notifyPostProcessor(DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance, DocumentRouteLevelChange event) {
		getRouteHeaderService().saveRouteHeader(document);
		ProcessDocReport report = null;
		try {
			report = document.getDocumentType().getPostProcessor().doRouteLevelChange(event);
		} catch (Exception e) {
			LOG.warn("Problems contacting PostProcessor", e);
			throw new RouteManagerException("Problems contacting PostProcessor:  " + e.getMessage());
		}
		document = getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
		if (!report.isSuccess()) {
			LOG.error("PostProcessor rejected route level change::" + report.getMessage(), report.getProcessException());
			throw new RouteManagerException("Route Level change failed in post processor::" + report.getMessage());
		}
		return document;
	}

	/**
	 * This method initializes the document by materializing and activating the
	 * first node instance on the document.
	 */
	public void initializeDocument(DocumentRouteHeaderValue document) {
		// we set up a local route context here just so that we are able to
		// utilize the saveNode method at the end of
		// this method. Incidentally, this was changed from pulling the existing
		// context out because it would override
		// the document in the route context in the case of a document being
		// initialized for reporting purposes.
		RouteContext context = new RouteContext();
		context.setDocument(document);
		if (context.getEngineState() == null) {
			context.setEngineState(new EngineState());
		}
		Process process = document.getDocumentType().getPrimaryProcess();
		if (process == null || process.getInitialRouteNode() == null) {
			throw new IllegalArgumentException("DocumentType '" + document.getDocumentType().getName() + "' has no primary process configured!");
		}
		RouteNodeInstance nodeInstance = helper.getNodeFactory().createRouteNodeInstance(document.getRouteHeaderId(), process.getInitialRouteNode());
		nodeInstance.setActive(true);
		helper.getNodeFactory().createBranch(EdenConstants.PRIMARY_BRANCH_NAME, null, nodeInstance);
		// TODO we may (probably) need only one of these initial node instances
		document.getInitialRouteNodeInstances().add(nodeInstance);
		saveNode(context, nodeInstance);
	}

	protected RouteNodeService getRouteNodeService() {
		return KEWServiceLocator.getRouteNodeService();
	}

	private boolean isRunawayProcessDetected(EngineState engineState) throws NumberFormatException {
	    String maxNodesConstant = Utilities.getApplicationConstant(EdenConstants.APP_CONST_MAX_NODES_BEFORE_RUNAWAY_PROCESS);
	    int maxNodes = (Utilities.isEmpty(maxNodesConstant)) ? 50 : Integer.valueOf(maxNodesConstant);
	    return engineState.getCompleteNodeInstances().size() > maxNodes;
	}

	protected RouteHeaderService getRouteHeaderService() {
		return KEWServiceLocator.getRouteHeaderService();
	}

}