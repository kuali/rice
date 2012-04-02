/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kew.doctype;

import org.kuali.rice.kew.api.KewApiConstants;


/**
 * DocumentType policy enum type.
 * Encapsulates  policies of the document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class DocumentTypePolicyEnum {

	/**
     * FIXME: needs docs
     */
    public static final DocumentTypePolicyEnum DISAPPROVE = new DocumentTypePolicyEnum(KewApiConstants.DISAPPROVE_POLICY);
    
    /**
     * This policy determines whether to use the internal KEW Super User document handler URL when opening a document from
     * super user search. If set to false the client must implement a custom super user screen to be used when the doc
     * handler URL has a post variable of the name defined by {@link KewApiConstants#COMMAND_PARAMETER} and a value of
     * {@link KewApiConstants#SUPERUSER_COMMAND}. The default is 'true'.
     */
    public static final DocumentTypePolicyEnum USE_KEW_SUPERUSER_DOCHANDLER = new DocumentTypePolicyEnum(KewApiConstants.USE_KEW_SUPERUSER_DOCHANDLER);

    /**
     * determines how to handle the situation where the user has initiated an action but there is not a corresponding pending request. This policy has a default of true.
     * If set to false an exception should be thrown from ApproveAction, CompleteAction, AcknowledgeAction
     * and ClearFYIAction classes when there does not exist a corresponding pending request for the user who is submitting the action. 
     * When set to false, this will result in one of the users getting an error message if 2 users attempt to submit the same action
     * at the same time (this can happen in workgroup situtations). 
     */
    public static final DocumentTypePolicyEnum ALLOW_UNREQUESTED_ACTION = new DocumentTypePolicyEnum(KewApiConstants.ALLOW_UNREQUESTED_ACTION_POLICY);
    
    /**
     * determines whether a document will go processed without any approval requests.  If
     * a document has this policy set to false and doesn't generate and approval requests the document will
     * be put in exception routing, which is the exception workgroup associated with the last route node or the
     * workgroup defined in the 'defaultExceptionGroupName'.  This policy if not defined in this or a parent
     * document type defaults to true
     */
    public static final DocumentTypePolicyEnum DEFAULT_APPROVE = new DocumentTypePolicyEnum(KewApiConstants.DEFAULT_APPROVE_POLICY);
    /**
     * determines if the user that initiated a document must 'route' the document when it is
     * in the initiated state.  Defaults to true.
     */
    public static final DocumentTypePolicyEnum INITIATOR_MUST_ROUTE = new DocumentTypePolicyEnum(KewApiConstants.INITIATOR_MUST_ROUTE_POLICY);
    /**
     * determines if the user that initiated a document must 'route' the document when it is
     * in the initiated state.  Defaults to true.
     */
    public static final DocumentTypePolicyEnum INITIATOR_MUST_SAVE = new DocumentTypePolicyEnum(KewApiConstants.INITIATOR_MUST_SAVE_POLICY);
    public static final DocumentTypePolicyEnum INITIATOR_MUST_CANCEL = new DocumentTypePolicyEnum(KewApiConstants.INITIATOR_MUST_CANCEL_POLICY);
    public static final DocumentTypePolicyEnum INITIATOR_MUST_BLANKET_APPROVE = new DocumentTypePolicyEnum(KewApiConstants.INITIATOR_MUST_BLANKET_APPROVE_POLICY);

    /**
     * determines whether the document can be brought into a simulated route from the route log.  A
     * simulation of where the document would end up if it where routed to completion now.  Defaults to false.
     */
    // determines if route log will show the look into the future link
    public static final DocumentTypePolicyEnum LOOK_FUTURE = new DocumentTypePolicyEnum(KewApiConstants.LOOK_INTO_FUTURE_POLICY);

    public static final DocumentTypePolicyEnum SEND_NOTIFICATION_ON_SU_APPROVE = new DocumentTypePolicyEnum(KewApiConstants.SEND_NOTIFICATION_ON_SU_APPROVE_POLICY);

    public static final DocumentTypePolicyEnum SUPPORTS_QUICK_INITIATE = new DocumentTypePolicyEnum(KewApiConstants.SUPPORTS_QUICK_INITIATE_POLICY);

    public static final DocumentTypePolicyEnum NOTIFY_ON_SAVE = new DocumentTypePolicyEnum(KewApiConstants.NOTIFY_ON_SAVE_POLICY);
    
    /**
     * The Document Status Policy determines whether the KEW Route Status or the Application Document Status (or both) 
     * are to be used for a specific document type.
     */
    public static final DocumentTypePolicyEnum DOCUMENT_STATUS_POLICY = new DocumentTypePolicyEnum(KewApiConstants.DOCUMENT_STATUS_POLICY);

    /**
     * This document type policy allows us to configure if the "Perform Post Processor Logic" for the super user action on action requests is displayed.  
     * KULRICE-3584
     */
    public static final DocumentTypePolicyEnum ALLOW_SU_POSTPROCESSOR_OVERRIDE_POLICY = new DocumentTypePolicyEnum(KewApiConstants.ALLOW_SU_POSTPROCESSOR_OVERRIDE_POLICY);

    public static final DocumentTypePolicyEnum FAIL_ON_INACTIVE_GROUP = new DocumentTypePolicyEnum(KewApiConstants.FAIL_ON_INACTIVE_GROUP_POLICY);

    public static final DocumentTypePolicyEnum ENROUTE_ERROR_SUPPRESSION = new DocumentTypePolicyEnum(KewApiConstants.ENROUTE_ERROR_SUPPRESSION_POLICY);

    public static final DocumentTypePolicyEnum REGENERATE_ACTION_REQUESTS_ON_CHANGE = new DocumentTypePolicyEnum(KewApiConstants.REGENERATE_ACTION_REQUESTS_ON_CHANGE_POLICY);

    /**
     * Governs whether FYIs should be sent on *pending* (not completed) action requests when returning to a previous node
     * @since 2.1
     * @see https://jira.kuali.org/browse/KULRICE-5931
     */
    public static final DocumentTypePolicyEnum NOTIFY_PENDING_ON_RETURN = new DocumentTypePolicyEnum(KewApiConstants.NOTIFY_PENDING_ON_RETURN_POLICY);
    /**
     * Governs whether FYIs should be sent on *completed* (not pending) action requests when returning to a previous node (for use with Recall)
     * @since 2.1
     * @see https://jira.kuali.org/browse/KULRICE-5931
     */
    public static final DocumentTypePolicyEnum NOTIFY_COMPLETED_ON_RETURN = new DocumentTypePolicyEnum(KewApiConstants.NOTIFY_COMPLETED_ON_RETURN_POLICY);
    /**
     * Specifies additional recipients of Recall notifications.  This configuration is supplied as an additional recipients element
     * in the document policy element, which conforms to the Rule:ResponsibilityIdentifiers group schema.
     * @since 2.1
     * @see https://jira.kuali.org/browse/KULRICE-5931
     */
    public static final DocumentTypePolicyEnum RECALL_NOTIFICATION = new DocumentTypePolicyEnum(KewApiConstants.RECALL_NOTIFICATION_POLICY);

    private final String name;

    public DocumentTypePolicyEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "[DocumentTypePolicyEnum: name=" + name + "]";
    }

    public static DocumentTypePolicyEnum lookup(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Document type policy name must be non-null");
        }
        if (DISAPPROVE.name.equalsIgnoreCase(name)) {
            return DISAPPROVE;
        } else if (DEFAULT_APPROVE.name.equalsIgnoreCase(name)) {
            return DEFAULT_APPROVE;
        } else if (ALLOW_UNREQUESTED_ACTION.name.equalsIgnoreCase(name)) {
            return ALLOW_UNREQUESTED_ACTION;
        } else if (INITIATOR_MUST_ROUTE.name.equalsIgnoreCase(name)) {
            return INITIATOR_MUST_ROUTE;
        } else if (INITIATOR_MUST_SAVE.name.equalsIgnoreCase(name)) {
            return INITIATOR_MUST_SAVE;
        } else if (INITIATOR_MUST_BLANKET_APPROVE.name.equalsIgnoreCase(name)) {
            return INITIATOR_MUST_BLANKET_APPROVE;
        } else if (INITIATOR_MUST_CANCEL.name.equalsIgnoreCase(name)) {
            return INITIATOR_MUST_CANCEL;
        } else if (LOOK_FUTURE.name.equalsIgnoreCase(name)) {
            return LOOK_FUTURE;
        } else if (SEND_NOTIFICATION_ON_SU_APPROVE.name.equalsIgnoreCase(name)) {
        	return SEND_NOTIFICATION_ON_SU_APPROVE;
        } else if (SUPPORTS_QUICK_INITIATE.name.equalsIgnoreCase(name)) {
        	return SUPPORTS_QUICK_INITIATE;
        } else if (NOTIFY_ON_SAVE.name.equalsIgnoreCase(name)) {
        	return NOTIFY_ON_SAVE;
        } else if (USE_KEW_SUPERUSER_DOCHANDLER.name.equalsIgnoreCase(name)) {
            return USE_KEW_SUPERUSER_DOCHANDLER;
        } else if (DOCUMENT_STATUS_POLICY.name.equalsIgnoreCase(name)) {
        	return DOCUMENT_STATUS_POLICY;
        }else if (ALLOW_SU_POSTPROCESSOR_OVERRIDE_POLICY.name.equalsIgnoreCase(name)) {
        	return ALLOW_SU_POSTPROCESSOR_OVERRIDE_POLICY;
        } else if (FAIL_ON_INACTIVE_GROUP.name.equalsIgnoreCase(name)) {
            return FAIL_ON_INACTIVE_GROUP;
        } else if (ENROUTE_ERROR_SUPPRESSION.name.equalsIgnoreCase(name)) {
            return ENROUTE_ERROR_SUPPRESSION;
        } else if(REGENERATE_ACTION_REQUESTS_ON_CHANGE.name.equalsIgnoreCase(name)) {
            return REGENERATE_ACTION_REQUESTS_ON_CHANGE;
        } else if(NOTIFY_PENDING_ON_RETURN.name.equalsIgnoreCase(name)) {
            return NOTIFY_PENDING_ON_RETURN;
        } else if(NOTIFY_COMPLETED_ON_RETURN.name.equalsIgnoreCase(name)) {
            return NOTIFY_COMPLETED_ON_RETURN;
        } else if(RECALL_NOTIFICATION.name.equalsIgnoreCase(name)) {
            return RECALL_NOTIFICATION;
        } else {
            throw new IllegalArgumentException("Invalid Document type policy: '" + name + "'");
        }
    }
}
