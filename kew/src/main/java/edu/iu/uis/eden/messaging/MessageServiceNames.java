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
package edu.iu.uis.eden.messaging;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.bus.services.KSBServiceLocator;

import edu.iu.uis.eden.actionrequests.DocumentRequeuerService;
import edu.iu.uis.eden.actions.asyncservices.ActionInvocationService;
import edu.iu.uis.eden.actions.asyncservices.BlanketApproveProcessorService;
import edu.iu.uis.eden.actions.asyncservices.MoveDocumentService;
import edu.iu.uis.eden.docsearch.SearchableAttributeProcessingService;
import edu.iu.uis.eden.mail.ActionListImmediateEmailReminderService;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.RuleCacheProcessor;

/**
 * Utility class for accessing names of common asynchronous services.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MessageServiceNames {

	public static final String DOCUMENT_ROUTING_SERVICE = "DocumentRoutingService";

	public static final String ACTION_LIST_IMMEDIATE_REMINDER_SERVICE = "ImmediateEmailService";

	public static final String BLANKET_APPROVE_PROCESSING_SERVICE = "BlanketApproveProcessorService";

	public static final String SEARCHABLE_ATTRIBUTE_PROCESSING_SERVICE = "SearchableAttributeProcessorService";

	public static final String DOCUMENT_REQUEUE_PROCESSING_SERVICE = "DocumentRequeueProcessorService";

	public static final String WORKGROUP_MEMBERSHIP_CHANGE_SERVICE = "WorkgroupMembershipChangeService";

	public static final String SERVICE_REMOVER_SERVICE = "RemoteClassRemoverService";

	public static final String RULE_CACHE_PROCESSOR_SERVICE = "RuleCacheProcessorService";

	public static final String ROLE_POKER = "RolePokerProcessorService";

	public static final String MOVE_DOCUMENT_PROCESSOR = "MoveDocumentProcessor";

	public static final String ACTION_INVOCATION_PROCESSOR = "ActionInvocationProcessor";

	private static QName getQName(String baseServiceName, DocumentRouteHeaderValue document) {
		if (document != null) {
			return new QName(document.getDocumentType().getMessageEntity(), baseServiceName);
		}
		return new QName(baseServiceName);
	}

	private static QName getQName(String baseServiceName, String messageEntity) {
		if (!StringUtils.isEmpty(messageEntity)) {
			return new QName(messageEntity, baseServiceName);
		}
		return new QName(baseServiceName);
	}

	public static KEWXMLService getRouteDocumentMessageService(DocumentRouteHeaderValue document) {
		return (KEWXMLService) getServiceAsynchronously(getQName(DOCUMENT_ROUTING_SERVICE, document), document);
	}

	public static MoveDocumentService getMoveDocumentProcessorService(DocumentRouteHeaderValue document) {
		return (MoveDocumentService) getServiceAsynchronously(getQName(MOVE_DOCUMENT_PROCESSOR, document), document);
	}

	public static ActionInvocationService getActionInvocationProcessorService(DocumentRouteHeaderValue document) {
		return (ActionInvocationService) getServiceAsynchronously(getQName(ACTION_INVOCATION_PROCESSOR, document), document);
	}

	public static BlanketApproveProcessorService getBlanketApproveProcessorService(DocumentRouteHeaderValue document) {
		return (BlanketApproveProcessorService) getServiceAsynchronously(getQName(BLANKET_APPROVE_PROCESSING_SERVICE, document), document);
	}

	public static ActionListImmediateEmailReminderService getImmediateEmailService() {
		return (ActionListImmediateEmailReminderService) getServiceAsynchronously(getQName(ACTION_LIST_IMMEDIATE_REMINDER_SERVICE, (DocumentRouteHeaderValue) null), (Long)null);
	}

	public static SearchableAttributeProcessingService getSearchableAttributeService(DocumentRouteHeaderValue document) {
		return (SearchableAttributeProcessingService) getServiceAsynchronously(getQName(SEARCHABLE_ATTRIBUTE_PROCESSING_SERVICE, document), document);
	}

	public static RuleCacheProcessor getRuleCacheProcessor() {
		return (RuleCacheProcessor) getServiceAsynchronously(new QName(MessageServiceNames.RULE_CACHE_PROCESSOR_SERVICE), (Long)null);
	}

	public static DocumentRequeuerService getDocumentRequeuerService(String messageEntity, Long documentId, long waitTime) {
		QName serviceName = getQName(DOCUMENT_REQUEUE_PROCESSING_SERVICE, messageEntity);
		if (waitTime > 0) {
			return (DocumentRequeuerService) getDelayedServiceAsynchronously(serviceName, documentId, waitTime);
		}
		return (DocumentRequeuerService) getServiceAsynchronously(serviceName, documentId);
	}

	public static Object getServiceAsynchronously(QName serviceName, DocumentRouteHeaderValue document) {
		return getServiceAsynchronously(serviceName, getDocId(document));
	}

	public static Object getServiceAsynchronously(QName serviceName, Long documentId) {
		return KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, null, null, (documentId == null ? null : documentId.toString()), null);
	}

	public static Object getDelayedServiceAsynchronously(QName serviceName, DocumentRouteHeaderValue document, long waitTime) {
		return getDelayedServiceAsynchronously(serviceName, getDocId(document), waitTime);
	}

	public static Object getDelayedServiceAsynchronously(QName serviceName, Long documentId, long waitTime) {
		return KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, null, (documentId == null ? null : documentId.toString()), null, waitTime);
	}

	private static Long getDocId(DocumentRouteHeaderValue document) {
		return (document == null ? null : document.getRouteHeaderId());
	}

}
