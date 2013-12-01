/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.sampleu.kew;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoQuickLinksAft extends WebDriverLegacyITBase {

    /**
     *  AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Quicklinks&channelUrl="
     *   + WebDriverUtils.getBaseUrlString() + "/kew/QuickLinks.do";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Quicklinks&channelUrl="
            + WebDriverUtils.getBaseUrlString() + "/kew/QuickLinks.do";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws InterruptedException {
       waitAndClickMainMenu(null);
       waitAndClickByLinkText("Quicklinks");        
    }

    protected void testDemoQuickLinks() throws Exception { 
      selectFrameIframePortlet();
      assertTextPresent("Quick EDoc Search");
      assertTextPresent("Created=");
      assertTextPresent("Quick Action List");
      assertTextPresent("ENROUTE");
      assertElementPresentByXpath("//a[@href='RouteLog.do?documentId=3017']");
    }

    @Test
    public void testDemoQuickLinksBookmark() throws Exception {
        testDemoQuickLinks();
        passed();
    }

    @Test
    public void testDemoQuickLinksNav() throws Exception {
        testDemoQuickLinks();
        passed();
    }
}
