/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.messaging;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.actionrequest.service.DocumentRequeuerService;
import org.kuali.rice.kew.api.action.BlanketApprovalOrchestrationQueue;
import org.kuali.rice.kew.api.action.ActionInvocationQueue;
import org.kuali.rice.kew.actions.asyncservices.MoveDocumentService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.DocumentProcessingQueue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;

/**
 * Utility class for accessing names of common asynchronous services.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageServiceNames {

	public static final QName DOCUMENT_PROCESSING_QUEUE = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "documentProcessingQueueSoap");

	public static final String ACTION_LIST_IMMEDIATE_REMINDER_SERVICE = "ImmediateEmailService";

	public static final QName BLANKET_APPROVE_PROCESSING_SERVICE = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "BlanketApproveProcessorService");

	public static final QName DOCUMENT_REQUEUE_PROCESSING_SERVICE = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "DocumentRequeueProcessorService");

	public static final QName ROLE_POKER = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "RolePokerProcessorService");

	public static final String MOVE_DOCUMENT_PROCESSOR = "MoveDocumentProcessor";

    public static final QName ACTION_INVOCATION_QUEUE = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "actionInvocationQueueSoap");

	private static QName getQName(String baseServiceName, DocumentRouteHeaderValue document) {
		if (document != null) {
			return new QName(document.getDocumentType().getApplicationId(), baseServiceName);
		}
		return new QName(baseServiceName);
	}

	private static QName getQName(String baseServiceName, String applicationId) {
		if (!StringUtils.isEmpty(applicationId)) {
			return new QName(applicationId, baseServiceName);
		}
		return new QName(baseServiceName);
	}

    public static DocumentProcessingQueue getDocumentProcessingQueue(DocumentRouteHeaderValue document) {
        return (DocumentProcessingQueue)getServiceAsynchronously(DOCUMENT_PROCESSING_QUEUE, document);
    }

	public static MoveDocumentService getMoveDocumentProcessorService(DocumentRouteHeaderValue document) {
		return (MoveDocumentService) getServiceAsynchronously(getQName(MOVE_DOCUMENT_PROCESSOR, document), document);
	}

	public static ActionInvocationQueue getActionInvocationProcessorService(DocumentRouteHeaderValue document) {
		return (ActionInvocationQueue) getServiceAsynchronously(ACTION_INVOCATION_QUEUE, document);
	}

	public static BlanketApprovalOrchestrationQueue getBlanketApproveProcessorService(DocumentRouteHeaderValue document) {
		return (BlanketApprovalOrchestrationQueue) getServiceAsynchronously(BLANKET_APPROVE_PROCESSING_SERVICE, document);
	}
	
	public static DocumentRequeuerService getDocumentRequeuerService(String applicationId, String documentId, long waitTime) {
		if (waitTime > 0) {
			return (DocumentRequeuerService) getDelayedServiceAsynchronously(DOCUMENT_REQUEUE_PROCESSING_SERVICE, documentId, waitTime);
		}
		return (DocumentRequeuerService) getServiceAsynchronously(DOCUMENT_REQUEUE_PROCESSING_SERVICE, documentId, applicationId);
	}

	public static Object getServiceAsynchronously(QName serviceName, DocumentRouteHeaderValue document) {
        String applicationId = document.getDocumentType().getApplicationId();
		return getServiceAsynchronously(serviceName, getDocId(document), applicationId);
	}

	public static Object getServiceAsynchronously(QName serviceName, String documentId, String applicationId) {
		return KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, applicationId, null, null, (documentId == null ? null : documentId.toString()), null);
	}

	public static Object getDelayedServiceAsynchronously(QName serviceName, DocumentRouteHeaderValue document, long waitTime) {
		return getDelayedServiceAsynchronously(serviceName, getDocId(document), waitTime);
	}

	public static Object getDelayedServiceAsynchronously(QName serviceName, String documentId, long waitTime) {
		return KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, null, (documentId == null ? null : documentId.toString()), null, waitTime);
	}

	private static String getDocId(DocumentRouteHeaderValue document) {
		return (document == null ? null : document.getDocumentId());
	}

}
