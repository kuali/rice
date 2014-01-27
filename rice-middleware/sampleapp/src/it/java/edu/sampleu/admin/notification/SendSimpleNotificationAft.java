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
public class SendSimpleNotificationAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Channel%20Subscriptions&channelUrl=" + WebDriverUtils
     *  .getBaseUrlString() + "/ken/DisplayUserPreferences.form";
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Send%20Simple%20Notification&channelUrl=" + WebDriverUtils
                    .getBaseUrlString() + "/ken/SendSimpleNotificationMessage.form";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Send Simple Notification");
    }
    
    private void testSimpleNotification() throws Exception{
        selectFrameIframePortlet();
        selectByName("channelName","Kuali Rice Channel (2 subscribers, 0 default recipients)");
        selectByName("priorityName","Normal");
        clearTextByName("sendDateTime");
        clearTextByName("senderNames");
        waitAndTypeByName("senderNames","admin,rice-team");
        waitAndTypeByName("autoRemoveDateTime","01/16/2030 06:16 AM");
        waitAndTypeByName("title","Test Title");
        waitAndTypeByName("message","Test Message");
        waitAndClickByName("submit");
        waitForTextPresent("Notification(s) sent.");
    }
    
    @Test
    public void testSimpleNotificationBookmark() throws Exception {
        testSimpleNotification();
        passed();
    }

    @Test
    public void testSimpleNotificationNav() throws Exception {
        testSimpleNotification();
        passed();
    }
    
}
