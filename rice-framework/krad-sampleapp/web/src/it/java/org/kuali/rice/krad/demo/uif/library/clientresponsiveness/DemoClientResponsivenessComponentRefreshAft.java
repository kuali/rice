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
package org.kuali.rice.krad.demo.uif.library.clientresponsiveness;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * Functional Test ensures correct behavior of the client responsiveness component refresh actions.
 */
public class DemoClientResponsivenessComponentRefreshAft extends WebDriverLegacyITBase {

    /* Bookmark url for client repsonsiveness component refresh view */
    protected static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ComponentRefresh&methodToCall=start";

    /**
     * Returns the client responsiveness component refresh view
     * @return the url for the client responsiveness component refresh view
     */
    @Override
    protected String getBookmarkUrl() { return BOOKMARK_URL; }

    /**
     * Navigates to the base page, before the tests start
     * @throws Exception if errors while navigating to base page
     */
    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("Component Refresh");
    }

    /**
     * Ensures proper behaviour of the method to call on refresh action
     * @throws Exception if errors while testing the method to call on refresh action
     */
    protected void testClientResponsivenessMethodToCallOnRefresh() throws Exception {
        assertIsVisibleByXpath("//input[@name='booleanField1']", "Is Visible");
        waitAndTypeByName("inputField1","asdf");
        waitAndClickByName("booleanField1");
        waitForTextNotPresent("Loading...");
    }

    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessResetDataOnRefresh() throws Exception {
        waitAndClickByLinkText("Reset Data On Refresh");
        assertIsVisibleByXpath("//input[@name='booleanField2']", "Is Visible");
        waitAndTypeByName("inputField2","yada");
        waitAndClickByName("booleanField2");
        waitForTextNotPresent("Loading...");
        assertFalse("Input field empty assertion", "yada".equals(waitAndGetAttributeByName("inputField2", "value")));
    }

    /**
     * Test by bookmark
     * @throws Exception if errors while testing bookmark
     */
    @Test
    public void testClientResponsivenessComponentRefreshBookmark() throws Exception {
        testClientResponsivenessMethodToCallOnRefresh();
        testClientResponsivenessResetDataOnRefresh();
        passed();
    }

    /**
     * Test by navigation
     * @throws Exception if errors while testing navigation
     */
    @Test
    public void testClientResponsivenessComponentRefreshNav() throws Exception {
        testClientResponsivenessMethodToCallOnRefresh();
        testClientResponsivenessResetDataOnRefresh();
        passed();
    }
}
