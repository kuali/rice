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
package edu.samplu.krad.validationmessagesview;

import org.kuali.rice.testtools.common.Failable;
import org.kuali.rice.testtools.selenium.ITUtil;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtil;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ServerWarningsAftBase extends WebDriverLegacyITBase {

    /**
     * "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start"
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start";

    protected void bookmark() {
        open(WebDriverUtil.getBaseUrlString() + BOOKMARK_URL);
    }

    /**
     * Nav tests start at {@link org.kuali.rice.testtools.selenium.ITUtil#PORTAL}.
     * Bookmark Tests should override and return {@link ServerWarningsAftBase#BOOKMARK_URL}
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    protected void navigation() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByXpath(VALIDATION_FRAMEWORK_DEMO_XPATH);
        switchToWindow(KUALI_VIEW_WINDOW_TITLE);
    }

    protected void testServerWarningsNav(Failable failable) throws Exception {
        navigation();
        testServerWarningsIT();
        passed();
    }

    protected void testServerWarningsBookmark(Failable failable) throws Exception {
        testServerWarningsIT();
        passed();
    }    
}
