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
package edu.samplu.admin.test;

import edu.samplu.common.ITUtil;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * TestNG implementation of ComponentSTNGBase that navigates through the UI to the page under test.  In the future
 * the idea is to generate this class using the test methods from ComponentAbstractSmokeTestBase and following the simple pattern of
 * <pre>super.testNavTestMethod(this);</pre>
 *
 * @see ComponentSTNGBase
 * @see ComponentSTNGBkMrkGen
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentSTNGNavGen extends ComponentSTNGBase {

    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    @Override
    public void gotoTest() throws Exception {
        navigtaion();
    }

    /**
     * @link ComponentAbstractSmokeTestBase#testComponentCreateNewCancelNav
     * @throws Exception
     */
    @Test(groups = { "all", "fast", "default" }, description = "testComponentCreateNewCancelNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testComponentCreateNewCancelNav() throws Exception {
        setUp();
        testComponentCreateNewCancelNav(this);
    }

    /**
     * @link ComponentAbstractSmokeTestBase#testComponentParameterNav
     * @throws Exception
     */
    @Test(groups = { "all", "fast", "default" }, description = "testComponentParameterNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testComponentParameterNav() throws Exception {
        setUp();
        testComponentParameterNav(this);
    }
}
