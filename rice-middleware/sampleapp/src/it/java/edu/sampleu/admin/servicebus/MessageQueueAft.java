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
package edu.sampleu.admin.servicebus;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageQueueAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Message%20Queue&channelUrl="+WebDriverUtils
     * .getBaseUrlString()+"/ksb/MessageQueue.do"
     */
    public static final String BOOKMARK_URL =AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Message%20Queue&channelUrl="+WebDriverUtils
            .getBaseUrlString()+"/ksb/MessageQueue.do";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Message Queue");
    }
    
    private void testMessageQueue() throws Exception{
        selectFrameIframePortlet();
        String [] assertText={"Message ID:","Service Name:","Application ID:","IP Number:","Queue Status:","App Specific Value 1:","App Specific Value 2:"};
        assertTextPresent(assertText);
        waitAndClickByXpath("//input[@value='Execute Message Fetcher']");
        acceptAlert();
        waitForTextPresent("Documents currently in route queue:");
    }
    
    @Test
    public void testMessageQueueBookmark() throws Exception {
        testMessageQueue();
        passed();
    }

    @Test
    public void testMessageQueueNav() throws Exception {
        testMessageQueue();
        passed();
    }
    
}
