/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.core.util;

import org.kuali.rice.ken.service.NotificationService;
import org.kuali.rice.kew.actiontaken.service.ActionTakenService;
import org.kuali.rice.ksb.messaging.MessageHelper;

/**
 * This object extends the RiceServiceInjectedObject to test that the service injection
 * via annotation works in derived classes.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RiceServiceInjectedDescendent extends RiceServiceInjectedObject {
    @RiceService(name="enActionTakenService")
    public ActionTakenService ats;
    
    public MessageHelper messageHelper;
    
    public NotificationService ns;

    // try a service in a different module
    
    // for some reason I'm having trouble getting beans from the KSB resource loader
    // when specifying the loader by name; we'll give it a shot with KEN instead
    @RiceService(/*resourceLoader="KSB_ROOT_RESOURCE_LOADER", */name="enMessageHelper")
    public void setMessageHelper(MessageHelper mh) {
        this.messageHelper = mh;
    }
    
    public MessageHelper getMessageHelper() {
        return messageHelper;
    }
    
    /*@RiceService(resourceLoader="KEN_SPRING_RESOURCE_LOADER", name="notificationService")*/
    // looks like they have possibly been all consolidated down?
    @RiceService(resourceLoader="RICE_SPRING_RESOURCE_LOADER_NAME", name="notificationService")
    public void setNotificationService(NotificationService ns) {
        this.ns = ns;
    }
    
    public NotificationService getNotificationService() {
        return ns;
    }
    
    
    
    public void setActionTakenService(ActionTakenService ats) {
        this.ats = ats;
    }
    
    public ActionTakenService getActionTakenService() {
        return ats;
    }
}
