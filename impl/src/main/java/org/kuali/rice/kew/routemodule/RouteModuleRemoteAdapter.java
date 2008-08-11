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
package org.kuali.rice.kew.routemodule;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.rice.kew.clientapp.RouteModuleRemote;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.util.ResponsibleParty;


/**
 * Adapts a {@link RouteModuleRemote} to the {@link RouteModule} interface.
 *
 * @see RouteModuleRemote
 * @see RouteModule
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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
            DocumentContentDTO documentContentVO = DTOConverter.convertDocumentContent(routeHeader.getDocContent(), routeHeaderVO.getRouteHeaderId());
            ActionRequestDTO[] actionRequestVOs = routeModule.findActionRequests(routeHeaderVO, documentContentVO);
            if (actionRequestVOs != null && actionRequestVOs.length > 0) {
                Set rootRequests = findRootRequests(actionRequestVOs);

                for (Iterator iterator = rootRequests.iterator(); iterator.hasNext();) {
                    ActionRequestDTO actionRequestVO = (ActionRequestDTO) iterator.next();
                    actionRequestVO.setRouteHeaderId(routeHeader.getRouteHeaderId());
                    actionRequests.add(DTOConverter.convertActionRequestVO(actionRequestVO));
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

    public ResponsibleParty resolveResponsibilityId(Long responsibilityId) throws WorkflowException {
        try {
            return DTOConverter.convertResponsiblePartyVO(routeModule.resolveResponsibilityId(responsibilityId));
        } catch (RemoteException e) {
            if (e.getCause() instanceof WorkflowException) {
                throw (WorkflowException)e.getCause();
            }
            throw new WorkflowException("Remote exception when resolving responsibility ids from route module "+routeModule.toString(), e);
        }
    }

    private Set findRootRequests(ActionRequestDTO[] actionRequestVOs) {
        Set rootRequests = new HashSet();
        for (int index = 0; index < actionRequestVOs.length; index++) {
            rootRequests.add(findRootRequest(actionRequestVOs[index], new HashSet()));
        }
        return rootRequests;
    }

    /**
     * Walks to the top of the request graph and returns the root request.  Also attempts to
     * avoid bad data by detecting cycles in the graph.
     */
    private ActionRequestDTO findRootRequest(ActionRequestDTO actionRequestVO, Set parents) {
        if (actionRequestVO.getParentActionRequest() != null) {
            if (parents.contains(actionRequestVO.getParentActionRequest())) {
                throw new WorkflowRuntimeException("Detected a cycle in action request graph returned from route module "+routeModule.getClass());
            }
            parents.add(actionRequestVO.getParentActionRequest());
            return findRootRequest(actionRequestVO.getParentActionRequest(), parents);
        }
        return actionRequestVO;
    }

}
