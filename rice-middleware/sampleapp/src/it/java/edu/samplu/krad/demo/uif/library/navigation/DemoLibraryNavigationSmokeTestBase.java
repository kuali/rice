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
package edu.samplu.krad.demo.uif.library.navigation;

import com.thoughtworks.selenium.SeleneseTestBase;
import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import edu.samplu.krad.demo.uif.library.DemoLibraryITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DemoLibraryNavigationSmokeTestBase extends DemoLibraryITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=ComponentLibraryHome
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-NavigationGroup-View";

    /**
     * /kr-krad/kradsampleapp?viewId=NavigationGroup-NavigationView&methodToCall=start
     */
    public static final String BOOKMARK_VIEW_URL = "/kr-krad/kradsampleapp?viewId=NavigationGroup-NavigationView&methodToCall=start";

    @Override
    public String getTestUrl() {
        return ITUtil.KRAD_PORTAL;
    }

    protected void testNavigationTabs() throws Exception {
        navigateToExample("Demo-NavigationGroup-Example1");

        waitAndClickByLinkText("Navigation Group Tab Example");
        selectWindow(driver.getWindowHandles().toArray()[1].toString());
        waitForElementPresentByClassName("uif-headerText-span");
        SeleneseTestBase.assertTrue(driver.getTitle().contains("Kuali :: Navigation View"));
        assertTextPresent("Navigation View");
    }

    protected void testNavigationView() throws Exception {
        assertNavigationView("Page 2", "Test Course 2");
        assertNavigationView("Page 3", "Test Course 3");
        assertNavigationView("Page 1", "Test Course 1");
    }

    protected void assertNavigationView(String linkText, String supportTitleText) throws Exception {
        waitAndClickByLinkText(linkText);
        waitForElementPresentByClassName("uif-viewHeader-supportTitle");
        SeleneseTestBase.assertTrue(getTextByClassName("uif-viewHeader-supportTitle").contains(supportTitleText));
    }

    public void testNavigationMenuBookmark(Failable failable) throws Exception {
        testNavigationTabs();
        testNavigationView();
        passed();
    }

    public void testNavigationMenuNav(Failable failable) throws Exception {
        navigateToLibraryDemo("Navigation", "Navigation Group");
        testNavigationTabs();
        testNavigationView();
        passed();
    }

    public void testNavigationViewBookmark(Failable failable) throws Exception {
        testNavigationView();
        passed();
    }
}
