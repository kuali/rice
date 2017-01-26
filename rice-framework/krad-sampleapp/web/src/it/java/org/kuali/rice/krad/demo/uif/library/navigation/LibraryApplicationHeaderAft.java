/**
 * Copyright 2005-2017 The Kuali Foundation
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
public class LibraryApplicationHeaderAft extends LibraryNavigationBase {

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
    	waitAndClickLibraryLink();
        waitAndClickByLinkText("Navigation");
        waitAndClickByLinkText("Application Header");
    }

    protected void testApplicationHeaderFluid() throws Exception {
        waitAndClickByLinkText("Fluid");
        waitAndClickByLinkText("Fluid Application Header");
        switchToWindow("Kuali :: Default Application Header");
        waitForElementPresentByXpath("//h1/span");
        assertElementPresentByXpath("//header[@class='container-fluid uif-viewHeader uif-header' and @data-header_for='Demo-AppHeader-View1']");
        waitForTextPresent("Fluid Application Header");
        selectParentWindow();
    }

    protected void testApplicationHeaderFixed() throws Exception {
        waitAndClickByLinkText("Fixed");
        waitAndClickByLinkText("Fixed Application Header");
        switchToWindow("Kuali :: Default Application Header");
        waitForElementPresentByXpath("//h1/span");
        assertElementPresentByXpath("//header[@class='container uif-viewHeader uif-header' and @data-header_for='Demo-AppHeader-View2']");
        waitForTextPresent("Fixed Application Header");
        selectParentWindow();
    }

    @Test
    public void testApplicationHeaderBookmark() throws Exception {
        testApplicationHeaderFluid();
        testApplicationHeaderFixed();
        passed();
    }

    @Test
    public void testApplicationHeaderNav() throws Exception {
        testApplicationHeaderFluid();
        testApplicationHeaderFixed();
        passed();
    }
}
