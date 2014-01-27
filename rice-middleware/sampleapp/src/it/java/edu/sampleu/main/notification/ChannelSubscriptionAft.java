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
package edu.sampleu.main.notification;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ChannelSubscriptionAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Channel%20Subscriptions&channelUrl=" + WebDriverUtils
     *  .getBaseUrlString() + "/ken/DisplayUserPreferences.form";
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Channel%20Subscriptions&channelUrl=" + WebDriverUtils
                    .getBaseUrlString() + "/ken/DisplayUserPreferences.form";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickByLinkText("Channel Subscriptions");
    }
    
    private void testChannelSubscription() throws Exception{
        selectFrameIframePortlet();
        waitAndClickByXpath("//a[@href='SubscribeToChannel.form?channelid=4']");
        waitAndClickByXpath("//a[@href='UnsubscribeFromChannel.form?channelid=4']");
    }
    
    @Test
    public void testChannelSubscriptionBookmark() throws Exception {
        testChannelSubscription();
        passed();
    }

    @Test
    public void testChannelSubscriptionNav() throws Exception {
        testChannelSubscription();
        passed();
    }
    
}
