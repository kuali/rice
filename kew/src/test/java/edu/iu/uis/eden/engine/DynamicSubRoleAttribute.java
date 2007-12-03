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
package edu.iu.uis.eden.engine;

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.engine.node.NodeState;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.AbstractRoleAttribute;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.user.AuthenticationUserId;

public class DynamicSubRoleAttribute extends AbstractRoleAttribute {

    private List roleNames = new ArrayList();
    
    public DynamicSubRoleAttribute() {
        roleNames.add("DynamicSub");
    }

    public List getRoleNames() {
        return roleNames;
    }

    public List getQualifiedRoleNames(String roleName, DocumentContent documentContent) throws EdenUserNotFoundException {
        List qualifiedRoleNames = new ArrayList();
        qualifiedRoleNames.add(roleName);
        return qualifiedRoleNames;
    }

    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        NodeState nodeState = routeContext.getNodeInstance().getNodeState("role");
        if (nodeState == null) {
            return new ResolvedQualifiedRole();
        }
        String networkId = nodeState.getValue();
        String label = "role " + networkId;
        List recipients = new ArrayList();
        recipients.add(new AuthenticationUserId(networkId));
        return new ResolvedQualifiedRole(label, recipients);
    }   
}