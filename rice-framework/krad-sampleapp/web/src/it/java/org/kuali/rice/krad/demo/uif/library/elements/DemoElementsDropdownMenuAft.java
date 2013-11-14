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
     * /kr-krad/kradsampleapp?viewId=Demo-DropdownMenu-View
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-DropdownMenu-View";
 
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
        waitAndClickByLinkText("User thclark");
        assertElementPresentByXpath("//div[@id='Demo-DropdownMenu-Example1']/div[3]/div/ul/li/a[contains(text(),'Preferences')]");
    }
    
    protected void testLibraryElementsDropdownWithDivider() throws Exception {
        waitAndClickByLinkText("Dropdown with Divider");
        waitAndClickByLinkText("Action List");
        assertElementPresentByXpath("//div[@id='Demo-DropdownMenu-Example2']/div[3]/div/ul/li/a[contains(text(),'Proposal #1034')]");
        assertElementPresentByXpath("//div[@id='Demo-DropdownMenu-Example2']/div[3]/div/ul/li[@class='divider']");
    }
    
    protected void testLibraryElementsDropdownWithHeader() throws Exception {
        waitAndClickByLinkText("Dropdown with Headers");
        waitAndClickByLinkText("Favorites");
        assertElementPresentByXpath("//div[@id='Demo-DropdownMenu-Example3']/div[3]/div/ul/li/a[contains(text(),'Proposal')]");
        assertElementPresentByXpath("//div[@id='Demo-DropdownMenu-Example3']/div[3]/div/ul/li[@class='dropdown-header']");
    }
    
    protected void testLibraryElementsDropdownWithDisabled() throws Exception {
        waitAndClickByLinkText("Dropdown with Disabled");
        waitAndClickByXpath("//div[@id='Demo-DropdownMenu-Example4']/div[3]/div/a");
        assertElementPresentByXpath("//div[@id='Demo-DropdownMenu-Example4']/div[3]/div/ul/li/a[@class='uif-actionLink']");
        assertElementPresentByXpath("//div[@id='Demo-DropdownMenu-Example4']/div[3]/div/ul/li/a[@class='uif-actionLink disabled']");
    }
    
    protected void testLibraryElementsDropdownWithActionOptions() throws Exception {
        waitAndClickByLinkText("Dropdown with Action Options");
        waitAndClickByXpath("//div[@id='Demo-DropdownMenu-Example5']/div[3]/div/a");
        assertElementPresentByXpath("//div[@id='Demo-DropdownMenu-Example5']/div[3]/div/ul/li/a[contains(text(),'Refresh Section')]");
    }
    
    protected void testLibraryElementsDropdownWithToggleButton() throws Exception {
        waitAndClickByLinkText("Dropdown with Toggle Button");
        waitAndClickByXpath("//div[@id='Demo-DropdownMenu-Example6']/div[3]/div/a/span[@class='caret']");
        assertElementPresentByXpath("//div[@id='Demo-DropdownMenu-Example6']/div[3]/div/ul/li/a[contains(text(),'Preferences')]");
    }

    @Test
    public void testLibraryElementsDropdownBookmark() throws Exception {
        testLibraryElementsDropdownBasic();
        testLibraryElementsDropdownWithDivider();
        testLibraryElementsDropdownWithHeader();
        testLibraryElementsDropdownWithDisabled();
        testLibraryElementsDropdownWithActionOptions();
        testLibraryElementsDropdownWithToggleButton();
        passed();
    }

    @Test
    public void testLibraryElementsDropdownNav() throws Exception {
        testLibraryElementsDropdownBasic();
        testLibraryElementsDropdownWithDivider();
        testLibraryElementsDropdownWithHeader();
        testLibraryElementsDropdownWithDisabled();
        testLibraryElementsDropdownWithActionOptions();
        testLibraryElementsDropdownWithToggleButton();
        passed();
    }  
}
