/*
 * Copyright 2006-2015 The Kuali Foundation
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
package org.kuali.rice.kew.batch;

/**
 * Service responsible for polling the KREW_ACTN_ITM_CHANGED_T table and notifying an external action list if there are
 * any changes.
 *
 *  @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ExternalActnListNotificationService extends Runnable {

    public static final String EXTERNAL_ACTN_LIST_NOTIFICATION_POLL_INTERVAL_PROP =
            "external.actn.list.notification.poll.interval.seconds";
    public static final String EXTERNAL_ACTN_LIST_NOTIFICATION_INIT_DELAY_SECS_PROP =
            "external.actn.list.notification.initial.delay.seconds";

    int getExternalActnListNotificationPollIntervalSeconds();

    int getExternalActnListNotificationInitialDelaySeconds();

}
