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

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * TestNG implementation of LoginLogoutSTNGBase that navigates through the UI to the page under test.  In the future
 * the idea is to generate this class using the test methods from LoginLogoutAbstractBaseSmokeTest and following the  simple pattern of
 * super.testNavTestMethod(SeleneseFailable).
 *
 * @see LoginLogoutSTNGBase
 * @see LoginLogoutSTNGBkMrkGen
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LoginLogoutSTNGNavGen extends LoginLogoutSTNGBase {

    @Test(groups = { "all", "fast", "default", "nav" }, description = "testNavLogout")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testNavLogout() throws Exception {
        setUp();
        super.testNavLogout(this);
    }
}
