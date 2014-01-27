/**
 * Copyright 2005-2014 The Kuali Foundation
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
package edu.sampleu.admin.notification;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SendEventNotificationAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Send%20Event%20Notification&channelUrl=" + WebDriverUtils
     *.getBaseUrlString() + "/ken/SendEventNotificationMessage.form";
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Send%20Event%20Notification&channelUrl=" + WebDriverUtils
                    .getBaseUrlString() + "/ken/SendEventNotificationMessage.form";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Send Event Notification");
    }
    
    private void testEventNotification() throws Exception{
        selectFrameIframePortlet();
        selectByName("channelName","Kuali Rice Channel (2 subscribers, 0 default recipients)");
        selectByName("priorityName","Normal");
        clearTextByName("sendDateTime");
        clearTextByName("senderNames");
        waitAndTypeByName("senderNames","admin,rice-team");
        waitAndTypeByName("autoRemoveDateTime","01/16/2030 06:16 AM");
        waitAndTypeByName("title","Test Title");
        waitAndTypeByName("message","Test Message");
        waitAndTypeByName("summary","Test Event Summary");
        waitAndTypeByName("description","Test Event Description");
        waitAndTypeByName("location","California");
        clearTextByName("startDateTime");
        waitAndTypeByName("startDateTime","01/16/2015 02:38 PM");
        waitAndTypeByName("stopDateTime","02/16/2015 02:38 PM");
        waitAndClickByName("submit");
        waitForTextPresent("Notification(s) sent.");
    }
    
    @Test
    public void testEventNotificationBookmark() throws Exception {
        testEventNotification();
        passed();
    }

    @Test
    public void testEventNotificationNav() throws Exception {
        testEventNotification();
        passed();
    }
    
}
