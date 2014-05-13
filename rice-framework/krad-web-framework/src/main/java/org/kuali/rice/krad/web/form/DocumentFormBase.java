/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.web.form;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.bind.RequestAccessible;


import java.util.ArrayList;
import java.util.List;

/**
 * Base form for all <code>DocumentView</code> screens
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentFormBase extends UifFormBase {
	private static final long serialVersionUID = 2190268505427404480L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentFormBase.class);

	private String annotation = "";

    @RequestAccessible
	private String command;

    @RequestAccessible
	private String docId;

    @RequestAccessible
	private String docTypeName;

	protected Document document;

    private List<ActionRequest> actionRequests;
    private List<String> selectedActionRequests;
    private String superUserAnnotation;


    public DocumentFormBase() {
	    super();

	    instantiateDocument();
	}

	public String getAnnotation() {
		return this.annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getDocTypeName() {
        if(this.docTypeName == null && !this.getDefaultDocumentTypeName().isEmpty())
        {
            return this.getDefaultDocumentTypeName();
        }
		return this.docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

	public String getCommand() {
		return this.command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getDocId() {
		return this.docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

    protected String getDefaultDocumentTypeName() {
        return "";
    }

    protected void instantiateDocument() {
        if (document == null && StringUtils.isNotBlank(getDefaultDocumentTypeName())) {
            Class<? extends Document> documentClass = KRADServiceLocatorWeb.getDataDictionaryService()
                    .getValidDocumentClassByTypeName(getDefaultDocumentTypeName());
            try {
                Document newDocument = documentClass.newInstance();
                setDocument(newDocument);
            } catch (Exception e) {
                LOG.error("Unable to instantiate document class " + documentClass + " document type "
                        + getDefaultDocumentTypeName());
                throw new RuntimeException("Unable to instantiate document class " + documentClass + " document type "
                        + getDefaultDocumentTypeName(),e);
            }
        }
    }

	/**
	 * Retrieves the principal name (network id) for the document's initiator
	 *
	 * @return String initiator name
	 */
	public String getDocumentInitiatorNetworkId() {
		String initiatorNetworkId = "";
		if (getWorkflowDocument() != null) {
			String initiatorPrincipalId = getWorkflowDocument().getInitiatorPrincipalId();
			Person initiator = KimApiServiceLocator.getPersonService().getPerson(initiatorPrincipalId);
			if (initiator != null) {
				initiatorNetworkId = initiator.getPrincipalName();
			}
		}

		return initiatorNetworkId;
	}

	/**
	 * Retrieves the create date for the forms document and formats for
	 * presentation
	 *
	 * @return String formatted document create date
	 */
    public String getDocumentCreateDate() {
        String createDateStr = "";
        if (getWorkflowDocument() != null && getWorkflowDocument().getDateCreated() != null) {
            createDateStr = CoreApiServiceLocator.getDateTimeService().toString(
                    getWorkflowDocument().getDateCreated().toDate(), "hh:mm a MM/dd/yyyy");
        }

        return createDateStr;
    }

	/**
	 * Retrieves the <code>WorkflowDocument</code> instance from the forms
	 * document instance
	 *
	 * @return WorkflowDocument for the forms document
	 */
	public WorkflowDocument getWorkflowDocument() {
		return getDocument().getDocumentHeader().getWorkflowDocument();
	}

    public List<ActionRequest> getActionRequests() {
        return actionRequests;
    }


    public List<ActionRequest> getActionRequestsRequiringApproval() {
        List<ActionRequest> actionRequests = getActionRequests();
        List<ActionRequest> actionRequestsApprove = new ArrayList<ActionRequest>();;

        for (ActionRequest actionRequest: actionRequests) {
            if  ((StringUtils.equals(actionRequest.getActionRequested().getCode(), ActionRequestType.APPROVE.getCode())) ||
                    (StringUtils.equals(actionRequest.getActionRequested().getCode(), ActionRequestType.COMPLETE.getCode()))) {
                actionRequestsApprove.add(actionRequest);
            }
        }

        return actionRequestsApprove;
    }


    public boolean isSuperUserActionAvaliable() {
        List<ActionRequest> actionRequests = getActionRequestsRequiringApproval();
        boolean hasSingleActionToTake = false;
        boolean canSuperUserApprove = false;
        boolean canSuperUserDisapprove = false;

        hasSingleActionToTake =  ( isSuperUserApproveSingleActionRequestAuthorized() &&
                isStateAllowsApproveSingleActionRequest() &&
                !actionRequests.isEmpty());

        if (!hasSingleActionToTake) {
            canSuperUserApprove = (isSuperUserApproveDocumentAuthorized() && isStateAllowsApproveOrDisapprove());
        }

        if (!canSuperUserApprove) {
            canSuperUserDisapprove = (isSuperUserDisapproveDocumentAuthorized() && isStateAllowsApproveOrDisapprove());
        }

        return (hasSingleActionToTake || canSuperUserApprove || canSuperUserDisapprove) ;
    }

    public boolean isSuperUserApproveSingleActionRequestAuthorized() {
        String principalId =  GlobalVariables.getUserSession().getPrincipalId();
        String docId = this.getDocId();
        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(docTypeName);
        String docTypeId = null;

        if (documentType != null) {
            docTypeId = documentType.getId();
        }

        if ( KewApiServiceLocator.getDocumentTypeService().isSuperUserForDocumentTypeId(principalId, docTypeId) ) {
            return true;
        }
        List<RouteNodeInstance> routeNodeInstances= KewApiServiceLocator.getWorkflowDocumentService().getRouteNodeInstances(docId);
        String documentStatus =  KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(docId).getCode();

        return KewApiServiceLocator.getDocumentTypeService().canSuperUserApproveSingleActionRequest(
                principalId, getDocTypeName(), routeNodeInstances, documentStatus);
    }

    public boolean isSuperUserApproveDocumentAuthorized() {
        String principalId =  GlobalVariables.getUserSession().getPrincipalId();
        String docId = this.getDocId();
        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(docTypeName);
        String docTypeId = null;

        if (documentType != null) {
            docTypeId = documentType.getId();
        }

        if ( KewApiServiceLocator.getDocumentTypeService().isSuperUserForDocumentTypeId(principalId, docTypeId) ) {
            return true;
        }
        List<RouteNodeInstance> routeNodeInstances= KewApiServiceLocator.getWorkflowDocumentService().getRouteNodeInstances(docId);
        String documentStatus =  KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(docId).getCode();

        return KewApiServiceLocator.getDocumentTypeService().canSuperUserApproveDocument(
                principalId, this.getDocTypeName(), routeNodeInstances, documentStatus);
    }

    public boolean isSuperUserDisapproveDocumentAuthorized() {
        String principalId =  GlobalVariables.getUserSession().getPrincipalId();
        String docId = this.getDocId();
        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(docTypeName);
        String docTypeId = null;

        if (documentType != null) {
            docTypeId = documentType.getId();
        }

        if ( KewApiServiceLocator.getDocumentTypeService().isSuperUserForDocumentTypeId(principalId, docTypeId) ) {
            return true;
        }
        List<RouteNodeInstance> routeNodeInstances= KewApiServiceLocator.getWorkflowDocumentService().getRouteNodeInstances(docId);
        String documentStatus =  KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(docId).getCode();

        return KewApiServiceLocator.getDocumentTypeService().canSuperUserDisapproveDocument(
                principalId, this.getDocTypeName(), routeNodeInstances, documentStatus);
    }

    public boolean isSuperUserAuthorized() {
        String docId = this.getDocId();

        if (StringUtils.isBlank(docId) || docTypeName == null) {
            return false;
        }

        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(docTypeName);
        String docTypeId = null;

        if (documentType != null) {
            docTypeId = documentType.getId();
        }
        String principalId =  GlobalVariables.getUserSession().getPrincipalId();

        if ( KewApiServiceLocator.getDocumentTypeService().isSuperUserForDocumentTypeId(principalId, docTypeId) ) {
            return true;
        }
        List<RouteNodeInstance> routeNodeInstances= KewApiServiceLocator.getWorkflowDocumentService().getRouteNodeInstances(
                docId);
        String documentStatus =  KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(docId).getCode();

        return ((KewApiServiceLocator.getDocumentTypeService().canSuperUserApproveSingleActionRequest(
                principalId, this.getDocTypeName(), routeNodeInstances, documentStatus)) ||
                (KewApiServiceLocator.getDocumentTypeService().canSuperUserApproveDocument(
                        principalId, this.getDocTypeName(), routeNodeInstances, documentStatus)) ||
                (KewApiServiceLocator.getDocumentTypeService().canSuperUserDisapproveDocument (
                        principalId, this.getDocTypeName(), routeNodeInstances, documentStatus))) ;
    }

    public boolean isStateAllowsApproveOrDisapprove() {
        if(this.getDocument().getDocumentHeader().hasWorkflowDocument()) {
            DocumentStatus status = null;
            WorkflowDocument document = WorkflowDocumentFactory.loadDocument(
                    GlobalVariables.getUserSession().getPrincipalId(),
                    this.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentId());
            if (document != null) {
                status = document.getStatus();
            } else {
                status = this.getDocument().getDocumentHeader().getWorkflowDocument().getStatus();
            }

            return !(isStateProcessedOrDisapproved(status) ||
                    isStateInitiatedFinalCancelled(status) ||
                    StringUtils.equals(status.getCode(), DocumentStatus.SAVED.getCode()));
        } else {
            return false;
        }
    }

    public boolean isStateAllowsApproveSingleActionRequest() {
        if(this.getDocument().getDocumentHeader().hasWorkflowDocument()) {
            DocumentStatus status = null;
            WorkflowDocument document = WorkflowDocumentFactory.loadDocument(GlobalVariables.getUserSession().getPrincipalId(),
                    this.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentId());
            if (document != null) {
                status = document.getStatus();
            } else {
                status = this.getDocument().getDocumentHeader().getWorkflowDocument().getStatus();
            }

            return !(isStateInitiatedFinalCancelled(status));
        } else {
            return false;
        }
    }

    public boolean isStateProcessedOrDisapproved(DocumentStatus status) {
        return (StringUtils.equals(status.getCode(), DocumentStatus.PROCESSED.getCode()) ||
                StringUtils.equals(status.getCode(), DocumentStatus.DISAPPROVED.getCode()));
    }

    public boolean isStateInitiatedFinalCancelled(DocumentStatus status) {
        return (StringUtils.equals(status.getCode(), DocumentStatus.INITIATED.getCode()) ||
                StringUtils.equals(status.getCode(), DocumentStatus.FINAL.getCode()) ||
                StringUtils.equals(status.getCode(), DocumentStatus.CANCELED.getCode()));
    }


}
