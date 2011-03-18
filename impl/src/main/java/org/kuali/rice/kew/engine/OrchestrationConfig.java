/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.engine;

import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.util.KEWConstants;

import java.util.HashSet;
import java.util.Set;


/**
 * Specifies configuration for orchestration through the engine.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class OrchestrationConfig {

    private boolean sendNotifications = true;
    private String notificationType = KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ;
    private Set<String> destinationNodeNames = new HashSet<String>();
    private ActionTakenValue cause;
    private boolean runPostProcessorLogic = true;
    
    public Set<? extends String> getDestinationNodeNames() {
        return destinationNodeNames;
    }
    public void setDestinationNodeNames(Set<String> destinationNodeNames) {
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
	public boolean isRunPostProcessorLogic() {
		return this.runPostProcessorLogic;
	}
	public void setRunPostProcessorLogic(boolean runPostProcessorLogic) {
		this.runPostProcessorLogic = runPostProcessorLogic;
	}    
    
}
