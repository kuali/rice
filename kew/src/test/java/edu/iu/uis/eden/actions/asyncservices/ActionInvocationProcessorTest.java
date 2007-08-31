/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.actions.asyncservices;

import org.junit.Test;
import org.kuali.workflow.test.WorkflowTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.user.WorkflowUser;


/**
 * 
 * @author rkirkend
 *
 */
public class ActionInvocationProcessorTest extends WorkflowTestCase {

    
    @Test public void testActionInvocationProcessorWorksWithNoActionItem() throws Exception {
	NetworkIdVO netId = new NetworkIdVO("rkirkend");
	WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(netId);
	WorkflowDocument doc = new WorkflowDocument(netId, "TestDocumentType");
	
	assertFalse(! KEWServiceLocator.getActionListService().findByRouteHeaderId(doc.getRouteHeaderId()).isEmpty());
	
	new ActionInvocationProcessor().invokeAction(user, doc.getRouteHeaderId(), new ActionInvocation(new Long(-1), "A"));
	assertTrue(true);
    }
    
    
}
