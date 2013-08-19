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
package edu.samplu.krad.library.layoutmanagers;

import org.junit.Test;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.SmokeTestBase;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryLayoutManagersListLayoutSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ListLayoutManager-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ListLayoutManager-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Layout Managers");
        waitAndClickByLinkText("List Layout");
    }

    protected void testLayoutManagersListLayout() throws Exception {
       assertElementPresentByXpath("//div[@data-parent='Demo-ListLayoutManager-Example1']/ul/li");
       assertElementPresentByXpath("//div[@data-parent='Demo-ListLayoutManager-Example1']/ul/li[35]");
    }
    
    @Test
    public void testLayoutManagersListLayoutBookmark() throws Exception {
        testLayoutManagersListLayout();
        passed();
    }

    @Test
    public void testLayoutManagersListLayoutNav() throws Exception {
        testLayoutManagersListLayout();
        passed();
    }  
}