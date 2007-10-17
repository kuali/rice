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
package edu.iu.uis.eden.engine.transition;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.ProcessResult;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.engine.node.SimpleResult;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.util.XmlHelper;

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
			Document doc = XmlHelper.buildJDocument(new StringReader(nodeInstance.getRouteNode().getContentFragment()));
			
			
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
