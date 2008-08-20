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
package org.kuali.rice.kew.engine.transition;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.ProcessResult;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.SimpleResult;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.util.XmlHelper;


/**
 * TransitionEngine responsible for returning the workflow engine to another RouteNode
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class LoopTransitionEngine extends TransitionEngine {

	@Override
	public ProcessResult isComplete(RouteContext context) throws Exception {
		return new SimpleResult(true);
	}
	
    /**
     * Determines the next nodes instances for the transition.  If the node instance already
     * has next nodes instances (i.e. a dynamic node), then those will be returned.  Otherwise
     * it will resolve the next nodes from the RouteNode prototype.
     */
    protected List resolveNextNodeInstances(RouteNodeInstance nodeInstance, List nextRouteNodes) {
    	
    	try {
    		Map<String, String> cfgMap = Utilities.getKeyValueCollectionAsMap(nodeInstance.getRouteNode().getConfigParams());
            String contentFragment = cfgMap.get("contentFragment");
            Document doc = XmlHelper.buildJDocument(new StringReader(contentFragment));		
			
		} catch (InvalidXmlException e) {
			throw new WorkflowRuntimeException(e);
		}
    	
    	
        List nextNodeInstances = new ArrayList();
        for (Iterator iterator = nextRouteNodes.iterator(); iterator.hasNext();) {
            RouteNode nextNode = (RouteNode) iterator.next();
            RouteNodeInstance nextNodeInstance = getRouteHelper().getNodeFactory().createRouteNodeInstance(nodeInstance.getDocumentId(), nextNode);
            nextNodeInstance.setBranch(nodeInstance.getBranch());
            nextNodeInstance.setProcess(nodeInstance.getProcess());
            nextNodeInstances.add(nextNodeInstance);
        }
        return nextNodeInstances;
    }

}
