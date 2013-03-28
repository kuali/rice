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

import org.junit.Test;

/**
 * JUnit implementation of LoginLogoutSTJUnitBase that navigates through the UI to the page under test.  In the future
 * the idea is to generate this class using the test methods from LoginLogoutAbstractSmokeTestBase and following the simple pattern of
 * <pre>super.testNavTestMethod(this);</pre>
 *
 * @see LoginLogoutSTJUnitBase
 * @see LoginLogoutSTJUnitBkMrkGen
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LoginLogoutSTJUnitNavGen extends LoginLogoutSTJUnitBase {

    /**
     * @link LoginLogoutSTJUnitBase#testLogoutNav
     * @throws Exception
     */
    @Test
    public void testLogoutNav() throws Exception {
        testLogoutNav(this);
    }
}
