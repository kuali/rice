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
package edu.samplu.mainmenu.test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.SeleneseFailable;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * Abstract base class for LoginLogout Smoke tests.  Framework specific classes should not be depended upon in this
 * class but abstracted behind SeleneseFailable.
 *
 * @see SeleneseFailable
 * @see LoginLogoutSTNGBase
 * @see LoginLogoutSTJUnitBase
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class LoginLogoutAbstractBaseSmokeTest extends WebDriverLegacyITBase {

    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    @Override
    public String getUserName() {
        return "admin";
    }

    /**
     * Navigate to the page under test and call {@link #testLogout}
     *
     * @param failable {@link SeleneseFailable}
     * @throws Exception
     */
    public void testNavLogout(SeleneseFailable failable) throws Exception {
        navigate(failable);
        testLogout(failable);
    }

    private void navigate(SeleneseFailable failable) throws InterruptedException {
        waitAndClickMainMenu(failable);
        waitForPageToLoad();
    }

    /**
     * Test Logout on the current page.  Sets the passed flag (used with SauceLabs, reporting, etc.) as appropriate.
     *
     * @param failable {@link SeleneseFailable}
     * @throws Exception
     */
    public void testLogout(SeleneseFailable failable) throws Exception {
        waitAndClickLogout(failable);
        passed();
    }
}
