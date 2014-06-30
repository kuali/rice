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
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LoginInvalidUserAft extends WebDriverLegacyITBase {

    @Override
    protected String getBookmarkUrl() {
        return AutomatedFunctionalTestUtils.PORTAL;
    }

    @Override
    protected void navigate() throws Exception {
        // no-op should not need to navigate for invalid login test
    }

    @Override
    public void testSetUp()  {
        System.setProperty(WebDriverUtils.REMOTE_AUTOLOGIN_PROPERTY, "false"); // turn off auto login so we can test invalid login
        super.testSetUp();
    }

    @Test
    public void testAdStarUserNameBookmark() throws InterruptedException {
        try {
            System.setProperty(WebDriverUtils.REMOTE_AUTOLOGIN_PROPERTY, "true");
            login(driver, "ad*", this);
            fail("Expected Invalid Login exception with user " + AutomatedFunctionalTestUtils.DTS_TWO);
        } catch (AssertionError e) {
            waitForElementVisibleById("Rice-LoginButton", "ad* should not be a valid login");
            passed();
        }
    }

    /**
     * Invalid user name test
     * @throws InterruptedException
     */
    @Test
    public void testInvalidUserNameBookmark() throws InterruptedException {
        try {
            System.setProperty(WebDriverUtils.REMOTE_AUTOLOGIN_PROPERTY, "true");
            login(driver, AutomatedFunctionalTestUtils.DTS_TWO, this);
            fail("Expected Invalid Login exception with user " + AutomatedFunctionalTestUtils.DTS_TWO);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("Invalid"));
            passed();
        }
    }
}
