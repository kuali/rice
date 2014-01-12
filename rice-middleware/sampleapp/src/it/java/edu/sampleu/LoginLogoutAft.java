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
package edu.sampleu;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * Abstract base class for LoginLogout Smoke Tests.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LoginLogoutAft extends WebDriverLegacyITBase {

    @Override
    protected String getBookmarkUrl() {
        return AutomatedFunctionalTestUtils.PORTAL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickMainMenu();
        waitForPageToLoad();
    }

    /**
     * Navigate to the page under test click logout.  {@link LoginLogoutAft#waitAndClickLogout(org.kuali.rice.testtools.common.JiraAwareFailable)}
     *
     * @throws {@link Exception}
     */
    @Test
    public void testLogoutNav() throws Exception {
        testLogout();
        passed();
    }

    private void testLogout() throws InterruptedException {
        jGrowl("Logging Out");
        waitAndClickLogout();
        assertIsVisibleById("Rice-LoginButton");
    }
}
