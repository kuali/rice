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
package org.kuali.rice.krad.demo.uif.library.elements;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoElementsDropdownMenuAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-DropdownMenuView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-DropdownMenuView";
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Dropdown Menu");
    }

    protected void testLibraryElementsDropdownBasic() throws Exception {
        waitAndClickByLinkText("Basic Dropdown");
        waitAndClickByLinkText("User thclark");
        assertElementPresentByXpath("//a[contains(text(),'Preferences')]");
        assertElementPresentByXpath("//a[contains(text(),'Logout')]");
    }
    
    protected void testLibraryElementsDropdownWithDivider() throws Exception {
        waitAndClickByLinkText("Dropdown with Divider");
        waitAndClickByLinkText("Action List");
        assertElementPresentByXpath("//a[contains(text(),'Proposal #1034')]");
        assertElementPresentByXpath("//li[@class='divider']");
    }
    
    protected void testLibraryElementsDropdownWithHeader() throws Exception {
        waitAndClickByLinkText("Dropdown with Headers");
        waitAndClickByLinkText("Favorites");
        assertElementPresentByXpath("//a[contains(text(),'Proposal')]");
        assertElementPresentByXpath("//li[@class='dropdown-header']");
    }
    
    protected void testLibraryElementsDropdownWithDisabled() throws Exception {
        waitAndClickByLinkText("Dropdown with Disabled");
        waitAndClickByXpath("//a[contains(text(),'Select Action')]");
        assertElementPresentByXpath("//a[contains(text(),'Approve') and @class='uif-actionLink']");
        assertElementPresentByXpath("//a[contains(text(),'Disapprove') and @class='uif-actionLink disabled']");
    }
    
    protected void testLibraryElementsDropdownWithActionOptions() throws Exception {
        waitAndClickByLinkText("Dropdown with Action Options");
        waitAndClickByXpath("//a[contains(text(),'Select Action Options')]");
        assertElementPresentByXpath("//a[contains(text(),'Refresh Section')]");
    }
    
    protected void testLibraryElementsDropdownWithToggleButton() throws Exception {
        waitAndClickByLinkText("Dropdown with Toggle Button");
        waitAndClickByXpath("//div[@id='Demo-DropdownMenu-Example6']/div/div/a/span[@class='caret']");
        assertElementPresentByXpath("//a[contains(text(),'Preferences')]");
    }

    private void testAllDropdowns() throws Exception {
        testLibraryElementsDropdownBasic();
        testLibraryElementsDropdownWithDivider();
        testLibraryElementsDropdownWithHeader();
        testLibraryElementsDropdownWithDisabled();
        testLibraryElementsDropdownWithActionOptions();
        testLibraryElementsDropdownWithToggleButton();
    }

    @Test
    public void testLibraryElementsDropdownBookmark() throws Exception {
        testAllDropdowns();
        passed();
    }

    @Test
    public void testLibraryElementsDropdownNav() throws Exception {
        testAllDropdowns();
        passed();
    }  
}
