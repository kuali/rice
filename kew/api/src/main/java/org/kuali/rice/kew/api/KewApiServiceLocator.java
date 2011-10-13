/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.api;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.Responsibility.ResponsibilityChangeProcessor;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.actionlist.ActionListService;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.extension.ExtensionRepositoryService;
import org.kuali.rice.kew.api.group.GroupMembershipChangeQueue;
import org.kuali.rice.kew.api.mail.ImmediateEmailReminderQueue;
import org.kuali.rice.kew.api.note.NoteService;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService;
import org.kuali.rice.kew.api.rule.RuleService;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;

import javax.xml.namespace.QName;

/**
 * A static service locator which aids in locating the various services that
 * form the Kuali Service Bus API.
 */
public class KewApiServiceLocator {

    // Rice 2.0 - TODO - should these be using the QName versions instead?  How else will they be fetched in "remote" mode?
	public static final String WORKFLOW_DOCUMENT_ACTIONS_SERVICE = "rice.kew.workflowDocumentActionsService";
	public static final String WORKFLOW_DOCUMENT_SERVICE = "rice.kew.workflowDocumentService";
    public static final String ACTION_LIST_SERVICE = "rice.kew.actionListService";
	public static final String DOCUMENT_TYPE_SERVICE = "rice.kew.documentTypeService";
	public static final String NOTE_SERVICE = "rice.kew.noteService";
    public static final String EXTENSION_REPOSITORY_SERVICE = "rice.kew.extensionRepositoryService";
    public static final String RULE_SERVICE = "rice.kew.ruleService";
    public static final String KEW_TYPE_REPOSITORY_SERVICE = "rice.kew.kewTypeRepositoryService";
    public static final String PEOPLE_FLOW_SERVICE = "rice.kew.peopleFlowService";

    public static final QName DOCUMENT_ATTRIBUTE_INDEXING_QUEUE_NAME = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "documentAttributeIndexingQueueSoap");
    public static final QName GROUP_MEMBERSHIP_CHANGE_QUEUE_NAME = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "groupMembershipChangeQueueSoap");
    public static final QName RULE_CACHE_PROCESSOR_QUEUE_NAME = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "ruleCacheProcessorSoap");
    public static final QName RULE_DELEGATION_CACHE_PROCESSOR_QUEUE_NAME = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "ruleDelegationCacheProcessorSoap");
    public static final QName IMMEDIATE_EMAIL_REMINDER_QUEUE = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "immediateEmailReminderQueueSoap");
    public static final QName RESPONSIBILITY_CHANGE_PROCESSOR_QUEUE_NAME = new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, "responsibilityChangeProcessorSoap");

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static WorkflowDocumentActionsService getWorkflowDocumentActionsService() {
        return getService(WORKFLOW_DOCUMENT_ACTIONS_SERVICE);
    }
    
    public static WorkflowDocumentService getWorkflowDocumentService() {
        return getService(WORKFLOW_DOCUMENT_SERVICE);
    }

    public static ActionListService getActionListService() {
        return getService(ACTION_LIST_SERVICE);
    }
    
    public static DocumentTypeService getDocumentTypeService() {
        return getService(DOCUMENT_TYPE_SERVICE);
    }
    
    public static NoteService getNoteService() {
    	return getService(NOTE_SERVICE);
    }

    public static RuleService getRuleService() {
    	return getService(RULE_SERVICE);
    }

    public static ExtensionRepositoryService getExtensionRepositoryService() {
        return getService(EXTENSION_REPOSITORY_SERVICE);
    }

    public static KewTypeRepositoryService getKewTypeRepositoryService() {
        return getService(KEW_TYPE_REPOSITORY_SERVICE);
    }

    public static PeopleFlowService getPeopleFlowService() {
        return getService(PEOPLE_FLOW_SERVICE);
    }

    public static DocumentAttributeIndexingQueue getDocumentAttributeIndexingQueue() {
        return getDocumentAttributeIndexingQueue(null);
    }

    public static DocumentAttributeIndexingQueue getDocumentAttributeIndexingQueue(String applicationId) {
        return (DocumentAttributeIndexingQueue)KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(DOCUMENT_ATTRIBUTE_INDEXING_QUEUE_NAME, applicationId);
    }
    
    public static GroupMembershipChangeQueue getGroupMembershipChangeQueue() {
        return (GroupMembershipChangeQueue)KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(GROUP_MEMBERSHIP_CHANGE_QUEUE_NAME);
    }

    public static ResponsibilityChangeProcessor getResponsibilityChangeProcessor() {
        return (ResponsibilityChangeProcessor)KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(RESPONSIBILITY_CHANGE_PROCESSOR_QUEUE_NAME);
    }
    
    public static ImmediateEmailReminderQueue getImmediateEmailReminderQueue() {
        return KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(IMMEDIATE_EMAIL_REMINDER_QUEUE);
    }
}
