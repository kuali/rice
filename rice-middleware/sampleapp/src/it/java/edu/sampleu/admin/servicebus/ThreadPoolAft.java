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
public class ThreadPoolAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Thread%20Pool&channelUrl="+WebDriverUtils
     * .getBaseUrlString()+"/ksb/ThreadPool.do";
     */
    public static final String BOOKMARK_URL =AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Thread%20Pool&channelUrl="+WebDriverUtils
            .getBaseUrlString()+"/ksb/ThreadPool.do";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Thread Pool");
    }
    
    private void testThreadPool() throws Exception{
        selectFrameIframePortlet();
        String [] assertText={"Core Pool Size:","Maximum Pool Size:","Pool Size:","Active Count:","Largest Pool Size:",
                "Keep Alive Time:","Task Count:","Completed Task Count:","RouteQueue.TimeIncrement:","RouteQueue.maxRetryAttempts:"};
        assertTextPresent(assertText);
    }
    
    @Test
    public void testThreadPoolBookmark() throws Exception {
        testThreadPool();
        passed();
    }

    @Test
    public void testThreadPoolNav() throws Exception {
        testThreadPool();
        passed();
    }
    
}
