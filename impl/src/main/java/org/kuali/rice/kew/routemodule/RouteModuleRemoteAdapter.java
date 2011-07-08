/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.routemodule;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.util.ResponsibleParty;


/**
 * Adapts a {@link RouteModuleRemote} to the {@link RouteModule} interface.
 *
 * @see RouteModuleRemote
 * @see RouteModule
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RouteModuleRemoteAdapter implements RouteModule {

    private RouteModuleRemote routeModule;

    public RouteModuleRemoteAdapter(RouteModuleRemote routeModule) {
        if (routeModule == null) {
            throw new IllegalArgumentException("RouteModuleRemoteAdapter cannot adapt a null RouteModuleRemote");
        }
        this.routeModule = routeModule;
    }

    public List findActionRequests(RouteContext context) throws Exception {
    	// TODO add new findActionRequests to RouteModuleRemote
    	return findActionRequests(context.getDocument());
    }

    public List findActionRequests(DocumentRouteHeaderValue routeHeader) throws WorkflowException {
        try {
            List actionRequests = new ArrayList();
            RouteHeaderDTO routeHeaderVO = DTOConverter.convertRouteHeader(routeHeader, null);
            DocumentContentDTO documentContentVO = DTOConverter.convertDocumentContent(routeHeader.getDocContent(), routeHeaderVO.getDocumentId());
            ActionRequestDTO[] actionRequestVOs = routeModule.findActionRequests(routeHeaderVO, documentContentVO);
            if (actionRequestVOs != null && actionRequestVOs.length > 0) {
            	assertRootRequests(actionRequestVOs);

                for (ActionRequestDTO actionRequestVO : actionRequestVOs) {
                    actionRequestVO.setDocumentId(routeHeader.getDocumentId());
                    
                    // TODO this should be moved to a validate somewhere's...
                    if (actionRequestVO.getPrincipalId() == null && actionRequestVO.getGroupId() == null) {
                    	throw new RiceRuntimeException("Post processor didn't set a user or workgroup on the request");
                    }
                    
                    actionRequests.add(DTOConverter.convertActionRequestDTO(actionRequestVO));
                }
            }
            return actionRequests;
        } catch (RemoteException e) {
            if (e.getCause() instanceof WorkflowException) {
                throw (WorkflowException)e.getCause();
            }
            throw new WorkflowException("Remote exception when finding action requests from route module "+routeModule.toString(), e);
        }
    }

    public ResponsibleParty resolveResponsibilityId(String responsibilityId) throws WorkflowException {
        try {
            return DTOConverter.convertResponsiblePartyVO(routeModule.resolveResponsibilityId(responsibilityId));
        } catch (RemoteException e) {
            if (e.getCause() instanceof WorkflowException) {
                throw (WorkflowException)e.getCause();
            }
            throw new WorkflowException("Remote exception when resolving responsibility ids from route module "+routeModule.toString(), e);
        }
    }
    
    /**
     * Asserts that the given array of ActionRequestDTOs are only root requests (as required by the RouteModuleRemote API)
     */
    private void assertRootRequests(ActionRequestDTO[] actionRequestVOs) throws WorkflowException {
    	for (ActionRequestDTO actionRequest : actionRequestVOs) {
    		if (actionRequest.getParentActionRequestId() != null) {
    			throw new WorkflowException("Encountered an action request in the graph which was NOT a root request.  RouteModuleRemote.findActionRequests should only produce root ActionRequestDTO objects.");
    		}
    	}
    }

}
