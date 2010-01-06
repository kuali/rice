/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.engine.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.RouteHelper;


/**
 * A simple {@link SplitNode} implementation which always splits to all branches that are defined for the split.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SimpleSplitNode implements SplitNode {

    public SplitResult process(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
    	List branchNames = new ArrayList();
    	for (Iterator iterator = routeContext.getNodeInstance().getRouteNode().getNextNodes().iterator(); iterator.hasNext(); ) {
			RouteNode routeNode = (RouteNode) iterator.next();
			branchNames.add(routeNode.getBranch().getName());
		}
        return new SplitResult(branchNames);
    }
    
}
