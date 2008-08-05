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

import java.util.List;

import org.kuali.rice.kew.actionrequests.ActionRequestValue;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.routetemplate.FlexRM;


/**
 * Adapts {@link FlexRM} to the {@link RouteModule} interface.
 * 
 * @see FlexRM
 * @see RouteModule
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class FlexRMAdapter extends FlexRM implements RouteModule {

    public List<ActionRequestValue> findActionRequests(RouteContext context) throws Exception {
        RouteNodeInstance nodeInstance = context.getNodeInstance();
        return getActionRequests(context.getDocument(), nodeInstance, nodeInstance.getRouteNode().getRouteMethodName());
    }

    /*
    public List<ActionRequestValue> findActionRequests(DocumentRouteHeaderValue routeHeader, RouteNodeInstance nodeInstance) throws WorkflowException {
        return getActionRequests(routeHeader, nodeInstance, ruleTemplateName);
    }

    public List<ActionRequestValue> findActionRequests(DocumentRouteHeaderValue routeHeader) throws WorkflowException {
        return getActionRequests(routeHeader, ruleTemplateName);
    }*/

    public String toString() {
        return "FlexRMAdapter";
    }
}