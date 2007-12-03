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
package org.kuali.notification;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.messaging.KEWXMLService;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * Tests integration with KEW
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KEWIntegrationTest extends NotificationTestCaseBase {
    /**
     * Tests that we can obtain KEW user and workgroup services
     */
    @Test
    public void testKEWServicesAreAccessible() throws Exception {
	UserService userService = KEWServiceLocator.getUserService();
	assertNotNull(userService);
	LOG.info("Default KEW UserService: " + userService);

        WorkgroupService workgroupService = KEWServiceLocator.getWorkgroupService();
        assertNotNull(workgroupService);
        LOG.info("Default KEW WorkgroupService: " + workgroupService);
        
        KEWXMLService notification = (KEWXMLService) GlobalResourceLoader.getService(new QName("KEN", "sendNotificationKewXmlService"));
        assertNotNull(notification);
//        XmlIngesterService is = SpringServiceLocator..getXmlIngesterService();
        // check that the quickstart user is present 
        //assertNotNull(userService.getWorkflowUser(new WorkflowUserId("quickstart")));
    }
}
