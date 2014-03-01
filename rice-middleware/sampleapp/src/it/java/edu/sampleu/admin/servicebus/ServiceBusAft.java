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
public class ServiceBusAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Service%20Bus&channelUrl="+WebDriverUtils
     * .getBaseUrlString()+"/ksb/ServiceBus.do";
     */
    public static final String BOOKMARK_URL =AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Service%20Bus&channelUrl="+WebDriverUtils
            .getBaseUrlString()+"/ksb/ServiceBus.do";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Service Bus");
    }
    
    private void testServiceBus() throws Exception{
        selectFrameIframePortlet();
        waitForTextPresent("Application ID: TRAVEL");
        waitForTextPresent("Published Services:");
        waitForTextPresent("All Bus Services:");
        String [] assertText={"Service Name","Endpoint URL","Instance ID","Application ID","Service Version","Type"};
        assertTextPresent(assertText);
    }
    
    @Test
    public void testServiceBusBookmark() throws Exception {
        testServiceBus();
        passed();
    }

    @Test
    public void testServiceBusNav() throws Exception {
        testServiceBus();
        passed();
    }
    
}
