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

import org.apache.log4j.Logger;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.clientapp.RouteModuleRemote;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteModuleServiceImpl implements RouteModuleService {

    private static final Logger LOG = Logger.getLogger(RouteModuleServiceImpl.class);

    public RouteModule findRouteModule(RouteNode node) throws ResourceUnavailableException {
        String routeMethodName = node.getRouteMethodName();
        LOG.debug("Finding route module for routeMethodName="+routeMethodName+" at route level "+node.getRouteNodeName());
        RouteModule routeModule = null;
        if (node.isFlexRM()) {
            routeModule = getFlexRMRouteModule(routeMethodName);
        } else {
            routeModule = getRouteModule(routeMethodName);
        }
        return routeModule;
    }

    public RouteModule findRouteModule(ActionRequestValue actionRequest) throws ResourceUnavailableException {
        if (actionRequest.getNodeInstance() == null) {
            return null;
        }
        return findRouteModule(actionRequest.getNodeInstance().getRouteNode());
    }

    private RouteModule getRouteModule(String routeMethodName) throws ResourceUnavailableException {
        if (routeMethodName == null) {
            return null;
        } else if ("".equals(routeMethodName.trim()) || EdenConstants.ROUTE_LEVEL_NO_ROUTE_MODULE.equals(routeMethodName)) {
                return null;
        }
        Object routeModule = GlobalResourceLoader.getObject(new ObjectDefinition(routeMethodName));//SpringServiceLocator.getExtensionService().getRouteModule(routeMethodName);
        if (routeModule instanceof RouteModule) {
            return (RouteModule)routeModule;
        } else if (routeModule instanceof RouteModuleRemote) {
            return new RouteModuleRemoteAdapter((RouteModuleRemote)routeModule);
        }
        throw new WorkflowRuntimeException("Could not locate the Route Module with the given name: " + routeMethodName);
    }

    private RouteModule getFlexRMRouteModule(String ruleTemplateName) {
        return new FlexRMAdapter(ruleTemplateName);
    }


}
