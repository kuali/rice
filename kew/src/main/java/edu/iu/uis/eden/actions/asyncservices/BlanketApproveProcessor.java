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
package edu.iu.uis.eden.actions.asyncservices;

import java.util.Set;

import org.kuali.workflow.actions.asyncservices.DeferredActionServiceImpl;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actions.BlanketApproveAction;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Responsible for invoking the async piece of BlanketApprove
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BlanketApproveProcessor implements BlanketApproveProcessorService {
	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BlanketApproveProcessor.class);

	public void doBlanketApproveWork(Long documentId, WorkflowUser user, Long actionTakenId, Set<String> nodeNames) {
		new DeferredActionServiceImpl().performDeferredWork("blanketapprove", documentId, user, actionTakenId, nodeNames);
	}
}
