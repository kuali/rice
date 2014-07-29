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
package org.kuali.rice.krad.demo.uif.library.navigation;

import org.junit.Test;

import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoNavigationGroupAft extends DemoLibraryNavigationBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-NavigationGroupView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-NavigationGroupView&methodToCall=start";
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Navigation");
        waitAndClickByLinkText("Navigation Group");
    }

    protected void testNavigationView() throws Exception {
       waitAndClickByLinkText("Navigation Group Tab Example");
       switchToWindow("Kuali :: Navigation View");
       waitForElementPresentByXpath("//nav[@id='Uif-Navigation']/ul/li");
       assertElementPresentByXpath("//nav[@id='Uif-Navigation']/ul/li[3]");
       super.testNavigationView();
       switchToWindow("Kuali");
    }
    
    protected void testNavigationMenuView() throws Exception {
        selectByName("exampleShown","Navigation Group Menu");
        waitAndClickByLinkText("Navigation Group Menu Example");
        switchToWindow("Kuali :: Navigation Menu View");
        waitForElementPresentByXpath("//div[@class='uif-menuNavigationGroup']");
        waitAndClick(By.className("icon-angle-left"));
        waitForElementPresentByXpath("//div[@class='uif-menuNavigationGroup sidebar-collapsed']");
        switchToWindow("Kuali");
    }
    
    protected void testNavigationWithToggleMenu() throws Exception {
        selectByName("exampleShown","With Toggle Menu");
        waitAndClickByLinkText("Navigation Toggle Menu");
        switchToWindow("Kuali :: Navigation Menu View");
        waitAndClickByLinkText("Page 1");
        waitAndClickByLinkText("Page 2");
        waitAndClickByLinkText("More Content");
        switchToWindow("Kuali");
    }

    @Test
    public void testNavigationViewBookmark() throws Exception {
        testNavigationView();
        passed();
    }

    @Test
    public void testNavigationViewNav() throws Exception {
        testNavigationView();
        passed();
    }  
    
    @Test
    public void testNavigationMenuViewBookmark() throws Exception {
        testNavigationMenuView();
        passed();
    }

    @Test
    public void testNavigationMenuViewNav() throws Exception {
        testNavigationMenuView();
        passed();
    }  
    
    @Test
    public void testNavigationToggleMenuBookmark() throws Exception {
        testNavigationWithToggleMenu();
        passed();
    }

    @Test
    public void testNavigationToggleMenuNav() throws Exception {
        testNavigationWithToggleMenu();
        passed();
    }  
}
