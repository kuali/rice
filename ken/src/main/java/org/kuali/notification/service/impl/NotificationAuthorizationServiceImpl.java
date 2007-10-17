/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.notification.service.impl;

import java.util.List;

import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.NotificationProducer;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.service.NotificationAuthorizationService;
import org.kuali.notification.util.NotificationConstants;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.workgroup.GroupNameId;

/**
 * NotificationAuthorizationService implementation - this is the default out-of-the-box implementation of the service.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationAuthorizationServiceImpl implements NotificationAuthorizationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NotificationAuthorizationServiceImpl.class);
    
    private BusinessObjectDao businessObjectDao;
    
    /**
     * Constructs a NotificationAuthorizationServiceImpl class instance.
     * @param businessObjectDao
     */
    public NotificationAuthorizationServiceImpl(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * @see org.kuali.notification.service.NotificationAuthorizationService#isProducerAuthorizedToSendNotificationForChannel(org.kuali.notification.bo.NotificationProducer, org.kuali.notification.bo.NotificationChannel)
     */
    public boolean isProducerAuthorizedToSendNotificationForChannel(NotificationProducer producer, NotificationChannel channel) {
	List channels = producer.getChannels();
	
	if(channels.contains(channel)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Implements by calling the is user member of service in KEW's workgroup service, looking for a specific membership 
     * in the "NotificationAdmin" workgroup.
     * @see org.kuali.notification.service.NotificationAuthorizationService#isUserAdministrator(java.lang.String)
     */
    public boolean isUserAdministrator(String userId) {
	try {
	    GroupNameId groupNameId = new GroupNameId(NotificationConstants.KEW_CONSTANTS.NOTIFICATION_ADMIN_GROUP_NAME);
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdVO(userId));
    
	    return KEWServiceLocator.getWorkgroupService().isUserMemberOfGroup(groupNameId, user);
	} catch(EdenUserNotFoundException eunfe) {
	    LOG.error(eunfe);
	    return false;
	}
    }
}