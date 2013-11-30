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
package edu.samplu.kew.workflow;

import com.thoughtworks.selenium.SeleneseTestBase;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoUserPreferenceAft extends WebDriverLegacyITBase {

    /**
     *  AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=User%20Preferences&channelUrl="
     *  + WebDriverUtils.getBaseUrlString() + "/kew/Preferences.do?returnLocation=" 
     *  + AutomatedFunctionalTestUtils.PORTAL;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=User%20Preferences&channelUrl="
            + WebDriverUtils.getBaseUrlString() + "/kew/Preferences.do?returnLocation=" 
            + AutomatedFunctionalTestUtils.PORTAL;
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws InterruptedException {
       waitAndClickMainMenu(null);
       waitAndClickByLinkText("User Preferences");        
    }

    protected void testDemoUserPreference() throws Exception { 
      selectFrameIframePortlet();
      assertTextPresent("General");
      assertTextPresent("Automatic Refresh Rate:");
      assertElementPresentByXpath("//input[@name='preferences.refreshRate' and @value='15']");
      assertElementPresentByXpath("//input[@name='preferences.pageSize' and @value='10']");
      assertTextPresent("Fields Displayed In Action List");
      assertTextPresent("Document Type");
      assertElementPresentByXpath("//input[@name='preferences.showDocType' and @value='yes']");
      assertTextPresent("Document Route Status Colors for Actionlist Entries");
      assertTextPresent("Email Notification Preferences");
      assertTextPresent("Receive Primary Delegate Emails");
      assertElementPresentByXpath("//input[@name='preferences.notifyPrimaryDelegation' and @value='yes']");
    }

    @Test
    public void testDemoUserPreferenceBookmark() throws Exception {
        testDemoUserPreference();
        passed();
    }

    @Test
    public void testDemoUserPreferenceNav() throws Exception {
        testDemoUserPreference();
        passed();
    }
}
