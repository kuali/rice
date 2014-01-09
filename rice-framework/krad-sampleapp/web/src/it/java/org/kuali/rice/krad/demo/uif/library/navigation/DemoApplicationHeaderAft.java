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
package org.kuali.rice.krad.demo.uif.library.navigation;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoApplicationHeaderAft extends DemoLibraryNavigationBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-AppHeaderView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-AppHeaderView";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Navigation");
        waitAndClickByLinkText("Application Header");
    }

    protected void testApplicationHeader() throws Exception {
        waitAndClickByLinkText("Default Application Header");
        switchToWindow("Kuali :: Default Application Header");
        waitForElementPresentByXpath("//h1/span");
        waitForTextPresent("Default Application Header");
    }

    @Test
    public void testApplicationHeaderBookmark() throws Exception {
        testApplicationHeader();
        passed();
    }

    @Test
    public void testApplicationHeaderNav() throws Exception {
        testApplicationHeader();
        passed();
    }
}
