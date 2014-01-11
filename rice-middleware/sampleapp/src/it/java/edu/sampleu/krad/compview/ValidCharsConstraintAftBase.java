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
package edu.sampleu.krad.compview;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ValidCharsConstraintAftBase extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page4
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page4";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigation() throws Exception {
        waitAndClickKRAD();
        waitAndClickByLinkText("Uif Components (Kitchen Sink)");
        switchToWindow(KUALI_UIF_COMPONENTS_WINDOW_XPATH);
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Validation - Regex");
    }

    protected void testValidCharsConstraintNav(JiraAwareFailable failable) throws Exception {
        navigation();
        testValidCharsConstraintIT();
        passed();
    }

    protected void testValidCharsConstraintBookmark(JiraAwareFailable failable) throws Exception {
        testValidCharsConstraintIT();
        passed();
    }
}