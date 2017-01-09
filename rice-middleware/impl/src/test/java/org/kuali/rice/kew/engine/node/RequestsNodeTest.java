/*
 * Copyright 2005-2017 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.kew.engine.node;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routemodule.RouteModule;
import org.kuali.rice.kew.routemodule.service.RouteModuleService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RequestsNode.class, KEWServiceLocator.class })
public class RequestsNodeTest {

    @Mock
    private ActionRequestService actionRequestService;
    @Mock
    private ActionTakenValue actionTaken;
    @Mock
    private DocumentRouteHeaderValue documentRouteHeaderValue;
    @Mock
    private RouteContext routeContext;
    @Mock
    private RouteModule routeModule;
    @Mock
    private RouteModuleService routeModuleService;
    @Mock
    private RouteNode routeNode;
    @Mock
    private RouteNodeInstance routeNodeInstance;

    @InjectMocks
    private RequestsNode requestsNode;

    private final RouteNodeInstance nodeInstance = new RouteNodeInstance();

    @Before
    public void setup() throws Exception {
        when(routeContext.getNodeInstance()).thenReturn(routeNodeInstance);
        when(routeContext.getDocument()).thenReturn(documentRouteHeaderValue);
        when(routeNodeInstance.getRouteNode()).thenReturn(routeNode);
        mockStatic(KEWServiceLocator.class);
        PowerMockito.when(KEWServiceLocator.getRouteModuleService()).thenReturn(routeModuleService);
        when(routeModuleService.findRouteModule(routeNode)).thenReturn(routeModule);
        PowerMockito.when(KEWServiceLocator.getActionRequestService()).thenReturn(actionRequestService);
        when(actionRequestService.initializeActionRequestGraph(any(ActionRequestValue.class), eq(documentRouteHeaderValue), eq(nodeInstance))).thenAnswer(
                new Answer<ActionRequestValue>() {
                    @Override
                    public ActionRequestValue answer(InvocationOnMock invocation) throws Throwable {
                        return invocation.getArgumentAt(0, ActionRequestValue.class);
                    }
                });
        when(routeContext.isSimulation()).thenReturn(false);
        when(actionRequestService.saveActionRequest(any(ActionRequestValue.class))).thenAnswer(new Answer<ActionRequestValue>() {
            @Override
            public ActionRequestValue answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgumentAt(0, ActionRequestValue.class);
            }
        }
        );
    }

    @Test
    public void testIdenticalActionRequestValuesWithNoChildrenAreDuplicates() throws Exception {
        List<ActionRequestValue> actionRequestValues = new ArrayList<ActionRequestValue>();
        actionRequestValues.add(getActionRequest());
        actionRequestValues.add(getActionRequest());
        when(routeModule.findActionRequests(routeContext)).thenReturn(actionRequestValues);

        List<ActionRequestValue> deduplicatedActionRequestValues = requestsNode.getNewActionRequests(routeContext);
        assertEquals(1, deduplicatedActionRequestValues.size());
    }

    @Test
    public void testActionRequestsWithDifferentNumbersOfChildrenAreNotDuplicates() throws Exception {
        List<ActionRequestValue> actionRequestValues = new ArrayList<ActionRequestValue>();
        ActionRequestValue firstActionRequest = getActionRequest();
        List<ActionRequestValue> children = new ArrayList<ActionRequestValue>();
        children.add(getActionRequest());
        firstActionRequest.setChildrenRequests(children);
        actionRequestValues.add(firstActionRequest);
        ActionRequestValue secondActionRequest = getActionRequest();
        secondActionRequest.setChildrenRequests(Collections.<ActionRequestValue>emptyList());
        actionRequestValues.add(secondActionRequest);
        when(routeModule.findActionRequests(routeContext)).thenReturn(actionRequestValues);

        List<ActionRequestValue> deduplicatedActionRequestValues = requestsNode.getNewActionRequests(routeContext);
        assertEquals(2, deduplicatedActionRequestValues.size());
    }

    @Test
    public void testActionRequestsWithIdenticalChildrenAreDuplicates() throws Exception {
        List<ActionRequestValue> actionRequestValues = new ArrayList<ActionRequestValue>();
        List<ActionRequestValue> children = new ArrayList<ActionRequestValue>();
        children.add(getActionRequest());
        ActionRequestValue firstActionRequest = getActionRequest();
        firstActionRequest.setChildrenRequests(children);
        actionRequestValues.add(firstActionRequest);
        ActionRequestValue secondActionRequest = getActionRequest();
        secondActionRequest.setChildrenRequests(children);
        actionRequestValues.add(secondActionRequest);
        when(routeModule.findActionRequests(routeContext)).thenReturn(actionRequestValues);

        List<ActionRequestValue> deduplicatedActionRequestValues = requestsNode.getNewActionRequests(routeContext);
        assertEquals(1, deduplicatedActionRequestValues.size());
    }

    @Test
    public void testActionRequestsWithDifferingChildrenAreDuplicates() throws Exception {
        List<ActionRequestValue> actionRequestValues = new ArrayList<ActionRequestValue>();
        List<ActionRequestValue> firstChildren = new ArrayList<ActionRequestValue>();
        firstChildren.add(getActionRequest());
        ActionRequestValue firstActionRequest = getActionRequest();
        firstActionRequest.setChildrenRequests(firstChildren);
        actionRequestValues.add(firstActionRequest);
        ActionRequestValue secondActionRequest = getActionRequest();
        List<ActionRequestValue> secondChildren = new ArrayList<ActionRequestValue>();
        ActionRequestValue differingActionRequest = getActionRequest();
        differingActionRequest.setPrincipalId("otherPrincipal");
        secondChildren.add(differingActionRequest);
        secondActionRequest.setChildrenRequests(secondChildren);
        actionRequestValues.add(secondActionRequest);
        when(routeModule.findActionRequests(routeContext)).thenReturn(actionRequestValues);

        List<ActionRequestValue> deduplicatedActionRequestValues = requestsNode.getNewActionRequests(routeContext);
        assertEquals(2, deduplicatedActionRequestValues.size());
    }

    private ActionRequestValue getActionRequest() {
        ActionRequestValue actionRequestValue = new ActionRequestValue();
        actionRequestValue.setActionRequestId("12345");
        actionRequestValue.setPrincipalId("principal");
        actionRequestValue.setStatus("STATUS");
        actionRequestValue.setResponsibilityId("responsibilityId");
        actionRequestValue.setGroupId("groupId");
        actionRequestValue.setPriority(0);
        actionRequestValue.setRouteLevel(0);
        actionRequestValue.setResponsibilityDesc("Responsibility description");
        actionRequestValue.setAnnotation("An annotation");
        actionRequestValue.setForceAction(true);
        actionRequestValue.setQualifiedRoleName("qualifiedRoleName");
        actionRequestValue.setRoleName("roleName");
        actionRequestValue.setApprovePolicy("approvePolicy");
        actionRequestValue.setCurrentIndicator(true);
        actionRequestValue.setNodeInstance(nodeInstance);
        actionRequestValue.setActionTaken(actionTaken);
        actionRequestValue.setDelegationType(DelegationType.PRIMARY);
        actionRequestValue.setRuleBaseValuesId("ruleBaseValuesId");
        actionRequestValue.setDisplayStatus("displayStatus");
        actionRequestValue.setQualifiedRoleNameLabel("qualifiedRoleNameLabel");
        actionRequestValue.setParentActionRequest(null);
        actionRequestValue.setDocVersion(1);
        actionRequestValue.setDocumentId("documentId");
        return actionRequestValue;
    }

}
