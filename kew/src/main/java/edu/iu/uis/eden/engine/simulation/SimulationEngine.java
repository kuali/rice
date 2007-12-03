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
package edu.iu.uis.eden.engine.simulation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.MDC;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.ActivationContext;
import edu.iu.uis.eden.engine.EngineState;
import edu.iu.uis.eden.engine.ProcessContext;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.StandardWorkflowEngine;
import edu.iu.uis.eden.engine.node.NoOpNode;
import edu.iu.uis.eden.engine.node.NodeJotter;
import edu.iu.uis.eden.engine.node.Process;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.DocumentSimulatedRouteException;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.PerformanceLogger;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * A WorkflowEngine implementation which runs simulations.  This object is not thread-safe
 * and therefore a new instance needs to be instantiated on every use.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SimulationEngine extends StandardWorkflowEngine {

	private SimulationCriteria criteria;
    private SimulationResults results = new SimulationResults();
    private RouteHelper helper = new RouteHelper();

    public SimulationResults runSimulation(SimulationCriteria criteria) throws Exception {
        this.criteria = criteria;
        validateCriteria(criteria);
        process(criteria.getDocumentId(), null);
        return results;
    }

    public void process(Long documentId, Long nodeInstanceId) throws InvalidActionTakenException, EdenUserNotFoundException, DocumentSimulatedRouteException {
    	RouteContext context = RouteContext.createNewRouteContext();
    	try {
    		ActivationContext activationContext = new ActivationContext(ActivationContext.CONTEXT_IS_SIMULATION);
    		activationContext.setActionsToPerform(!criteria.getActionsToTake().isEmpty());
    		context.setActivationContext(activationContext);
    		context.setEngineState(new EngineState());
    		DocumentRouteHeaderValue document = createSimulationDocument(documentId, criteria, context);
    		if ( (criteria.isDocumentSimulation()) && ( (document.isProcessed()) || (document.isFinal()) ) ) {
    			results.setDocument(document);
    			return;
    		}
    		routeDocumentIfNecessary(document, criteria, context);
    		results.setDocument(document);
    		documentId = document.getRouteHeaderId();
    		MDC.put("docID", documentId);
    		PerformanceLogger perfLog = new PerformanceLogger(documentId);
    		try {
    			LOG.info("Processing document for Simulation: " + documentId);
    			List activeNodeInstances = getRouteNodeService().getActiveNodeInstances(document);
    			List nodeInstancesToProcess = determineNodeInstancesToProcess(activeNodeInstances, criteria.getDestinationNodeName());

    			context.setDocument(document);
    			// TODO set document content
    			context.setEngineState(new EngineState());
    			ProcessContext processContext = new ProcessContext(true, nodeInstancesToProcess);
    			while (! nodeInstancesToProcess.isEmpty()) {
    				RouteNodeInstance nodeInstance = (RouteNodeInstance)nodeInstancesToProcess.remove(0);
    				NodeJotter.jotNodeInstance(context.getDocument(), nodeInstance);
    				context.setNodeInstance(nodeInstance);
    				processContext = processNodeInstance(context, helper);
    				if (!hasReachedCompletion(processContext, context.getEngineState().getGeneratedRequests(), nodeInstance, criteria)) {
    					if (processContext.isComplete()) {
    						if (!processContext.getNextNodeInstances().isEmpty()) {
    							nodeInstancesToProcess.addAll(processContext.getNextNodeInstances());
    						}
    						context.getActivationContext().getSimulatedActionsTaken().addAll(processPotentialActionsTaken(context, document, nodeInstance, criteria));
    					}
    				} else {
    					context.getActivationContext().getSimulatedActionsTaken().addAll(processPotentialActionsTaken(context, document, nodeInstance, criteria));
    				}
    			}
    			List simulatedActionRequests = context.getEngineState().getGeneratedRequests();
    			Collections.sort(simulatedActionRequests, new Utilities().new RouteLogActionRequestSorter());
    			results.setSimulatedActionRequests(simulatedActionRequests);
    			results.setSimulatedActionsTaken(context.getActivationContext().getSimulatedActionsTaken());
            } catch (InvalidActionTakenException e) {
                throw e;
            } catch (EdenUserNotFoundException e) {
                throw e;
            } catch (Exception e) {
                String errorMsg = "Error running simulation for document " + ((criteria.isDocumentSimulation()) ? "id " + documentId.toString() : "type " + criteria.getDocumentTypeName());
                LOG.error(errorMsg,e);
                throw new DocumentSimulatedRouteException(errorMsg, e);
    		} finally {
    			perfLog.log("Time to run simulation.");
    			RouteContext.clearCurrentRouteContext();
    			MDC.remove("docID");
    		}
    	} finally {
    		RouteContext.releaseCurrentRouteContext();
    	}
    }

    /**
     * If there are multiple paths, we need to figure out which ones we need to follow for blanket approval.
     * This method will throw an exception if a node with the given name could not be located in the routing path.
     * This method is written in such a way that it should be impossible for there to be an infinate loop, even if
     * there is extensive looping in the node graph.
     */
    private List determineNodeInstancesToProcess(List activeNodeInstances, String nodeName) throws InvalidActionTakenException {
        if (Utilities.isEmpty(nodeName)) {
            return activeNodeInstances;
        }
        List nodeInstancesToProcess = new ArrayList();
        for (Iterator iterator = activeNodeInstances.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            if (nodeName.equals(nodeInstance.getName())) {
                // one of active node instances is node instance to stop at
                return new ArrayList();
            } else {
                if (isNodeNameInPath(nodeName, nodeInstance)) {
                    nodeInstancesToProcess.add(nodeInstance);
                }
            }
        }
        if (nodeInstancesToProcess.size() == 0) {
            throw new InvalidActionTakenException("Could not locate a node with the given name in the blanket approval path '" + nodeName + "'.  " +
                    "The document is probably already passed the specified node or does not contain the node.");
        }
        return nodeInstancesToProcess;
    }

    private boolean isNodeNameInPath(String nodeName, RouteNodeInstance nodeInstance) {
        boolean isInPath = false;
        for (Iterator iterator = nodeInstance.getRouteNode().getNextNodes().iterator(); iterator.hasNext();) {
            RouteNode nextNode = (RouteNode) iterator.next();
            isInPath = isInPath || isNodeNameInPath(nodeName, nextNode, new HashSet());
        }
        return isInPath;
    }

    private boolean isNodeNameInPath(String nodeName, RouteNode node, Set inspected) {
        boolean isInPath = !inspected.contains(node.getRouteNodeId()) && node.getRouteNodeName().equals(nodeName);
        inspected.add(node.getRouteNodeId());
        if (helper.isSubProcessNode(node)) {
            Process subProcess = node.getDocumentType().getNamedProcess(node.getRouteNodeName());
            RouteNode subNode = subProcess.getInitialRouteNode();
            isInPath = isInPath || isNodeNameInPath(nodeName, subNode, inspected);
        }
        for (Iterator iterator = node.getNextNodes().iterator(); iterator.hasNext();) {
            RouteNode nextNode = (RouteNode) iterator.next();
            isInPath = isInPath || isNodeNameInPath(nodeName, nextNode, inspected);
        }
        return isInPath;
    }

    private boolean hasReachedCompletion(ProcessContext processContext, List actionRequests, RouteNodeInstance nodeInstance, SimulationCriteria criteria) throws EdenUserNotFoundException {
        if (!criteria.getDestinationRecipients().isEmpty()) {
            for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
                ActionRequestValue request = (ActionRequestValue) iterator.next();
                for (Iterator userIt = criteria.getDestinationRecipients().iterator(); userIt.hasNext();) {
                    Recipient recipient = (Recipient) userIt.next();
                    if (request.isRecipientRoutedRequest(recipient)) {
                        return true;
                    }
                }
            }
        }
        String nodeName = criteria.getDestinationNodeName();
        return (Utilities.isEmpty(nodeName) && processContext.isComplete() && processContext.getNextNodeInstances().isEmpty())
            || nodeInstance.getRouteNode().getRouteNodeName().equals(nodeName);
    }

    private List processPotentialActionsTaken(RouteContext routeContext, DocumentRouteHeaderValue routeHeader, RouteNodeInstance justProcessedNode, SimulationCriteria criteria) throws EdenUserNotFoundException {
    	List actionsTaken = new ArrayList();
    	List requestsToCheck = new ArrayList();
    	requestsToCheck.addAll(routeContext.getEngineState().getGeneratedRequests());
        requestsToCheck.addAll(routeHeader.getActionRequests());
    	List pendingActionRequestValues = getCriteriaActionsToDoByNodeName(requestsToCheck, justProcessedNode.getName());
        List actionsToTakeForNode = generateActionsToTakeForNode(justProcessedNode.getName(), routeHeader, criteria, pendingActionRequestValues);

        for (Iterator iter = actionsToTakeForNode.iterator(); iter.hasNext();) {
            ActionTakenValue actionTaken = (ActionTakenValue) iter.next();
            KEWServiceLocator.getActionRequestService().deactivateRequests(actionTaken, pendingActionRequestValues, routeContext.getActivationContext());
            actionsTaken.add(actionTaken);
//            routeContext.getActivationContext().getSimulatedActionsTaken().add(actionTaken);
        }
    	return actionsTaken;
    }

    private List generateActionsToTakeForNode(String nodeName, DocumentRouteHeaderValue routeHeader, SimulationCriteria criteria, List pendingActionRequests) throws EdenUserNotFoundException {
        List actions = new ArrayList();
        if ( (criteria.getActionsToTake() != null) && (!criteria.getActionsToTake().isEmpty()) ) {
            for (Iterator iter = criteria.getActionsToTake().iterator(); iter.hasNext();) {
                SimulationActionToTake simAction = (SimulationActionToTake) iter.next();
                if (nodeName.equals(simAction.getNodeName())) {
                    actions.add(createDummyActionTaken(routeHeader, simAction.getUser(), simAction.getActionToPerform(), findDelegatorForActionRequests(pendingActionRequests)));
                }
            }
        }
        return actions;
    }

    private List getCriteriaActionsToDoByNodeName(List generatedRequests, String nodeName) {
    	List requests = new ArrayList();
        for (Iterator iterator = generatedRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue request = (ActionRequestValue) iterator.next();
            if ( (request.isPending()) && request.getNodeInstance() != null && nodeName.equals(request.getNodeInstance().getName())) {
            	requests.add(request);
            }
        }
        return requests;
    }

    /*private void simulateDocumentType(SimulationCriteria criteria) throws Exception {
    	DocumentType documentType = SpringServiceLocator.getDocumentTypeService().findByName(criteria.getDocumentTypeName());
    	if (documentType == null) {
    		throw new DocumentTypeNotFoundException("Could not locate document type for the given name '" + criteria.getDocumentTypeName() + "'");
    	}
    	if (criteria.getRuleTemplateNames().isEmpty()) {
    		throw new IllegalArgumentException("Must specify at least one rule template name to report against.");
    	}
    	List nodes = findRouteNodesForTemplate(documentType, criteria.getRuleTemplateNames());
    	RouteContext context = RouteContext.getCurrentRouteContext();
    	try {
    		context.setSimulation(true);
        	context.setEngineState(new EngineState());
        	context.setDocument(createSimulationDocument(criteria.getDocumentId(), criteria));
        	context.setDocumentContent(new StandardDocumentContent(criteria.getXmlContent(), context));
        	context.setDoNotSendApproveNotificationEmails(true);
        	results.setDocument(context.getDocument());
        	Branch simulationBranch = null;
    		for (Iterator iterator = nodes.iterator(); iterator.hasNext(); ) {
    			RouteNode node = (RouteNode) iterator.next();
    			context.setNodeInstance(createSimulationNodeInstance(context, node));
    			// for simulation, we'll have one branch
    			if (simulationBranch == null) {
    				simulationBranch = createSimulationBranch(context);
    			}
    			context.getNodeInstance().setBranch(simulationBranch);
    			RouteModule routeModule = SpringServiceLocator.getRouteModuleService().findRouteModule(node);
    			results.getSimulatedActionRequests().addAll(initializeActionRequests(context, routeModule.findActionRequests(context)));
    		}
    	} finally {
    		RouteContext.clearCurrentRouteContext();
    	}

    }*/

    private void validateCriteria(SimulationCriteria criteria) {
    	if (criteria.getDocumentId() == null && Utilities.isEmpty(criteria.getDocumentTypeName())) {
		throw new IllegalArgumentException("No document type name or route header id given, cannot simulate a document without a document type name or a route header id.");
    	}
    	if (criteria.getXmlContent() == null) {
    		criteria.setXmlContent("");
    	}
    }

    /**
     * Creates the document to run the simulation against by loading it from the database or creating a fake document for
     * simulation purposes depending on the passed simulation criteria.
     *
     * If the documentId is available, we load the document from the database, otherwise we create one based on the given
     * DocumentType and xml content.
     */
    private DocumentRouteHeaderValue createSimulationDocument(Long documentId, SimulationCriteria criteria, RouteContext context) {
    	DocumentRouteHeaderValue document = null;
    	if (criteria.isDocumentSimulation()) {
            document = getDocumentForSimulation(documentId);
            if (!Utilities.isEmpty(criteria.getXmlContent())) {
                document.setDocContent(criteria.getXmlContent());
            }

//    		document = getRouteHeaderService().getRouteHeader(documentId);
    	} else if (criteria.isDocumentTypeSimulation()) {
        	DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(criteria.getDocumentTypeName());
        	if (documentType == null) {
        		throw new IllegalArgumentException("Specified document type could not be found for name '"+criteria.getDocumentTypeName()+"'");
        	}
        	documentId = context.getEngineState().getNextSimulationId();
        	document = new DocumentRouteHeaderValue();
        	context.setDocument(document);
        	document.setRouteHeaderId(documentId);
        	document.setCreateDate(new Timestamp(System.currentTimeMillis()));
        	document.setDocContent(criteria.getXmlContent());
        	document.setDocRouteLevel(new Integer(0));
        	document.setDocumentTypeId(documentType.getDocumentTypeId());
    		document.setDocRouteStatus(EdenConstants.ROUTE_HEADER_INITIATED_CD);
    		initializeDocument(document);
    		installSimulationNodeInstances(context, criteria);
        }
        if (document == null) {
        	throw new IllegalArgumentException("Workflow simulation engine could not locate document with id "+documentId);
        }
		return document;
    }

    private DocumentRouteHeaderValue getDocumentForSimulation(Long documentId) {
        DocumentRouteHeaderValue document = getRouteHeaderService().getRouteHeader(documentId);
        return (DocumentRouteHeaderValue)deepCopy(document);
    }

    private Serializable deepCopy(Serializable src) {
        Serializable obj = null;
        if (src != null) {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                ByteArrayOutputStream serializer = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(serializer);
                oos.writeObject(src);

                ByteArrayInputStream deserializer = new ByteArrayInputStream(serializer.toByteArray());
                ois = new ObjectInputStream(deserializer);
                obj = (Serializable) ois.readObject();
            }
            catch (IOException e) {
                throw new RuntimeException("unable to complete deepCopy from src '" + src.toString() + "'", e);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("unable to complete deepCopy from src '" + src.toString() + "'", e);
            }
            finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                }
                catch (IOException e) {
                    // ignoring this IOException, since the streams are going to be abandoned now anyway
                }
            }
        }
        return obj;
    }

    private void routeDocumentIfNecessary(DocumentRouteHeaderValue document, SimulationCriteria criteria, RouteContext routeContext) throws InvalidActionTakenException {
    	if (criteria.getRoutingUser() != null) {
            ActionTakenValue action = createDummyActionTaken(document, criteria.getRoutingUser(), EdenConstants.ACTION_TAKEN_ROUTED_CD, null);
    		routeContext.getActivationContext().getSimulatedActionsTaken().add(action);
            simulateDocumentRoute(action, document, criteria.getRoutingUser(), routeContext);
    	}
    }

    /**
     * Looks at the rule templates and/or the startNodeName and creates the appropriate node instances to run simulation against.
     * After creating the node instances, it hooks them all together and installs a "terminal" simulation node to stop the simulation
     * node at the end of the simulation.
     */
    private void installSimulationNodeInstances(RouteContext context, SimulationCriteria criteria) {
    	DocumentRouteHeaderValue document = context.getDocument();
    	RouteNodeInstance initialNodeInstance = (RouteNodeInstance)document.getInitialRouteNodeInstance(0);
    	List simulationNodes = new ArrayList();
    	if (!criteria.getNodeNames().isEmpty()) {
    		for (Iterator iterator = criteria.getNodeNames().iterator(); iterator.hasNext(); ) {
				String nodeName = (String) iterator.next();
				LOG.debug("Installing simulation starting node '"+nodeName+"'");
	    		List nodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(document.getDocumentType(), true);
	    		boolean foundNode = false;
	    		for (Iterator iterator2 = nodes.iterator(); iterator2.hasNext(); ) {
					RouteNode node = (RouteNode) iterator2.next();
					if (node.getRouteNodeName().equals(nodeName)) {
						simulationNodes.add(node);
						foundNode = true;
						break;
					}
				}
	    		if (!foundNode) {
	    			throw new IllegalArgumentException("Could not find node on the document type for the given name '"+nodeName+"'");
	    		}
    		}
    	} else if (!criteria.getRuleTemplateNames().isEmpty()) {
    		List nodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(document.getDocumentType(), true);
    		for (Iterator iterator = criteria.getRuleTemplateNames().iterator(); iterator.hasNext(); ) {
				String ruleTemplateName = (String) iterator.next();
				boolean foundNode = false;
				for (Iterator iterator2 = nodes.iterator(); iterator2.hasNext(); ) {
					RouteNode node = (RouteNode) iterator2.next();
					String routeMethodName = node.getRouteMethodName();
					if (node.isFlexRM() && ruleTemplateName.equals(routeMethodName)) {
						simulationNodes.add(node);
						foundNode = true;
						break;
					}
				}
				if (!foundNode) {
	    			throw new IllegalArgumentException("Could not find node on the document type with the given rule template name '"+ruleTemplateName+"'");
	    		}
			}
    	} else {
    	    // can we assume we want to use all the nodes?
            List nodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(document.getDocumentType(), true);
            for (Iterator iterator2 = nodes.iterator(); iterator2.hasNext(); ) {
                RouteNode node = (RouteNode) iterator2.next();
                simulationNodes.add(node);
            }
    	}
    	// hook all of the simulation nodes together
    	RouteNodeInstance currentNodeInstance = initialNodeInstance;
    	for (Iterator iterator = simulationNodes.iterator(); iterator.hasNext(); ) {
			RouteNode simulationNode = (RouteNode) iterator.next();
			RouteNodeInstance nodeInstance = helper.getNodeFactory().createRouteNodeInstance(document.getRouteHeaderId(), simulationNode);
			nodeInstance.setBranch(initialNodeInstance.getBranch());
			currentNodeInstance.addNextNodeInstance(nodeInstance);
			saveNode(context, currentNodeInstance);
			currentNodeInstance = nodeInstance;
		}
    	installSimulationTerminationNode(context, document.getDocumentType(), currentNodeInstance);
    }

    private void installSimulationTerminationNode(RouteContext context, DocumentType documentType, RouteNodeInstance lastNodeInstance) {
    	RouteNode terminationNode = new RouteNode();
    	terminationNode.setDocumentType(documentType);
    	terminationNode.setDocumentTypeId(documentType.getDocumentTypeId());
    	terminationNode.setNodeType(NoOpNode.class.getName());
    	terminationNode.setRouteNodeName("SIMULATION_TERMINATION_NODE");
    	RouteNodeInstance terminationNodeInstance = helper.getNodeFactory().createRouteNodeInstance(lastNodeInstance.getDocumentId(), terminationNode);
    	terminationNodeInstance.setBranch(lastNodeInstance.getBranch());
    	lastNodeInstance.addNextNodeInstance(terminationNodeInstance);
    	saveNode(context, lastNodeInstance);
    }

    // below is fairly a copy of RouteDocumentAction... but actions have to be faked for now
    private void simulateDocumentRoute(ActionTakenValue actionTaken, DocumentRouteHeaderValue document, WorkflowUser user, RouteContext routeContext) throws InvalidActionTakenException {
    	ActionRequestService actionRequestService = KEWServiceLocator.getActionRequestService();
        // TODO delyea - deep copy below
        List actionRequests = new ArrayList();
        for (Iterator iter = actionRequestService.findPendingByDoc(document.getRouteHeaderId()).iterator(); iter.hasNext();) {
            ActionRequestValue arv = (ActionRequestValue) iter.next();
            actionRequests.add((ActionRequestValue)deepCopy(arv));
        }
//        actionRequests.addAll(actionRequestService.findPendingByDoc(document.getRouteHeaderId()));
        LOG.debug("Simulate Deactivating all pending action requests");
        // deactivate any requests for the user that routed the document.
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            // requests generated to the user who is routing the document should be deactivated
            if ( (user.getWorkflowId().equals(actionRequest.getWorkflowId())) && (actionRequest.isActive()) ) {
            	actionRequestService.deactivateRequest(actionTaken, actionRequest, routeContext.getActivationContext());
            }
            // requests generated by a save action should be deactivated
            else if (EdenConstants.SAVED_REQUEST_RESPONSIBILITY_ID.equals(actionRequest.getResponsibilityId())) {
            	actionRequestService.deactivateRequest(actionTaken, actionRequest, routeContext.getActivationContext());
            }
        }

//        String oldStatus = document.getDocRouteStatus();
        document.markDocumentEnroute();
//        String newStatus = document.getDocRouteStatus();
//        notifyStatusChange(newStatus, oldStatus);
//        getRouteHeaderService().saveRouteHeader(document);
    }

    private ActionTakenValue createDummyActionTaken(DocumentRouteHeaderValue routeHeader, WorkflowUser userToPerformAction, String actionToPerform, Recipient delegator) {
        ActionTakenValue val = new ActionTakenValue();
        val.setActionTaken(actionToPerform);
        if (EdenConstants.ACTION_TAKEN_ROUTED_CD.equals(actionToPerform)) {
            val.setActionTaken(EdenConstants.ACTION_TAKEN_COMPLETED_CD);
        }
		val.setAnnotation("");
		val.setDocVersion(routeHeader.getDocVersion());
		val.setRouteHeaderId(routeHeader.getRouteHeaderId());
		val.setWorkflowId(userToPerformAction.getWorkflowUserId().getWorkflowId());
		if (delegator instanceof WorkflowUser) {
			val.setDelegatorWorkflowId(((WorkflowUser) delegator).getWorkflowUserId().getWorkflowId());
		} else if (delegator instanceof Workgroup) {
			val.setDelegatorWorkgroupId(((Workgroup) delegator).getWorkflowGroupId().getGroupId());
		}
		val.setRouteHeader(routeHeader);
		val.setCurrentIndicator(Boolean.TRUE);
		return val;
    }

	/**
	 * Used by actions taken
	 *
	 * Returns the highest priority delegator in the list of action requests.
	 */
	private Recipient findDelegatorForActionRequests(List actionRequests) throws EdenUserNotFoundException {
		return KEWServiceLocator.getActionRequestService().findDelegator(actionRequests);
	}

    /**
     * Executes a "saveNode" for the simulation engine, this does not actually save the document, but rather
     * assigns it some simulation ids.
     *
     * Resolves KULRICE-368
     */
    @Override
    protected void saveNode(RouteContext context, RouteNodeInstance nodeInstance) {
		// we shold be in simulation mode here

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

    /*private Branch createSimulationBranch(RouteContext context) {
    	Branch branch = helper.getNodeFactory().createBranch("SIMULATION", null, context.getNodeInstance());
    	branch.setBranchId(context.getEngineState().getNextSimulationId());
    	return branch;
    }

    private RouteNodeInstance createSimulationNodeInstance(RouteContext context, RouteNode node) {
    	RouteNodeInstance nodeInstance = helper.getNodeFactory().createRouteNodeInstance(context.getDocument().getRouteHeaderId(), node);
    	nodeInstance.setRouteNodeInstanceId(context.getEngineState().getNextSimulationId());
    	return nodeInstance;
    }

    private List findRouteNodesForTemplate(DocumentType documentType, List ruleTemplateNames) {
    	List routeNodes = new ArrayList();
    	List flattenedRouteNodes = SpringServiceLocator.getRouteNodeService().getFlattenedNodes(documentType, true);
    	for (Iterator iterator = ruleTemplateNames.iterator(); iterator.hasNext(); ) {
			String ruleTemplateName = (String) iterator.next();
			boolean foundNode = false;
			for (Iterator iterator2 = flattenedRouteNodes.iterator(); iterator2.hasNext(); ) {
				RouteNode node = (RouteNode) iterator2.next();
				if (node.isFlexRM() && ruleTemplateName.equals(node.getRouteMethodName())) {
					routeNodes.add(node);
					foundNode = true;
					break;
				}
			}
			if (!foundNode) {
				throw new IllegalArgumentException("Could not locate route node with rule template '"+ruleTemplateName+"' on Document Type '"+documentType.getName());
			}
		}
    	return routeNodes;
    }

    private List initializeActionRequests(RouteContext context, List actionRequests) {
    	if (actionRequests == null) {
    		return new ArrayList();
    	}
    	for (Iterator iterator = actionRequests.iterator(); iterator.hasNext(); ) {
			ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
			SpringServiceLocator.getActionRequestService().initializeActionRequestGraph(actionRequest, context.getDocument(), context.getNodeInstance());
		}
    	return actionRequests;
    }*/

    /**
     * ByteArrayOutputStream implementation that doesnÕt synchronize methods and
     * doesnÕt copy the data on toByteArray().
     */
//    public class FastByteArrayOutputStream extends OutputStream {
//        /**
//         * Buffer and size
//         */
//        protected byte[] buf = null;
//
//        protected int size = 0;
//
//        /**
//         * Constructs a stream with buffer capacity size 5K
//         */
//        public FastByteArrayOutputStream() {
//            this(5 * 1024);
//        }
//
//        /**
//         * Constructs a stream with the given initial size
//         */
//        public FastByteArrayOutputStream(int initSize) {
//            this.size = 0;
//            this.buf = new byte[initSize];
//        }
//
//        /**
//         * Ensures that we have a large enough buffer for the given size.
//         */
//        private void verifyBufferSize(int sz) {
//            if (sz > buf.length) {
//                byte[] old = buf;
//                buf = new byte[Math.max(sz, 2 * buf.length)];
//                System.arraycopy(old, 0, buf, 0, old.length);
//                old = null;
//            }
//        }
//
//        public int getSize() {
//            return size;
//        }
//
//        /**
//         * Returns the byte array containing the written data. Note that this
//         * array will almost always be larger than the amount of data actually
//         * written.
//         */
//        public byte[] getByteArray() {
//            return buf;
//        }
//
//        public final void write(byte b[]) {
//            verifyBufferSize(size + b.length);
//            System.arraycopy(b, 0, buf, size, b.length);
//            size += b.length;
//        }
//
//        public final void write(byte b[], int off, int len) {
//            verifyBufferSize(size + len);
//            System.arraycopy(b, off, buf, size, len);
//            size += len;
//        }
//
//        public final void write(int b) {
//            verifyBufferSize(size + 1);
//            buf[size++] = (byte) b;
//        }
//
//        public void reset() {
//            size = 0;
//        }
//
//        /**
//         * Returns a ByteArrayInputStream for reading back the written data
//         */
//        public InputStream getInputStream() {
//            return new FastByteArrayInputStream(buf, size);
//        }
//
//    }

}
