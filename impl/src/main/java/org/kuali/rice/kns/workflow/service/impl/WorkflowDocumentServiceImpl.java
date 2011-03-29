/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.workflow.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.exception.DocumentTypeNotFoundException;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.exception.UnknownDocumentIdException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class is the implementation of the WorkflowDocumentService, which makes use of Workflow.
 */
@Transactional
public class WorkflowDocumentServiceImpl implements WorkflowDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowDocumentServiceImpl.class);

    private KualiWorkflowInfo workflowInfoService;

    public boolean workflowDocumentExists(String documentHeaderId) {
        boolean exists = false;

        if (StringUtils.isBlank(documentHeaderId)) {
            throw new IllegalArgumentException("invalid (blank) documentHeaderId");
        }

        Long routeHeaderId = null;
        try {
            routeHeaderId = new Long(documentHeaderId);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("cannot convert '" + documentHeaderId + "' into a Long");
        }

        exists = workflowInfoService.routeHeaderExists(routeHeaderId);

        return exists;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#createWorkflowDocument(java.lang.String,
     *      org.kuali.rice.kew.user.WorkflowUser)
     */
    public KualiWorkflowDocument createWorkflowDocument(String documentTypeId, Person person) throws WorkflowException {
        String watchName = "createWorkflowDocument";
        StopWatch watch = new StopWatch();
        watch.start();
        if (LOG.isDebugEnabled()) {
            LOG.debug(watchName + ": started");
        }
        if (StringUtils.isBlank(documentTypeId)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeId");
        }
        if (person == null) {
            throw new IllegalArgumentException("invalid (null) person");
        }

        if (StringUtils.isBlank(person.getPrincipalName())) {
            throw new IllegalArgumentException("invalid (empty) PrincipalName");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating workflowDoc(" + documentTypeId + "," + person.getPrincipalName() + ")");
        }

        KualiWorkflowDocument document = new KualiWorkflowDocumentImpl(person.getPrincipalId(), documentTypeId);

        // workflow doesn't complain about invalid docTypes until the first call to getRouteHeaderId, but the rest of our code
        // assumes that you get the exception immediately upon trying to create a document of that invalid type
        //
        // and it throws the generic WorkflowException, apparently, instead of the more specific DocumentTypeNotFoundException,
        // so as long as I'm here I'll do that conversion as well
        try {
            document.getRouteHeaderId();
        }
        catch (WorkflowException e) {
            if (e.getMessage().contains("Could not locate the given document type name")) {
                throw new DocumentTypeNotFoundException("unknown document type '" + documentTypeId + "'");
            }
            throw e;
        }

        watch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug(watchName + ": " + watch.toString());	
        }

        return document;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#createWorkflowDocument(java.lang.Long,
     *      org.kuali.rice.kew.user.WorkflowUser)
     */
    public KualiWorkflowDocument createWorkflowDocument(Long documentHeaderId, Person user) throws WorkflowException {
        if (documentHeaderId == null) {
            throw new IllegalArgumentException("invalid (null) documentHeaderId");
        }
        if (user == null) {
            throw new IllegalArgumentException("invalid (null) workflowUser");
        }
        else if (StringUtils.isEmpty(user.getPrincipalName())) {
            throw new IllegalArgumentException("invalid (empty) workflowUser");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving document(" + documentHeaderId + "," + user.getPrincipalName() + ")");
        }

        KualiWorkflowDocument document = new KualiWorkflowDocumentImpl(user.getPrincipalId(), documentHeaderId);
        if (document.getRouteHeader() == null) {
            throw new UnknownDocumentIdException("unable to locate document with documentHeaderId '" + documentHeaderId + "'");
        }
        return document;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#acknowledge(org.kuali.rice.kew.rule.FlexDoc)
     */
    public void acknowledge(KualiWorkflowDocument workflowDocument, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("acknowleding document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
        }

        handleAdHocRouteRequests(workflowDocument, annotation, filterAdHocRecipients(adHocRecipients, new String[] { KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ }));
        workflowDocument.acknowledge(annotation);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#approve(org.kuali.rice.kew.rule.FlexDoc)
     */
    public void approve(KualiWorkflowDocument workflowDocument, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("approving document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
        }

        handleAdHocRouteRequests(workflowDocument, annotation, filterAdHocRecipients(adHocRecipients, new String[] { KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_APPROVE_REQ }));
        workflowDocument.approve(annotation);
    }


    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#superUserApprove(org.kuali.rice.kns.workflow.service.KualiWorkflowDocument,
     *      java.lang.String)
     */
    public void superUserApprove(KualiWorkflowDocument workflowDocument, String annotation) throws WorkflowException {
    	if ( LOG.isInfoEnabled() ) {
    		LOG.info("super user approve document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
    	}
        workflowDocument.superUserApprove(annotation);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#superUserCancel(org.kuali.rice.kns.workflow.service.KualiWorkflowDocument,
     *      java.lang.String)
     */
    public void superUserCancel(KualiWorkflowDocument workflowDocument, String annotation) throws WorkflowException {
        LOG.info("super user cancel document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
        workflowDocument.superUserCancel(annotation);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#superUserDisapprove(org.kuali.rice.kns.workflow.service.KualiWorkflowDocument,
     *      java.lang.String)
     */
    public void superUserDisapprove(KualiWorkflowDocument workflowDocument, String annotation) throws WorkflowException {
    	if ( LOG.isInfoEnabled() ) {
    		LOG.info("super user disapprove document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
    	}
        workflowDocument.superUserDisapprove(annotation);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#blanketApprove(org.kuali.rice.kew.rule.FlexDoc)
     */
    public void blanketApprove(KualiWorkflowDocument workflowDocument, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("blanket approving document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
        }

        handleAdHocRouteRequests(workflowDocument, annotation, filterAdHocRecipients(adHocRecipients, new String[] { KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ }));
        workflowDocument.blanketApprove(annotation);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#cancel(org.kuali.rice.kew.rule.FlexDoc)
     */
    public void cancel(KualiWorkflowDocument workflowDocument, String annotation) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("canceling document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
        }

        workflowDocument.cancel(annotation);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#clearFyi(org.kuali.rice.kew.rule.FlexDoc)
     */
    public void clearFyi(KualiWorkflowDocument workflowDocument, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("clearing FYI for document(" + workflowDocument.getRouteHeaderId() + ")");
        }

        handleAdHocRouteRequests(workflowDocument, "", filterAdHocRecipients(adHocRecipients, new String[] { KEWConstants.ACTION_REQUEST_FYI_REQ }));
        workflowDocument.fyi();
    }

    public void sendWorkflowNotification(KualiWorkflowDocument workflowDocument, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
    	sendWorkflowNotification(workflowDocument, annotation, adHocRecipients, null);
    }
    
    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#sendWorkflowNotification(org.kuali.rice.kns.workflow.service.KualiWorkflowDocument, java.lang.String, java.util.List)
     */
    public void sendWorkflowNotification(KualiWorkflowDocument workflowDocument, String annotation, List<AdHocRouteRecipient> adHocRecipients, String notificationLabel) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("sending FYI for document(" + workflowDocument.getRouteHeaderId() + ")");
        }

        handleAdHocRouteRequests(workflowDocument, annotation, adHocRecipients, notificationLabel);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#disapprove(org.kuali.rice.kew.rule.FlexDoc)
     */
    public void disapprove(KualiWorkflowDocument workflowDocument, String annotation) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("disapproving document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
        }

        workflowDocument.disapprove(annotation);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#route(org.kuali.rice.kew.rule.FlexDoc)
     */
    public void route(KualiWorkflowDocument workflowDocument, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("routing document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
        }

        handleAdHocRouteRequests(workflowDocument, annotation, filterAdHocRecipients(adHocRecipients, new String[] { KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_APPROVE_REQ }));
        workflowDocument.routeDocument(annotation);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#save(org.kuali.rice.kns.workflow.service.KualiWorkflowDocument, java.lang.String)
     */
    public void save(KualiWorkflowDocument workflowDocument, String annotation) throws WorkflowException {
        if (workflowDocument.isStandardSaveAllowed()) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("saving document(" + workflowDocument.getRouteHeaderId() + ",'" + annotation + "')");
        }

        workflowDocument.saveDocument(annotation);
    }
        else {
            this.saveRoutingData(workflowDocument);
        }
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#saveRoutingData(org.kuali.rice.kns.workflow.service.KualiWorkflowDocument)
     */
    public void saveRoutingData(KualiWorkflowDocument workflowDocument) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("saving document(" + workflowDocument.getRouteHeaderId() + ")");
        }

        workflowDocument.saveRoutingData();
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.WorkflowDocumentService#getCurrentRouteLevelName(org.kuali.rice.kns.workflow.service.KualiWorkflowDocument)
     */
    public String getCurrentRouteLevelName(KualiWorkflowDocument workflowDocument) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getting current route level name for document(" + workflowDocument.getRouteHeaderId());
        }
//        return KEWServiceLocator.getRouteHeaderService().getRouteHeader(workflowDocument.getRouteHeaderId()).getCurrentRouteLevelName();
        KualiWorkflowDocument freshCopyWorkflowDoc = createWorkflowDocument(workflowDocument.getRouteHeaderId(), GlobalVariables.getUserSession().getPerson());
        return freshCopyWorkflowDoc.getCurrentRouteNodeNames();
    }

    private void handleAdHocRouteRequests(KualiWorkflowDocument workflowDocument, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException {
    	handleAdHocRouteRequests(workflowDocument, annotation, adHocRecipients, null);
    }
    
    /**
     * Convenience method for generating ad hoc requests for a given document
     *
     * @param flexDoc
     * @param annotation
     * @param adHocRecipients
     * @throws InvalidActionTakenException
     * @throws InvalidRouteTypeException
     * @throws InvalidActionRequestException
     */
    private void handleAdHocRouteRequests(KualiWorkflowDocument workflowDocument, String annotation, List<AdHocRouteRecipient> adHocRecipients, String notificationLabel) throws WorkflowException {

        if (adHocRecipients != null && adHocRecipients.size() > 0) {
            String currentNode = null;
            String[] currentNodes = workflowDocument.getNodeNames();
            if (currentNodes.length == 0) {
                WorkflowInfo workflowInfo = new WorkflowInfo();
                RouteNodeInstanceDTO[] nodes = workflowInfo.getTerminalNodeInstances(workflowDocument.getRouteHeaderId());
                currentNodes = new String[nodes.length];
                for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
                    currentNodes[nodeIndex] = nodes[nodeIndex].getName();
                }
            }
            // for now just pick a node and go with it...
            for (int i = 0; i < currentNodes.length; i++) {
                currentNode = currentNodes[i];
            }

            
            for (AdHocRouteRecipient recipient : adHocRecipients) {
                if (StringUtils.isNotEmpty(recipient.getId())) {
                	String newAnnotation = annotation;
                	if ( StringUtils.isBlank( annotation ) ) {
                		try {
                			String message = KNSServiceLocator.getKualiConfigurationService().getPropertyString(RiceKeyConstants.MESSAGE_ADHOC_ANNOTATION);
                			newAnnotation = MessageFormat.format(message, GlobalVariables.getUserSession().getPrincipalName() );
                		} catch ( Exception ex ) {
                			LOG.warn("Unable to set annotation", ex );
                		}
                	}
                    if (AdHocRouteRecipient.PERSON_TYPE.equals(recipient.getType())) {
                        // TODO make the 1 a constant
                    	KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(recipient.getId());
                		if (principal == null) {
                			throw new RiceRuntimeException("Could not locate principal with name '" + recipient.getId() + "'");
                		}
                        workflowDocument.adHocRouteDocumentToPrincipal(recipient.getActionRequested(), currentNode, newAnnotation, principal.getPrincipalId(), "", true, notificationLabel);
                    }
                    else {
                    	Group group = KIMServiceLocator.getIdentityManagementService().getGroup(recipient.getId());
                		if (group == null) {
                			throw new RiceRuntimeException("Could not locate group with id '" + recipient.getId() + "'");
                		}
                    	workflowDocument.adHocRouteDocumentToGroup(recipient.getActionRequested(), currentNode, newAnnotation, group.getGroupId() , "", true, notificationLabel);
                    }
                }
            }
        }
    }

    /**
     * Convenience method to filter out any ad hoc recipients that should not be allowed given the action requested of the user that
     * is taking action on the document
     *
     * @param adHocRecipients
     */
    private List<AdHocRouteRecipient> filterAdHocRecipients(List<AdHocRouteRecipient> adHocRecipients, String[] validTypes) {
        // now filter out any but ack or fyi from the ad hoc list
        List<AdHocRouteRecipient> realAdHocRecipients = new ArrayList<AdHocRouteRecipient>();
        if (adHocRecipients != null) {
            for (AdHocRouteRecipient proposedRecipient : adHocRecipients) {
                if (StringUtils.isNotBlank(proposedRecipient.getActionRequested())) {
                    for (int i = 0; i < validTypes.length; i++) {
                        if (validTypes[i].equals(proposedRecipient.getActionRequested())) {
                            realAdHocRecipients.add(proposedRecipient);
                        }
                    }
                }
            }
        }
        return realAdHocRecipients;
    }


    public void setWorkflowInfoService(KualiWorkflowInfo workflowInfoService) {
        this.workflowInfoService = workflowInfoService;
    }

    public KualiWorkflowInfo getWorkflowInfoService() {
        return workflowInfoService;
    }
}
