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
package edu.iu.uis.eden.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.MDC;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * The RevokeAdHocApprove revokes the specified AdHoc requests.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RevokeAdHocAction extends ActionTakenEvent {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RevokeAdHocAction.class);

    private AdHocRevoke revoke;

    public RevokeAdHocAction(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD);
    }

    public RevokeAdHocAction(DocumentRouteHeaderValue routeHeader, WorkflowUser user, AdHocRevoke revoke, String annotation) {
        super(routeHeader, user, annotation);
        this.revoke = revoke;
        setActionTakenCode(EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD);
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#isActionCompatibleRequest(java.util.List)
     */
    @Override
    public String validateActionRules() throws EdenUserNotFoundException {
        String superError = super.validateActionTakenRules();
        if (!Utilities.isEmpty(superError)) {
            return superError;
        }
        if (!getRouteHeader().isValidActionToTake(getActionPerformedCode())) {
            return "Revoke adhoc request is not valid on this document";
        }
        return "";
    }

    /**
     * Records the approve action.
     * - Checks to make sure the document status allows the action.
     * - Checks that the user has not taken a previous action.
     * - Deactivates the pending requests for this user
     * - Records the action
     *
     * @throws InvalidActionTakenException
     * @throws EdenUserNotFoundException
     */
    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
    	MDC.put("docId", getRouteHeader().getRouteHeaderId());
        checkLocking();
        updateSearchableAttributesIfPossible();

        String errorMessage = validateActionRules();
        if (!Utilities.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }
//        if (! routeHeader.isValidActionToTake(getActionTakenCode())) {
//              LOG.warn("RevokeAdHocRequest action is not valid on this document.");
//              throw new InvalidActionTakenException("Revoke adhoc request is not valid on this document.");
//        }

        LOG.debug("Revoking adhoc request : " + annotation);

        List requestsToRevoke = new ArrayList();
        List actionRequests = getActionRequestService().findPendingRootRequestsByDocId(getRouteHeaderId());
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
			ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
			if (revoke.matchesActionRequest(actionRequest)) {
				requestsToRevoke.add(actionRequest);
			}
		}
        if (requestsToRevoke.isEmpty() && revoke.getActionRequestId() != null) {
        	throw new InvalidActionTakenException("Failed to revoke action request with id " + revoke.getActionRequestId() +
        			".  ID does not represent a valid ad hoc request!");
        }

        Recipient delegator = findDelegatorForActionRequests(actionRequests);
        LOG.debug("Record the revoke action");
        saveActionTaken(delegator);

        LOG.debug("Revoke all matching action requests, number of matching requests: " + requestsToRevoke.size());
        getActionRequestService().deactivateRequests(actionTaken, requestsToRevoke);
        notifyActionTaken(this.actionTaken);

    }

}
