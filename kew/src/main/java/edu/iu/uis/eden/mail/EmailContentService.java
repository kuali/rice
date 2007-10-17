/*
 * Copyright 2005-2007 The Kuali Foundation.
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
// Created on Jan 18, 2007

package edu.iu.uis.eden.mail;

import java.util.Collection;

import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.feedback.web.FeedbackForm;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Interface for generating email message content for various types of messages the system needs to send
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @see EmailContent
 */
public interface EmailContentService {
    public EmailContent generateImmediateReminder(WorkflowUser user, ActionItem actionItem, DocumentType documentType);
    public EmailContent generateDailyReminder(WorkflowUser user, Collection<ActionItem> actionItems);
    public EmailContent generateWeeklyReminder(WorkflowUser user, Collection<ActionItem> actionItems);
    public EmailContent generateFeedback(FeedbackForm form);
    
    /* these are more or less helper utilities and probably should live in some core helper class */
    public String getDocumentTypeEmailAddress(DocumentType documentType);
    public String getApplicationEmailAddress();
}