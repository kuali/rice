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
package org.kuali.rice.kew.actions;

import org.kuali.rice.kew.engine.BlanketApproveEngine;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kim.bo.entity.KimPrincipal;


/**
 * Used to determine store notifications to be sent in the {@link BlanketApproveEngine}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationContext {

    private String notificationRequestCode;
    private KimPrincipal principalTakingAction;
    private String actionTakenCode;
    
    public NotificationContext(String notificationRequestCode, KimPrincipal principalTakingAction, String actionTakenCode) {
        this.notificationRequestCode = notificationRequestCode;
        this.principalTakingAction = principalTakingAction;
        this.actionTakenCode = actionTakenCode;
    }

    public String getActionTakenCode() {
        return actionTakenCode;
    }

    public String getNotificationRequestCode() {
        return notificationRequestCode;
    }

    public KimPrincipal getPrincipalTakingAction() {
        return principalTakingAction;
    }
    
    

}
