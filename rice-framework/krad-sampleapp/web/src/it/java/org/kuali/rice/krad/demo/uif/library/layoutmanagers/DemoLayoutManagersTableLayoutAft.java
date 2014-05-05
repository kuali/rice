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
package org.kuali.rice.krad.demo.uif.library.layoutmanagers;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * This class tests the Table Layout Cases included in Demo Library
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLayoutManagersTableLayoutAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TableLayoutManagerView&methodToCall=start
     */
    public static final String BOOKMARK_URL =
            "/kr-krad/kradsampleapp?viewId=Demo-TableLayoutManagerView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Layout Managers");
        waitAndClickByLinkText("Table Layout");
    }

    protected void testLayoutManagersBasicTableLayout() throws Exception {
        selectByName("exampleShown","Basic Table Layout");
        waitForElementPresentByXpath("//button[@id='Demo-TableLayoutManager-Example1_add']");
    }

    @Test
    public void testLayoutManagersBoxLayoutBookmark() throws Exception {
        testLayoutManagersBasicTableLayout();
        passed();
    }

    @Test
    public void testLayoutManagersBoxLayoutNav() throws Exception {
        testLayoutManagersBasicTableLayout();
        passed();
    }
}
