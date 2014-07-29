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
        waitAndClickLibraryLink();
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
        assertElementPresentByXpath("//a[contains(text(),'Proposal #1035')]");
        assertElementPresentByXpath("//a[contains(text(),'Proposal #1036')]");
        assertElementPresentByXpath("//a[contains(text(),'Proposal #1037')]");
        assertElementPresentByXpath("//a[contains(text(),'Full Action List')]");
        assertElementPresentByXpath("//li[@class='divider']");
    }
    
    protected void testLibraryElementsDropdownWithHeader() throws Exception {
        waitAndClickByLinkText("Dropdown with Headers");
        waitAndClickByLinkText("Favorites");
        assertElementPresentByXpath("//a[contains(text(),'Proposal')]");
        assertElementPresentByXpath("//a[contains(text(),'Budget')]");
        assertElementPresentByXpath("//a[contains(text(),'Yearly Budget')]");
        assertElementPresentByXpath("//li[@class='dropdown-header']");
    }
    
    protected void testLibraryElementsDropdownWithDisabled() throws Exception {
        waitAndClickByLinkText("Dropdown with Disabled");
        waitAndClickByXpath("//a[contains(text(),'Select Action')]");
        assertElementPresentByXpath("//a[contains(text(),'Approve') and @class='uif-actionLink']");
        assertElementPresentByXpath("//a[contains(text(),'Disapprove') and @class='uif-actionLink disabled']");
        assertElementPresentByXpath("//a[contains(text(),'Approve')]");
    }
    
    protected void testLibraryElementsDropdownWithActionOptions() throws Exception {
        waitAndClickByLinkText("Dropdown with Action Options");
        waitAndClickByXpath("//a[contains(text(),'Select Action Options')]");
        assertElementPresentByXpath("//a[contains(text(),'Refresh Section')]");
        assertElementPresentByXpath("//a[contains(text(),'Action Script')]");
        assertElementPresentByXpath("//a[contains(text(),'Pre-Submit Script')]");
    }
    
    protected void testLibraryElementsDropdownWithToggleButton() throws Exception {
        waitAndClickByLinkText("Dropdown with Toggle Button");
        waitAndClickByXpath("//section[@id='Demo-DropdownMenu-Example6']/div/a/span[@class='caret']");
        assertElementPresentByXpath("//a[contains(text(),'Preferences')]");
        assertElementPresentByXpath("//a[contains(text(),'Logout')]");
    }
    
    protected void testLibraryElementsDropdownWithTable() throws Exception {
        waitAndClickByLinkText("Dropdown with Table");
        waitAndClickByLinkText("more...");
        assertTextPresent(new String[] {"Doc Nbr:","Initiator:","Status:","PI:","Created:","Updated:","Proposal Nbr:","Sponsor Name:"});
        assertTextPresent(new String[] {"2743","thrclark","In Progress","Ken Graves","04:27pm 07/09/2013","12:22pm 07/12/2013","#23533","NIH"});
    }

    private void testAllDropdowns() throws Exception {
        testLibraryElementsDropdownBasic();
        testLibraryElementsDropdownWithDivider();
        testLibraryElementsDropdownWithHeader();
        testLibraryElementsDropdownWithDisabled();
        testLibraryElementsDropdownWithActionOptions();
        testLibraryElementsDropdownWithToggleButton();
        testLibraryElementsDropdownWithTable();
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
