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

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoNavigationGroupAft extends AutomatedFunctionalTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-NavigationGroup-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-NavigationGroup-View&methodToCall=start";
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Navigation");
        waitAndClickByLinkText("Navigation Group");
    }

    protected void testNavigationView() throws Exception {
       waitAndClickByLinkText("Navigation Group Tab Example");
       switchToWindow("Kuali :: Navigation View");
       waitForElementPresentByXpath("//div[@id='Uif-Navigation']/ul/li");
       assertElementPresentByXpath("//div[@id='Uif-Navigation']/ul/li[3]");
       switchToWindow("Kuali");
    }
    
    protected void testNavigationMenuView() throws Exception {
        selectByName("exampleShown","Navigation Group Menu");
        waitAndClickByLinkText("Navigation Group Menu Example");
        switchToWindow("Kuali :: Navigation Menu View");
        waitForElementPresentByXpath("//div[@class='uif-navigationMenu-wrapper']");
        waitAndClickByLinkText("<<");
        waitForElementPresentByXpath("//div[@class='uif-navigationMenu-wrapper' and @style='display: none;']");
        switchToWindow("Kuali");
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeaderBookmark() throws Exception {
        testNavigationView();
        testNavigationMenuView();
        passed();
    }

    @Test
    public void testGeneralFeaturesUnifiedViewHeaderNav() throws Exception {
        testNavigationView();
        testNavigationMenuView();
        passed();
    }  
}
