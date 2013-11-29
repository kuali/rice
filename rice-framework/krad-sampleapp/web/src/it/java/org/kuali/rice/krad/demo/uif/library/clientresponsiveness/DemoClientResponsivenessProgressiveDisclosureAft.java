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
package org.kuali.rice.krad.demo.uif.library.clientresponsiveness;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoClientResponsivenessProgressiveDisclosureAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ProgressiveDisclosure-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ProgressiveDisclosure-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("Progressive Disclosure");
    }

    protected void testClientResponsivenessProgressiveDisclosure() throws Exception {
         //Scenario-1
        waitAndClickByLinkText("Default");
        assertIsNotVisibleByXpath("//input[@name='inputField1']", "Is Visible");
        waitAndClickByName("booleanField1");
        assertIsVisibleByXpath("//input[@name='inputField1']","Not Visible");
        assertIsNotVisibleByXpath("//input[@name='inputField3']", "Is Visible");
        waitAndTypeByName("inputField2", "show");
        waitAndClickByLinkText("<< Close Library Navigation");
        Thread.sleep(2000);
        assertIsVisibleByXpath("//input[@name='inputField3']", "testClientResponsivenessProgressiveDisclosure text input based disclosure not visible");
    }
    
    protected void testClientResponsivenessProgressiveDisclosureAjaxRetrieval() throws Exception {
        waitAndClickByLinkText("Ajax Retrieval");
        checkForIncidentReport("DemoClientResponsivenessProgressiveDisclosureAft Ajax Retrieval");
        assertIsNotVisibleByXpath("//input[@name='inputField4']", "element");
        waitAndClickByName("booleanField2");
        Thread.sleep(2000);
        assertIsVisibleByXpath("//input[@name='inputField4']", "element");
    }
    
    protected void testClientResponsivenessProgressiveDisclosureRefreshWhenShown() throws Exception {
        waitAndClickByLinkText("Refresh when Shown");
        assertIsNotVisibleByXpath("//input[@name='inputField5']", "element");
        waitAndClickByName("booleanField3");
        Thread.sleep(2000);
        assertIsVisibleByXpath("//input[@name='inputField5']", "element");
    }
    
    @Test
    public void testClientResponsivenessProgressiveDisclosureBookmark() throws Exception {
        testClientResponsivenessProgressiveDisclosureAjaxRetrieval();
        testClientResponsivenessProgressiveDisclosureRefreshWhenShown();
        testClientResponsivenessProgressiveDisclosure();
        passed();
    }

    @Test
    public void testClientResponsivenessProgressiveDisclosureNav() throws Exception {
        testClientResponsivenessProgressiveDisclosureAjaxRetrieval();
        testClientResponsivenessProgressiveDisclosureRefreshWhenShown();
        testClientResponsivenessProgressiveDisclosure();
        passed();
    }  
}
