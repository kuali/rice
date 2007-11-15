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
package edu.iu.uis.eden.engine.node.hierarchyrouting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.node.Branch;
import edu.iu.uis.eden.engine.node.DynamicNode;
import edu.iu.uis.eden.engine.node.DynamicResult;
import edu.iu.uis.eden.engine.node.NoOpNode;
import edu.iu.uis.eden.engine.node.NodeState;
import edu.iu.uis.eden.engine.node.Process;
import edu.iu.uis.eden.engine.node.RequestsNode;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.engine.node.SimpleJoinNode;
import edu.iu.uis.eden.engine.node.SimpleSplitNode;
import edu.iu.uis.eden.engine.node.hierarchyrouting.HierarchyProvider.Stop;
import edu.iu.uis.eden.engine.transition.SplitTransitionEngine;
import edu.iu.uis.eden.routeheader.StandardDocumentContent;

/**
 * Generic hierarchy routing node
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class HierarchyRoutingNode implements DynamicNode {
    protected final Logger LOG = Logger.getLogger(getClass());
        
    protected static final String SPLIT_PROCESS_NAME = "Hierarchy Split";
    protected static final String JOIN_PROCESS_NAME = "Hierarchy Join";
    protected static final String REQUEST_PROCESS_NAME = "Hierarchy Request";
    protected static final String NO_STOP_NAME = "No stop";

    // constants for the process state in tracking stops we've traveled
    private static final String VISITED_STOPS = "visited_stops";
    private static final String V_STOPS_DEL = ",";
    
    private static final String INITIAL_SPLIT_NODE_MARKER = "InitialSplitNode";

    protected abstract HierarchyProvider getHierarchyProvider(RouteContext context);

    public DynamicResult transitioningInto(RouteContext context, RouteNodeInstance dynamicNodeInstance, RouteHelper helper) throws Exception {

        HierarchyProvider provider = getHierarchyProvider(context);
        DocumentType documentType = setUpDocumentType(provider, context.getDocument().getDocumentType(), dynamicNodeInstance);
        RouteNode splitNode = documentType.getNamedProcess(SPLIT_PROCESS_NAME).getInitialRouteNode();

        //set up initial SplitNodeInstance
        RouteNodeInstance splitNodeInstance = helper.getNodeFactory().createRouteNodeInstance(context.getDocument().getRouteHeaderId(), splitNode);
        splitNodeInstance.setBranch(dynamicNodeInstance.getBranch());
        markAsInitialSplitNode(splitNodeInstance);
        
        int i = 0;
        List<Stop> stops = provider.getLeafStops(new StandardDocumentContent(context.getDocument().getDocContent()));
        if (stops.isEmpty()) {
            // if we have no stops, then just return a no-op node with IU-UNIV attached, this will terminate the process
            RouteNode noStopNode = documentType.getNamedProcess(NO_STOP_NAME).getInitialRouteNode();
            RouteNodeInstance noChartOrgInstance = helper.getNodeFactory().createRouteNodeInstance(context.getDocument().getRouteHeaderId(), noStopNode);
            noChartOrgInstance.setBranch(dynamicNodeInstance.getBranch());
            
            provider.setRequestNodeInstanceState(noChartOrgInstance, null);
            //noChartOrgInstance.addNodeState(new NodeState(CHART_NODE_STATE_KEY, "IU"));
            //noChartOrgInstance.addNodeState(new NodeState(ORG_NODE_STATE_KEY, "UNIV"));
            return new DynamicResult(true, noChartOrgInstance);
        }
        for (Stop stop: stops) {
            RouteNode requestNode = getStopRequestNode(stop, documentType);
            createInitialRequestNodeInstance(provider, stop, splitNodeInstance, dynamicNodeInstance, requestNode);
        }

        return new DynamicResult(false, splitNodeInstance);
    }

    public DynamicResult transitioningOutOf(RouteContext context, RouteHelper helper) throws Exception {
        HierarchyProvider provider = getHierarchyProvider(context);
        
        RouteNodeInstance processInstance = context.getNodeInstance().getProcess();
        RouteNodeInstance curStopNode = context.getNodeInstance();
        Map<Long, RouteNodeInstance> stopRequestNodeMap = new HashMap<Long, RouteNodeInstance>();
        findStopRequestNodes(provider, context, stopRequestNodeMap);//SpringServiceLocator.getRouteNodeService().findProcessNodeInstances(processInstance);
        
        /*
        String chartCd = chartOrgNode.getNodeState(CHART_NODE_STATE_KEY).getValue();
        String orgCd = chartOrgNode.getNodeState(ORG_NODE_STATE_KEY).getValue();
        if (chartCd.equals("IU") && orgCd.equals("UNIV")) {
            return new DynamicResult(true, null);
        }
        Organization org = IUServiceLocator.getFISDataService().findOrganization(chartCd, orgCd);
        */
        
        Stop stop = provider.getStopAtRouteNode(curStopNode);

        if (provider.isRoot(stop)) {
            return new DynamicResult(true, null);
        }        
        
        //create a join node for the next node and attach any sibling orgs to the join
        //if no join node is necessary i.e. no siblings create a requests node
        InnerTransitionResult transition = canTransitionFrom(provider, stop, stopRequestNodeMap.values(), helper);
        DynamicResult result = null;
        if (transition.isCanTransition()) {
            DocumentType documentType = context.getDocument().getDocumentType();
            //          make a simple requests node
            RouteNodeInstance requestNode = createNextStopRequestNodeInstance(provider, context, stop, processInstance, helper);

            if (transition.getSiblings().isEmpty()) {
                result = new DynamicResult(false, requestNode);
            } else {

                if (true) {
                throw new RuntimeException("SDFDFS");
                }
                //create a join to transition us to the next org
                RouteNode joinPrototype = documentType.getNamedProcess(JOIN_PROCESS_NAME).getInitialRouteNode();
                RouteNodeInstance joinNode = helper.getNodeFactory().createRouteNodeInstance(context.getDocument().getRouteHeaderId(), joinPrototype);
                String branchName = "Branch " + getHierarchyProvider(context).getStopIdentifier(stop);
                Branch joinBranch = helper.getNodeFactory().createBranch(branchName, null, joinNode);
                joinNode.setBranch(joinBranch);

                for (RouteNodeInstance sibling: transition.getSiblings()) {
                    helper.getJoinEngine().addExpectedJoiner(joinNode, sibling.getBranch());
                }

                //set the next org after the join
                joinNode.addNextNodeInstance(requestNode);

                result = new DynamicResult(false, joinNode);
            }

        } else {
            result = new DynamicResult(false, null);
        }
        result.getNextNodeInstances().addAll(getNewlyAddedOrgRouteInstances(provider, context, helper));
        return result;
    }
    
    private void findStopRequestNodes(HierarchyProvider provider, RouteContext context, Map<Long, RouteNodeInstance> stopRequestNodes) {
        List nodeInstances = KEWServiceLocator.getRouteNodeService().getFlattenedNodeInstances(context.getDocument(), true);
        for (Iterator iterator = nodeInstances.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            /*if (nodeInstance.getNodeState(CHART_NODE_STATE_KEY) != null && nodeInstance.getNodeState(ORG_NODE_STATE_KEY) != null) {
                chartOrgNodes.put(nodeInstance.getRouteNodeInstanceId(), nodeInstance);
            }*/
            if (provider.requestNodeHasStopState(nodeInstance)) {
                stopRequestNodes.put(nodeInstance.getRouteNodeInstanceId(), nodeInstance);
            }
        }
    }
    
    /**
     * Constructs a Map of RouteNodeInstances which represent the ChartOrgNodes attached the the currently running
     * dynamic node process.  The initial invocation of this method should pass an empty map which will be used as the
     * accumulator for the return value;
     * 
     * We can't go to the db for this because when in simulation these RouteNodeInstances will be in memory
     */
    /*private void findChartOrgNodes(RouteHelper helper, RouteNodeInstance chartOrgNode, RouteNodeInstance process, Map chartOrgNodes) {
        if (chartOrgNode.getProcess() != null &&
                process.getRouteNodeInstanceId().equals(chartOrgNode.getProcess().getRouteNodeInstanceId()) &&
                !chartOrgNodes.containsKey(chartOrgNode.getRouteNodeInstanceId())) {
            if (chartOrgNode.getNodeState(CHART_NODE_STATE_KEY) != null && chartOrgNode.getNodeState(ORG_NODE_STATE_KEY) != null) {
                chartOrgNodes.put(chartOrgNode.getRouteNodeInstanceId(), chartOrgNode);
            }
            // this will only be used in the case where the dynamic node returns a sub process and that sub process is given the organization state
            // that would normally be applied to the node, this will usually happen as a result of overriding the getChartOrgNode method below.
            // ERA example: ERADOC -> DynamicChartOrgNode -> ChartOrgRoutingNode -> SubProcess -> (RSP -> RSP Dispatch), so in this case
            // we are getting the ChartOrgRoutingNode as the passed in process, but the RSP or RSP Dispatch node is the chartOrgNode and the
            // chart+org state is on the SubProcess.
            else if (chartOrgNode.getProcess().getNodeState(CHART_NODE_STATE_KEY) != null && chartOrgNode.getProcess().getNodeState(ORG_NODE_STATE_KEY) != null) {
                chartOrgNodes.put(chartOrgNode.getProcess().getRouteNodeInstanceId(), chartOrgNode.getProcess());
            }
            // We are attempting to get a flattened view of the entire graph of nodes created by
            // this dynamic node and since our initial node of entry into this method is a leaf,
            // we'll look backward and forward.
            // The containsKey check above will prevent us from recursing infinitely.
            chartOrgNode = findNavigationalNodeInstance(chartOrgNode);
            findChartOrgNodes(helper, chartOrgNode.getNextNodeInstances(), process, chartOrgNodes);
            findChartOrgNodes(helper, chartOrgNode.getPreviousNodeInstances(), process, chartOrgNodes);
        }
    }
    
    private void findChartOrgNodes(RouteHelper helper, List nodeInstances, RouteNodeInstance process, Map chartOrgNodes) {
        if (nodeInstances != null) {
            for (Iterator iterator = nodeInstances.iterator(); iterator.hasNext();) {
                RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
                findChartOrgNodes(helper, nodeInstance, process, chartOrgNodes);
            }
        }
    }*/
    
    /**
     * If the given node actually represents a sub process or a dynamic node, this method will return the initial node within
     * that process.  We need to be able to do this because a sub process node instance has no previous or next nodes so we need
     * to be able to have a way to hook into the navigational structure of the document route to be able to walk backwards and
     * forwards along the route path. 
     */
    //private RouteNodeInstance findNavigationalNodeInstance(DocumentRouteHeaderValue document, RouteNodeInstance processNodeInstance) {
    //    List flattenedNodeInstances = SpringServiceLocator.getRouteNodeService().getF
        
    //}

    private RouteNodeInstance createNextStopRequestNodeInstance(HierarchyProvider provider, RouteContext context, Stop stop, RouteNodeInstance processInstance, RouteHelper helper) {
        /*Organization futureOrg = new Organization();
        futureOrg.setFinCoaCd(stop.getReportsToChart());
        futureOrg.setOrgCd(stop.getReportsToOrg());*/
        Stop futureStop = provider.getParent(stop);
        RouteNode requestsPrototype = getStopRequestNode(futureStop, context.getDocument().getDocumentType());
        RouteNodeInstance requestNode = helper.getNodeFactory().createRouteNodeInstance(context.getDocument().getRouteHeaderId(), requestsPrototype);
        requestNode.setBranch(processInstance.getBranch());
        getHierarchyProvider(context).setRequestNodeInstanceState(requestNode, futureStop);
        /*requestNode.addNodeState(new NodeState(CHART_NODE_STATE_KEY, futureOrg.getFinCoaCd()));
        requestNode.addNodeState(new NodeState(ORG_NODE_STATE_KEY, futureOrg.getOrgCd()));
        */
        addStopToProcessState(provider, processInstance, futureStop);
        return requestNode;
    }


    /**
     * @param nodeInstance the node instance
     * @param stop the stop
     * @return whether the nodeInstance contains stop state that matches the specified stop
     */
    protected boolean requestNodeIsAtStop(RouteNodeInstance nodeInstance, Stop stop) {
       return false; // TODO implement 
    }

    /**
     * i can only transition from this if all the nodes left are completed immediate siblings
     * 
     * @param org
     * @param requestNodes
     * @return List of Nodes that are siblings to the org passed in
     */
    private InnerTransitionResult canTransitionFrom(HierarchyProvider provider, Stop currentStop, Collection<RouteNodeInstance> requestNodes, RouteHelper helper) {

        InnerTransitionResult result = new InnerTransitionResult();
        result.setCanTransition(false);

        for (RouteNodeInstance requestNode: requestNodes) {
            /*NodeState chartState = chartOrgNode.getNodeState(CHART_NODE_STATE_KEY);
            NodeState orgState = chartOrgNode.getNodeState(ORG_NODE_STATE_KEY);
            // continue if this node has no chart/org state
            if (chartState == null || orgState == null) {
                continue;
            }*/
            if (!provider.requestNodeHasStopState(requestNode)) {
                continue;
            }
            
            
            /*String chartCd = chartState.getValue();
            String orgCd = orgState.getValue();
            if (currentOrg.getFinCoaCd().equals(chartCd) && currentOrg.getOrgCd().equals(orgCd)) {
                continue;
            }*/
            
            Stop requestNodeStop = provider.getStopAtRouteNode(requestNode);
            LOG.error("Request node: " + requestNode.getRouteNodeInstanceId() + " has stop " + requestNodeStop.toString());
            if (requestNodeStop != null && provider.equals(currentStop, requestNodeStop)) {
                continue;
            }

            
            /*Organization nodeOrg = IUServiceLocator.getFISDataService().findOrganization(chartCd, orgCd);
            Organization parent = IUServiceLocator.getFISDataService().findOrganization(currentOrg.getReportsToChart(), currentOrg.getReportsToOrg());
            */
            Stop nodeOrg = provider.getStopAtRouteNode(requestNode);
            Stop parent = provider.getParent(currentStop);
            if (provider.isRoot(parent) || hasAsParent(provider, parent, nodeOrg)) {
                if (requestNode.isActive()) {
                    return result;
                }
                //it's done and has our parent as a parent is it a direct sibling? if not let the other branch(s) catch up
                //if (parent.getFinCoaCd().equals(currentStop.getFinCoaCd()) && parent.getOrgCd().equals(currentStop.getOrgCd())) {
                if (false) {
                  result.getSiblings().add(requestNode);
                }
            }
        }
        result.setCanTransition(true);
        return result;
    }

    
    private static class InnerTransitionResult {
        private boolean canTransition;
        private List<RouteNodeInstance> siblings = new ArrayList<RouteNodeInstance>();

        public boolean isCanTransition() {
            return canTransition;
        }

        public void setCanTransition(boolean canTransition) {
            this.canTransition = canTransition;
        }

        public List<RouteNodeInstance> getSiblings() {
            return siblings;
        }

        public void setSiblings(List<RouteNodeInstance> siblings) {
            this.siblings = siblings;
        }
    }

    private static void markAsInitialSplitNode(RouteNodeInstance splitNode) {
        splitNode.addNodeState(new NodeState(INITIAL_SPLIT_NODE_MARKER, INITIAL_SPLIT_NODE_MARKER));
    }

    /**
     * @param routeNodeInstance
     * @return
     */
    private static boolean isInitialSplitNode(RouteNodeInstance routeNodeInstance) {
        return routeNodeInstance.getNodeState(INITIAL_SPLIT_NODE_MARKER) != null;
    }

    /**
     * Adds the org to the process state 
     * @param processInstance
     * @param org
     */
    private void addStopToProcessState(HierarchyProvider provider, RouteNodeInstance processInstance, Stop stop) {
        String stopStateName = provider.getStopIdentifier(stop);
        NodeState visitedStopsState = processInstance.getNodeState(VISITED_STOPS);
        if (visitedStopsState == null) {
            processInstance.addNodeState(new NodeState(VISITED_STOPS, stopStateName + V_STOPS_DEL));
        } else if (! getVisitedStopsList(processInstance).contains(stopStateName)) {
            visitedStopsState.setValue(visitedStopsState.getValue() + stopStateName + V_STOPS_DEL);
        }
    }
    
    /**
     * @param process
     * @return List of stop strings on the process state
     */
    private static List<String> getVisitedStopsList(RouteNodeInstance process) {
        return Arrays.asList(process.getNodeState(VISITED_STOPS).getValue().split(V_STOPS_DEL));
    }
    
    /**
     * Determines if the org has been routed to or will be.
     * @param stop
     * @param process
     * @return boolean if this is an org we would not hit in routing
     */
    private boolean isNewStop(HierarchyProvider provider, Stop stop, RouteNodeInstance process) {
        
        String orgStateName = provider.getStopIdentifier(stop);
        List<String> visitedOrgs = getVisitedStopsList(process);
        boolean isInVisitedList = visitedOrgs.contains(orgStateName);
        if (isInVisitedList) {
            return false;
        }
        boolean willEventualRouteThere = false;
        //determine if we will eventually route to this chart anyway
        for (Iterator<String> iter = visitedOrgs.iterator(); iter.hasNext() && willEventualRouteThere == false; ) {
            String visitedStopStateName = iter.next();
            //String[] orgStrings = ((String) iter.next()).split(CHART_ORG_DEL);
            //Organization visitedOrg = IUServiceLocator.getFISDataService().findOrganization(orgStrings[0], orgStrings[1]);
            Stop visitedStop = provider.getStopByIdentifier(visitedStopStateName);
            willEventualRouteThere = hasAsParent(provider, stop, visitedStop) || willEventualRouteThere;
        }
        return ! willEventualRouteThere;
    }

    /**
     * Creates a Org Request RouteNodeInstance that is a child of the passed in split.  This is used to create the initial 
     * request RouteNodeInstances off the begining split.
     * @param org
     * @param splitNodeInstance
     * @param processInstance
     * @param requestsNode
     * @return Request RouteNodeInstance bound to the passed in split as a 'nextNodeInstance'
     */
    private RouteNodeInstance createInitialRequestNodeInstance(HierarchyProvider provider, Stop stop, RouteNodeInstance splitNodeInstance, RouteNodeInstance processInstance, RouteNode requestsNode) {
        String branchName = "Branch " + provider.getStopIdentifier(stop);
        RouteNodeInstance orgRequestInstance = SplitTransitionEngine.createSplitChild(branchName, requestsNode, splitNodeInstance);
        splitNodeInstance.addNextNodeInstance(orgRequestInstance);
        provider.setRequestNodeInstanceState(orgRequestInstance, stop);
        //orgRequestInstance.addNodeState(new NodeState(CHART_NODE_STATE_KEY, org.getFinCoaCd()));
        //orgRequestInstance.addNodeState(new NodeState(ORG_NODE_STATE_KEY, org.getOrgCd()));
        addStopToProcessState(provider, processInstance, stop);
        return orgRequestInstance;
    }
    
    /**
     * Check the xml and determine there are any orgs declared that we will not travel through on our current trajectory.
     * @param context
     * @param helper
     * @return RouteNodeInstances for any orgs we would not have traveled through that are now in the xml.
     * @throws Exception
     */
    private List<RouteNodeInstance> getNewlyAddedOrgRouteInstances(HierarchyProvider provider, RouteContext context, RouteHelper helper) throws Exception {
        RouteNodeInstance processInstance = context.getNodeInstance().getProcess();
        RouteNodeInstance chartOrgNode = context.getNodeInstance();
        //check for new stops in the xml
        List<Stop> stops = provider.getLeafStops(new StandardDocumentContent(context.getDocument().getDocContent()));
        List<RouteNodeInstance> newStopsRoutingTo = new ArrayList<RouteNodeInstance>();
        for (Stop stop: stops) {
            if (isNewStop(provider, stop, processInstance)) {
                //the idea here is to always use the object referenced by the engine so simulation can occur
                List<RouteNodeInstance> processNodes = chartOrgNode.getPreviousNodeInstances();
                for (RouteNodeInstance splitNodeInstance: processNodes) {
                    if (isInitialSplitNode(splitNodeInstance)) {                        
                        RouteNode requestsNode = getStopRequestNode(stop, context.getDocument().getDocumentType());
                        RouteNodeInstance newOrgRequestNode = createInitialRequestNodeInstance(provider, stop, splitNodeInstance, processInstance, requestsNode);
                        newStopsRoutingTo.add(newOrgRequestNode);
                    }
                }
            }
        }
        return newStopsRoutingTo;
    }    
    
    /**
     * @param parent
     * @param child
     * @return true - if child or one of it's eventual parents reports to parent false - if child or one of it's eventual parents does not report to parent
     */
    private boolean hasAsParent(HierarchyProvider provider, Stop parent, Stop child) {
        if (provider.isRoot(child)) {
            return false;
        } else if (provider.equals(parent, child)) {
            return true;
        } else {
            child = provider.getParent(child);
            return hasAsParent(provider, parent, child);
        }
    }


    /**
     * Make the 'floating' split, join and request RouteNodes that will be independent processes. These are the prototypes from which our RouteNodeInstance will belong
     * 
     * @param documentType
     * @param dynamicNodeInstance
     */
    private DocumentType setUpDocumentType(HierarchyProvider provider, DocumentType documentType, RouteNodeInstance dynamicNodeInstance) {
        boolean altered = false;
        if (documentType.getNamedProcess(SPLIT_PROCESS_NAME) == null) {
            RouteNode splitNode = getSplitNode(dynamicNodeInstance);
            documentType.addProcess(getPrototypeProcess(splitNode, documentType));
            altered = true;
        }
        if (documentType.getNamedProcess(JOIN_PROCESS_NAME) == null) {
            RouteNode joinNode = getJoinNode(dynamicNodeInstance);
            documentType.addProcess(getPrototypeProcess(joinNode, documentType));
            altered = true;
        }
        if (documentType.getNamedProcess(REQUEST_PROCESS_NAME) == null) {
            RouteNode requestsNode = getRequestNode(provider, dynamicNodeInstance);
            documentType.addProcess(getPrototypeProcess(requestsNode, documentType));
            altered = true;
        }
        if (documentType.getNamedProcess(NO_STOP_NAME) == null) {
            RouteNode noChartOrgNode = getNoChartOrgNode(dynamicNodeInstance);
            documentType.addProcess(getPrototypeProcess(noChartOrgNode, documentType));
            altered = true;
        }
        if (altered) {
                //side step normal version etc. because it's a pain.
            KEWServiceLocator.getDocumentTypeService().save(documentType);
        }
        return KEWServiceLocator.getDocumentTypeService().findByName(documentType.getName());
    }

    /**
     * Places a Process on the documentType wrapping the node and setting the node as the process's initalRouteNode
     * 
     * @param node
     * @param documentType
     * @return Process wrapping the node passed in
     */
    protected Process getPrototypeProcess(RouteNode node, DocumentType documentType) {
        Process process = new Process();
        process.setDocumentType(documentType);
        process.setInitial(false);
        process.setInitialRouteNode(node);
        process.setName(node.getRouteNodeName());
        return process;
    }

    /**
     * @param process
     * @return Route Node of the JoinNode that will be prototype for the split RouteNodeInstances generated by this component
     */
    private static RouteNode getSplitNode(RouteNodeInstance process) {
        RouteNode dynamicNode = process.getRouteNode();
        RouteNode splitNode = new RouteNode();
        splitNode.setActivationType(dynamicNode.getActivationType());
        splitNode.setDocumentType(dynamicNode.getDocumentType());
        splitNode.setFinalApprovalInd(dynamicNode.getFinalApprovalInd());
        splitNode.setExceptionWorkgroupId(dynamicNode.getExceptionWorkgroupId());
        splitNode.setMandatoryRouteInd(dynamicNode.getMandatoryRouteInd());
        splitNode.setNodeType(SimpleSplitNode.class.getName());
        splitNode.setRouteMethodCode("FR");
        splitNode.setRouteMethodName(null);
        splitNode.setRouteNodeName(SPLIT_PROCESS_NAME);
        return splitNode;
        //SubRequests
    }

    /**
     * @param process
     * @return Route Node of the JoinNode that will be prototype for the join RouteNodeInstances generated by this component
     */
    private static RouteNode getJoinNode(RouteNodeInstance process) {
        RouteNode dynamicNode = process.getRouteNode();
        RouteNode joinNode = new RouteNode();
        joinNode.setActivationType(dynamicNode.getActivationType());
        joinNode.setDocumentType(dynamicNode.getDocumentType());
        joinNode.setFinalApprovalInd(dynamicNode.getFinalApprovalInd());
        joinNode.setExceptionWorkgroupId(dynamicNode.getExceptionWorkgroupId());
        joinNode.setMandatoryRouteInd(dynamicNode.getMandatoryRouteInd());
        joinNode.setNodeType(SimpleJoinNode.class.getName());
        joinNode.setRouteMethodCode("FR");
        joinNode.setRouteMethodName(null);
        joinNode.setRouteNodeName(JOIN_PROCESS_NAME);
        return joinNode;
    }

    /**
     * @param process
     * @return RouteNode of RequestsNode that will be prototype for RouteNodeInstances having requets that are generated by this component
     */
    private RouteNode getRequestNode(HierarchyProvider provider, RouteNodeInstance process) {
        RouteNode dynamicNode = process.getRouteNode();
        RouteNode requestsNode = new RouteNode();
        requestsNode.setActivationType(dynamicNode.getActivationType());
        requestsNode.setDocumentType(dynamicNode.getDocumentType());
        requestsNode.setFinalApprovalInd(dynamicNode.getFinalApprovalInd());
        requestsNode.setExceptionWorkgroupId(dynamicNode.getExceptionWorkgroupId());
        requestsNode.setMandatoryRouteInd(dynamicNode.getMandatoryRouteInd());
        requestsNode.setNodeType(RequestsNode.class.getName());
        requestsNode.setRouteMethodCode("FR");
        requestsNode.setRouteMethodName(process.getRouteNode().getRouteMethodName());
        requestsNode.setRouteNodeName(REQUEST_PROCESS_NAME);
        provider.configureRequestNode(process, requestsNode);
        return requestsNode;
    }

    /**
     * @param process
     * @return RouteNode of a no-op node which will be used if the user sends no Chart+Org XML to this routing component.
     */
    private static RouteNode getNoChartOrgNode(RouteNodeInstance process) {
        RouteNode dynamicNode = process.getRouteNode();
        RouteNode noChartOrgNOde = new RouteNode();
        noChartOrgNOde.setActivationType(dynamicNode.getActivationType());
        noChartOrgNOde.setDocumentType(dynamicNode.getDocumentType());
        noChartOrgNOde.setFinalApprovalInd(dynamicNode.getFinalApprovalInd());
        noChartOrgNOde.setExceptionWorkgroupId(dynamicNode.getExceptionWorkgroupId());
        noChartOrgNOde.setMandatoryRouteInd(dynamicNode.getMandatoryRouteInd());
        noChartOrgNOde.setNodeType(NoOpNode.class.getName());
        noChartOrgNOde.setRouteMethodCode("FR");
        noChartOrgNOde.setRouteMethodName(null);
        noChartOrgNOde.setRouteNodeName(NO_STOP_NAME);
        return noChartOrgNOde;
    }

 
    
    // methods which can be overridden to change the chart org routing node behavior
    
    protected RouteNode getStopRequestNode(Stop stop, DocumentType documentType) {
        return documentType.getNamedProcess(REQUEST_PROCESS_NAME).getInitialRouteNode();
    }
    
}