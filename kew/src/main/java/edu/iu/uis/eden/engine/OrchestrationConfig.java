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
package edu.iu.uis.eden.engine;

import java.util.HashSet;
import java.util.Set;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;

/**
 * Specifies configuration for orchestration through the engine.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OrchestrationConfig {

    private boolean sendNotifications = true;
    private String notificationType = EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ;
    private Set destinationNodeNames = new HashSet();
    private ActionTakenValue cause;
    
    public Set getDestinationNodeNames() {
        return destinationNodeNames;
    }
    public void setDestinationNodeNames(Set destinationNodeNames) {
        this.destinationNodeNames = destinationNodeNames;
    }
    public String getNotificationType() {
        return notificationType;
    }
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
    public boolean isSendNotifications() {
        return sendNotifications;
    }
    public void setSendNotifications(boolean sendNotifications) {
        this.sendNotifications = sendNotifications;
    }
    public ActionTakenValue getCause() {
        return cause;
    }
    public void setCause(ActionTakenValue cause) {
        this.cause = cause;
    }
    
}
