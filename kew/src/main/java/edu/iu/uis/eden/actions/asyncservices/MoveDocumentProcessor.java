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

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actions.MoveDocumentAction;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Service to do the async work of moving a document.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MoveDocumentProcessor implements MoveDocumentService { 
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MoveDocumentProcessor.class);

	public void moveDocument(WorkflowUser user, DocumentRouteHeaderValue document, ActionTakenValue actionTaken, Set nodeNames) {
		KEWServiceLocator.getRouteHeaderService().lockRouteHeader(document.getRouteHeaderId(), true);
		MoveDocumentAction moveAction = new MoveDocumentAction(document, user, "", null);
        moveAction.setActionTaken(actionTaken);
        moveAction.setActionTakenCode(EdenConstants.ACTION_TAKEN_MOVE_CD);
        LOG.debug("Doing move document work " + document.getRouteHeaderId());
        try {
			moveAction.doMoveDocumentWork(nodeNames);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
        moveAction.queueDocument();
        LOG.debug("Work done and document requeued, document " + document.getRouteHeaderId());
	}
}
