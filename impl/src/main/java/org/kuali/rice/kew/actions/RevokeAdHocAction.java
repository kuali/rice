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
package org.kuali.rice.kew.actions;

import org.apache.log4j.MDC;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

import java.util.ArrayList;
import java.util.List;


/**
 * The RevokeAdHocApprove revokes the specified AdHoc requests.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RevokeAdHocAction extends ActionTakenEvent {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RevokeAdHocAction.class);

    private AdHocRevoke revoke;

    public RevokeAdHocAction(DocumentRouteHeaderValue routeHeader, KimPrincipal principal) {
        super(KEWConstants.ACTION_TAKEN_ADHOC_REVOKED_CD, routeHeader, principal);
    }

    public RevokeAdHocAction(DocumentRouteHeaderValue routeHeader, KimPrincipal principal, AdHocRevoke revoke, String annotation) {
        super(KEWConstants.ACTION_TAKEN_ADHOC_REVOKED_CD, routeHeader, principal, annotation);
        this.revoke = revoke;
    }

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.actions.ActionTakenEvent#isActionCompatibleRequest(java.util.List)
     */
    @Override
    public String validateActionRules() {
        if (!getRouteHeader().isValidActionToTake(getActionPerformedCode())) {
            return "Revoke adhoc request is not valid on this document";
        }
        return "";
    }
    
    @Override
    public String validateActionRules(List<ActionRequestValue> actionRequests) {
    	return validateActionRules();
    }

    /**
     * Records the approve action.
     * - Checks to make sure the document status allows the action.
     * - Checks that the user has not taken a previous action.
     * - Deactivates the pending requests for this user
     * - Records the action
     *
     * @throws InvalidActionTakenException
     */
    public void recordAction() throws InvalidActionTakenException {
    	MDC.put("docId", getRouteHeader().getDocumentId());
        updateSearchableAttributesIfPossible();

        String errorMessage = validateActionRules();
        if (!org.apache.commons.lang.StringUtils.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }

        LOG.debug("Revoking adhoc request : " + annotation);

        List<ActionRequestValue> requestsToRevoke = new ArrayList<ActionRequestValue>();
        List<ActionRequestValue> actionRequests = getActionRequestService().findPendingRootRequestsByDocId(getDocumentId());
        for (ActionRequestValue actionRequest : actionRequests)
        {
            if (revoke.matchesActionRequest(actionRequest))
            {
                requestsToRevoke.add(actionRequest);
            }
        }
        if (requestsToRevoke.isEmpty() && revoke.getActionRequestId() != null) {
        	throw new InvalidActionTakenException("Failed to revoke action request with id " + revoke.getActionRequestId() +
        			".  ID does not represent a valid ad hoc request!");
        }

        Recipient delegator = findDelegatorForActionRequests(actionRequests);
        LOG.debug("Record the revoke action");
        ActionTakenValue actionTaken = saveActionTaken(delegator);

        LOG.debug("Revoke all matching action requests, number of matching requests: " + requestsToRevoke.size());
        getActionRequestService().deactivateRequests(actionTaken, requestsToRevoke);
        notifyActionTaken(actionTaken);

    }

}
