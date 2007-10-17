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
package edu.iu.uis.eden.util;

import java.util.HashMap;
import java.util.Map;

import edu.iu.uis.eden.EdenConstants;

/**
 * Utility class to translate the various codes used in Eden into labels and vice versa.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CodeTranslator {
    public static final Map arLabels = getArLabels();
    public static final Map atLabels = getAtLabels();
    public static final Map arStatusLabels = getArStatusLabels();
    public static final Map routeStatusLabels = getRouteStatusLabels();
    public static final Map activeIndicatorLabels = getActiveIndicatorLabels();
    public static final Map activationPolicyLabels = getActivationPolicyLabels();
    public static final Map policyLabels = getPolicyLabels();
    public static final Map approvePolicyLabels = getApprovePolicyLabels();

    private static Map getAtLabels() {
        Map newAtLabels = new HashMap();
        newAtLabels.put(EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD, EdenConstants.ACTION_TAKEN_ACKNOWLEDGED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_ADHOC_CD, EdenConstants.ACTION_TAKEN_ADHOC);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD, EdenConstants.ACTION_TAKEN_ADHOC_REVOKED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_APPROVED_CD, EdenConstants.ACTION_TAKEN_APPROVED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD, EdenConstants.ACTION_TAKEN_BLANKET_APPROVE);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_CANCELED_CD, EdenConstants.ACTION_TAKEN_CANCELED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_COMPLETED_CD, EdenConstants.ACTION_TAKEN_COMPLETED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_ROUTED_CD, EdenConstants.ACTION_TAKEN_ROUTED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_DENIED_CD, EdenConstants.ACTION_TAKEN_DENIED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_FYI_CD, EdenConstants.ACTION_TAKEN_FYI);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SAVED_CD, EdenConstants.ACTION_TAKEN_SAVED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_RETURNED_TO_PREVIOUS_CD, EdenConstants.ACTION_TAKEN_RETURNED_TO_PREVIOUS);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_LOG_DOCUMENT_ACTION_CD, EdenConstants.ACTION_TAKEN_LOG_DOCUMENT_ACTION);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_MOVE_CD, EdenConstants.ACTION_TAKEN_MOVE);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SU_APPROVED_CD, EdenConstants.ACTION_TAKEN_SU_APPROVED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SU_CANCELED_CD, EdenConstants.ACTION_TAKEN_SU_CANCELED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SU_DISAPPROVED_CD, EdenConstants.ACTION_TAKEN_SU_DISAPPROVED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SU_ROUTE_LEVEL_APPROVED_CD, EdenConstants.ACTION_TAKEN_SU_ROUTE_LEVEL_APPROVED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_APPROVED_CD, EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_APPROVED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SU_RETURNED_TO_PREVIOUS_CD, EdenConstants.ACTION_TAKEN_SU_RETURNED_TO_PREVIOUS);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_ACKNOWLEDGED_CD, EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_ACKNOWLEDGED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_FYI_CD, EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_FYI);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_COMPLETED_CD, EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_COMPLETED);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_TAKE_WORKGROUP_AUTHORITY_CD, EdenConstants.ACTION_TAKEN_TAKE_WORKGROUP_AUTHORITY);
        newAtLabels.put(EdenConstants.ACTION_TAKEN_RELEASE_WORKGROUP_AUTHORITY_CD, EdenConstants.ACTION_TAKEN_RELEASE_WORKGROUP_AUTHORITY);
        return newAtLabels;
    }

    private static Map getArLabels() {
        Map newArLabels = new HashMap();
        newArLabels.put(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
        newArLabels.put(EdenConstants.ACTION_REQUEST_APPROVE_REQ, EdenConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
        newArLabels.put(EdenConstants.ACTION_REQUEST_COMPLETE_REQ, EdenConstants.ACTION_REQUEST_COMPLETE_REQ_LABEL);
        newArLabels.put(EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
        return newArLabels;
    }

    private static Map getArStatusLabels() {
        Map newArStatusLabels = new HashMap();
        newArStatusLabels.put(EdenConstants.ACTION_REQUEST_ACTIVATED, EdenConstants.ACTIVE_LABEL);
        newArStatusLabels.put(EdenConstants.ACTION_REQUEST_INITIALIZED, EdenConstants.ACTION_REQUEST_INITIALIZED_LABEL);
        newArStatusLabels.put(EdenConstants.ACTION_REQUEST_DONE_STATE, EdenConstants.ACTION_REQUEST_DONE_STATE_LABEL);
        return newArStatusLabels;
    }

    private static Map getRouteStatusLabels() {
        Map newRouteStatusLabels = new HashMap();
        newRouteStatusLabels.put(EdenConstants.ROUTE_HEADER_APPROVED_CD, EdenConstants.ROUTE_HEADER_APPROVED_LABEL);
        newRouteStatusLabels.put(EdenConstants.ROUTE_HEADER_CANCEL_CD, EdenConstants.ROUTE_HEADER_CANCEL_LABEL);
        newRouteStatusLabels.put(EdenConstants.ROUTE_HEADER_DISAPPROVED_CD, EdenConstants.ROUTE_HEADER_DISAPPROVED_LABEL);
        newRouteStatusLabels.put(EdenConstants.ROUTE_HEADER_ENROUTE_CD, EdenConstants.ROUTE_HEADER_ENROUTE_LABEL);
        newRouteStatusLabels.put(EdenConstants.ROUTE_HEADER_EXCEPTION_CD, EdenConstants.ROUTE_HEADER_EXCEPTION_LABEL);
        newRouteStatusLabels.put(EdenConstants.ROUTE_HEADER_FINAL_CD, EdenConstants.ROUTE_HEADER_FINAL_LABEL);
        newRouteStatusLabels.put(EdenConstants.ROUTE_HEADER_INITIATED_CD, EdenConstants.ROUTE_HEADER_INITIATED_LABEL);
        newRouteStatusLabels.put(EdenConstants.ROUTE_HEADER_PROCESSED_CD, EdenConstants.ROUTE_HEADER_PROCESSED_LABEL);
        newRouteStatusLabels.put(EdenConstants.ROUTE_HEADER_SAVED_CD, EdenConstants.ROUTE_HEADER_SAVED_LABEL);
        return newRouteStatusLabels;
    }

    private static Map getActiveIndicatorLabels() {
        Map newActiveIndicatorLabels = new HashMap();
        newActiveIndicatorLabels.put(new Boolean(true), EdenConstants.ACTIVE_LABEL_LOWER);
        newActiveIndicatorLabels.put(new Boolean(false), EdenConstants.INACTIVE_LABEL_LOWER);
        return newActiveIndicatorLabels;
    }

    private static Map getPolicyLabels() {
        Map newDocTypeActiveIndicatorLabels = new HashMap();
        newDocTypeActiveIndicatorLabels.put(EdenConstants.TRUE_CD, EdenConstants.YES_LABEL);
        newDocTypeActiveIndicatorLabels.put(EdenConstants.FALSE_CD, EdenConstants.NO_LABEL);
        newDocTypeActiveIndicatorLabels.put(EdenConstants.INHERITED_CD, EdenConstants.INHERITED_LABEL);
        return newDocTypeActiveIndicatorLabels;
    }

    private static Map getActivationPolicyLabels() {
        Map newActivationPolicyLabels = new HashMap();
        newActivationPolicyLabels.put(EdenConstants.ROUTE_LEVEL_PARALLEL, EdenConstants.ROUTE_LEVEL_PARALLEL_LABEL);
        newActivationPolicyLabels.put(EdenConstants.ROUTE_LEVEL_SEQUENCE, EdenConstants.ROUTE_LEVEL_SEQUENCE_LABEL);
        return newActivationPolicyLabels;
    }

    private static Map getApprovePolicyLabels() {
        Map approvePolicyLabels = new HashMap();
        approvePolicyLabels.put(EdenConstants.APPROVE_POLICY_ALL_APPROVE, EdenConstants.APPROVE_POLICY_ALL_APPROVE_LABEL);
        approvePolicyLabels.put(EdenConstants.APPROVE_POLICY_FIRST_APPROVE, EdenConstants.APPROVE_POLICY_FIRST_APPROVE_LABEL);
        return approvePolicyLabels;
    }

    /**
     * Given an actionRequest code return the appropriate label.
     *
     * @param actionRequestCode
     *            The actionRequestCode to be translated.
     * @return action request label
     */
    static public String getActionRequestLabel(String actionRequestCode) {
        return (String) arLabels.get(actionRequestCode);
    }

    /**
     * Given an action taken code return the appropriate label for it.
     *
     * @param actionTakenCode
     *            action taken code to use to find the label.
     * @return action taken label
     */
    static public String getActionTakenLabel(String actionTakenCode) {
        return (String) atLabels.get(actionTakenCode);
    }

    /**
     * Return the label for the given request status level.
     *
     * @param status
     *            code of the request status
     * @return label for the corresponding code.
     */
    static public String getActionRequestStatusLabel(String status) {
        return (String) arStatusLabels.get(status);
    }

    static public String getRouteStatusLabel(String status) {
        return (String) routeStatusLabels.get(status);
    }

    static public String getActiveIndicatorLabel(Boolean indicator) {
        return (String) activeIndicatorLabels.get(indicator);
    }

    static public String getActivationPolicyLabel(String code) {
        return (String) activationPolicyLabels.get(code);
    }

    static public String getPolicyLabel(String code) {
        return (String) policyLabels.get(code);
    }

}
