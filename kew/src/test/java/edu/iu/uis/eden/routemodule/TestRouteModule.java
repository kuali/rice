
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
package edu.iu.uis.eden.routemodule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.util.ResponsibleParty;
import edu.iu.uis.eden.workgroup.GroupNameId;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TestRouteModule implements RouteModule {

    private static Map responsibilityMap = new HashMap();
    
    public List findActionRequests(RouteContext context) throws ResourceUnavailableException, WorkflowException {
    	return findActionRequests(context.getDocument());
    }
    
    public List findActionRequests(DocumentRouteHeaderValue routeHeader) throws ResourceUnavailableException, WorkflowException {
        TestRouteLevel routeLevel = TestRouteModuleXMLHelper.parseCurrentRouteLevel(routeHeader);
        List actionRequests = new ArrayList();
        if (routeLevel == null) {
            return actionRequests;
        }
        for (Iterator iterator = routeLevel.getResponsibilities().iterator(); iterator.hasNext();) {
            TestResponsibility responsibility = (TestResponsibility) iterator.next();
            TestRecipient recipient = responsibility.getRecipient();
            Recipient realRecipient = getRealRecipient(recipient);
            ActionRequestFactory arFactory = new ActionRequestFactory(routeHeader);
            Long responsibilityId = KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId();
            ActionRequestValue request = arFactory.addRootActionRequest(responsibility.getActionRequested(), new Integer(responsibility.getPriority()), realRecipient, "", responsibilityId, Boolean.FALSE, null, null);
            responsibilityMap.put(request.getResponsibilityId(), recipient);
            for (Iterator delIt = responsibility.getDelegations().iterator(); delIt.hasNext();) {
                TestDelegation delegation = (TestDelegation) delIt.next();
                TestRecipient delegationRecipient = delegation.getResponsibility().getRecipient();
                Recipient realDelegationRecipient = getRealRecipient(delegationRecipient);
                responsibilityId = KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId();
                ActionRequestValue delegationRequest = arFactory.addDelegationRequest(request, realDelegationRecipient, responsibilityId, Boolean.FALSE, delegation.getType(), "", null);
                responsibilityMap.put(delegationRequest.getResponsibilityId(), delegationRecipient);
            }
            actionRequests.add(request);
        }
        return actionRequests;
    }

    public Recipient getRealRecipient(TestRecipient recipient) throws WorkflowException {
        Recipient realRecipient = null;
        if (recipient.getType().equals(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD)) {
        	realRecipient = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(recipient.getId()));
        } else if (recipient.getType().equals(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD)) {
        	realRecipient = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(recipient.getId()));
        } else {
        	throw new WorkflowException("Could not resolve recipient with type " + recipient.getType());
        }
        return realRecipient;
    }
    
    public ResponsibleParty resolveResponsibilityId(Long responsibilityId) throws ResourceUnavailableException, WorkflowException {
        TestRecipient recipient = (TestRecipient)responsibilityMap.get(responsibilityId);
        if (recipient == null) {
            return null;
        }
        ResponsibleParty responsibleParty = new ResponsibleParty();
        if (recipient.getType().equals(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD)) {
            responsibleParty.setUserId(new AuthenticationUserId(recipient.getId()));
        } else if (recipient.getType().equals(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD)) {
            responsibleParty.setGroupId(new GroupNameId(recipient.getId()));
        } else if (recipient.getType().equals(EdenConstants.ACTION_REQUEST_ROLE_RECIPIENT_CD)) {
            responsibleParty.setRoleName(recipient.getId());
        } else {
            throw new WorkflowException("Invalid recipient type code of '"+recipient.getType()+"' for responsibility id "+responsibilityId);
        }
        return responsibleParty;
    }
    
}
