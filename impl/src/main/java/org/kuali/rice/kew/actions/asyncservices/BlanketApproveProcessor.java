/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.actions.asyncservices;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.actions.BlanketApproveAction;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.principal.Principal;



/**
 * Responsible for invoking the async piece of BlanketApprove
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BlanketApproveProcessor implements BlanketApproveProcessorService {
	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BlanketApproveProcessor.class);

    @Override
	public void doBlanketApproveWork(String documentId, String principalId, String actionTakenId, Set<String> nodeNames) {
        doBlanketApproveWork(documentId, principalId, actionTakenId, nodeNames, false);
	}

    @Override
	public void doBlanketApproveWork(String documentId, String principalId, String actionTakenId, Set<String> nodeNames, boolean shouldSearchIndex) {
		if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is null or blank");
        }

        if (StringUtils.isBlank(documentId)) {
            throw new RiceIllegalArgumentException("documentId is null");
        }

        if (StringUtils.isBlank(actionTakenId)) {
            throw new RiceIllegalArgumentException("actionTakenId is null");
        }
        if (nodeNames == null) {
            throw new RiceIllegalArgumentException("nodeNames is null");
        }

        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
		DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		ActionTakenValue actionTaken = KEWServiceLocator.getActionTakenService().findByActionTakenId(actionTakenId);
		Principal principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(principalId);
		BlanketApproveAction blanketApprove = new BlanketApproveAction(document, principal, "", nodeNames);
		LOG.debug("Doing blanket approve work document " + document.getDocumentId());
		try {
			blanketApprove.performDeferredBlanketApproveWork(actionTaken);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
		if (shouldSearchIndex) {
            DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(document.getDocumentType().getApplicationId());
            queue.indexDocument(documentId);
		}
		LOG.debug("Work done and document requeued, document " + document.getDocumentId());
	}
}
