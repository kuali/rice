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
package edu.iu.uis.eden.mail;

import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * A service which allows for the sending of email reminders triggered by the state
 * of a user's Action List.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionListEmailService {

    public void sendImmediateReminder(WorkflowUser user, ActionItem actionItem);
    public void sendDailyReminder();
    public void sendWeeklyReminder();
    public void scheduleBatchEmailReminders() throws Exception;

}

