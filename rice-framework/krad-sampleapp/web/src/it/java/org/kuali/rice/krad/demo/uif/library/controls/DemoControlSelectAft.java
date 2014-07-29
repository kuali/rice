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
package org.kuali.rice.krad.demo.uif.library.controls;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoControlSelectAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-SelectControlView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-SelectControlView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Select");
    }

    protected void testLibraryControlSelectDefault() throws Exception {
        assertElementPresentByXpath("//select[@name='inputField1']");
        selectByName("inputField1","Option 1");
    }
    
    protected void testLibraryControlSelectMultiSelect() throws Exception {
        waitAndClickByLinkText("MultiSelect");
        assertElementPresentByXpath("//select[@name='multiSelectField1' and @multiple='multiple']");
        selectByName("multiSelectField1","Select 1");
        selectByName("multiSelectField1","Select 2");
        selectByName("multiSelectField1","Select 3");
    }
    
    protected void testLibraryControlSelectDisabled() throws Exception {
        waitAndClickByLinkText("Disabled");
        assertElementPresentByXpath("//select[@name='inputField1' and @disabled='disabled']");
    }
    
    protected void testLibraryControlSelectGroupedOptions() throws Exception {
        waitAndClickByXpath("//li[@data-tabfor='Demo-SelectControl-Example5']/a");
        waitForElementPresentByXpath("//select/optgroup[@label='American']");
        waitForElementPresentByXpath("//select/optgroup/option");
        waitForElementPresentByXpath("//select/optgroup[@label='Japan']");
        waitForElementPresentByXpath("//select[@multiple]/optgroup[@label='American']");
        waitForElementPresentByXpath("//select[@multiple]/optgroup/option");
        waitForElementPresentByXpath("//select[@multiple]/optgroup[@label='Japan']");
    }
    
    protected void testLibraryControlSelectNavigation() throws Exception {
        waitAndClickByXpath("//li[@data-tabfor='Demo-SelectControl-Example4']/a");
        assertElementPresentByXpath("//div[@data-parent='Demo-SelectControl-Example4']/select/option[@data-location='http://www.kuali.org']");
        selectByXpath("//div[@data-parent='Demo-SelectControl-Example4']/select","Kuali.org");
        //After this step it might throw some Javascript error on console as browser may invoke popup with "Stay on page" or "Leave Page" option.
    }
    
    
    @Test
    public void testControlSelectBookmark() throws Exception {
        testLibraryControlSelectDefault();
        testLibraryControlSelectMultiSelect();
        testLibraryControlSelectDisabled();
        testLibraryControlSelectGroupedOptions();
        testLibraryControlSelectNavigation();
        passed();
    }

    @Test
    public void testControlSelectNav() throws Exception {
        testLibraryControlSelectDefault();
        testLibraryControlSelectMultiSelect();
        testLibraryControlSelectDisabled();
        testLibraryControlSelectGroupedOptions();
        testLibraryControlSelectNavigation();
        passed();
    }  
}
