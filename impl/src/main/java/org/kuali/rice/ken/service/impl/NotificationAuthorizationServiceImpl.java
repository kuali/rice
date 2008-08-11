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
package org.kuali.rice.ken.service.impl;

import java.util.List;

import org.kuali.rice.core.dao.GenericDao;
import org.kuali.rice.ken.bo.NotificationChannel;
import org.kuali.rice.ken.bo.NotificationProducer;
import org.kuali.rice.ken.service.NotificationAuthorizationService;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.workgroup.GroupNameId;


/**
 * NotificationAuthorizationService implementation - this is the default out-of-the-box implementation of the service.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationAuthorizationServiceImpl implements NotificationAuthorizationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NotificationAuthorizationServiceImpl.class);
    
    private GenericDao businessObjectDao;
    
    /**
     * Constructs a NotificationAuthorizationServiceImpl class instance.
     * @param businessObjectDao
     */
    public NotificationAuthorizationServiceImpl(GenericDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationAuthorizationService#isProducerAuthorizedToSendNotificationForChannel(org.kuali.rice.ken.bo.NotificationProducer, org.kuali.rice.ken.bo.NotificationChannel)
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
     * @see org.kuali.rice.ken.service.NotificationAuthorizationService#isUserAdministrator(java.lang.String)
     */
    public boolean isUserAdministrator(String userId) {
	try {
	    GroupNameId groupNameId = new GroupNameId(NotificationConstants.KEW_CONSTANTS.NOTIFICATION_ADMIN_GROUP_NAME);
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO(userId));
    
	    return KEWServiceLocator.getWorkgroupService().isUserMemberOfGroup(groupNameId, user);
	} catch(KEWUserNotFoundException eunfe) {
	    LOG.error(eunfe);
	    return false;
	}
    }
}