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
package edu.sampleu.krad.validationmessagesview;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

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

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigation() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByXpath(VALIDATION_FRAMEWORK_DEMO_XPATH);
        switchToWindow(KUALI_VIEW_WINDOW_TITLE);
    }

    protected void testServerWarningsNav(JiraAwareFailable failable) throws Exception {
        navigation();
        testServerWarningsIT();
        passed();
    }

    protected void testServerWarningsBookmark(JiraAwareFailable failable) throws Exception {
        testServerWarningsIT();
        passed();
    }    
}
