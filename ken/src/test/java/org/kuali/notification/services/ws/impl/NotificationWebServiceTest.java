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
package org.kuali.notification.services.ws.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests XFire Notification web service.  See KEW SimpleDocumentActionsWebServiceTest
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Ignore // requires webapp to be up
public class NotificationWebServiceTest {
    private static final Logger LOG = Logger.getLogger(NotificationWebServiceTest.class);
    private static final String notificationMessageAsXml;
    
    static {
        InputStream notificationXML = NotificationWebServiceTest.class.getResourceAsStream("webservice_notification.xml");
        try {
            notificationMessageAsXml = IOUtils.toString(notificationXML);
        } catch (IOException ioe) {
            throw new RuntimeException("Error loading webservice_notification.xml");
        }
    }

    @Test
    public void test() throws Exception {
        /*NotificationWebServiceSoapBindingStub stub = new NotificationWebServiceSoapBindingStub(new URL(getWebServiceURL()), null);

        String responseAsXml = stub.sendNotification(notificationMessageAsXml);

        LOG.info(responseAsXml);
        assertTrue(StringUtils.contains(responseAsXml, "<status>Success</status>"));*/
    }
}