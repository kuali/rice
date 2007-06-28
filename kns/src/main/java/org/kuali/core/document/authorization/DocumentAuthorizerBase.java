/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.document.authorization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.DocumentInitiationAuthorizationException;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.ValidActionsVO;

/**
 * DocumentAuthorizer containing common, reusable document-level authorization code.
 */
public class DocumentAuthorizerBase implements DocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(DocumentAuthorizerBase.class);

    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getEditMode(Document d, UniversalUser u) {
        Map editModeMap = new HashMap();
        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = d.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(d, u)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
        }

        editModeMap.put(editMode, "TRUE");

        return editModeMap;
    }


    /**
     * Individual document families will need to reimplement this according to their own needs; this version should be good enough
     * to be usable during initial development.
     *
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPersonUserIdentifier() + "'");

        DocumentActionFlags flags = new DocumentActionFlags(); // all flags default to false

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        boolean hasInitiateAuthorization = hasInitiateAuthorization(document, user);

        flags.setCanClose(true); // can always close a document

        // if a document is canceled, everything other than close should be set to false
        // if a document is NOT canceled, then we want to process the rest
        if (!workflowDocument.stateIsCanceled()) {
            flags.setCanReload(!workflowDocument.stateIsInitiated());

            flags.setCanBlanketApprove(workflowDocument.isBlanketApproveCapable());

            // The only exception to the supervisor user canSupervise is when the supervisor
            // user is also the initiator, and does NOT have an approval request. In other words if they
            // are the document initiator, and its still in Initiated or Saved phase, they cant have access
            // to the supervisor buttons. If they're the initiator, but for some reason they are also
            // approving the document, then they can have the supervisor button & functions.
            boolean canSuperviseAsInitiator = !(hasInitiateAuthorization && !workflowDocument.isApprovalRequested());
            flags.setCanSupervise(user.isSupervisorUser() && canSuperviseAsInitiator);

            // default normal documents to be unable to copy
            flags.setCanCopy(false);

            if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
                ValidActionsVO validActions = workflowDocument.getRouteHeader().getValidActions();
                flags.setCanCancel(hasInitiateAuthorization || validActions.contains(EdenConstants.ACTION_TAKEN_CANCELED_CD));

                flags.setCanSave(hasInitiateAuthorization || validActions.contains(EdenConstants.ACTION_TAKEN_SAVED_CD));

                flags.setCanRoute(hasInitiateAuthorization || validActions.contains(EdenConstants.ACTION_TAKEN_ROUTED_CD));

                flags.setCanPerformRouteReport(workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved());

                flags.setCanAcknowledge(workflowDocument.isAcknowledgeRequested());
                flags.setCanFYI(workflowDocument.isFYIRequested());

                flags.setCanAdHocRoute(flags.getCanSave() || flags.getCanRoute());
            }
            else if (workflowDocument.stateIsEnroute()) {
                flags.setCanApprove(workflowDocument.isApprovalRequested());

                flags.setCanDisapprove(workflowDocument.isApprovalRequested());

                flags.setCanAcknowledge(workflowDocument.isAcknowledgeRequested());
                flags.setCanFYI(workflowDocument.isFYIRequested());

                flags.setCanAdHocRoute(workflowDocument.isApprovalRequested() || workflowDocument.isAcknowledgeRequested());
            }
            else if (workflowDocument.stateIsApproved() || workflowDocument.stateIsFinal() || workflowDocument.stateIsDisapproved()) {
                flags.setCanAcknowledge(workflowDocument.isAcknowledgeRequested());
                flags.setCanFYI(workflowDocument.isFYIRequested());

                flags.setCanAdHocRoute(false);
            }
            else if (workflowDocument.stateIsException()) {
                flags.setCanCancel(user.isWorkflowExceptionUser());
                flags.setCanApprove(user.isWorkflowExceptionUser());
                flags.setCanDisapprove(user.isWorkflowExceptionUser());

                flags.setCanAdHocRoute(false);
            }
        }

        setAnnotateFlag(flags);

        return flags;
    }

    /**
     * Helper method to set the annotate flag based on other workflow tags
     * @param flags
     */
    public void setAnnotateFlag(DocumentActionFlags flags) {
        boolean canWorkflow = flags.getCanSave() || flags.getCanRoute() || flags.getCanCancel() || flags.getCanBlanketApprove() || flags.getCanApprove() || flags.getCanDisapprove() || flags.getCanAcknowledge() || flags.getCanAdHocRoute();
        flags.setCanAnnotate(canWorkflow);
    }

    /**
     * DocumentTypeAuthorizationException can be extended to customize the initiate error message
     * @see org.kuali.core.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.core.bo.user.KualiUser)
     */
    public void canInitiate(String documentTypeName, UniversalUser user) {
        if (! KNSServiceLocator.getAuthorizationService().isAuthorized(user, "initiate", documentTypeName)) {
            // build authorized workgroup list for error message
            Set authorizedWorkgroups = KNSServiceLocator.getAuthorizationService().getAuthorizedWorkgroups("initiate", documentTypeName);
            String workgroupList = StringUtils.join(authorizedWorkgroups.toArray(), ",");
            throw new DocumentInitiationAuthorizationException(new String[] {workgroupList,documentTypeName});
        }
    }

    /**
     * Default implementation here is if a user cannot initiate a document they cannot copy one.
     * @see org.kuali.core.authorization.DocumentAuthorizer#canCopy(java.lang.String, org.kuali.core.bo.user.KualiUser)
     */
    public boolean canCopy(String documentTypeName, UniversalUser user) {
        return KNSServiceLocator.getAuthorizationService().isAuthorized(user, "initiate", documentTypeName);
    }

    /**
     * Determines whether the given user should have initiate permissions on the given document.
     * @param document - current document
     * @param user - current user
     * @return boolean (true if they should have permissions)
     */
    public boolean hasInitiateAuthorization(Document document, UniversalUser user) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return workflowDocument.getInitiatorNetworkId().equalsIgnoreCase(user.getPersonUserIdentifier());
    }

}